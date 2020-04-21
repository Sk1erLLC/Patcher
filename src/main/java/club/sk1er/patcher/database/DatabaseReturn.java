package club.sk1er.patcher.database;

public class DatabaseReturn {
    private byte[] data;
    private byte[] mcMeta;
    private String packName;

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
