import org.jetbrains.dokka.gradle.DokkaTask
import ua.pp.ihorzak.aktormailbox.buildsrc.Library
import ua.pp.ihorzak.aktormailbox.buildsrc.Plugin
import ua.pp.ihorzak.aktormailbox.buildsrc.Version

plugins {
    `java-library`
    kotlin("jvm")
}

apply(plugin = Plugin.DOKKA)

group = "ua.pp.ihorzak"
version = Version.PROJECT

kotlin {
    explicitApi()
}

dependencies {
    implementation(Library.KOTLIN_STDLIB)
    implementation(Library.KOTLINX_COROUTINES_CORE)

    testImplementation(Library.KOTLIN_TEST)
    testImplementation(Library.KOTLINX_COROUTINES_TEST)
    testImplementation(Library.MOCKITO_KOTLIN)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<DokkaTask>().configureEach {
    moduleName.set("aktor-mailbox")
    moduleVersion.set(Version.PROJECT)
}