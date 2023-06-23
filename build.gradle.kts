import gg.essential.gradle.util.noServerRunConfigs

plugins {
    kotlin("jvm")
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
}

val modGroup: String by project
val modBaseName: String by project
group = modGroup
base.archivesName.set("$modBaseName-${platform.mcVersionStr}")

val accessTransformerName = "patcher1${platform.mcMinor}_at.cfg"

loom {
    noServerRunConfigs()
    forge {
        accessTransformer(rootProject.file("src/main/resources/$accessTransformerName"))
    }
    mixin {
        defaultRefmapName.set("patcher.mixins.refmap.json")
    }
    runConfigs {
        getByName("client") {
            property("fml.coreMods.load", "club.sk1er.patcher.tweaker.PatcherTweaker")
            property("patcher.debugBytecode", "true")
            property("mixin.debug.verbose", "true")
            property("mixin.debug.export", "true")
            property("mixin.dumpTargetOnFailure", "true")
            programArgs("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
            programArgs("--mixin", "patcher.mixins.json")
        }
    }
}

repositories {
    maven("https://repo.essential.gg/repository/maven-public/")
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

val embed by configurations.creating
configurations.implementation.get().extendsFrom(embed)

dependencies {
    compileOnly("gg.essential:essential-$platform:4246+g8be73312c")
    embed("gg.essential:loader-launchwrapper:1.1.3")

    compileOnly("org.spongepowered:mixin:0.8.5-SNAPSHOT")
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
        "TweakClass" to "gg.essential.loader.stage0.EssentialSetupTweaker",
        "TweakOrder" to "0",
        "MixinConfigs" to "patcher.mixins.json"
    ))
}