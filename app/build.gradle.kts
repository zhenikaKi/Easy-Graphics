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

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf("room.schemaLocation" to "$projectDir/schemas")
                )
            }
        }
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
    implementation("com.pes.materialcolorpicker:library:1.2.5")
    //MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:${Version.CHART_LIBRARY}")
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
    implementation("androidx.room:room-runtime:${Version.ROOM}")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    kapt("androidx.room:room-compiler:${Version.ROOM}")

    //Cicerone
    implementation("com.github.terrakok:cicerone:${Version.CICERONE}")

    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.COROUTINES}")

    //Koin
    //Основная библиотека
    implementation("io.insert-koin:koin-core:${Version.KOIN}")
    //Koin для поддержки Android (Scope,ViewModel ...)
    implementation("io.insert-koin:koin-android:${Version.KOIN}")
    //Для совместимости с Java
    implementation("io.insert-koin:koin-android-compat:${Version.KOIN}")
}