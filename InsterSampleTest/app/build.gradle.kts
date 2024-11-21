plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.instar"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.instar"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.material:material:1.7.0")
    // RecyclerView (AndroidX)
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    // Facebook 지원
    implementation("com.facebook.android:facebook-android-sdk:11.2.0")
    // 파이어베이스 로그인 지원
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation("com.google.firebase:firebase-core:21.1.1")
    // 파이어베이스 파일 스토리지
    implementation("com.google.firebase:firebase-storage:20.0.0")
    // 파이어베이스 파이어스토어 데이터베이스
    implementation("com.google.firebase:firebase-firestore:24.0.0")
    // 이미지 로더 라이브러리
    implementation("com.github.bumptech.glide:glide:4.12.0")
    // 구글 로그인 지원
    implementation("com.google.android.gms:play-services-auth:20.0.0")
    // 트위터 라이브러리
    implementation("com.twitter.sdk.android:twitter:3.3.0")
    // 푸시 알람 라이브러리
    implementation("com.google.firebase:firebase-messaging:23.0.0")
    // Okhttp 라이브러리
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    // Gson 라이브러리
    implementation("com.google.code.gson:gson:2.8.8")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.glide)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.ui.graphics.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}