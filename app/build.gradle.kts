plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
    id("kotlin-parcelize")
}

android {
    namespace = "com.bizzagi.daytrip"
    compileSdk = 35

    packaging {
        resources {
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.addAll(listOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.txt",
                "META-INF/DEPENDENCIES"
            ))
        }
    }

    defaultConfig {
        applicationId = "com.bizzagi.daytrip"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildTypes {
            debug {
                buildConfigField("String", "BASE_URL", "\"https://c242-dt01-api-service-304525226938.asia-southeast2.run.app/v1/\"")
            }
            release {
                buildConfigField("String", "BASE_URL", "\"https://c242-dt01-api-service-304525226938.asia-southeast2.run.app/v1/\"")
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)
    implementation(libs.junit.junit)
    implementation(libs.junit.jupiter)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.androidx.viewpager2)
    implementation (libs.threetenabp)

    //places detail api
    implementation (libs.places)
    implementation (libs.places.ktx)
    implementation (libs.play.services.maps)
    implementation (libs.play.services.location)
    implementation (libs.google.maps.services)
    implementation (libs.android.maps.utils)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit2.converter.gson)
    implementation (libs.java.jwt)
    implementation (libs.glide)

    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.mockitoKotlin)
    testImplementation(libs.mockitoInline)
    testImplementation(libs.junit.junit)
    testImplementation(libs.truth)

    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.junitJupiterEngine)
    testRuntimeOnly(libs.junitJupiterEngine)

    testImplementation(libs.mockk)

    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)
}