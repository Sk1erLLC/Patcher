package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ModContainerFactoryTransformer implements PatcherTransformer {

    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.fml.common.ModContainerFactory"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("build")) {
                clearInstructions(method);
                method.instructions.insert(skipBaseModDetection());
                break;
            }
        }
    }

    private InsnList skipBaseModDetection() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/fml/common/ModContainerFactory", "modTypes", "Ljava/util/Map;"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHooksPackage("ModContainerFactoryHook"), "build", "(Lnet/minecraftforge/fml/common/discovery/asm/ASMModParser;Lnet/minecraftforge/fml/common/discovery/ModCandidate;Ljava/util/Map;)Lnet/minecraftforge/fml/common/ModContainer;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }
}
