package club.sk1er.patcher.util.enhancement.text;

import club.sk1er.mods.core.util.Multithreading;
import club.sk1er.patcher.util.enhancement.Enhancement;
import club.sk1er.patcher.util.enhancement.text.CachedString;
import club.sk1er.patcher.util.hash.StringHash;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheWriter;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.SharedDrawable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class EnhancedFontRenderer implements Enhancement {

    private static final int MAX = 5000 /* Worth bumping up to 10_000? */;
    public static SharedDrawable drawable;
    public static List<EnhancedFontRenderer> INSTANCES = new ArrayList<>();
    public final List<StringHash> obfuscated = new ArrayList<>();
    private final Map<String, Integer> stringWidthCache = new HashMap<>();
    private final Queue<Integer> glRemoval = new ConcurrentLinkedQueue<>();
    private Cache<StringHash, CachedString> stringCache = Caffeine.newBuilder()
        .writer(new RemovalListener())
        .executor(Multithreading.POOL)
        .maximumSize(MAX).build();


    public EnhancedFontRenderer() {
        INSTANCES.add(this);
    }

    public int getGlList() {
        Integer poll = glRemoval.poll();
        if (poll == null) {
            return GLAllocation.generateDisplayLists(1);
        }
        return poll;
    }

    public Map<String, Integer> getStringWidthCache() {
        return stringWidthCache;
    }

    public Queue<Integer> getGlRemoval() {
        return glRemoval;
    }

    public CachedString get(StringHash key) {
        return stringCache.getIfPresent(key);
    }

    public void cache(StringHash key, CachedString value) {
        stringCache.put(key, value);
    }

    @Override
    public String getName() {
        return "Font";
    }

    @Override
    public void tick() {
        stringCache.invalidateAll(obfuscated);
        obfuscated.clear();
        if (drawable == null) {
            try {
                drawable = new SharedDrawable(Display.getDrawable());
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
        }
    }

    public void invalidateAll() {
        this.stringCache.invalidateAll();
    }

    private class RemovalListener implements CacheWriter<StringHash, CachedString> {

        @Override
        public void write(@Nonnull StringHash key, @Nonnull CachedString value) {

        }

        @Override
        public void delete(@Nonnull StringHash key, CachedString value, @Nonnull RemovalCause cause) {
            if (value == null) return;
            getGlRemoval().add(value.getListId());
        }

    }

}