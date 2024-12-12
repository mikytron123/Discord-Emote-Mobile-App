import java.util.Properties
import java.io.FileInputStream
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.compose")
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
    compileSdk = 35

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
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
    kotlinOptions {
        jvmTarget = "19"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
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
    val ktor_version = "3.0.2"

    val compose_ui_version = "1.7.6"

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-android:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-jackson:$ktor_version")

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui:$compose_ui_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_ui_version")
    implementation("androidx.compose.material:material-icons-extended:$compose_ui_version")
    implementation("androidx.compose.material:material:1.7.6")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("com.github.skydoves:landscapist-glide:2.4.4")

    implementation("com.google.dagger:hilt-android:2.52")
    //kapt("com.google.dagger:hilt-android-compiler:2.44.2."
    kapt("com.google.dagger:hilt-compiler:2.52")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-tooling:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_ui_version")

//    implementation("com.github.penfeizhou.android.animation:apng:2.25.0"
//    implementation("com.github.penfeizhou.android.animation:glide-plugin:2.25.0"
    implementation("com.linecorp:apng:1.12.0")
    implementation("com.github.skydoves:landscapist-coil:2.4.4")
    implementation("me.tatarka.android:apngrs-coil:0.4")
    implementation("me.tatarka.android:apngrs:0.4")
}