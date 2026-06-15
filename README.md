# PTS - PLAKA TANIMA SİSTEMİ

**License Plate Recognition System for Android**

PTS, şüpheli araçların plakaları gerçek zamanda tanıyabilen akıllı bir mobil uygulamadır. YOLO nesne algılama modeli ve Firebase backend'i kullanarak plakaları tespit eder ve şüpheli plakaları kaydeder.

## 🎯 Özellikler

- ✅ **Gerçek Zamanlı Plaka Tanıması** - YOLO modeli ile kamera akışından plaka algılaması
- ✅ **Firebase Kimlik Doğrulama** - Email/Şifre ve Parmak İzi desteği
- ✅ **Admin Panel** - Şüpheli plakaları yönetme ve onaylama
- ✅ **Firestore Database** - Bulut tabanlı veri depolama
- ✅ **Biometrik Giriş** - Parmak İzi kimlik doğrulama
- ✅ **İnaktivite Zaman Aşımı** - Otomatik çıkış özelliği
- ✅ **Gerçek Zamanlı Senkronizasyon** - Tüm cihazlara anlık plaka güncellemeleri

## 📋 Gereksinimler

- Android SDK 24 veya üzeri
- Kotlin 1.9.0+
- Firebase Project
- YOLOv8 Eğitilmiş Model (TFLite)

## 🚀 Kurulum

### 1. Repository'i Klonlayın
```bash
git clone https://github.com/byKingg/PTS.git
cd PTS
```

### 2. Firebase Kurulumu

1. [Firebase Console](https://console.firebase.google.com) ziyaret edin
2. Yeni proje oluşturun: "PTS"
3. Android uygulamasını ekleyin
4. `google-services.json` dosyasını `app/` dizinine yerleştirin
5. Authentication'da Email/Şifre ve Biometric etkinleştirin
6. Firestore Database oluşturun

### 3. YOLO Modelini Ekleyin

1. YOLOv8 modelini eğitin veya önceden eğitilmiş bir model indirin
2. TFLite formatına dönüştürün:
```bash
yolo export model=yolov8n.pt format=tflite imgsz=640
```
3. `yolov8n.tflite` dosyasını `app/src/main/assets/` dizinine yerleştirin

### 4. Android Studio'da Açın

```bash
android-studio .
```

### 5. Projeyi Yapılandırın

- Gradle'ı senkronize edin
- Build yapın ve test etmek için emülatöre yükleyin

## 📁 Proje Yapısı

```
PTS/
├── app/src/main/
│   ├── java/com/kingsecurity/pts/
│   │   ├── activities/
│   │   │   ├── SplashActivity.kt
│   │   │   ├── LoginActivity.kt
│   │   │   ├── RegisterActivity.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── BiometricActivity.kt
│   │   │   └── AdminPanelActivity.kt
│   │   ├── models/
│   │   │   ├── User.kt
│   │   │   ├── SuspiciousPlate.kt
│   │   │   └── DetectionResult.kt
│   │   ├── ml/
│   │   │   └── PlateDetectionManager.kt
│   │   ├── firebase/
│   │   │   ├── FirebaseManager.kt
│   │   │   └── UserManager.kt
│   │   ├── utils/
│   │   │   ├── SharedPrefHelper.kt
│   │   │   ├── BiometricHelper.kt
│   │   │   ├── InactivityManager.kt
│   │   │   └── Constants.kt
│   │   └── adapters/
│   │       └── SuspiciousPlateAdapter.kt
│   ├── res/
│   │   ├── layout/
│   │   ├── values/
│   │   └── drawable/
│   └── AndroidManifest.xml
├── build.gradle
├── settings.gradle
├── firestore.rules
└── database.rules
```

## 🔐 Firestore Yapısı

### Collections

#### `users`
```json
{
  "userId": "user123",
  "email": "user@example.com",
  "isApproved": true,
  "isAdmin": false,
  "createdAt": 1624291200000,
  "lastLogin": 1624291200000
}
```

#### `suspicious_plates`
```json
{
  "plateNumber": "34ABC1234",
  "reason": "Şüpheli araç",
  "severity": "HIGH",
  "createdAt": 1624291200000,
  "createdBy": "admin123"
}
```

#### `detections`
```json
{
  "plateNumber": "34ABC1234",
  "confidence": 0.95,
  "timestamp": 1624291200000,
  "userId": "user123",
  "isSuspicious": true,
  "location": "Camera Feed"
}
```

## 🎯 Kullanım

### İlk Kez Giriş

1. Uygulamayı açın
2. **Kayıt Ol** butonuna tıklayın
3. Email ve şifre girin
4. Admin onayını bekleyin
5. Giriş yapın

### Tarama

1. **Taramaya Başla** butonuna tıklayın
2. Kamera araç plakaları tarar
3. Şüpheli plaka bulunursa uyarı verilir

### Admin Panel (Yalnızca Admin)

1. **Admin Panel** butonuna tıklayın
2. **+** butonuyla yeni şüpheli plaka ekleyin
3. Plakalar otomatik tüm cihazlarda güncellenir

## 🔒 Güvenlik

- ✅ Firebase Authentication ile güvenli giriş
- ✅ Parmak İzi kimlik doğrulama
- ✅ Firestore Security Rules ile veri koruması
- ✅ Admin onay mekanizması
- ✅ Otomatik çıkış (5 dakika inaktivite)

## 📱 Desteklenen Cihazlar

- Android 7.0 (API 24) veya üzeri
- Biometric sensörü olan cihazlar (parmak izi)

## 🤝 Katkıda Bulunma

Katkılar hoş geldiniz! Lütfen bir issue açın veya PR gönderin.

## 📄 Lisans

MIT License - [LİSANSI GÖR](LICENSE)

## 👨‍💻 Geliştirici

**Powered by KingSecurity © 2026**

---

**Not:** Bu proje eğitim ve yasal amaçlar için kullanılmalıdır.