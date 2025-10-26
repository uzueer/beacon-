plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    buildFeatures {
        viewBinding = true
    }
    namespace = "com.example.meshsosapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.meshsosapp"
        minSdk = 24
        targetSdk = 36
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    // Core Android libraries
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")


    //this is for the locatoin
    implementation("com.google.android.gms:play-services-location:21.2.0")


    ////finally for the nordic library the fucking bluetooth mesh thing
    implementation("no.nordicsemi.android:mesh:3.3.7")
    implementation("no.nordicsemi.android:ble:2.6.0")
    implementation("com.google.android.gms:play-services-nearby:18.3.0")
    ///this the google json library for converting the chatmessage object to simple format like jason before sending over bluetooth
    implementation("com.google.code.gson:gson:2.10.1")

// RecyclerView for chat messages
    implementation("androidx.recyclerview:recyclerview:1.3.2")

// Testing libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}