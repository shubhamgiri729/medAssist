# Smart Medication Assistant System 📱💊

A software-centric, cloud-integrated mHealth application designed to transform traditional prescription management into a digitized, interactive healthcare process while reducing medication non-adherence through automated reminders and real-time synchronization.

---

## 📌 Project Overview

Medication non-adherence—often caused by forgetfulness or misunderstanding prescription instructions—can significantly reduce treatment effectiveness and increase healthcare risks.

The **Smart Medication Assistant System** is a mobile-based mHealth application that replaces expensive hardware-dependent solutions such as electronic pillboxes with a scalable, software-only ecosystem featuring:

- Automated medication reminders
- Real-time cloud synchronization
- Prescription management
- Patient adherence monitoring
- Secure authentication and notifications

This project aims to provide an efficient, accessible, and intelligent medication management platform for both patients and healthcare administrators.

---

## ✨ Key Features

- ⏰ Automated time-based medication alerts
- ☁️ Real-time cloud synchronization using Firebase
- 📋 Digital prescription management
- 🔔 Push notifications using Firebase Cloud Messaging (FCM)
- 📊 Patient medication adherence tracking
- 🔐 Secure login and authentication
- 📱 User-friendly Android interface
- 🧾 Historical medication logs and monitoring

---

## 📈 Performance Highlights

- ✅ ~100% alert accuracy during testing
- ⚡ Average synchronization latency below 5 seconds
- 📊 Reliable real-time patient compliance tracking
- 🔄 Seamless cloud-based prescription updates

---

## 🏗️ System Architecture

The platform follows a **three-layer decoupled architecture** for scalability and maintainability.

### 1️⃣ User Interface Layer

#### Patient Mobile Application
- Receives medication reminders
- Displays prescriptions and dosage schedules
- Allows users to log medication intake events

#### Administrative Portal
- Manages patient profiles
- Creates and updates digital prescriptions
- Monitors historical adherence reports

---

### 2️⃣ Application Layer

Handles:
- Business logic execution
- Prescription parsing
- Time-based scheduling rules
- Notification trigger management

---

### 3️⃣ Cloud Data Layer

Powered by Firebase services:

- **Firebase Firestore** for real-time NoSQL database synchronization
- **Firebase Cloud Messaging (FCM)** for push notifications
- **Firebase Authentication** for secure login management

---

## 🛠️ Tech Stack

### Frontend & Mobile Development
- Kotlin
- Java
- Android Studio
- Jetpack Compose

### Backend & Cloud Services
- Firebase Firestore
- Firebase Cloud Messaging (FCM)
- Firebase Authentication

### Database
- NoSQL Cloud Database (Firestore)

---

## 📂 Project Structure

```bash
SmartMedicationAssistant/
│
├── app/
├── java/
├── res/
├── firebase/
├── manifests/
└── gradle/
```

---

## 🚀 Getting Started

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/your-username/smart-medication-assistant.git
cd smart-medication-assistant
```

---

### 2️⃣ Firebase Setup

1. Create a project in the Firebase Console
2. Enable:
   - Firebase Authentication
   - Cloud Firestore
   - Firebase Cloud Messaging
3. Download the `google-services.json` file
4. Place it inside:

```bash
/app/
```

---

### 3️⃣ Build and Run

1. Open the project in Android Studio
2. Allow Gradle to sync dependencies
3. Run the application on:
   - Android Emulator
   - Physical Android Device

> Recommended Android API Level: 26 or higher

---

## 🔔 Notification Workflow

1. Admin uploads prescription
2. Prescription data syncs with Firestore
3. Application layer generates reminder schedules
4. FCM triggers push notifications
5. Patient logs medication status
6. Compliance records update in real-time

---

## 📊 Future Enhancements

- AI-based medication recommendation system
- Voice assistant integration
- Multi-language support
- Doctor-patient live chat
- Wearable device integration
- Offline synchronization support

---

## 👨‍💻 Developer

**Shubham Giri**  
---

## 📜 License

This project is developed for educational and research purposes.

---
