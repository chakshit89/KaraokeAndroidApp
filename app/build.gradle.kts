plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
//    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.karoke.myoyasing"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.karoke.myoyasing"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}
ksp {
    arg(
        "room.schemaLocation",
        "$projectDir/schemas"
    )
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.animation.core)
    implementation("be.tarsos.dsp:core:2.5")

        implementation ("nl.dionsegijn:konfetti-compose:2.2.0")

// JSON serialization for user_melody.json
    implementation("com.google.code.gson:gson:2.11.0")
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.animation.core)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.compose.foundation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Data Store Dependencies
    implementation(libs.data.store)

    // View Model Dependencies
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Navigation Compose Dependencies
    implementation(libs.navigation.compose)

    // Coroutines Dependencies
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Retrofit Dependencies
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.exoplayer)
    implementation(libs.exoplayer.ui)
    implementation(libs.exoplayer.ui.compose)
    implementation(libs.exoplayer.hls)
    implementation("com.google.android.gms:play-services-cast-framework:21.4.0")
    // Room Dependencies
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation("androidx.media3:media3-exoplayer:1.11.1")
    implementation("com.github.kaushik-naik:TAndroidLame:a485052f28")
    implementation("androidx.media3:media3-common:1.11.1")
    implementation("androidx.media3:media3-datasource:1.11.1")
    // Koin Dependencies
    implementation("com.hbb20:ccp:2.7.3")
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.navigation)
    implementation(libs.koin.compose)

    //Audio background voice remover
    implementation("io.github.kaleyravideo:android-deepfilternet:0.0.8")
    implementation("androidx.room:room-ktx:2.6.1")
    // Coil Dependencies
    implementation("androidx.browser:browser:1.7.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation(libs.coil.compose)
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
}
