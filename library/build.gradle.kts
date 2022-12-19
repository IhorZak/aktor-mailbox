import ua.pp.ihorzak.aktormailbox.buildsrc.Library
import ua.pp.ihorzak.aktormailbox.buildsrc.Version

plugins {
    `java-library`
    kotlin("jvm")
}

group = "ua.pp.ihorzak"
version = Version.PROJECT

kotlin {
    explicitApi()
}

dependencies {
    implementation(Library.KOTLIN_STDLIB)
    implementation(Library.KOTLINX_COROUTINES_CORE)

    testImplementation(Library.KOTLIN_TEST)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}