import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
}

val props = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "local.properties")))
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

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                output.outputFileName = "EasyGraphics_v${Config.VERSION_NAME}.apk"
            }
    }

    signingConfigs {
        create("release") {
            storeFile = file(props.getProperty("sign.storeFile"))
            storePassword = props.getProperty("sign.storePassword")
            keyAlias = props.getProperty("sign.keyAlias")
            keyPassword = props.getProperty("sign.keyPassword")
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs["release"]
            isMinifyEnabled = false
            proguardFiles (
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "../proguard-rules.pro"
            )
        }

        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
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
    //implementation("io.github.ekiryushin:scrolltableview:${Version.SCROLL_TABLE_VIEW}")
    implementation("com.github.evrencoskun:TableView:${Version.TABLE_VIEW}")

    implementation("com.pes.materialcolorpicker:library:${Version.COLOR_PICKER}")
    //MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:${Version.CHART_LIBRARY}")
    //UI
    implementation("com.google.android.material:material:${Version.MATERIAL}")
    implementation("androidx.constraintlayout:constraintlayout:${Version.CONSTRAIN}")
    implementation("androidx.appcompat:appcompat:${Version.APPCOMPAT}")
    implementation("androidx.recyclerview:recyclerview:${Version.RECYCLERVIEW}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Version.LIFECYCLE}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Version.LIFECYCLE}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.LIFECYCLE}")
    implementation("androidx.lifecycle:lifecycle-extensions:${Version.LIFECYCLE_EXTENSION}")

    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Version.KOTLIN}")
    implementation("androidx.core:core-ktx:${Version.KOTLIN_CORE}")

    //Room
    implementation("androidx.room:room-ktx:${Version.ROOM}")
    implementation("androidx.room:room-runtime:${Version.ROOM}")
    kapt("androidx.room:room-compiler:${Version.ROOM}")
    //Gson
    implementation("com.google.code.gson:gson:${Version.GSON}")

    //Cicerone
    implementation("com.github.terrakok:cicerone:${Version.CICERONE}")

    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.COROUTINES}")

    //Koin
    //???????????????? ????????????????????
    implementation("io.insert-koin:koin-core:${Version.KOIN}")
    //Koin ?????? ?????????????????? Android (Scope,ViewModel ...)
    implementation("io.insert-koin:koin-android:${Version.KOIN}")
    //?????? ?????????????????????????? ?? Java
    implementation("io.insert-koin:koin-android-compat:${Version.KOIN}")
}