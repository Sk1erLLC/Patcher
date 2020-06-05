/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.jar

import club.sk1er.patcher.coroutines.MCDispatchers
import com.google.common.collect.Lists
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import net.minecraftforge.fml.common.LoaderException
import net.minecraftforge.fml.common.MetadataCollection
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.ModContainerFactory
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.discovery.ITypeDiscoverer
import net.minecraftforge.fml.common.discovery.ModCandidate
import net.minecraftforge.fml.common.discovery.asm.ASMModParser
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.jar.JarFile

class JarDiscoverer : ITypeDiscoverer {

    private val logger: Logger = LogManager.getLogger("Patcher - JarDiscoverer")

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun discover(candidate: ModCandidate, table: ASMDataTable): List<ModContainer>? {
        val foundMods: MutableList<ModContainer> = Lists.newArrayList()
        logger.info("Examining file ${candidate.modContainer.name} for potential mods.")

        runBlocking(MCDispatchers.IO) {
            val jar = JarFile(candidate.modContainer)

            jar.use {
                val modInfo = jar.getEntry("mcmod.info")
                val mc: MetadataCollection

                mc = if (modInfo != null) {
                    logger.info("Located mcmod.info file in file ${candidate.modContainer.name}")
                    MetadataCollection.from(jar.getInputStream(modInfo), candidate.modContainer.name)
                } else {
                    logger.warn("The mod container ${candidate.modContainer.name} appears to be missing an mcmod.info file.")
                    MetadataCollection.from(null, "")
                }

                Collections.list(jar.entries()).map { zip ->
                    async {
                        if (!(zip.name != null && zip.name.startsWith("__MACOSX"))) {
                            val match = ITypeDiscoverer.classFile.matcher(zip.name)

                            if (match.matches()) {
                                val modParser: ASMModParser
                                try {
                                    modParser = ASMModParser(jar.getInputStream(zip))
                                    candidate.addClassEntry(zip.name)
                                } catch (e: LoaderException) {
                                    logger.error(
                                        "There was a problem reading the entry ${zip.name} in the jar ${candidate.modContainer.path} - probably a corrupt zip.",
                                        e
                                    )
                                    jar.close()
                                    throw e
                                }

                                modParser.validate()
                                modParser.sendToTable(table, candidate)
                                val container =
                                    ModContainerFactory.instance().build(modParser, candidate.modContainer, candidate)
                                if (container != null) {
                                    table.addContainer(container)
                                    foundMods.add(container)
                                    container.bindMetadata(mc)
                                }
                            }
                        }
                    }
                }.awaitAll()
            }
        }

        return foundMods
    }
}