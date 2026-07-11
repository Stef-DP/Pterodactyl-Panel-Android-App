# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep interface com.stefdp.pterodactylpanel.network.client.** { *; }
-keep interface com.stefdp.pterodactylpanel.network.application.** { *; }

-keep class com.stefdp.pterodactylpanel.network.application.** { *; }
-keep class com.stefdp.pterodactylpanel.network.application.models.** { *; }

-keep class com.stefdp.pterodactylpanel.network.client.** { *; }
-keep class com.stefdp.pterodactylpanel.network.client.models.** { *; }

-keep class com.stefdp.pterodactylpanel.network.websocket.** { *; }
-keep class com.stefdp.pterodactylpanel.network.websocket.models.** { *; }

-keep class com.stefdp.pterodactylpanel.screens.** { *; }
-keep interface com.stefdp.pterodactylpanel.screens.** { *; }

-keep class com.stefdp.pterodactylpanel.BuildConfig { *; }

-keep class androidx.lifecycle.ViewTreeLifecycleOwner { *; }
-keep class androidx.lifecycle.ViewTreeViewModelStoreOwner { *; }
-keep class androidx.savedstate.ViewTreeSavedStateRegistryOwner { *; }