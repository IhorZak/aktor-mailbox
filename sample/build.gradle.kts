import ua.pp.ihorzak.aktormailbox.buildsrc.Library
import ua.pp.ihorzak.aktormailbox.buildsrc.Version

plugins {
    id("java")
    kotlin("jvm")
}

group = "ua.pp.ihorzak.aktor-mailbox"
version = Version.PROJECT

dependencies {
    implementation(project(":library"))

    implementation(Library.KOTLIN_STDLIB)
    implementation(Library.KOTLINX_COROUTINES_CORE)
}