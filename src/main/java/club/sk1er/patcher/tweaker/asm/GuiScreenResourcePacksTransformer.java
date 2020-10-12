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

package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import net.minecraftforge.fml.common.Loader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class GuiScreenResourcePacksTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiScreenResourcePacks"};
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

            if ((methodName.equals("drawScreen") || methodName.equals("func_73863_a")) && !Loader.isModLoaded("ResourcePackOrganizer")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof IntInsnNode && ((IntInsnNode) next).operand == 77) {
                        ((IntInsnNode) next).operand = 102;
                        break;
                    }
                }
            } else if (methodName.equals("actionPerformed") || methodName.equals("func_146284_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        String methodInsnName = mapMethodNameFromNode(next);
                        if (methodInsnName.equals("saveOptions") || methodInsnName.equals("func_74303_b")) {
                            methodNode.instructions.insertBefore(next.getNext(), new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "club/sk1er/patcher/hooks/FallbackResourceManagerHook",
                                "clearCache",
                                "()V",
                                false));
                            break;
                        }
                    }
                }
            }
        }
    }
}
