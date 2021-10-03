package club.sk1er.patcher.asm.external.mods.optifine.reflectionoptimizations.modern;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class CustomColorsReflectionOptimizer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.optifine.CustomColors$5"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("getColor")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("callInt")) {
                        for (int nodes = 0; nodes < 3; nodes++) {
                            method.instructions.remove(next.getPrevious());
                        }

                        method.instructions.insertBefore(next, new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                            "net/minecraft/world/biome/BiomeGenBase", "getWaterColorMultiplier", "()I", false));
                        method.instructions.remove(next);
                        break;
                    }
                }

                break;
            }
        }
    }
}
