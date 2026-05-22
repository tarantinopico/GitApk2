plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
}

android {
  namespace = "com.fluidgit.app"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.fluidgit.app"
    minSdk = 28
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.ui.text.google.fonts)
  implementation(libs.androidx.core.ktx)
  implementation("androidx.core:core-splashscreen:1.0.1")
  implementation("androidx.biometric:biometric:1.2.0-alpha05")
  implementation("androidx.compose.material:material-icons-extended:1.6.8")
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
  
  // Room
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  "ksp"(libs.androidx.room.compiler)
  
  // Coroutines
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)

  // DataStore & Security
  implementation(libs.androidx.datastore.preferences)
  implementation("androidx.security:security-crypto:1.1.0-alpha06")

  // Material 3
  implementation(libs.androidx.compose.material3)

  // Moshi
  implementation(libs.moshi.kotlin)

  // Networking
  implementation(libs.retrofit)
  implementation(libs.converter.moshi)

  // Image loading
  implementation(libs.coil.compose)

  // Web Browser
  implementation("androidx.browser:browser:1.8.0")

  // Hilt
  implementation(libs.hilt.android)
  "ksp"(libs.hilt.compiler)

  // JGit
  implementation(libs.jgit)
  implementation(libs.jgit.ssh.jsch)

  // Glance
  implementation("androidx.glance:glance-appwidget:1.1.0")
  implementation("androidx.glance:glance-material3:1.1.0")

  // Bouncy Castle (for GPG)
  implementation("org.bouncycastle:bcpg-jdk18on:1.78.1")
  implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")

  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
}
