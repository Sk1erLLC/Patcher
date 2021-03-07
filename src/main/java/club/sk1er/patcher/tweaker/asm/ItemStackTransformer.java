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
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class ItemStackTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.item.ItemStack"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "cachedDisplayName", "Ljava/lang/String;", null, null));

        for (MethodNode methodNode : classNode.methods) {
            final String methodName = mapMethodName(classNode, methodNode);
            switch (methodName) {
                case "getTooltip":
                case "func_82840_a":
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == Opcodes.LDC && ((LdcInsnNode) node).cst.equals("Color: #")) {
                            final AbstractInsnNode next = node.getNext().getNext().getNext().getNext().getNext();
                            methodNode.instructions.insert(next, fixHexColorPrintingEnd());
                            methodNode.instructions.remove(next);
                            methodNode.instructions.insert(node.getNext(), fixHexColorPrintingBeginning());
                            break;
                        }
                    }
                    break;

                case "getDisplayName":
                case "func_82833_r":
                    methodNode.instructions.insert(returnCachedDisplayName());
                    methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), setCachedDisplayName());
                    break;

                case "setStackDisplayName":
                case "func_151001_c":
                    methodNode.instructions.insert(resetCachedDisplayName());
                    break;
            }
        }
    }

    private InsnList resetCachedDisplayName() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/item/ItemStack", "cachedDisplayName", "Ljava/lang/String;"));
        LabelNode ifnull = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNULL, ifnull));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ACONST_NULL));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/item/ItemStack", "cachedDisplayName", "Ljava/lang/String;"));
        list.add(ifnull);
        return list;
    }

    private InsnList setCachedDisplayName() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/item/ItemStack", "cachedDisplayName", "Ljava/lang/String;"));
        return list;
    }

    private InsnList returnCachedDisplayName() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/item/ItemStack", "cachedDisplayName", "Ljava/lang/String;"));
        LabelNode ifnull = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNULL, ifnull));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/item/ItemStack", "cachedDisplayName", "Ljava/lang/String;"));
        list.add(new InsnNode(Opcodes.ARETURN));
        list.add(ifnull);
        return list;
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
