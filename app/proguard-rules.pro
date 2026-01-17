# ============================================================================
# BikeRideDetection ProGuard/R8 Configuration
# ============================================================================
# For more details, see:
#   http://developer.android.com/guide/developing/tools/proguard.html
# ============================================================================

# ============================================================================
# GENERAL OPTIMIZATION SETTINGS
# ============================================================================

# Preserve line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name
-renamesourcefileattribute SourceFile

# Keep Kotlin metadata for reflection
-keepattributes *Annotation*, InnerClasses, Signature, Exceptions

# Optimization passes (R8 default is 5)
-optimizationpasses 5

# Don't warn about missing classes that are not used
-dontwarn javax.annotation.**
-dontwarn org.jetbrains.annotations.**

# ============================================================================
# HILT / DAGGER DEPENDENCY INJECTION
# ============================================================================

-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Hilt entry points
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.Module class * { *; }

# ============================================================================
# KOTLIN COROUTINES
# ============================================================================

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ============================================================================
# JETPACK DATASTORE
# ============================================================================

-keep class androidx.datastore.** { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# ============================================================================
# FIREBASE CRASHLYTICS
# ============================================================================

-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Keep Crashlytics classes
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

# ============================================================================
# FIREBASE ANALYTICS
# ============================================================================

-keep class com.google.firebase.analytics.** { *; }
-dontwarn com.google.firebase.analytics.**

# ============================================================================
# FIREBASE PERFORMANCE MONITORING
# ============================================================================

-keep class com.google.firebase.perf.** { *; }
-dontwarn com.google.firebase.perf.**

# ============================================================================
# GOOGLE PLAY SERVICES
# ============================================================================

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Activity Recognition
-keep class com.google.android.gms.location.** { *; }

# ============================================================================
# ANDROID SERVICES & RECEIVERS
# ============================================================================

# Keep CallScreeningService (critical for app functionality)
-keep class com.example.bikeridedetection.service.BikeCallScreeningService { *; }

# Keep BroadcastReceiver for activity transitions
-keep class com.example.bikeridedetection.service.BikeTransitionReceiver { *; }

# Keep all services
-keep class com.example.bikeridedetection.service.** { *; }

# ============================================================================
# DOMAIN MODELS (for serialization/reflection if needed)
# ============================================================================

-keep class com.example.bikeridedetection.domain.model.** { *; }

# ============================================================================
# TIMBER LOGGING
# ============================================================================

-dontwarn org.jetbrains.annotations.**
-keep class timber.log.Timber { *; }

# ============================================================================
# SECURITY HARDENING
# ============================================================================

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Remove Timber debug logs in release
-assumenosideeffects class timber.log.Timber {
    public static void v(...);
    public static void d(...);
    public static void i(...);
}

# ============================================================================
# VIEWBINDING
# ============================================================================

-keep class * implements androidx.viewbinding.ViewBinding {
    public static * inflate(android.view.LayoutInflater);
    public static * inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
    public static * bind(android.view.View);
}

# ============================================================================
# LIFECYCLE COMPONENTS
# ============================================================================

-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }