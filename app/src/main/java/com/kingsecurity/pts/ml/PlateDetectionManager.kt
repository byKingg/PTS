package com.kingsecurity.pts.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import com.kingsecurity.pts.models.DetectionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlateDetectionManager(private val context: Context) {
    
    private var interpreter: Interpreter? = null
    private val firestore = FirebaseFirestore.getInstance()
    private var suspiciousPlates = listOf<String>()
    
    init {
        initializeModel()
        loadSuspiciousPlatesFromFirebase()
    }
    
    private fun initializeModel() {
        try {
            val modelBuffer = FileUtil.loadMappedFile(context, "yolov8n.tflite")
            interpreter = Interpreter(modelBuffer)
            Log.d("PlateDetection", "YOLO modeli başarıyla yüklendi")
        } catch (e: Exception) {
            Log.e("PlateDetection", "Model yükleme hatası: ${e.message}")
        }
    }
    
    private fun loadSuspiciousPlatesFromFirebase() {
        firestore.collection("suspicious_plates")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("PlateDetection", "Plaka yükleme hatası: ${error.message}")
                    return@addSnapshotListener
                }
                
                suspiciousPlates = snapshots?.mapNotNull { doc ->
                    doc.getString("plateNumber")
                } ?: emptyList()
                
                Log.d("PlateDetection", "${suspiciousPlates.size} şüpheli plaka yüklendi")
            }
    }
    
    suspend fun detectPlate(bitmap: Bitmap, userId: String): DetectionResult = withContext(Dispatchers.Default) {
        if (interpreter == null) {
            return@withContext DetectionResult(
                plateNumber = "HATA",
                confidence = 0f,
                timestamp = System.currentTimeMillis(),
                userId = userId,
                isSuspicious = false
            )
        }
        
        try {
            // Görüntüyü ön işle
            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(640, 640, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(0f, 255f))
                .build()
            
            var tensorImage = TensorImage(org.tensorflow.lite.DataType.FLOAT32)
            tensorImage.load(bitmap)
            tensorImage = imageProcessor.process(tensorImage)
            
            // Modelı çalıştır
            val output = Array(1) { FloatArray(8400) }
            interpreter?.run(tensorImage.buffer, output)
            
            // Sonuçları işle
            val detectionResult = postProcessResults(output[0], userId)
            
            // Tespit edilen plaka şüpheli mi kontrol et
            if (detectionResult.plateNumber != "TESPİT_BULUNAMADI") {
                detectionResult.isSuspicious = suspiciousPlates.any { suspiciousPlate ->
                    detectionResult.plateNumber.contains(suspiciousPlate, ignoreCase = true)
                }
            }
            
            detectionResult
        } catch (e: Exception) {
            Log.e("PlateDetection", "Çıkarsama hatası: ${e.message}")
            DetectionResult(
                plateNumber = "HATA",
                confidence = 0f,
                timestamp = System.currentTimeMillis(),
                userId = userId,
                isSuspicious = false
            )
        }
    }
    
    private fun postProcessResults(output: FloatArray, userId: String): DetectionResult {
        var maxConfidence = 0f
        var bestPlateNumber = "TESPİT_BULUNAMADI"
        
        // YOLO çıkışını işle (basitleştirilmiş)
        for (i in 0 until output.size step 85) {
            if (i + 4 < output.size) {
                val confidence = output[i + 4]
                
                if (confidence > 0.5f && confidence > maxConfidence) {
                    maxConfidence = confidence
                    // Modelden plaka numarasını çıkart
                    // Bu bir yer tutucu - gerçek uygulama YOLO eğitim ayarlarına bağlıdır
                    bestPlateNumber = "34ABC${(1000..9999).random()}"
                }
            }
        }
        
        return DetectionResult(
            plateNumber = bestPlateNumber,
            confidence = maxConfidence,
            timestamp = System.currentTimeMillis(),
            userId = userId,
            isSuspicious = false,
            location = "Kamera Akışı"
        )
    }
    
    suspend fun saveDetectionToFirebase(detection: DetectionResult, userId: String) = withContext(Dispatchers.IO) {
        try {
            val detectionData = mapOf(
                "plateNumber" to detection.plateNumber,
                "confidence" to detection.confidence,
                "timestamp" to detection.timestamp,
                "userId" to userId,
                "isSuspicious" to detection.isSuspicious,
                "location" to detection.location
            )
            
            firestore.collection("detections")
                .add(detectionData)
                .addOnSuccessListener {
                    Log.d("PlateDetection", "Tespit kaydedildi: ${detection.plateNumber}")
                }
                .addOnFailureListener { e ->
                    Log.e("PlateDetection", "Tespit kaydetme hatası: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("PlateDetection", "Hata: ${e.message}")
        }
    }
    
    fun release() {
        interpreter?.close()
        interpreter = null
        Log.d("PlateDetection", "Kaynaklar serbest bırakıldı")
    }
}