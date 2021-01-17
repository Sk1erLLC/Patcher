package club.sk1er.patcher.tweaker.asm.optifine.xpfix;

import club.sk1er.patcher.tweaker.ClassTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class GuiIngameForgeTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.GuiIngameForge"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("renderExperience")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals(8453920)) {
                        method.instructions.insertBefore(next.getNext(), new MethodInsnNode(Opcodes.INVOKESTATIC,
                            ClassTransformer.optifineVersion.equals("I7") ? "CustomColors" : "net/optifine/CustomColors",
                            "getExpBarTextColor",
                            "(I)I",
                            false));
                        break;
                    }
                }

                break;
            }
        }
    }
}
