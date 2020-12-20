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

import club.sk1er.patcher.hooks.ForgeHooksClientHook;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class ForgeHooksClientTransformer implements PatcherTransformer {

    private final String hooksClass = getHooksPackage("ForgeHooksClientHook");

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.ForgeHooksClient"};
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
            final String methodName = methodNode.name;
            if (methodName.equals("getSkyBlendColour")) {
                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        final String methodInsnName = mapMethodNameFromNode(next);
                        if (methodInsnName.equals("getY") || methodInsnName.equals("func_177956_o")) {
                            ((MethodInsnNode) next).name = "func_177952_p"; // getZ
                            break;
                        }
                    }
                }
            } else if (methodName.equals("drawScreen")) {
                methodNode.instructions.insert(redirectMouse());
                methodNode.instructions.insertBefore(
                    methodNode.instructions.getLast().getPrevious(),
                    addReturn()
                );
            }
        }
    }

    private InsnList addReturn() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, hooksClass, "drawScreenReturn", "(Lnet/minecraft/client/gui/GuiScreen;)V", false));
        return list;
    }

    private InsnList redirectMouse() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, hooksClass, "drawScreenHead", "(Lnet/minecraft/client/gui/GuiScreen;)V", false));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, hooksClass, "drawScreenMouseX", "(I)I", false));
        list.add(new VarInsnNode(Opcodes.ISTORE, 1));
        list.add(new VarInsnNode(Opcodes.ILOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, hooksClass, "drawScreenMouseY", "(I)I", false));
        list.add(new VarInsnNode(Opcodes.ISTORE, 2));
        return list;
    }
}
