    plugins {
        id("com.android.application")
        id("org.jetbrains.kotlin.android")
    }

    android {
        namespace = "com.example.players"
        compileSdk = 34

        defaultConfig {
            applicationId = "com.example.players"
            minSdk = 21
            targetSdk = 34
            versionCode = 1
            versionName = "1.0"
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

        // ===== HABILITAR COMPOSE =====
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.1"
        }
        sourceSets {
            getByName("main") {
                assets {
                    srcDirs("src\\main\\assets", "src\\main\\assets\\mock_players.json")
                }
            }
        }
    }

    dependencies {

        // Android Base
        implementation("androidx.appcompat:appcompat:1.6.0")
        implementation("androidx.core:core-ktx:1.9.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")

        // ========== COMPOSE ==========
        implementation(platform("androidx.compose:compose-bom:2024.04.01"))

        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.ui:ui-graphics")   // <-- Color está acá
        implementation("androidx.compose.ui:ui-tooling-preview")
        implementation("androidx.compose.material3:material3")

        debugImplementation("androidx.compose.ui:ui-tooling")
        debugImplementation("androidx.compose.ui:ui-test-manifest")

        // Retrofit
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")

        // Gson
        implementation("com.google.code.gson:gson:2.10.1")

        // Test
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.3")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

        implementation("org.mindrot:jbcrypt:0.4")
        implementation("com.google.android.material:material:1.12.0")

        implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    }

