// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("androidx.navigation.safeargs.kotlin") version "2.9.8" apply false
    id("org.jetbrains.kotlin.android") version "2.4.0" apply false
}