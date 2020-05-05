package club.sk1er.patcher.tweaker;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.tree.ClassNode;

public class RenderChunkTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.chunk.RenderChunk"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {

    }
}
