package ua.pp.ihorzak.aktormailbox.buildsrc

object Classpath {
    const val VERSIONS = "com.github.ben-manes:gradle-versions-plugin:${Version.VERSIONS}"

    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.KOTLIN}"

    const val DOKKA = "org.jetbrains.dokka:dokka-gradle-plugin:${Version.DOKKA}"

    const val PUBLISH = "io.github.gradle-nexus:publish-plugin:${Version.NEXUS_PUBLISH}"
}