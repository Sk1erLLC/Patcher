package club.sk1er.patcher.asm.external.mods.sidebarmod;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class GuiSidebarTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"revamp.sidebarmod.gui.GuiSidebar"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("drawSidebar")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals(553648127)) {
                        ((LdcInsnNode) next).cst = -1;
                    }
                }

                break;
            }
        }
    }
}
