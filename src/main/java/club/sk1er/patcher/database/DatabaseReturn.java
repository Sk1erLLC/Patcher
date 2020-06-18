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

package club.sk1er.patcher.database;

public class DatabaseReturn {
    private final byte[] data;
    private final byte[] mcMeta;
    private final String packName;

    public DatabaseReturn(byte[] data, byte[] mcMeta, String packName) {
        this.data = data;
        this.mcMeta = mcMeta;
        this.packName = packName;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getMcMeta() {
        return mcMeta;
    }

    public String getPackName() {
        return packName;
    }
}
