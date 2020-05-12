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

package club.sk1er.patcher.hooks;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ASMDataTableHook {

    private static Map<ModContainer, ImmutableSetMultimap<String, ASMDataTable.ASMData>> containerAnnotationData;

    public static SetMultimap<String, ASMDataTable.ASMData> getAnnotationsFor(ModContainer container,
                                                                              List<ModContainer> containers,
                                                                              SetMultimap<String, ASMDataTable.ASMData> globalAnnotationData) {
        if (containerAnnotationData == null) {
            containerAnnotationData = toImmutableMap(containers.parallelStream()
                .map(cont -> Pair.of(cont, ImmutableSetMultimap.copyOf(Multimaps.filterValues(globalAnnotationData, new ModContainerPredicate(cont)))))
                .collect(Collectors.toList()));
        }

        return containerAnnotationData.get(container);
    }

    private static <K, V> Map<K, V> toImmutableMap(List<Pair<K, V>> pairs) {
        ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        for (Pair<K, V> pair : pairs) {
            builder.put(pair.getKey(), pair.getValue());
        }
        return builder.build();
    }

    private static class ModContainerPredicate implements Predicate<ASMDataTable.ASMData> {
        private final ModContainer container;

        public ModContainerPredicate(ModContainer container) {
            this.container = container;
        }

        @Override
        public boolean apply(ASMDataTable.ASMData data) {
            return container.getSource().equals(Objects.requireNonNull(data).getCandidate().getModContainer());
        }
    }
}
