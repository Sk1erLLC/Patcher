package club.sk1er.patcher.optifine;

import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.Set;

public class OptiFineGenerations {
    @SerializedName("iGeneration")
    private final Set<String> iGeneration = new HashSet<>(1);

    @SerializedName("lGeneration")
    private final Set<String> lGeneration = new HashSet<>(2);

    @SerializedName("mGeneration")
    private final Set<String> mGeneration = new HashSet<>();

    @SerializedName("futureGeneration")
    private final Set<String> futureGeneration = new HashSet<>();

    public Set<String> getIGeneration() {
        return iGeneration;
    }

    public Set<String> getLGeneration() {
        return lGeneration;
    }

    public Set<String> getMGeneration() {
        return mGeneration;
    }

    public Set<String> getFutureGeneration() {
        return futureGeneration;
    }
}
