/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.tweaker.asm.optifine;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.lwjgl.input.Mouse;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Iterator;
import java.util.ListIterator;

// By LlamaLad7
public class OptifineEntityRendererTransformer implements PatcherTransformer {
    private static final float normalModifier = 4f;
    private static float currentModifier = 4f;
    public static boolean zoomed = false;

    public static float getModifier() {
        if (!PatcherConfig.scrollToZoom) {
            return normalModifier;
        }

        int moved = Mouse.getDWheel();

        if (moved > 0) {
            currentModifier += 0.25f * currentModifier;
        } else if (moved < 0) {
            currentModifier -= 0.25f * currentModifier;
        }

        if (currentModifier < 0.8) {
            currentModifier = 0.8f;
        }

        if (currentModifier > 600) {
            currentModifier = 600f;
        }

        return currentModifier;
    }

    public static void resetCurrent() {
        currentModifier = normalModifier;
    }

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
            String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("getFOVModifier") || methodName.equals("func_78481_a")) {
                int zoomActiveIndex = -1;

                for (LocalVariableNode var : methodNode.localVariables) {
                    if (var.name.equals("zoomActive")) {
                        zoomActiveIndex = var.index;
                        break;
                    }
                }

                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode thing = iterator.next();
                    if (checkNode(thing)) {
                        methodNode.instructions.insertBefore(thing, new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "normalZoomSensitivity", "Z")); // False instead of true
                        methodNode.instructions.insertBefore(thing, new InsnNode(Opcodes.ICONST_1));
                        methodNode.instructions.insertBefore(thing, new InsnNode(Opcodes.IXOR));
                        methodNode.instructions.insert(thing, callReset());
                        methodNode.instructions.remove(thing);
                    } else if (checkDivNode(thing)) {
                        methodNode.instructions.remove(thing.getPrevious());
                        methodNode.instructions.insertBefore(thing, getDivisor());
                    } else if (checkZoomActiveNode(thing, zoomActiveIndex)) {
                        methodNode.instructions.insertBefore(thing, setZoomed(zoomActiveIndex));
                    }
                }
            } else if (methodName.equals("orientCamera") || methodName.equals("func_78467_g")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals(-0.10000000149011612F)) {
                        methodNode.instructions.insertBefore(next, fixParallax());
                        methodNode.instructions.remove(next);
                    } else if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        String methodInsnName = mapMethodNameFromNode((MethodInsnNode) next);

                        if (methodInsnName.equals("rayTraceBlocks") || methodInsnName.equals("func_72933_a")) {
                            ((MethodInsnNode) next).name = Patcher.isDevelopment() ? "rayTraceBlocks" : "func_147447_a";
                            ((MethodInsnNode) next).desc = FMLDeobfuscatingRemapper.INSTANCE.mapDesc(
                                "(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;ZZZ)Lnet/minecraft/util/MovingObjectPosition;"
                            );

                            methodNode.instructions.insertBefore(next, changeMethodRedirect());
                        }
                    }
                }
            }
        }
    }

    private InsnList changeMethodRedirect() {
        InsnList list = new InsnList();
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new InsnNode(Opcodes.ICONST_1));
        return list;
    }

    private InsnList fixParallax() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "parallaxFix", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new LdcInsnNode(0.05F));
        LabelNode gotoInsn = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifeq);
        list.add(new LdcInsnNode(-0.10000000149011612F));
        list.add(gotoInsn);
        return list;
    }

    private boolean checkNode(AbstractInsnNode node) {
        if (node.getNext() == null) return false;
        if (node.getOpcode() == Opcodes.ICONST_1) {
            AbstractInsnNode next = node.getNext();
            if (next.getOpcode() == Opcodes.PUTFIELD) {
                FieldInsnNode fieldInsn = (FieldInsnNode) next;
                return fieldInsn.name.equals("smoothCamera") || fieldInsn.name.equals("field_74326_T");
            }
        }
        return false;
    }

    private boolean checkDivNode(AbstractInsnNode node) {
        if (node.getOpcode() == Opcodes.FDIV) {
            if (node.getPrevious().getOpcode() == Opcodes.LDC) {
                LdcInsnNode prev = (LdcInsnNode) node.getPrevious();
                if (prev.cst instanceof Float) {
                    Float f = (Float) prev.cst;
                    return f.equals(4f);
                }
            }
        }
        return false;
    }

    private boolean checkZoomActiveNode(AbstractInsnNode node, int zoomActiveIndex) {
        if (node.getOpcode() == Opcodes.ILOAD) {
            VarInsnNode n = (VarInsnNode) node;
            if (n.var == zoomActiveIndex) {
                return node.getNext().getOpcode() == Opcodes.IFEQ;
            }
        }
        return false;
    }

    private InsnList getDivisor() {
        InsnList list = new InsnList();
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "club/sk1er/patcher/tweaker/asm/optifine/OptifineEntityRendererTransformer",
                "getModifier",
                "()F",
                false)); // Call my method
        return list;
    }

    private InsnList callReset() {
        InsnList list = new InsnList();
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "club/sk1er/patcher/tweaker/asm/optifine/OptifineEntityRendererTransformer",
                "resetCurrent",
                "()V",
                false)); // Call my method
        return list;
    }

    private InsnList setZoomed(int zoomActiveIndex) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ILOAD, zoomActiveIndex));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "club/sk1er/patcher/tweaker/asm/optifine/OptifineEntityRendererTransformer", "zoomed", "Z"));
        return list;
    }
}
