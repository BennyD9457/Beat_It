plugins {
    kotlin("jvm") version "2.0.21" // Match the classpath version
    application
}

repositories {
    mavenCentral() // Standard Maven repository
    maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap") // Ktor repository
}


dependencies {
    implementation(platform("io.ktor:ktor-bom:2.3.4")) // Use the BOM to ensure consistent versions
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-auth") // OAuth is part of this
    implementation("io.ktor:ktor-client-core") // Core HTTP client
    implementation("io.ktor:ktor-client-cio") // CIO HTTP client engine
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    testImplementation("io.ktor:ktor-server-tests")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.0")
}



kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Use Amazon Corretto 17
    }
}

application {
    mainClass.set("com.example.project.ApplicationKt")
}
