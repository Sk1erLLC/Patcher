import gg.essential.gradle.util.versionFromBuildIdAndBranch

plugins {
    kotlin("jvm") version "1.6.0" apply false
    id("gg.essential.multi-version.root")
}

version = versionFromBuildIdAndBranch()

preprocess {
    "1.12.2"(11202, "srg") {
        "1.8.9"(10809, "srg", file("versions/1.12.2-1.8.9.txt"))
    }
}
