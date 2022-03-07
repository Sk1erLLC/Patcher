package club.sk1er.patcher.commands

import gg.essential.api.commands.ArgumentParser
import gg.essential.api.commands.ArgumentQueue
import gg.essential.universal.UMinecraft
import net.minecraft.entity.player.EntityPlayer
import java.lang.reflect.Parameter

class PatcherPlayerArgumentParser : ArgumentParser<PatcherPlayer> {
    override fun parse(arguments: ArgumentQueue, param: Parameter): PatcherPlayer {
        val name = arguments.poll()
        return PatcherPlayer(
            name,
            getEntities()?.find { it.name == name }
        )
    }

    override fun complete(arguments: ArgumentQueue, param: Parameter): List<String> {
        val nameStart = arguments.poll()
        return getEntities()?.map { it.name }?.filter { it.startsWith(nameStart) }
            ?: emptyList()
    }

    private fun getEntities() = UMinecraft.getWorld()?.playerEntities?.filter { it.uniqueID.version() == 4 }
}

data class PatcherPlayer(val name: String, val entity: EntityPlayer?)