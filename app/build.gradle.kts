import java.util.Properties
import java.io.FileInputStream



plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(FileInputStream(file))
    }
}
val naverClientId = localProperties.getProperty("NAVER_CLIENT_ID")
    ?: throw GradleException("NAVER_CLIENT_ID not found in local.properties")
val naverClientSecret = localProperties.getProperty("NAVER_CLIENT_SECRET")
    ?: throw GradleException("NAVER_CLIENT_SECRET not found in local.properties")
val kakaoKey = localProperties.getProperty("KAKAO_NATIVE_KEY")
    ?: throw GradleException("KAKAO_NATIVE_KEY not found in local.properties")



//        val naverClientId = project.findProperty("NAVER_CLIENT_ID") as String
//        val naverClientSecret = project.findProperty("NAVER_CLIENT_SECRET") as String
//val naverClientId = project.findProperty("NAVER_CLIENT_ID") as? String
//    ?: throw GradleException("NAVER_CLIENT_ID is missing in local.properties")
//
//val naverClientSecret = project.findProperty("NAVER_CLIENT_SECRET") as? String
//    ?: throw GradleException("NAVER_CLIENT_SECRET is missing in local.properties")

android {
    namespace = "com.blessing.channel"
    compileSdk = 35
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    defaultConfig {
        applicationId = "com.blessing.channel"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "NAVER_CLIENT_ID", "\"$naverClientId\"")
        buildConfigField("String", "NAVER_CLIENT_SECRET", "\"$naverClientSecret\"")
        buildConfigField("String", "KAKAO_NATIVE_KEY", "\"$kakaoKey\"")
        manifestPlaceholders["kakao_scheme"] = "kakao$kakaoKey"
        manifestPlaceholders["kakao_native_key"] = kakaoKey



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
        buildConfig = true // ✅ 이 줄을 반드시 추가해야 함
    }
}

dependencies {

    implementation(libs.androidx.material)
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.kakao.sdk:v2-user:2.18.0")
    implementation("com.navercorp.nid:oauth:5.9.0")
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.legacy:legacy-support-core-utils:1.0.0")
    implementation("androidx.browser:browser:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.core:core-ktx:1.3.0")
    implementation("androidx.fragment:fragment-ktx:1.3.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.2.1")
    implementation("com.airbnb.android:lottie:3.1.0")
    implementation("com.navercorp.nid:oauth:5.9.0") // ✅ 네이버 OAuth


    // 최신 버전 확인 필요



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
}
