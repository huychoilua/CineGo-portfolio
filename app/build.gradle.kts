plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.cinego"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.cinego"
        minSdk = 26
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)


    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // 3. Guava cho Android
    implementation("com.google.guava:guava:31.1-android")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")

    // Thư viện phát YouTube mượt nhất hiện nay
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    // Thư viện tải ảnh Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation(platform("com.google.firebase:firebase-bom:34.12.0"))

    implementation("com.google.firebase:firebase-analytics")

    // 1. Glide: Load ảnh (poster, avatar) cực kỳ mượt và ổn định.
    // Hỗ trợ sẵn việc làm mờ ảnh (Blur) nếu cần.
    implementation("com.github.bumptech.glide:glide:5.0.7")

    // 2. Lottie: Làm hiệu ứng animation (ví dụ: màn hình splash, loading, tick xanh thành công)
    implementation("com.airbnb.android:lottie:6.7.1")

    // 3. ZXing: Thư viện chuẩn của Google để tạo mã QR cho vé xem phim
    implementation("com.google.zxing:core:3.5.4")

    // 4. Gson: Chuyển đổi dữ liệu JSON (Rất cần thiết khi chúng ta làm mock data hoặc gọi API sau này)
    implementation("com.google.code.gson:gson:2.13.2")
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)


    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}