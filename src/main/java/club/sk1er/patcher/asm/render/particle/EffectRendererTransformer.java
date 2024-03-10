package club.sk1er.patcher.asm.render.particle;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class EffectRendererTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.particle.EffectRenderer"};
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
            switch (methodName) {
                case "func_78872_b":
                case "renderLitParticles": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof VarInsnNode && next.getOpcode() == Opcodes.FSTORE && ((VarInsnNode) next).var == 8) {
                            while (next.getPrevious() != null) {
                                methodNode.instructions.remove(next.getPrevious());
                            }

                            methodNode.instructions.insertBefore(next.getNext(), reassignRotation());
                            methodNode.instructions.remove(next);
                            break;
                        }
                    }

                    break;
                }
            }
        }
    }

    private InsnList reassignRotation() {
        InsnList list = new InsnList();
        // unnecessary but just for compatibility
        list.add(new LdcInsnNode(0.017453292F));
        list.add(new VarInsnNode(Opcodes.FSTORE, 3));
        // actual fix for mc-74764
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/ActiveRenderInfo", isDevelopment() ? "getRotationX" : "func_178808_b", "()F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, 4));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/ActiveRenderInfo", isDevelopment() ? "getRotationZ" : "func_178803_d", "()F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, 5));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/ActiveRenderInfo", isDevelopment() ? "getRotationYZ" : "func_178805_e", "()F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, 6));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/ActiveRenderInfo", isDevelopment() ? "getRotationXY" : "func_178807_f", "()F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, 7));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/ActiveRenderInfo", isDevelopment() ? "getRotationXZ" : "func_178809_c", "()F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, 8));
        return list;
    }
}
