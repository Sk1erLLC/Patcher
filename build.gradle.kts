import cc.polyfrost.gradle.util.noServerRunConfigs

plugins {
    kotlin("jvm")
    id("cc.polyfrost.multi-version")
    id("cc.polyfrost.defaults")
}

val modGroup: String by project
val modBaseName: String by project
group = modGroup
base.archivesName.set("$modBaseName-${platform.mcVersionStr}")

val accessTransformerName = "patcher1${platform.mcMinor}_at.cfg"

loom {
    noServerRunConfigs()
    mixin {
        defaultRefmapName.set("patcher.mixins.refmap.json")
    }
    if (project.platform.isForge) {
        forge {
            accessTransformer(rootProject.file("src/main/resources/$accessTransformerName"))
        }
    }
    launchConfigs {
        getByName("client") {
            property("patcher.debugBytecode", "true")
            property("mixin.debug.verbose", "true")
            property("mixin.debug.export", "true")
            property("mixin.dumpTargetOnFailure", "true")
            if (project.platform.isForge) {
                property("fml.coreMods.load", "club.sk1er.patcher.tweaker.PatcherTweaker")
                arg("--tweakClass", "cc.polyfrost.oneconfigwrapper.OneConfigWrapper")
                arg("--mixin", "patcher.mixins.json")
            }
        }
    }
}

sourceSets {
    val dummy by creating
    main {
        compileClasspath += dummy.output
    }
}


repositories {
    maven("https://repo.polyfrost.cc/releases")
}

val embed by configurations.creating
configurations.implementation.get().extendsFrom(embed)

dependencies {
    compileOnly("cc.polyfrost:oneconfig-$platform:0.1.0-alpha+")
    embed("cc.polyfrost:elementa-$platform:+") {
        isTransitive = false
    }
    embed("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-alpha+")
    embed("com.github.videogame-hacker:Koffee:88ba1b0") {
        isTransitive = false
    }
    compileOnly("org.spongepowered:mixin:0.7.11-SNAPSHOT")
}

tasks.compileKotlin {
    kotlinOptions {
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xno-param-assertions", "-Xjvm-default=all-compatibility")
    }
}

tasks.processResources {
    rename("(.+_at.cfg)", "META-INF/$1")
}

tasks.jar {
    from(embed.files.map { zipTree(it) })

    manifest.attributes(mapOf(
        "FMLCorePlugin" to "club.sk1er.patcher.tweaker.PatcherTweaker",
        "ModSide" to "CLIENT",
        "FMLAT" to accessTransformerName,
        "FMLCorePluginContainsFMLMod" to "Yes, yes it does",
        "Main-Class" to "club.sk1er.container.ContainerMessage",
        "TweakClass" to "cc.polyfrost.oneconfigwrapper.OneConfigWrapper",
        "TweakOrder" to "0",
        "MixinConfigs" to "patcher.mixins.json"
    ))
}