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

package club.sk1er.patcher.asm.render.world.tileentity;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class TileEntityPistonRendererTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.tileentity.TileEntityPistonRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);

            if (methodName.equals("renderTileEntityAt") || methodName.equals("func_180535_a")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        String methodInsnName = mapMethodNameFromNode(next);

                        if (methodInsnName.equals("setTranslation") || methodInsnName.equals("func_178969_c")) {
                            AbstractInsnNode previous = next.getPrevious().getPrevious();
                            if (previous.getOpcode() == Opcodes.FADD) {
                                for (int nodes = 0; nodes < 34; nodes++) {
                                    method.instructions.remove(next.getPrevious());
                                }

                                method.instructions.insertBefore(next, getFixedPositionFirst());
                            } else if (previous.getOpcode() == Opcodes.FSUB) {
                                for (int nodes = 0; nodes < 22; nodes++) {
                                    method.instructions.remove(next.getPrevious());
                                }

                                method.instructions.insertBefore(next, getFixedPositionSecond());
                            }
                        }
                    }
                }

                break;
            }
        }
    }

    private InsnList getFixedPositionSecond() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 14));
        list.add(new VarInsnNode(Opcodes.DLOAD, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 10));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177958_n", "()I", false));
        list.add(new InsnNode(Opcodes.I2D));
        list.add(new InsnNode(Opcodes.DSUB));
        list.add(new VarInsnNode(Opcodes.DLOAD, 4));
        list.add(new VarInsnNode(Opcodes.ALOAD, 10));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177956_o", "()I", false));
        list.add(new InsnNode(Opcodes.I2D));
        list.add(new InsnNode(Opcodes.DSUB));
        list.add(new VarInsnNode(Opcodes.DLOAD, 6));
        list.add(new VarInsnNode(Opcodes.ALOAD, 10));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177952_p", "()I", false));
        list.add(new InsnNode(Opcodes.I2D));
        list.add(new InsnNode(Opcodes.DSUB));
        return list;
    }

    private InsnList getFixedPositionFirst() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 14));
        list.add(new VarInsnNode(Opcodes.DLOAD, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 10));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177958_n", "()I", false));
        list.add(new InsnNode(Opcodes.I2D));
        list.add(new InsnNode(Opcodes.DSUB));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.FLOAD, 8));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityPiston", "func_174929_b", "(F)F", false));
        list.add(new InsnNode(Opcodes.F2D));
        list.add(new InsnNode(Opcodes.DADD));
        list.add(new VarInsnNode(Opcodes.DLOAD, 4));
        list.add(new VarInsnNode(Opcodes.ALOAD, 10));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177956_o", "()I", false));
        list.add(new InsnNode(Opcodes.I2D));
        list.add(new InsnNode(Opcodes.DSUB));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.FLOAD, 8));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityPiston", "func_174928_c", "(F)F", false));
        list.add(new InsnNode(Opcodes.F2D));
        list.add(new InsnNode(Opcodes.DADD));
        list.add(new VarInsnNode(Opcodes.DLOAD, 6));
        list.add(new VarInsnNode(Opcodes.ALOAD, 10));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177952_p", "()I", false));
        list.add(new InsnNode(Opcodes.I2D));
        list.add(new InsnNode(Opcodes.DSUB));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.FLOAD, 8));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityPiston", "func_174926_d", "(F)F", false));
        list.add(new InsnNode(Opcodes.F2D));
        list.add(new InsnNode(Opcodes.DADD));
        return list;
    }
}
