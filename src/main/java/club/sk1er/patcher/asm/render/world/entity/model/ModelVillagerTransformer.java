package club.sk1er.patcher.asm.render.world.entity.model;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class ModelVillagerTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.model.ModelVillager"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("<init>") && method.desc.equals("(FFII)V")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof IntInsnNode && ((IntInsnNode) next).operand == 18) {
                        ((IntInsnNode) next).operand = 20;
                        break;
                    }
                }

                break;
            }
        }
    }
}
