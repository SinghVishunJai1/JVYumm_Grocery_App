plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Serialization plugin
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"

    id("com.google.gms.google-services")
}

android {
    namespace = "com.appdev.flash"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.appdev.flash"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Using ViewModel for MVVM Architecture
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.9.2")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Scalar converter to convert JSON to Kotlin String
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    // Retrofit Kotlin Serialization Converter
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // Coil for image loading (AsyncComposable function)
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Preferences DataStore (for small data storage)
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // Firebase BOM for version management
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
}
