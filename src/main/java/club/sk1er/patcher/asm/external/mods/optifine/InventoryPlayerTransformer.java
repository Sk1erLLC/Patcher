package club.sk1er.patcher.asm.external.mods.optifine;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

public class InventoryPlayerTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.entity.player.InventoryPlayer"};
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
            if (methodName.equals("changeCurrentItem") || methodName.equals("func_70453_c")) {
                methodNode.instructions.insert(dontScroll());
                break;
            }
        }
    }

    private InsnList dontScroll() {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("disableHotbarScrolling", "Z"));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(getPatcherSetting("scrollToZoom", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/asm/external/mods/optifine/EntityRendererTransformer", "zoomed", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(ifne);
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }
}
