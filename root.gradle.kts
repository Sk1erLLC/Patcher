import gg.essential.gradle.util.versionFromBuildIdAndBranch

plugins {
    kotlin("jvm") version "1.8.22" apply false
    id("gg.essential.multi-version.root")
}

// normal versions will be "1.x.x"
// betas will be "1.x.x+beta-y" / "1.x.x+branch_beta-y"
// rcs will be 1.x.x+rc-y
// extra branches will be 1.x.x+branch-y
version = "1.8.9"

preprocess {
    "1.12.2"(11202, "srg") {
        "1.8.9"(10809, "srg", file("versions/1.12.2-1.8.9.txt"))
    }
}
