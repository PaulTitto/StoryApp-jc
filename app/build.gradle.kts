plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.mosalab.submissionpaai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mosalab.submissionpaai"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        compose = true
    }

}

dependencies {
    implementation("org.slf4j:slf4j-nop:2.0.7")
    // Paging dependencies
    implementation("androidx.paging:paging-runtime-ktx:3.1.1")
    testImplementation("androidx.paging:paging-common-ktx:3.1.1")

// MockK for mocking
    testImplementation("io.mockk:mockk:1.12.5")

// JUnit for unit testing
    testImplementation("junit:junit:4.13.2")

// Coroutines testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

// Arch Core testing
    testImplementation("androidx.arch.core:core-testing:2.1.0")

    testImplementation("androidx.paging:paging-common-ktx:3.2.0")
    testImplementation("org.slf4j:slf4j-simple:1.7.36")
    testImplementation("androidx.paging:paging-testing:3.1.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.paging:paging-common:3.1.1")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("androidx.test:core:1.5.0")

    testImplementation("androidx.paging:paging-testing:3.1.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:core:1.5.0")
    testImplementation("org.robolectric:robolectric:4.10.3")

    testImplementation("androidx.paging:paging-testing:3.1.1")
    testImplementation("app.cash.turbine:turbine:0.12.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("app.cash.turbine:turbine:0.12.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.7.20")
    testImplementation("androidx.paging:paging-testing:3.1.1")

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation("com.google.maps.android:maps-compose:6.2.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")

//    Maps
    implementation("com.google.maps.android:maps-compose:2.13.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation("androidx.compose:compose-bom:2024.01.00")

    implementation("androidx.compose.material:material-icons-extended")

    implementation(libs.androidx.datastore.preferences)

    implementation("androidx.paging:paging-runtime:3.2.1")
    implementation("androidx.paging:paging-compose:3.2.1")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.animation.core.lint)
    implementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}