plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.wordmemory"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.wordmemory"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2024.02.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.android.material:material:1.11.0")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    ksp("androidx.room:room-compiler:2.6.0")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // ML Kit (Bundled version)
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation("com.google.mlkit:vision-common:16.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}

configurations.all {
    resolutionStrategy {
        force("com.google.mlkit:text-recognition:16.0.0")
        force("com.google.mlkit:vision-common:16.0.0")
        dependencySubstitution {
            substitute(module("com.google.android.gms:play-services-mlkit-text-recognition"))
                .using(module("com.google.mlkit:text-recognition:16.0.0"))
        }
    }
}
