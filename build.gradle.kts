import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import ua.pp.ihorzak.aktormailbox.buildsrc.Plugin

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        classpath(ua.pp.ihorzak.aktormailbox.buildsrc.Classpath.VERSIONS)
        classpath(ua.pp.ihorzak.aktormailbox.buildsrc.Classpath.KOTLIN)
        classpath(ua.pp.ihorzak.aktormailbox.buildsrc.Classpath.DOKKA)
    }
}

allprojects {
    apply(plugin = Plugin.VERSIONS)

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlinx.coroutines.ObsoleteCoroutinesApi"
            )
        }
    }

    tasks.withType<DependencyUpdatesTask> {
        outputFormatter = "json"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"

        checkForGradleUpdate = true
        gradleReleaseChannel = "current"
        revision = "release"

        fun isNonStableVersion(version: String): Boolean {
            val stableWordList = listOf("RELEASE", "FINAL", "GA")
            val unstableWordList = listOf("BETA", "RC", "-M")
            val upperCaseVersion = version.toUpperCase()
            val containsStableKeyword = stableWordList.any { upperCaseVersion.contains(it) }
            val containsUnstableKeyword = unstableWordList.any { upperCaseVersion.contains(it) }
            val versionRegex = "^[0-9,.v-]+(-r)?$".toRegex()
            val isStable = (containsStableKeyword && !containsUnstableKeyword) || versionRegex.matches(version)
            return isStable.not()
        }
        rejectVersionIf {
            isNonStableVersion(candidate.version)
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}