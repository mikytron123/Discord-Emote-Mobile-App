import java.util.Properties
import java.io.FileInputStream
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}
//var  kspropfile = file("../keystore.properties")
//var  ksprop = Properties()
//ksprop.load(FileInputStream(kspropfile));

var  authpropfile = file("../auth.properties")
var  authprop = Properties()
authprop.load(FileInputStream(authpropfile))

android {
//    signingConfigs {
//        release {
//
//            storePassword = ksprop["storePassword"]
//            keyAlias = ksprop["keyAlias"]
//            keyPassword = ksprop["keyPassword"]
//            storeFile = file(ksprop["storeFile"])
//        }
//    }
    namespace = "com.example.discordemotelist"
    compileSdk = 34

    defaultConfig {

        buildConfigField("String", "TOKEN", authprop["TOKEN"] as String)

        applicationId = "com.example.discordemotelist"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
kapt {
    correctErrorTypes = true
}

dependencies {
    val ktor_version = "2.3.3"

    val compose_ui_version = "1.5.0"

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-android:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-jackson:$ktor_version")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.ui:ui:$compose_ui_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_ui_version")
    implementation("androidx.compose.material:material:1.5.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.1")
    implementation("com.github.skydoves:landscapist-glide:2.1.8")

    implementation("com.google.dagger:hilt-android:2.47")
    //kapt("com.google.dagger:hilt-android-compiler:2.44.2."
    kapt("com.google.dagger:hilt-compiler:2.47")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-tooling:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_ui_version")

//    implementation("com.github.penfeizhou.android.animation:apng:2.25.0"
//    implementation("com.github.penfeizhou.android.animation:glide-plugin:2.25.0"
    implementation("com.linecorp:apng:1.11.0")
    implementation("com.github.skydoves:landscapist-coil:2.1.8")
    implementation("me.tatarka.android:apngrs-coil:0.1")
    implementation("me.tatarka.android:apngrs:0.1")
}