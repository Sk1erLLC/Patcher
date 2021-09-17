package club.sk1er.patcher.config

import club.sk1er.patcher.Patcher
import gg.essential.vigilance.data.PropertyAttributes
import gg.essential.vigilance.data.PropertyData
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.ValueBackedPropertyValue
import java.util.function.Consumer

object ConfigUtil {
    @JvmStatic
    fun createAndRegisterConfig(
        type: PropertyType, category: String, subCategory: String, name: String,
        description: String, defaultValue: Any?, min: Int, max: Int, onUpdate: Consumer<Any?>
    ): PropertyData {
        val config = createConfig(type, category, subCategory, name, description, defaultValue, min, max, onUpdate)
        register(config)
        return config
    }

    private fun createConfig(
        type: PropertyType, category: String, subCategory: String, name: String,
        description: String, defaultValue: Any?, min: Int, max: Int, onUpdate: Consumer<Any?>
    ): PropertyData {
        val attributes = PropertyAttributes(type, name, category, subCategory, description, min, max)
        val data = PropertyData(attributes, ValueBackedPropertyValue(defaultValue), Patcher.instance.patcherSoundConfig)
        data.setCallbackConsumer(onUpdate)
        return data
    }

    private fun register(data: PropertyData?) {
        Patcher.instance.patcherSoundConfig.registerProperty(data!!)
    }
}