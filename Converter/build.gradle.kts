plugins {
    id("com.android.application") version("8.8.0")
    id("org.jetbrains.kotlin.android") version("2.1.0")
    id("org.jetbrains.kotlin.plugin.compose") version("2.1.0")
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.material3:material3:1.3.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.8")
}

android {
    namespace = "vn.ac.vju.mad.converter"
    compileSdk = 35
    defaultConfig {
        minSdk = 21
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
