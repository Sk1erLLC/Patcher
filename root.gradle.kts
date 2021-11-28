plugins {
    kotlin("jvm") version "1.6.0" apply false
    id("com.replaymod.preprocess") version "7746c47"
}

configurations.register("compileClasspath")

preprocess {
    "1.12.2"(11202, "srg") {
        "1.8.9"(10809, "srg", file("versions/1.12.2-1.8.9.txt"))
    }
}
