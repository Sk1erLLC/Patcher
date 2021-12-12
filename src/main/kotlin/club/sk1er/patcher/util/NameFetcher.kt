package club.sk1er.patcher.util

import club.sk1er.patcher.Patcher
import club.sk1er.patcher.coroutines.MCDispatchers
import gg.essential.api.EssentialAPI
import gg.essential.api.utils.mojang.Name
import gg.essential.elementa.utils.ObservableList
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NameFetcher {
    val names = ObservableList(mutableListOf<Name>())
    private val format: DateFormat = SimpleDateFormat("MM/dd/yyyy")
    var uuid: UUID? = null
        private set
    var currentName: String? = null
        private set
    var callback: () -> Unit = {}

    fun execute(username: String, async: Boolean = true) {
        try {
            if (username.isEmpty()) {
                return
            }
            val fetchNames = Runnable {
                currentName = username
                uuid = null
                names.clear()
                try {
                    val uuid = EssentialAPI.getMojangAPI().getUUID(username) ?: return@Runnable
                    this.uuid = uuid.get()
                } catch (e: Exception) {
                    Patcher.instance.logger.warn("Failed fetching UUID.", e)
                    return@Runnable
                }
                if (uuid != null) {
                    val nameHistory =
                        EssentialAPI.getMojangAPI().getNameHistory(uuid)
                    if (nameHistory == null || nameHistory.isEmpty()) return@Runnable
                    currentName = nameHistory[nameHistory.size - 1]!!.name
                    nameHistory.forEach {
                        if (it == null) return@forEach
                        names.add(it)
                    }
                } else {
                    names.add(Name("Failed to fetch $username's names", 0))
                }
            }
            if (async) {
                MCDispatchers.PATCHER_SCOPE.launch {
                    fetchNames.run()
                }.invokeOnCompletion {
                    callback()
                }
            } else {
                fetchNames.run()
                callback()
            }
        } catch (e: Exception) {
            Patcher.instance.logger.warn("User catch failed, tried fetching {}.", username, e)
        }
    }

    fun getDate(index: Int): String {
        return format.format(names[index].changedToAt?.let { Date(it) })
    }
}