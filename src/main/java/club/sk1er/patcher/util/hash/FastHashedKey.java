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

package club.sk1er.patcher.util.hash;

public class FastHashedKey {
    public static int getFasterHashedKey(long input) {
        input = (input ^ (input >> 30)) * (0xbf58476d1ce4e5b9L);
        input = (input ^ (input >> 27)) * (0x94d049bb133111ebL);
        input = input ^ (input >> 31);
        return Long.hashCode(input);
    }
}
