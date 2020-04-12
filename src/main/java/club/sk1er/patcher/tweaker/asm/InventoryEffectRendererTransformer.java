package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

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
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), newEffectLogic());
                break;
            }
        }
    }

    private InsnList newEffectLogic() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "inventoryPosition", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/Minecraft", "func_71410_x", // getMinecraft
            "()Lnet/minecraft/client/Minecraft;", false));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71439_g", // thePlayer
            "Lnet/minecraft/client/entity/EntityPlayerSP;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/EntityPlayerSP", "func_70651_bq", // getActivePotionEffects
            "()Ljava/util/Collection;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Collection", "isEmpty", "()Z", true));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new InsnNode(Opcodes.ICONST_1));
        LabelNode gotoInsn = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifne);
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(gotoInsn);
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/InventoryEffectRenderer", "field_147045_u", // hasActivePotionEffects
            "Z"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/InventoryEffectRenderer", "field_146294_l", // width
            "I"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/InventoryEffectRenderer", "field_146999_f", // xSize
            "I"));
        list.add(new InsnNode(Opcodes.ISUB));
        list.add(new InsnNode(Opcodes.ICONST_2));
        list.add(new InsnNode(Opcodes.IDIV));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/InventoryEffectRenderer", "field_147003_i", // guiLeft
            "I"));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }
}
