package club.sk1er.patcher.asm.render.screen;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class InventoryEffectRendererTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.InventoryEffectRenderer"};
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

            if (methodName.equals("updateActivePotionEffects") || methodName.equals("func_175378_g")) {
                boolean foundOam = false;

                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                AbstractInsnNode next = null;
                while (iterator.hasNext()) {
                    next = iterator.next();

                    if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETSTATIC && ((FieldInsnNode) next).name.equals("NOINVMOVE")) {
                        foundOam = true;
                        break;
                    }
                }

                methodNode.instructions.insertBefore(next != null && foundOam ? next : methodNode.instructions.getLast().getPrevious(), newEffectLogic());
                break;
            }
        }
    }


    private InsnList newEffectLogic() {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("inventoryPosition", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/InventoryEffectRenderer", isDevelopment() ? "width" : "field_146294_l",
            "I"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/InventoryEffectRenderer", isDevelopment() ? "xSize" : "field_146999_f",
            "I"));
        list.add(new InsnNode(Opcodes.ISUB));
        list.add(new InsnNode(Opcodes.ICONST_2));
        list.add(new InsnNode(Opcodes.IDIV));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/InventoryEffectRenderer", isDevelopment() ? "guiLeft" : "field_147003_i",
            "I"));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }
}
