plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.stefdp.pterodactylpanel"
    compileSdk {
        version = release(37)
    }

    val baseAppName = "Pterodactyl Panel"

    defaultConfig {
        applicationId = "com.stefdp.pterodactylpanel"
        minSdk = 26
        targetSdk = 37
        versionCode = 2
        versionName = "1.0.1"
        ndkVersion = "29.0.14206865"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            manifestPlaceholders["appName"] = baseAppName

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            isDebuggable = false

            ndk {
                debugSymbolLevel = "FULL"
            }

//            signingConfig = signingConfigs.getByName("debug")
        }

        debug {
            isMinifyEnabled = false
            //noinspection NotShrinkingResources
            isShrinkResources = false

            isDebuggable = true

            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            manifestPlaceholders["appName"] = "$baseAppName (Debug)"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.core.splashscreen)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // HTTP Requests
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.logging.interceptor)

    // Skeleton Loading
    implementation(libs.compose.shimmer)

    // Date to Human Readable
    implementation(libs.human.readable)

    // SemVer
    implementation(libs.semver)

    // Ansi Parser
    implementation(libs.android.ansi)
    implementation(libs.android.ansi.ktx)

    // Charts
    implementation(libs.compose.charts)

    // Code Highlighting
    implementation(libs.highlight.view)
    implementation(libs.highlight.compose)

    // Gravatar
    implementation(libs.gravatar)
    implementation(libs.gravatar.ui)

    // AvatarKt
    implementation(libs.avatar.kt)
}