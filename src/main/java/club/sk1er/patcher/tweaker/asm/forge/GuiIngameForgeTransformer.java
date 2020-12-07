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

package club.sk1er.patcher.tweaker.asm.forge;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class GuiIngameForgeTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.GuiIngameForge"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.interfaces.add(getHooksPackage() + "accessors/IGuiIngameForge");

        for (MethodNode methodNode : classNode.methods) {
            switch(methodNode.name) {
                case "pre":
                case "post":
                case "renderCrosshairs":
                    methodNode.access = Opcodes.ACC_PUBLIC;
                    break;
            }

            String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("renderGameOverlay") || methodName.equals("func_175180_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        if (((MethodInsnNode) next).name.equals("renderExperience")) {
                            methodNode.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious(), toggleAlpha(true));
                            methodNode.instructions.insertBefore(next.getNext(), toggleAlpha(false));
                            break;
                        }
                    }
                }
            } else if (methodName.equals("renderCrosshairs")) {
                methodNode.instructions.insert(checkOverride());

                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof IntInsnNode && ((IntInsnNode) next).operand == 775) {
                        LabelNode ifne = new LabelNode();
                        InsnList list = new InsnList();
                        methodNode.instructions.insertBefore(next, createToggle(ifne, list));

                        for (int i = 0; i < 5; ++i) {
                            next = next.getNext();
                        }

                        methodNode.instructions.insertBefore(next, addIfeq(ifne, list));
                        break;
                    }
                }
            } else if (methodNode.name.equals("pre")) {
                methodNode.instructions.insert(checkCompatibilityMode());
            }
        }
    }

    private InsnList checkCompatibilityMode() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "hudCachingCompatibilityMode", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/cache/HudCaching", "renderingCacheOverride", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(ifeq);
        return list;
    }

    private InsnList checkOverride() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/cache/HudCaching", "renderingCacheOverride", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }

    private InsnList addIfeq(LabelNode ifne, InsnList list) {
        list.add(ifne);
        return list;
    }

    private InsnList createToggle(LabelNode ifne, InsnList list) {
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "removeInvertFromCrosshair", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        return list;
    }

    private InsnList toggleAlpha(boolean state) {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "net/minecraft/client/renderer/GlStateManager",
            state ? "func_179141_d" : "func_179118_c", // enableAlpha | disableAlpha
            "()V",
            false));
        return list;
    }
}
