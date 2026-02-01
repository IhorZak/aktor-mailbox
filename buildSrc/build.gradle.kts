plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(
        jdkVersion = JavaVersion.VERSION_11.majorVersion.toInt(),
    )
}