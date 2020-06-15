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
import com.google.common.base.Throwables
import com.google.common.collect.Lists
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import net.minecraftforge.fml.common.LoaderException
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.discovery.ModCandidate
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File

class AsyncModDiscoverer(
    private val candidates: ArrayList<ModCandidate>,
    private val nonModLibs: ArrayList<File>,
    private val dataTable: ASMDataTable
) {

    private val logger: Logger = LogManager.getLogger("Patcher - AsyncModDiscoverer")

    fun discover(): Pair<List<ModContainer>?, ArrayList<File>?> {
        val start = System.currentTimeMillis()
        val modList = mutableListOf<ModContainer>()
        logger.info("Searching for mods in async...")
        runBlocking(MCDispatchers.IO) {
            candidates.map { candidate ->
                async {
                    try {
                        val mods = candidate.explore(dataTable)
                        if (mods.isEmpty() && !candidate.isClasspath) {
                            nonModLibs.add(candidate.modContainer)
                        } else {
                            modList.addAll(mods)
                        }
                    } catch (le: LoaderException) {
                        logger.warn(
                            "Identified a problem with the mod candidate ${candidate.modContainer}, ignoring this source.",
                            le
                        )
                    } catch (t: Throwable) {
                        Throwables.propagate(t)
                    }
                }
            }.awaitAll()
        }

        logger.info("Finished mod discovery in ${(System.currentTimeMillis() - start) / 1000f}s.")
        return Pair(modList, nonModLibs)
    }
}