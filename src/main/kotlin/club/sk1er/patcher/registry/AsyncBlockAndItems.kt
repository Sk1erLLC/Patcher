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
import club.sk1er.patcher.hooks.IModelLoader
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.minecraftforge.client.model.ModelLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class AsyncBlockAndItems(private val modelLoader: ModelLoader) {

    private val logger: Logger = LogManager.getLogger("Patcher - AsyncBlockAndItems")

    fun load() {
        logger.info("Loading blocks & items in async...")
        val start = System.currentTimeMillis()
        runBlocking(MCDispatchers.IO) {
            val blocks = async {
                (modelLoader as IModelLoader).callLoadBlocks()
            }

            val items = async {
                (modelLoader as IModelLoader).callLoadItems()
            }

            blocks.await()
            items.await()
        }

        logger.info("Finished async block & item loading in ${(System.currentTimeMillis() - start) / 1000f} seconds.")
    }
}