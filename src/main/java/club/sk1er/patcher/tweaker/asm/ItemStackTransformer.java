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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.util.ListIterator;

public class ItemStackTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.item.ItemStack"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("getTooltip") || methodName.equals("func_82840_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.LDC && ((LdcInsnNode) node).cst.equals("Color: #")) {
                        final AbstractInsnNode next = node.getNext().getNext().getNext().getNext().getNext();
                        methodNode.instructions.insert(next, fixHexColorPrintingEnd());
                        methodNode.instructions.remove(next);
                        methodNode.instructions.insert(node.getNext(), fixHexColorPrintingBeginning());
                        return;
                    }
                }
            }
        }
    }

    private InsnList fixHexColorPrintingBeginning() {
        InsnList list = new InsnList();
        list.add(new LdcInsnNode("%06X"));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new InsnNode(Opcodes.ICONST_0));
        return list;
    }

    private InsnList fixHexColorPrintingEnd() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));
        list.add(new InsnNode(Opcodes.AASTORE));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/String", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false));
        return list;
    }
}
