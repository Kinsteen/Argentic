plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.protobuf")
}

android {
    namespace = "fr.kinsteen.argentic"
    compileSdk = 34

    defaultConfig {
        applicationId = "fr.kinsteen.argentic"
        minSdk = 31
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        configurations.all {
            resolutionStrategy {
                force("androidx.emoji2:emoji2-views-helper:1.3.0")
                force("androidx.emoji2:emoji2:1.3.0")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = true
            isDebuggable = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2024.09.03"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.exifinterface:exifinterface:1.3.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.03"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    val lifecycle_version = "2.8.6"
    val camerax_version = "1.3.4"
    val accompanist_version = "0.32.0"
    val koin_version = "3.4.3"
    val koin_ksp_version = "1.2.2"

    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("com.google.protobuf:protobuf-java:3.22.3")
    implementation("androidx.datastore:datastore:1.1.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("androidx.navigation:navigation-compose:2.8.2")

    ///// LIFECYCLE /////
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")

    //// CAMERA STUFF ////
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")
    implementation("androidx.camera:camera-extensions:$camerax_version")

    //// ACCOMPANIST ////
    implementation("com.google.accompanist:accompanist-permissions:$accompanist_version")

    ////DEPENDENCY INJECTION////
    ////KOIN BASE////
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-android:$koin_version")
    implementation("io.insert-koin:koin-androidx-compose:3.4.6")

    ////KOIN KSP////
    implementation("io.insert-koin:koin-annotations:$koin_ksp_version")
}


protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.19.3"
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}