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

import club.sk1er.patcher.tweaker.asm.forge.ASMDataTableTransformer;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Much more efficient version of {@link ASMDataTable}, used in {@link ASMDataTableTransformer#transform(ClassNode, String)}.
 */
@SuppressWarnings("unused")
public class ASMDataTableHook {

    /**
     * Create a copy of {@link ASMDataTable}'s containerAnnotationData,
     * safely used as it's only used in {@link ASMDataTableHook#getAnnotationsFor(ModContainer, List, SetMultimap)}.
     */
    public static Map<ModContainer, ImmutableSetMultimap<String, ASMDataTable.ASMData>> containerAnnotationData;

    /**
     * Create a much more efficient annotation check by running in a parallel stream and wrapping
     * the annotation data into an immutable map.
     *
     * @param container            Contains any information about a mod, such as modid, name, version, location, etc.
     * @param containers           Direct copy of {@link ASMDataTable}'s containers field.
     * @param globalAnnotationData Direct copy of {@link ASMDataTable}'s globalAnnotationData field.
     * @return Value of the annotation's data.
     */
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

    public static <K, V> Map<K, V> toImmutableMap(List<Pair<K, V>> pairs) {
        ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        for (Pair<K, V> pair : pairs) {
            builder.put(pair.getKey(), pair.getValue());
        }
        return builder.build();
    }

    /**
     * Direct copy of {@link ASMDataTable}'s inner class, ModContainerPredicate.
     * Cannot be used outside of the class as it is originally package private, but it is only used
     * once (in {@link ASMDataTable#getAnnotationsFor(ModContainer)} so we can create a direct copy without
     * worrying about other classes not being able to redirect to the proper method.
     */
    public static class ModContainerPredicate implements Predicate<ASMDataTable.ASMData> {
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
