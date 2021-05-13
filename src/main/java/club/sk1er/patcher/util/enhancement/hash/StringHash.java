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

package club.sk1er.patcher.util.enhancement.hash;

import club.sk1er.patcher.util.enhancement.hash.impl.AbstractHash;

public class StringHash extends AbstractHash {
    public StringHash(String text, float red, float green, float blue, float alpha, boolean shadow) {
        super(text, red, green, blue, alpha, shadow);
    }
}
