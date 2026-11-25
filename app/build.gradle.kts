import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// O plugin do Google Services exige o arquivo app/google-services.json (que NÃO vai para o GitHub).
// Se o arquivo não existir, o build continua (útil para testar a compilação), mas o Firebase
// não vai funcionar ao rodar o app. Veja a seção "Como configurar o Firebase" no README.
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
} else {
    logger.warn("AVISO: app/google-services.json não encontrado. O app compila, mas o Firebase não vai funcionar.")
}

// Chaves do Cloudinary lidas do local.properties (fora do Git). Nunca escreva chaves reais no código.
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
fun localProp(nome: String): String = localProps.getProperty(nome, "")

android {
    namespace = "com.techinova.kanban"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.techinova.kanban"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"${localProp("cloudinary.cloud_name")}\"")
        buildConfigField("String", "CLOUDINARY_UPLOAD_PRESET", "\"${localProp("cloudinary.upload_preset")}\"")
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)

    implementation(libs.cloudinary.android)
}
