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
import com.google.common.collect.*;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Much more efficient version of {@link ASMDataTable}, used in {@link ASMDataTableTransformer#transform(ClassNode, String)}.
 */
public class ASMDataTableHook {

    /**
     * Create a copy of {@link ASMDataTable}'s containerAnnotationData,
     * safely used as it's only used in {@link ASMDataTableHook#getAnnotationsFor(ModContainer, List, SetMultimap)}.
     * <p>
     * Declared as immutable as this is how Forge does it in their much faster search in 1.12.
     */
    public static Map<ModContainer, ImmutableSetMultimap<String, ASMDataTable.ASMData>> immutableContainerAnnotationData;

    /**
     * Direct mirror of the original containerAnnotationData, but mutable as this is how Forge does it
     * in 1.8, while 1.12 (containing the much faster search) uses immutable.
     */
    public static Map<ModContainer, SetMultimap<String, ASMDataTable.ASMData>> mutableContainerAnnotationData;


    public static boolean postedInfo = false;

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
        try {
            if (!postedInfo) {
                postedInfo = true;
                System.out.println("Attempting to use optimized annotation search.");
            }
            if (immutableContainerAnnotationData == null) {
                immutableContainerAnnotationData = containers.parallelStream()
                    .map(cont -> Pair.of(cont, ImmutableSetMultimap.copyOf(Multimaps.filterValues(globalAnnotationData, new ModContainerPredicate(cont)))))
                    .collect(toImmutableMap(Pair::getKey, Pair::getValue));
            }

            return immutableContainerAnnotationData.get(container);
        } catch (Exception e) {
            System.out.println("Failed to run optimized annotation search, defaulting to normal.");

            if (mutableContainerAnnotationData == null) {
                ImmutableMap.Builder<ModContainer, SetMultimap<String, ASMDataTable.ASMData>> mapBuilder = ImmutableMap.builder();

                for (ModContainer cont : containers) {
                    Multimap<String, ASMDataTable.ASMData> values = Multimaps.filterValues(globalAnnotationData, new ModContainerPredicate(cont));
                    mapBuilder.put(cont, ImmutableSetMultimap.copyOf(values));
                }

                mutableContainerAnnotationData = mapBuilder.build();
            }

            return mutableContainerAnnotationData.get(container);
        }
    }

    /**
     * Did someone say functional programming?
     * stolen from stack overflow i got no idea what this does lmfao
     */
    public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        return Collector.of(
            ImmutableMap.Builder<K, V>::new,
            (builder, entry) -> builder.put(keyMapper.apply(entry), valueMapper.apply(entry)),
            (leftBuild, rightBuild) -> leftBuild.putAll(rightBuild.build()), // do these names make sense? probably not.
            ImmutableMap.Builder::build);
    }

    // old map creation, just in the case of the above solution not working better, replace with this
    /*public static <K, V> Map<K, V> toImmutableMap(List<Pair<K, V>> pairs) {
        ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();

        for (Pair<K, V> pair : pairs) {
            builder.put(pair.getKey(), pair.getValue());
        }

        return builder.build();
    }*/

    /**
     * Direct copy of {@link ASMDataTable}'s inner class, ModContainerPredicate.
     * Cannot be used outside of the class as it is originally package private, but it is only used
     * once (in {@link ASMDataTable#getAnnotationsFor(ModContainer)} so we can create a direct copy without
     * worrying about other classes not being able to redirect to the proper class.
     */
    public static class ModContainerPredicate implements Predicate<ASMDataTable.ASMData> {
        private final ModContainer container;

        public ModContainerPredicate(ModContainer container) {
            this.container = container;
        }

        @Override
        public boolean apply(ASMDataTable.ASMData data) {
            return container.getSource() == Objects.requireNonNull(data).getCandidate().getModContainer();
        }
    }
}
