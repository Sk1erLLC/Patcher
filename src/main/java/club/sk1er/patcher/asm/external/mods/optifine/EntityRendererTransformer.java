package club.sk1er.patcher.asm.external.mods.optifine;

import club.sk1er.patcher.tweaker.ClassTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class EntityRendererTransformer implements PatcherTransformer {
    private final boolean dev = isDevelopment();

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.EntityRenderer"};
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
            switch (mapMethodName(classNode, methodNode)) {
                case "updateLightmap":
                case "func_78472_g": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next.getOpcode() == Opcodes.INVOKEVIRTUAL && next instanceof MethodInsnNode) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("isPotionActive") || methodInsnName.equals("func_70644_a")) {
                                final AbstractInsnNode suspect = next.getNext().getNext();
                                if (suspect.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) suspect).owner.endsWith("CustomColors")) {
                                    continue;
                                }

                                methodNode.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious().getPrevious(), clampLightmap());
                            }
                        }
                    }
                    break;
                }

                case "renderWorldPass":
                case "func_175068_a": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();
                        switch (ClassTransformer.optifineVersion) {
                            case "I7": {
                                if (next instanceof TypeInsnNode) {
                                    if (FMLDeobfuscatingRemapper.INSTANCE.map(((TypeInsnNode) next).desc).equals("net/minecraft/client/renderer/culling/Frustum")) {
                                        while (true) {
                                            final AbstractInsnNode insn = iterator.next();
                                            if (insn instanceof VarInsnNode) {
                                                methodNode.instructions.insert(insn, getStoreCameraInsn(((VarInsnNode) insn).var));
                                                break;
                                            }
                                        }
                                    }
                                }

                                break;
                            }

                            default: // vanilla & L6 will work for this
                            case "L5": {
                                int cameraVar = -1;

                                for (final LocalVariableNode var : methodNode.localVariables) {
                                    if (var.name.equals("icamera")) {
                                        cameraVar = var.index;
                                        break;
                                    }
                                }

                                if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                    final String methodInsnName = mapMethodNameFromNode(next);
                                    if (methodInsnName.equals("getRenderViewEntity") || methodInsnName.equals("func_175606_aa")) {
                                        next = next.getPrevious().getPrevious();

                                        methodNode.instructions.insertBefore(next, getStoreCameraInsn(cameraVar));
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    break;
                }

                case "func_78476_b":
                case "renderHand": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESPECIAL) {
                            String methodName = mapMethodNameFromNode(next);
                            if (methodName.equals("getFOVModifier") || methodName.equals("func_78481_a")) {
                                methodNode.instructions.insert(next, new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("EntityRendererHook"), "getHandFOVModifier", "(F)F", false));
                            }
                        }
                    }
                }
            }
        }
    }

    private InsnList clampLightmap() {
        // using srg name in dev crashes? Ok Forge
        final String clamp_float = dev ? "clamp_float" : "func_76131_a";
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.FLOAD, 12));
        clampFloat(12, 13, clamp_float, list);
        clampFloat(13, 14, clamp_float, list);
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new InsnNode(Opcodes.FCONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/util/MathHelper", clamp_float, "(FFF)F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, 14));
        return list;
    }

    private void clampFloat(int storeIndex, int loadIndex, String clamp_float, InsnList list) {
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new InsnNode(Opcodes.FCONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/util/MathHelper", clamp_float, "(FFF)F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, storeIndex));
        list.add(new VarInsnNode(Opcodes.FLOAD, loadIndex));
    }

    private InsnList getStoreCameraInsn(int var) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, var));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "club/sk1er/patcher/util/world/render/culling/ParticleCulling", "camera", "Lnet/minecraft/client/renderer/culling/ICamera;"));
        return list;
    }
}
