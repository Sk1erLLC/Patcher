package club.sk1er.patcher.asm.external.forge.loader;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class MinecraftForgeTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.common.MinecraftForge"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("initialize")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst instanceof String) {
                        final String cst = (String) ((LdcInsnNode) next).cst;
                        if (cst.startsWith("net.minecraft.client.particle,EffectRenderer")) {
                            ((LdcInsnNode) next).cst = cst.replace(',', '.');
                        }
                    }
                }

                break;
            }
        }
    }
}
