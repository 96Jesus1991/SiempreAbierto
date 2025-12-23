# ============================================
# SIEMPRE ABIERTO - Reglas ProGuard
# App 100% OFFLINE para conductores
# ============================================

# --------------------------------------------
# REGLAS GENERALES
# --------------------------------------------

# Mantener nombres de clases para debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Mantener anotaciones
-keepattributes *Annotation*

# --------------------------------------------
# KOTLIN
# --------------------------------------------

-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# --------------------------------------------
# ROOM DATABASE
# --------------------------------------------

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Mantener DAOs
-keep interface com.siempreabierto.data.dao.** { *; }

# Mantener Entities
-keep class com.siempreabierto.data.entities.** { *; }

# --------------------------------------------
# JETPACK COMPOSE
# --------------------------------------------

-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Mantener clases de UI
-keep class com.siempreabierto.ui.** { *; }

# --------------------------------------------
# NAVIGATION
# --------------------------------------------

-keep class androidx.navigation.** { *; }
-keepclassmembers class * {
    @androidx.navigation.NavDestination <methods>;
}

# --------------------------------------------
# GSON (para JSON de rutas y waypoints)
# --------------------------------------------

-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Mantener clases que se serializan con Gson
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# --------------------------------------------
# PLAY ASSET DELIVERY
# --------------------------------------------

-keep class com.google.android.play.core.** { *; }
-dontwarn com.google.android.play.core.**

# --------------------------------------------
# LOCATION SERVICES
# --------------------------------------------

-keep class com.google.android.gms.location.** { *; }
-dontwarn com.google.android.gms.**

# --------------------------------------------
# DATA CLASSES (mantener para serialización)
# --------------------------------------------

-keepclassmembers class com.siempreabierto.data.entities.* {
    <init>(...);
    <fields>;
}

# Waypoint y RoutePointOfInterest
-keep class com.siempreabierto.data.entities.Waypoint { *; }
-keep class com.siempreabierto.data.entities.RoutePointOfInterest { *; }
-keep class com.siempreabierto.data.entities.RouteSummary { *; }
-keep class com.siempreabierto.data.entities.ContributionSummary { *; }
-keep class com.siempreabierto.data.entities.VehicleProfile { *; }

# --------------------------------------------
# ENUMS Y OBJECTS
# --------------------------------------------

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.siempreabierto.data.entities.PlaceCategories { *; }
-keep class com.siempreabierto.data.entities.Regions { *; }
-keep class com.siempreabierto.data.entities.RestrictionTypes { *; }
-keep class com.siempreabierto.data.entities.ContributionTargets { *; }
-keep class com.siempreabierto.data.entities.ContributionActions { *; }
-keep class com.siempreabierto.data.entities.HelpTypes { *; }
-keep class com.siempreabierto.data.entities.HelperVehicleTypes { *; }
-keep class com.siempreabierto.data.entities.RouteVehicleTypes { *; }
-keep class com.siempreabierto.data.entities.UserVehicleTypes { *; }

# --------------------------------------------
# VIEWMODELS
# --------------------------------------------

-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}

# --------------------------------------------
# OPTIMIZACIONES
# --------------------------------------------

# Optimizaciones agresivas para reducir tamaño
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose

# Eliminar logs en release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# --------------------------------------------
# WARNINGS A IGNORAR
# --------------------------------------------

-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn java.lang.invoke.**
