package club.sk1er.patcher.asm.external.forge.render.screen;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
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
            final String methodName = mapMethodName(classNode, methodNode);
            if (methodNode.name.equals("updateCache")) {
                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();

                    // auto-closing stream
                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("read")) {
                        ((MethodInsnNode) next).owner = "net/minecraft/client/renderer/texture/TextureUtil";
                        ((MethodInsnNode) next).name = isDevelopment() ? "readBufferedImage" : "func_177053_a";
                        ((MethodInsnNode) next).desc = "(Ljava/io/InputStream;)Ljava/awt/image/BufferedImage;";
                        break;
                    }
                }
            } else if (methodName.equals("drawScreen") || methodName.equals("func_73863_a")) {
                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESTATIC) {
                        final String methodInsnName = mapMethodNameFromNode(next);

                        if (methodInsnName.equals("format") || methodInsnName.equals("func_135052_a")) {
                            methodNode.instructions.insertBefore(next.getNext(), replaceSlash());
                            break;
                        }
                    }
                }
            }
        }
    }

    private InsnList replaceSlash() {
        InsnList list = new InsnList();
        list.add(new LdcInsnNode("\\"));
        list.add(new LdcInsnNode(""));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;", false));
        return list;
    }
}
