pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.polyfrost.cc/releases")
    }
    plugins {
        val egtVersion = "0.1.22"
        id("cc.polyfrost.multi-version.root") version egtVersion
    }
}

rootProject.buildFileName = "root.gradle.kts"

listOf(
    "1.8.9-forge",
    "1.12.2-forge"
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }

}
