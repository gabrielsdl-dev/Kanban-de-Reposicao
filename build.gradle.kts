// Arquivo de build raiz: só declara os plugins. A configuração do app fica em app/build.gradle.kts.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.services) apply false
}
