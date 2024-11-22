// Top-level build file where you can add configuration options common to all sub-projects/modules.
    buildscript {

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()



    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
        classpath ("com.android.tools.build:gradle:8.1.0")

    }

}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}