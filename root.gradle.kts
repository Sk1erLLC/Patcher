plugins {
    kotlin("jvm") version "1.6.0" apply false
    id("com.replaymod.preprocess") version "0ab22d2"
    id("dev.architectury.loom") version "0.10.0.9999" apply false
}

configurations.register("compileClasspath")

preprocess {
    "1.12.2"(11202, "srg") {
        "1.8.9"(10809, "srg", file("versions/1.12.2-1.8.9.txt"))
    }
}
