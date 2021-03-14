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
package club.sk1er.patcher.registry

import club.sk1er.patcher.coroutines.MCDispatchers
import club.sk1er.patcher.hooks.accessors.IModelLoader
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import net.minecraftforge.client.model.ModelLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.system.measureNanoTime

class AsyncBlockAndItems(private val modelLoader: ModelLoader) {

    private val logger: Logger = LogManager.getLogger("Patcher - AsyncBlockAndItems")

    fun load() {
        val time = measureNanoTime {
            runBlocking(MCDispatchers.IO) {
                listOf(
                    async { (modelLoader as IModelLoader).callLoadBlocks() },
                    async { (modelLoader as IModelLoader).callLoadItems() }
                ).awaitAll()
            }
        }

        logger.info("Finished async block & item loading in ${time / 1_000_000}ms.")
    }
}