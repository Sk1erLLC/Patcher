package club.sk1er.patcher.tweaker.asm.forge;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class GuiModListTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.fml.client.GuiModList"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("updateCache")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    // auto-closing stream
                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("read")) {
                        ((MethodInsnNode) next).owner = "net/minecraft/client/renderer/texture/TextureUtil";
                        ((MethodInsnNode) next).name = "func_177053_a"; // readBufferedImage
                        ((MethodInsnNode) next).desc = "(Ljava/io/InputStream;)Ljava/awt/image/BufferedImage;";
                    }
                }

                break;
            }
        }
    }
}
