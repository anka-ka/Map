
import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "ru.netology.map"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.netology.map"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"



        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val properties = Properties()
        val file = rootProject.file("maps.properties")
        if (file.exists()) {
            file.inputStream().use { inputStream ->
                properties.load(inputStream)
            }
        }
        manifestPlaceholders["mapsApiKey"] = properties.getProperty("MAPS_API_KEY", "")
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

        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {
    implementation (libs.maps.mobile.v480lite)
    implementation (libs.maps.mobile.v480full)

    implementation (libs.gson)

    implementation (libs.androidx.swiperefreshlayout)

    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx.v253)
    implementation(libs.androidx.navigation.fragment.ktx.v260)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.swiperefreshlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}