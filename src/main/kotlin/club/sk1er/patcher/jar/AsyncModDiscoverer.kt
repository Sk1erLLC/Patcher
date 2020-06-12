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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
        logger.info("Searching for mods in async...")
        val modList = mutableListOf<ModContainer>()
        val mutex = Mutex()
        runBlocking(MCDispatchers.IO) {
            val jobs = candidates.map { candidate ->

                launch {
                    try {
                        val mods = candidate.explore(dataTable)
                        mutex.withLock {
                            if (mods.isEmpty() && !candidate.isClasspath) {
                                nonModLibs.add(candidate.modContainer)
                            } else {
                                modList.addAll(mods)
                            }
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
            }

            jobs.forEach { it.join() }
        }

        logger.info("Finished searching for mods in ${(System.currentTimeMillis() - start) / 1000F}s.")
        return Pair(modList, nonModLibs)
    }
}