package club.sk1er.patcher.coroutines

import kotlinx.coroutines.*
import java.util.concurrent.Executors

@OptIn(ObsoleteCoroutinesApi::class)
object MCDispatchers {

    val PATCHER_SCOPE = CoroutineScope(Dispatchers.Default)
    val IO = Executors.newFixedThreadPool(8).asCoroutineDispatcher()

}   