package club.sk1er.patcher.asm.external.mods.optifine;

import club.sk1er.patcher.tweaker.ClassTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

public class LagometerTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{ClassTransformer.optifineVersion.equals("I7") ? "Lagometer" : "net.optifine.Lagometer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("showLagometer")) {
                method.instructions.insert(checkPatcherSetting());
                break;
            }
        }
    }

    private InsnList checkPatcherSetting() {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("useVanillaMetricsRenderer", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }
}
