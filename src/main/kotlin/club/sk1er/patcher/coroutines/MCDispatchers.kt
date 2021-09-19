package club.sk1er.patcher.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi

@OptIn(ObsoleteCoroutinesApi::class)
object MCDispatchers {
    val PATCHER_SCOPE = CoroutineScope(Dispatchers.Default)
}