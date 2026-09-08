# Keep database models from being obfuscated or stripped by R8/ProGuard
-keep class com.tracker.syllabus.data.model.** { *; }

# Firebase Firestore Serialization rules
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
}
