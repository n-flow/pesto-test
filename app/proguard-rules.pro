# General Android rules
-keep class androidx.appcompat.** { *; }
-keep class com.google.android.material.** { *; }
-keep class androidx.constraintlayout.** { *; }

# Android Lifecycle
-keep class androidx.lifecycle.** { *; }

# Android Navigation
-keep class androidx.navigation.** { *; }

# Android Paging
-keep class androidx.paging.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }

# Legacy Support
-keep class androidx.legacy.** { *; }

# Picasso
-keep class com.squareup.picasso.** { *; }

# Google Play Services
-keep class com.google.android.gms.** { *; }

# Retrofit and OkHttp
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep class com.google.code.gson.** { *; }
-keep class retrofit2.** { *; }
-keep class retrofit.** { *; }
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep class com.jakewharton.retrofit.** { *; }
-keep class com.squareup.okhttp3.** { *; }
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz
-keepattributes Exceptions
-dontwarn retrofit2.**
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.**
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn org.conscrypt.*
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# CropView
-keep class com.isseiaoki.** { *; }

# CircleImageView
-keep class de.hdodenhof.** { *; }

# TextDrawable
-keep class com.amulyakhare.** { *; }

# Stripe
-keep class com.stripe.** { *; }

# Google Play Core
-keep class com.google.android.play.** { *; }

# AndroidX Activity and Fragment
-keep class androidx.activity.** { *; }
-keep class androidx.fragment.** { *; }

# ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }

# Keep Picasso's annotations
-keepnames class com.squareup.picasso.** { *; }

# Keep GSON's annotations
-keepattributes Signature
-keepattributes *Annotation*

# Keep classes that might be accessed through reflection
-keepclassmembers public class * {
    public static final **[] $VALUES;
}

# Keep enum values
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable and Serializable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep kotlinx's extensions
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keepclassmembers class **.ExtensionsKt {
    <methods>;
}

# Remove debug logs
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# Keep RxJava annotations
-keep class io.reactivex.annotations.** { *; }
# RxJava 2
-dontwarn io.reactivex.**
-keep class io.reactivex.** { *; }
-keepclassmembers class io.reactivex.** { *; }

# RxAndroid 2
-dontwarn io.reactivex.android.**
-keep class io.reactivex.android.** { *; }
-keepclassmembers class io.reactivex.android.** { *; }

# Keep classes from javax.naming
-keep class javax.naming.** { *; }

# Keep your model classes
-keep class com.parth.** { *; }

-dontwarn com.facebook.infer.annotation.*
-dontwarn com.facebook.infer.annotation.*



# This is also needed for R8 in compat mode since multiple
# optimizations will remove the generic signature such as class
# merging and argument removal. See:
# https://r8.googlesource.com/r8/+/refs/heads/main/compatibility-faq.md#troubleshooting-gson-gson
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Optional. For using GSON @Expose annotation
-keepattributes AnnotationDefault,RuntimeVisibleAnnotations