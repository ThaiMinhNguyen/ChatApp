import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "1.8.21"
}

android {
    namespace = "com.example.chatapp"
    compileSdk = 35

    signingConfigs{
        create("release"){
            storeFile = file("D:\\WorkSpace2\\Android\\keys\\app-release.jks")
            storePassword = "22012003"
            keyAlias = "key0"
            keyPassword = "22012003"
        }
    }

    buildFeatures{
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.chatapp"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val properties = Properties()
    val file = File(rootDir, "local.properties")
    if (file.exists() && file.isFile) {
        file.inputStream().use {
            properties.load(it)
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", properties.getProperty("SUPABASE_PUBLISHABLE_KEY"))
            buildConfigField("String", "SUPABASE_URL", properties.getProperty("SUPABASE_URL"))
            signingConfig = signingConfigs.getByName("debug")

        }
        debug {
            buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", properties.getProperty("SUPABASE_PUBLISHABLE_KEY"))
            buildConfigField("String", "SUPABASE_URL", properties.getProperty("SUPABASE_URL"))
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
    implementation(libs.androidx.navigation.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //Hilt Dagger dependencies
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    //Retrofit dependencies
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //Glide dependencies
    implementation(libs.glide)

    //Firebase dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)

    //Splash screen
    implementation(libs.androidx.core.splashscreen)

    //Circle Image View
    implementation(libs.circleimageview)

    implementation(libs.imagepicker)

    // Room (local database)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(platform("io.github.jan-tennert.supabase:bom:3.2.2"))
    implementation("io.github.jan-tennert.supabase:storage-kt")
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")
    implementation(libs.ktor.client.android)
}