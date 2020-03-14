package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class GuiChatTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiChat"};
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
            String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("initGui")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();

                    if (node instanceof MethodInsnNode && FMLDeobfuscatingRemapper.INSTANCE.map(((MethodInsnNode) node).name).equals("setMaxStringLength")) {
                        methodNode.instructions.remove(node.getPrevious());
                        methodNode.instructions.remove(node.getPrevious());
                        methodNode.instructions.remove(node.getPrevious());
                        methodNode.instructions.remove(node);
                    }
                }

                methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), adjustMaxLength());
            }
        }
    }

    private InsnList adjustMaxLength() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiChat", "field_146415_a", // inputField
                "Lnet/minecraft/client/gui/GuiTextField;"));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "adjustChatMessageLength", "Z"));
        LabelNode l1 = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, l1));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/Patcher", "allowsHigherChatLength", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, l1));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 256));
        LabelNode l2 = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, l2));
        list.add(l1);
        list.add(new IntInsnNode(Opcodes.BIPUSH, 100));
        list.add(l2);
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/gui/GuiTextField", "func_146203_f", // setMaxStringLength
                "(I)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
