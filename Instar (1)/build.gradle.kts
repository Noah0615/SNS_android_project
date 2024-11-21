import org.jetbrains.kotlin.gradle.internal.kapt.incremental.UnknownSnapshot

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:3.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
        classpath("com.google.gms:google-services:4.3.13")

    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
