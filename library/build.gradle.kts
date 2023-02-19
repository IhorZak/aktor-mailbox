import org.jetbrains.dokka.gradle.DokkaTask
import ua.pp.ihorzak.aktormailbox.buildsrc.Library
import ua.pp.ihorzak.aktormailbox.buildsrc.Plugin
import ua.pp.ihorzak.aktormailbox.buildsrc.Version
import java.util.*

plugins {
    `java-library`
    kotlin("jvm")
    `maven-publish`
    signing
}

apply(plugin = Plugin.DOKKA)

project.base.archivesName.set("aktor-mailbox")
group = "ua.pp.ihorzak"
version = Version.PROJECT

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

kotlin {
    explicitApi()
}

java {
    withSourcesJar()
}

dependencies {
    implementation(Library.KOTLIN_STDLIB)
    implementation(Library.KOTLINX_COROUTINES_CORE)

    testImplementation(Library.KOTLIN_TEST)
    testImplementation(Library.KOTLINX_COROUTINES_TEST)
    testImplementation(Library.MOCKITO_KOTLIN)
}

val dokkaJavadoc by tasks.getting(DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaJavadoc)
    archiveClassifier.set("javadoc")
    from(dokkaJavadoc.outputDirectory)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<DokkaTask>().configureEach {
    moduleName.set("aktor-mailbox")
    moduleVersion.set(Version.PROJECT)
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "ua.pp.ihorzak"
            artifactId = "aktor-mailbox"
            version = Version.PROJECT

            from(components["java"])

            artifact(javadocJar)

            pom {
                name.set("aktor-mailbox")
                description.set("aktor-mailbox is a Kotlin language library that enables the creation of actors with input message preprocessing capabilities")
                url.set("https://github.com/IhorZak/aktor-mailbox")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        name.set("Ihor Zakhozhyi")
                        email.set("ihorzak@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/IhorZak/aktor-mailbox.git")
                    developerConnection.set("scm:git:ssh://github.com/IhorZak/aktor-mailbox.git")
                    url.set("https://github.com/IhorZak/aktor-mailbox/tree/main")
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        localProperties.getProperty("signingKeyId"),
        localProperties.getProperty("signingKey"),
        localProperties.getProperty("signingPassword")
    )
    sign(publishing.publications["release"])
}