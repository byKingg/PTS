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
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import com.kingsecurity.pts.models.DetectionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

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
            Log.d("PlateDetection", "YOLO model loaded successfully")
        } catch (e: Exception) {
            Log.e("PlateDetection", "Error loading model: ${e.message}")
        }
    }
    
    private fun loadSuspiciousPlatesFromFirebase() {
        firestore.collection("suspicious_plates")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("PlateDetection", "Error loading plates: ${error.message}")
                    return@addSnapshotListener
                }
                
                suspiciousPlates = snapshots?.mapNotNull { doc ->
                    doc.getString("plateNumber")
                } ?: emptyList()
                
                Log.d("PlateDetection", "Loaded ${suspiciousPlates.size} suspicious plates")
            }
    }
    
    suspend fun detectPlate(bitmap: Bitmap, userId: String): DetectionResult = withContext(Dispatchers.Default) {
        if (interpreter == null) {
            return@withContext DetectionResult(
                plateNumber = "ERROR",
                confidence = 0f,
                timestamp = System.currentTimeMillis(),
                detectedBy = userId,
                isSuspicious = false
            )
        }
        
        try {
            // Preprocess the image
            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(640, 640, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(0f, 255f))
                .build()
            
            var tensorImage = TensorImage(org.tensorflow.lite.DataType.FLOAT32)
            tensorImage.load(bitmap)
            tensorImage = imageProcessor.process(tensorImage)
            
            // Run inference
            val output = Array(1) { FloatArray(8400) }
            interpreter?.run(tensorImage.buffer, output)
            
            // Post-process results
            val detectionResult = postProcessResults(output[0], userId)
            
            // Check if detected plate is suspicious
            if (detectionResult.plateNumber != "NO_DETECTION") {
                detectionResult.isSuspicious = suspiciousPlates.any { suspiciousPlate ->
                    detectionResult.plateNumber.contains(suspiciousPlate, ignoreCase = true)
                }
            }
            
            detectionResult
        } catch (e: Exception) {
            Log.e("PlateDetection", "Error during inference: ${e.message}")
            DetectionResult(
                plateNumber = "ERROR",
                confidence = 0f,
                timestamp = System.currentTimeMillis(),
                detectedBy = userId,
                isSuspicious = false
            )
        }
    }
    
    private fun postProcessResults(output: FloatArray, userId: String): DetectionResult {
        var maxConfidence = 0f
        var bestPlateNumber = "NO_DETECTION"
        
        // Process YOLO output (simplified)
        for (i in 0 until output.size step 85) {
            if (i + 4 < output.size) {
                val confidence = output[i + 4]
                
                if (confidence > 0.5f && confidence > maxConfidence) {
                    maxConfidence = confidence
                    // Extract plate number from model output
                    // This is a placeholder - actual implementation depends on your YOLO training
                    bestPlateNumber = "34ABC${(1000..9999).random()}"
                }
            }
        }
        
        return DetectionResult(
            plateNumber = bestPlateNumber,
            confidence = maxConfidence,
            timestamp = System.currentTimeMillis(),
            detectedBy = userId,
            isSuspicious = false,
            location = "Camera Feed"
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
                    Log.d("PlateDetection", "Detection saved: ${detection.plateNumber}")
                }
                .addOnFailureListener { e ->
                    Log.e("PlateDetection", "Error saving detection: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("PlateDetection", "Error: ${e.message}")
        }
    }
    
    fun release() {
        interpreter?.close()
        interpreter = null
    }
}