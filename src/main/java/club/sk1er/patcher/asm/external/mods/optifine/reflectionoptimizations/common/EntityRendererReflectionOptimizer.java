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

package club.sk1er.patcher.asm.external.mods.optifine.reflectionoptimizations.common;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class EntityRendererReflectionOptimizer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.EntityRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            final String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("updateCameraAndRender") || methodName.equals("func_181560_a")) {
                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("exists")) {
                        next = next.getPrevious();

                        for (int i = 0; i < 29; ++i) {
                            methodNode.instructions.remove(next.getNext());
                        }

                        methodNode.instructions.insertBefore(next, optimizeReflection());
                        methodNode.instructions.remove(next);
                        break;
                    }
                }

                break;
            }
        }
    }

    private InsnList optimizeReflection() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "field_78531_r", "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71462_r", "Lnet/minecraft/client/gui/GuiScreen;"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 8));
        list.add(new VarInsnNode(Opcodes.ILOAD, 9));
        list.add(new VarInsnNode(Opcodes.FLOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/ForgeHooksClient", "drawScreen", "(Lnet/minecraft/client/gui/GuiScreen;IIF)V", false));
        return list;
    }
}
