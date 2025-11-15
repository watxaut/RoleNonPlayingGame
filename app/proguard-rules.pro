# ProGuard Rules for Role Non-Playing Game
# Updated: 2025-11-15
# Security hardened configuration

# ================================================================================================
# GENERAL ANDROID RULES
# ================================================================================================

# Keep source file names and line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom exceptions
-keep public class * extends java.lang.Exception

# ================================================================================================
# KOTLIN
# ================================================================================================

# Keep Kotlin metadata
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeInvisibleAnnotations,RuntimeInvisibleParameterAnnotations

# Keep Kotlin Metadata
-keep class kotlin.Metadata { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# Kotlin serialization
-keepattributes InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.watxaut.rolenonplayinggame.**$$serializer { *; }
-keepclassmembers class com.watxaut.rolenonplayinggame.** {
    *** Companion;
}
-keepclasseswithmembers class com.watxaut.rolenonplayinggame.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ================================================================================================
# JETPACK COMPOSE
# ================================================================================================

# Keep all Composables
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Compose runtime
-keep class androidx.compose.runtime.** { *; }
-keep interface androidx.compose.runtime.** { *; }

# Keep Compose UI
-keep class androidx.compose.ui.** { *; }
-keep interface androidx.compose.ui.** { *; }

# Keep Compose Material3
-keep class androidx.compose.material3.** { *; }
-keep interface androidx.compose.material3.** { *; }

# Keep navigation
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# ================================================================================================
# ROOM DATABASE
# ================================================================================================

# Keep Room database classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** DATABASE_NAME;
}

# Keep DAO interfaces and implementations
-keep interface * extends androidx.room.Dao
-keep class * implements androidx.room.Dao { *; }

# Keep database entities
-keep @androidx.room.Entity class * {
    *;
}

# Keep all fields annotated with @ColumnInfo, @PrimaryKey, @Embedded, etc.
-keepclassmembers class * {
    @androidx.room.ColumnInfo <fields>;
    @androidx.room.PrimaryKey <fields>;
    @androidx.room.Embedded <fields>;
    @androidx.room.Relation <fields>;
}

# Keep entity constructors
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}

-dontwarn androidx.room.**

# ================================================================================================
# HILT / DAGGER
# ================================================================================================

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Hilt modules
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.components.SingletonComponent class * { *; }

# Keep injected classes
-keep class **_HiltModules { *; }
-keep class **_HiltModules$** { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# Keep classes with @Inject constructors
-keepclasseswithmembernames class * {
    @javax.inject.Inject <init>(...);
}

-keepclasseswithmembers class * {
    @javax.inject.Inject <fields>;
}

-keepclasseswithmembers class * {
    @javax.inject.Inject <methods>;
}

-dontwarn dagger.hilt.**
-dontwarn com.google.errorprone.annotations.**

# ================================================================================================
# SUPABASE
# ================================================================================================

# Keep Supabase client classes
-keep class io.github.jan.supabase.** { *; }
-keep interface io.github.jan.supabase.** { *; }

# Keep Supabase Auth
-keep class io.github.jan.supabase.auth.** { *; }
-keep class io.github.jan.supabase.auth.user.** { *; }

# Keep Supabase Postgrest
-keep class io.github.jan.supabase.postgrest.** { *; }

# Keep Supabase Realtime
-keep class io.github.jan.supabase.realtime.** { *; }

-dontwarn io.github.jan.supabase.**

# ================================================================================================
# KTOR CLIENT
# ================================================================================================

# Keep Ktor core
-keep class io.ktor.** { *; }
-keep interface io.ktor.** { *; }

# Keep Ktor client
-keep class io.ktor.client.** { *; }
-keep class io.ktor.client.engine.** { *; }
-keep class io.ktor.client.engine.cio.** { *; }

# Keep Ktor serialization
-keep class io.ktor.serialization.** { *; }
-keep class io.ktor.serialization.kotlinx.** { *; }

# Keep Ktor plugins
-keep class io.ktor.client.plugins.** { *; }
-keep class io.ktor.client.plugins.contentnegotiation.** { *; }
-keep class io.ktor.client.plugins.logging.** { *; }

-dontwarn io.ktor.**
-dontwarn org.slf4j.**

# ================================================================================================
# PROJECT-SPECIFIC RULES
# ================================================================================================

# Keep BuildConfig
-keep class com.watxaut.rolenonplayinggame.BuildConfig { *; }

# Keep Application class
-keep class com.watxaut.rolenonplayinggame.RoleNonPlayingGameApplication { *; }

# Keep MainActivity
-keep class com.watxaut.rolenonplayinggame.MainActivity { *; }

# Keep all domain models (data classes used for serialization)
-keep class com.watxaut.rolenonplayinggame.domain.model.** { *; }

# Keep DTOs (data transfer objects)
-keep class com.watxaut.rolenonplayinggame.data.remote.dto.** { *; }

# Keep entities
-keep class com.watxaut.rolenonplayinggame.data.local.entity.** { *; }

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep data classes
-keep class com.watxaut.rolenonplayinggame.**.data.** { *; }

# Keep sealed classes
-keep class com.watxaut.rolenonplayinggame.**$* { *; }

# ================================================================================================
# ANDROIDX SECURITY (EncryptedSharedPreferences)
# ================================================================================================

-keep class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }
-dontwarn androidx.security.crypto.**
-dontwarn com.google.crypto.tink.**

# ================================================================================================
# REMOVE LOGGING IN RELEASE
# ================================================================================================

# Remove Log.d, Log.v calls (keep Log.e, Log.w for crash reporting)
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Remove println
-assumenosideeffects class java.io.PrintStream {
    public void println(...);
    public void print(...);
}

# ================================================================================================
# OPTIMIZATION FLAGS
# ================================================================================================

# Enable aggressive optimization
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Optimization filters
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# ================================================================================================
# WARNINGS TO IGNORE
# ================================================================================================

-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn javax.annotation.**
-dontwarn java.lang.management.**

# ================================================================================================
# CRASH REPORTING (Keep stack traces readable)
# ================================================================================================

# Keep custom exceptions
-keep public class * extends java.lang.Exception {
    public <init>(...);
}

# Keep stack trace info
-keepattributes StackMapTable
-keepattributes LocalVariableTable
-keepattributes LocalVariableTypeTable
