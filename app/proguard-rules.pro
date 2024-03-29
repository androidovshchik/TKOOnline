-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-repackageclasses
# only for debug
# -keep class ru.iqsolution.tkoonline.** { *; }

# keep telemetry desc
-keep class ru.iqsolution.tkoonline.models.TelemetryDesc { *; }

# common case
-dontwarn org.slf4j.**

# special cases
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class com.google.android.gms.vision.CameraSource { *; }

# coroutines
-dontwarn kotlinx.coroutines.flow.**inlined**

# retrofit + okhttp
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**
# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**

# jackson
# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.**

# rabbitmq
-dontwarn io.micrometer.**
-dontwarn com.codahale.**
-dontwarn javax.naming.**
-dontwarn javax.security.**
-dontwarn java.beans.**

# stetho
-keep class com.facebook.stetho.** { *; }
-dontwarn com.facebook.stetho.**

# workmanager
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.InputMerger
# Keep all constructors on ListenableWorker, Worker (also marked with @Keep)
-keep public class * extends androidx.work.ListenableWorker {
    public <init>(...);
}
# We need to keep WorkerParameters for the ListenableWorker constructor
-keep class androidx.work.WorkerParameters