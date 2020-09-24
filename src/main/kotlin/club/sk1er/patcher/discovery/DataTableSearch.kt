package club.sk1er.patcher.discovery

import com.google.common.base.Predicate
import com.google.common.collect.ImmutableSetMultimap
import com.google.common.collect.Multimaps
import com.google.common.collect.SetMultimap
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData

@Suppress("unused")
class DataTableSearch {
    private var containerAnnotationData: Map<ModContainer, SetMultimap<String, ASMData>>? = null

    fun getAnnotationsFor(
        container: ModContainer,
        containers: List<ModContainer>,
        globalAnnotationData: SetMultimap<String, ASMData>
    ): SetMultimap<String, ASMData>? {
        if (this.containerAnnotationData == null) {
            this.containerAnnotationData = containers.map { cont ->
                cont to ImmutableSetMultimap.copyOf(
                    Multimaps.filterValues(
                        globalAnnotationData,
                        ModContainerPredicate(cont)
                    )
                )
            }.toMap()
        }

        return this.containerAnnotationData?.get(container)
    }

    inner class ModContainerPredicate(private val container: ModContainer) : Predicate<ASMData> {
        override fun apply(data: ASMData?): Boolean {
            return container.source == data?.candidate?.modContainer
        }
    }

    companion object {
        @JvmStatic val instance = DataTableSearch()
    }
}