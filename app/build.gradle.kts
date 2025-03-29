plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.jsb.arhomerenovat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jsb.arhomerenovat"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        mlModelBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "com/google/flatbuffers/**"
            excludes += "google/flatbuffers/**"  // Add this
            excludes += "META-INF/versions/9/module-info.class"  // Add this
        }
    }
    configurations.all {
        resolutionStrategy {
            force ("com.google.flatbuffers:flatbuffers-java:25.2.10")
            // Also force remove any other versions
            eachDependency { -> // Cannot infer a type for this parameter. Please specify it explicitly.
                if (this.requested.group == "com.google.flatbuffers") {
                    this.useVersion ("25.2.10") // Unexpected tokens (use ';' to separate expressions on the same line)
                }
            }
        }
    }
}

dependencies {

//     implementation(libs.androidx.core.ktx)
    // Correct syntax for excluding support-compat
    implementation("androidx.core:core-ktx:1.13.0")
    {
        exclude(group = "com.android.support", module = "support-compat")
    }

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.tensorflow.lite.metadata) {
        exclude (group = "com.google.flatbuffers")
        exclude (module = "flatbuffers-java")
    }
//    implementation(libs.sceneform.ux)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)



    implementation ("com.google.android.gms:play-services-location:21.3.0")


    //    Dagger Hilt
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Material3
    implementation(libs.material3)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material3.material3)

    // Room
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")

    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:$room_version")

    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:$room_version")

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:$room_version")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")


    implementation ("com.google.ar:core:1.47.0") {
        exclude (group = "com.google.flatbuffers")
        exclude (module = "flatbuffers-java")
    }
    // SceneView for AR
    implementation ("io.github.sceneview:arsceneview:0.10.0") {
        exclude (group = "com.google.flatbuffers")
        exclude (module = "flatbuffers-java")
    }
//    implementation ("io.github.sceneview:arsceneview:2.2.1") // Use the latest version

    // TensorFlow Lite dependencies
    // TensorFlow Lite with explicit FlatBuffers version
    implementation ("org.tensorflow:tensorflow-lite-gpu-api:2.14.0") {
        exclude (group = "com.google.flatbuffers")
        exclude (module = "flatbuffers-java")
    }
    implementation ("org.tensorflow:tensorflow-lite:2.14.0") {
        exclude (group = "com.google.flatbuffers")
        exclude (module = "flatbuffers-java")
    }
    implementation ("org.tensorflow:tensorflow-lite-gpu:2.14.0") {
        exclude (group = "com.google.flatbuffers")
        exclude (module = "flatbuffers-java")
    }

    // Manually add the latest FlatBuffers version (compatible with TF Lite)
//    implementation ("com.google.flatbuffers:flatbuffers-java:25.2.10")
    implementation ("org.tensorflow:tensorflow-lite-support:0.4.4"){
        exclude (group = "com.google.flatbuffers")
        exclude (module = "flatbuffers-java")
    }

    // CameraX dependencies
    implementation ("androidx.camera:camera-core:1.1.0")  // Core CameraX
    implementation ("androidx.camera:camera-camera2:1.1.0") // Camera2 implementation
    implementation ("androidx.camera:camera-lifecycle:1.1.0") // Lifecycle support for CameraX
    implementation ("androidx.camera:camera-view:1.4.1") // Optional, for CameraX view


    // Obj - a simple Wavefront OBJ file loader
    // https://github.com/javagl/Obj
    implementation ("de.javagl:obj:0.4.0")

    implementation ("com.google.android.material:material:1.1.0")

    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
}