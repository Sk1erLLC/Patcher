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

package club.sk1er.patcher.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext

@OptIn(ObsoleteCoroutinesApi::class)
object MCDispatchers {

    private val RENDERER = Dispatchers.Default
    val PATCHER_SCOPE = CoroutineScope(RENDERER)
    val IO = newFixedThreadPoolContext(8, "IO")

}