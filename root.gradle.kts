plugins {
    kotlin("jvm") version "1.6.0" apply false
    id("cc.polyfrost.multi-version.root")
}

// normal versions will be "1.x.x"
// betas will be "1.x.x+beta-y" / "1.x.x+branch_beta-y"
// rcs will be 1.x.x+rc-y
// extra branches will be 1.x.x+branch-y
version = "1.8.5"

preprocess {
    "1.12.2-forge"(11202, "srg") {
        "1.8.9-forge"(10809, "srg", file("versions/1.12.2-1.8.9.txt"))
    }
}
