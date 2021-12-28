buildscript {
    repositories {
        maven { url = java.net.URI("https://jitpack.io") }
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Version.BUILD_GRADLE}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.KOTLIN}")
    }
}

allprojects {
    repositories {
        maven { url = java.net.URI("https://jitpack.io") }
        google()
        mavenCentral()
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}