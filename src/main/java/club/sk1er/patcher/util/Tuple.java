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

package club.sk1er.patcher.util;

/**
 * Similar to {@link kotlin.Pair}, but for Java.
 * May now be redundant, as this was added way before we implemented Kotlin into the project.
 *
 * @param <E> Left side.
 * @param <K> Right side.
 */
public class Tuple<E, K> {

    private final E left;
    private final K right;

    public Tuple(E left, K right) {
        this.left = left;
        this.right = right;
    }

    public E getLeft() {
        return left;
    }

    public K getRight() {
        return right;
    }
}