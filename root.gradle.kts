plugins {
    kotlin("jvm") version "1.5.0" apply false
    id("fabric-loom") version "0.4-SNAPSHOT" apply false
    id("com.replaymod.preprocess") version "f64d217"
}

// Loom tries to find the active mixin version by recursing up to the root project and checking each project's
// compileClasspath and build script classpath (in that order). Since we've loom in our root project's classpath,
// loom will only find it after checking the root project's compileClasspath (which doesn't exist by default).
configurations.register("compileClasspath")

preprocess {
    "1.8.9"(10809, "srg")
}
