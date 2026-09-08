# 🎯 UPSC Syllabus & Spaced Repetition Tracker

A modern, native Android application designed to help UPSC (Civil Services Exam) aspirants track their syllabus coverage and master subjects through scientifically proven **Spaced Repetition** scheduling.

Built with **Kotlin**, **Jetpack Compose**, and **Firebase Firestore**.

---

## ✨ Features

- 📚 **Full UPSC Syllabus Seed Data**: Pre-loaded with comprehensive Civil Services prelims and mains subjects and topic structures.
- 🔁 **Smart Spaced Repetition Engine**: Calculates optimal revision intervals (Day 1 ➔ Day 4 ➔ Day 7 ➔ Day 30) based on unique study calendar days.
- ⚙️ **Custom Revision Intervals**: Set your own post-Day 30 revision intervals (e.g., every 25 days) directly from your profile settings.
- ↩️ **Undo Revision Capability**: Revert accidental "Read Today" taps with instant calculation rollback.
- ☁️ **Real-time Cloud Sync & Offline Support**: Firebase Firestore integration keeps all your progress safely backed up across devices.
- 🔐 **Secure Account Management**: Profile details (Name, Email, Phone) managed directly with instant Firestore synchronization.
- ⚡ **Ultra-Lightweight & Fast**: Optimized using R8 tree-shaking and resource shrinking (< 3 MB release APK size).

---

## 🛠️ Tech Stack

- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Backend / Database**: Firebase Authentication, Cloud Firestore
- **Asynchronous Flow**: Kotlin Coroutines & StateFlow
- **Build System**: Gradle 8.7 (Kotlin DSL)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Jellyfish / Ladybug or newer
- JDK 17
- Android SDK 34 (Android 14)

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/rubulakhter/upsc-syllabus-tracker.git
   cd upsc-syllabus-tracker
   ```

2. **Firebase Setup**:
   - Place your `google-services.json` inside the `app/` directory.

3. **Build and Run**:
   - Open the project in Android Studio and run on an emulator or physical device.

---

## 📱 Screenshots & Previews

| Dashboard | Subject Detail | Profile & Settings |
| :---: | :---: | :---: |
| Overview of overall progress & due revisions | Dynamic topic tracking & revision history | Manage personal details & custom intervals |

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
