pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("jvm") version "2.0.21" // Match the version on the classpath
    }
}

// Set the root project name
rootProject.name = "Backend_Beat_It"

// Include the server module
include(":server")
