import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

apply(plugin = "com.huawei.agconnect")

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.anankastudio.videocollector"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.anankastudio.videocollector"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String?
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://api.pexels.com/v1/\"")
            buildConfigField("String", "BASE_URL_VIDEO", "\"https://api.pexels.com/videos/\"")
            buildConfigField("String", "API_KEY", "\"CTkdChy7YYvi0TVFGn0R6gKoWAYEs5ncQRupkwgQ9mnr3s1agdwlYSYN\"")
        }
        debug {
            buildConfigField("String", "BASE_URL", "\"https://api.pexels.com/v1/\"")
            buildConfigField("String", "BASE_URL_VIDEO", "\"https://api.pexels.com/videos/\"")
            buildConfigField("String", "API_KEY", "\"CTkdChy7YYvi0TVFGn0R6gKoWAYEs5ncQRupkwgQ9mnr3s1agdwlYSYN\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.smoothbottombar)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.swiperefreshlayout)
    implementation(libs.glide)
    implementation(libs.shimmer)
    implementation(libs.flexbox)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.ui)
    implementation(libs.media3.common)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.hilt.android)
    implementation(libs.huawei.agconnect.core)
    implementation(libs.huawei.ads.prime)
    kapt(libs.hilt.compiler)
    ksp(libs.room.compiler)
    annotationProcessor(libs.glide.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}