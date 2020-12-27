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
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class BlockPosTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.util.BlockPos"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            String methodDesc = mapMethodDesc(methodNode);
            switch (methodName) {
                case "func_177981_b":
                case "func_177984_a":
                case "up":
                    clearInstructions(methodNode);

                    if (methodDesc.equals("()Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterDownAndUp(false, true));
                    } else if (methodDesc.equals("(I)Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterDownAndUp(true, true));
                    }

                    break;

                case "func_177977_b":
                case "func_177979_c":
                case "down":
                    clearInstructions(methodNode);

                    if (methodDesc.equals("()Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterDownAndUp(false, false));
                    } else if (methodDesc.equals("(I)Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterDownAndUp(true, false));
                    }

                    break;

                case "func_177964_d":
                case "func_177978_c":
                case "north":
                    clearInstructions(methodNode);

                    if (methodDesc.equals("()Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterNorthAndSouth(false, true));
                    } else if (methodDesc.equals("(I)Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterNorthAndSouth(true, true));
                    }

                    break;

                case "func_177968_d":
                case "func_177970_e":
                case "south":
                    clearInstructions(methodNode);

                    if (methodDesc.equals("()Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterNorthAndSouth(false, false));
                    } else if (methodDesc.equals("(I)Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterNorthAndSouth(true, false));
                    }

                    break;

                case "func_177976_e":
                case "func_177985_f":
                case "west":
                    clearInstructions(methodNode);

                    if (methodDesc.equals("()Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterWestAndEast(false, true));
                    } else if (methodDesc.equals("(I)Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterWestAndEast(true, true));
                    }

                    break;

                case "func_177965_g":
                case "func_177974_f":
                case "east":
                    clearInstructions(methodNode);

                    if (methodDesc.equals("()Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterWestAndEast(false, false));
                    } else if (methodDesc.equals("(I)Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterWestAndEast(true, false));
                    }

                    break;

                case "func_177967_a":
                case "func_177972_a":
                case "offset":
                    clearInstructions(methodNode);

                    if (methodDesc.equals("(Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterOffset(false));
                    } else if (methodDesc.equals("(Lnet/minecraft/util/EnumFacing;I)Lnet/minecraft/util/BlockPos;")) {
                        methodNode.instructions.insert(betterOffset(true));
                    }

                    break;
            }
        }
    }

    private InsnList betterOffset(boolean actualMethod) {
        InsnList list = new InsnList();

        if (actualMethod) {
            LabelNode ifne = new LabelNode();
            list.add(new VarInsnNode(Opcodes.ILOAD, 2));
            list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new InsnNode(Opcodes.ARETURN));
            list.add(ifne);
        }

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));

        if (actualMethod) {
            list.add(new VarInsnNode(Opcodes.ILOAD, 2));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                getHooksPackage("BlockPosHook"),
                "offsetFast",
                "(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;I)Lnet/minecraft/util/BlockPos;",
                false));
        } else {
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                getHooksPackage("BlockPosHook"),
                "offsetFast",
                "(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/util/BlockPos;",
                false));
        }

        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }

    private InsnList betterWestAndEast(boolean actualMethod, boolean west) {
        InsnList list = new InsnList();

        if (actualMethod) {
            LabelNode ifne = new LabelNode();
            list.add(new VarInsnNode(Opcodes.ILOAD, 1));
            list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new InsnNode(Opcodes.ARETURN));
            list.add(ifne);
        }

        list.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/util/BlockPos"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177958_n", "()I", false));

        if (actualMethod) {
            list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        } else {
            list.add(new InsnNode(Opcodes.ICONST_1));
        }

        if (west) {
            list.add(new InsnNode(Opcodes.ISUB));
        } else {
            list.add(new InsnNode(Opcodes.IADD));
        }

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177956_o", "()I", false));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177952_p", "()I", false));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/util/BlockPos", "<init>", "(III)V", false));
        list.add(new InsnNode(Opcodes.ARETURN));

        return list;
    }

    private InsnList betterNorthAndSouth(boolean actualMethod, boolean north) {
        InsnList list = new InsnList();

        if (actualMethod) {
            LabelNode ifne = new LabelNode();
            list.add(new VarInsnNode(Opcodes.ILOAD, 1));
            list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new InsnNode(Opcodes.ARETURN));
            list.add(ifne);
        }

        list.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/util/BlockPos"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177958_n", "()I", false));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177956_o", "()I", false));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177952_p", "()I", false));

        if (actualMethod) {
            list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        } else {
            list.add(new InsnNode(Opcodes.ICONST_1));
        }

        if (north) {
            list.add(new InsnNode(Opcodes.ISUB));
        } else {
            list.add(new InsnNode(Opcodes.IADD));
        }

        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/util/BlockPos", "<init>", "(III)V", false));
        list.add(new InsnNode(Opcodes.ARETURN));

        return list;
    }

    private InsnList betterDownAndUp(boolean actualMethod, boolean up) {
        InsnList list = new InsnList();

        if (actualMethod) {
            LabelNode ifne = new LabelNode();
            list.add(new VarInsnNode(Opcodes.ILOAD, 1));
            list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new InsnNode(Opcodes.ARETURN));
            list.add(ifne);
        }

        list.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/util/BlockPos"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177958_n", "()I", false));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177956_o", "()I", false));

        if (actualMethod) {
            list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        } else {
            list.add(new InsnNode(Opcodes.ICONST_1));
        }

        if (up) {
            list.add(new InsnNode(Opcodes.IADD));
        } else {
            list.add(new InsnNode(Opcodes.ISUB));
        }

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177952_p", "()I", false));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/util/BlockPos", "<init>", "(III)V", false));
        list.add(new InsnNode(Opcodes.ARETURN));

        return list;
    }
}
