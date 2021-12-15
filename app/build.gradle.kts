plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
    id("kotlin-android")
}

android {
    compileSdk = Config.COMPILE_SDK
    buildToolsVersion = Config.BUILD_TOOLS_VERSION
    defaultConfig {
        applicationId = Config.APPLICATION_ID
        minSdk = Config.MIN_SDK
        targetSdk = Config.TARGET_SDK
        versionCode = Config.VERSION_CODE
        versionName = Config.VERSION_NAME
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles (
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "../proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = Config.JVM_TARGET
    }

    viewBinding {
        android.buildFeatures.viewBinding = true
    }
}

dependencies {
    //MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    //UI
    implementation("com.google.android.material:material:${Version.MATERIAL}")
    implementation("androidx.constraintlayout:constraintlayout:${Version.CONSTRAIN}")
    implementation("androidx.appcompat:appcompat:${Version.APPCOMPAT}")
    implementation("androidx.recyclerview:recyclerview:${Version.RECYCLERVIEW}")

    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Version.KOTLIN}")
    implementation("androidx.core:core-ktx:${Version.KOTLIN_CORE}")

    //Room
    implementation("androidx.room:room-ktx:${Version.ROOM}")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    /*todo определиться со стеком
    //Moxy - Version.MOXY: 2.1.2
    implementation("com.github.moxy-community:moxy:${Version.MOXY}")
    implementation("com.github.moxy-community:moxy-ktx:${Version.MOXY}")
    implementation("com.github.moxy-community:moxy-androidx:${Version.MOXY}")
    kapt("com.github.moxy-community:moxy-compiler:${Version.MOXY}")

    //Cicerone - Version.CICERONE: 7.1
    implementation("com.github.terrakok:cicerone:${Version.CICERONE}")

    //RxJava - Version.RX_JAVA: 3.0.0
    implementation("io.reactivex.rxjava3:rxandroid:${Version.RX_JAVA}")
    implementation("io.reactivex.rxjava3:rxjava:${Version.RX_JAVA}")
    implementation("io.reactivex.rxjava3:rxkotlin:${Version.RX_JAVA}")

    //Coroutines - Version.COROUTINES: 1.5.1
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.COROUTINES}")

    //Koin - Version.KOIN :3.1.2
    //Основная библиотека
    implementation("io.insert-koin:koin-core:${Version.KOIN}")
    //Koin для поддержки Android (Scope,ViewModel ...)
    implementation("io.insert-koin:koin-android:${Version.KOIN}")
    //Для совместимости с Java
    implementation("io.insert-koin:koin-android-compat:${Version.KOIN}")

    //Dagger 2 - Version.DAGGER: 2.37, Version.KAPT_DAGGER: 2.35.1
    implementation("com.google.dagger:dagger:${Version.DAGGER}")
    implementation("com.google.dagger:dagger-android:${Version.DAGGER}")
    implementation("com.google.dagger:dagger-android-support:${Version.DAGGER}")
    kapt("com.google.dagger:dagger-compiler:${Version.KAPT_DAGGER}")
    kapt("com.google.dagger:dagger-android-processor:${Version.KAPT_DAGGER}")*/
}