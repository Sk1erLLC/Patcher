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

package club.sk1er.patcher.asm.network;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;

public class LazyLoadBaseTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.util.LazyLoadBase"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);

            if (methodName.equals("getValue") || methodName.equals("func_179281_c")) {
                clearInstructions(method);
                method.instructions.insert(this.getSynchronizedValue(method));
                break;
            }
        }
    }

    private InsnList getSynchronizedValue(MethodNode method) {
        InsnList list = new InsnList();

        LabelNode startOne = new LabelNode(), endOne = new LabelNode(), handlerOne = new LabelNode();
        method.tryCatchBlocks.add(new TryCatchBlockNode(startOne, endOne, handlerOne, null));

        LabelNode startTwo = new LabelNode(), endTwo = new LabelNode(), handlerTwo = new LabelNode();
        method.tryCatchBlocks.add(new TryCatchBlockNode(startTwo, endTwo, handlerTwo, null));

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/util/LazyLoadBase", "field_179282_b", "Z"));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ASTORE, 1));
        list.add(new InsnNode(Opcodes.MONITORENTER));
        list.add(startOne);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/util/LazyLoadBase", "field_179282_b", "Z"));
        LabelNode ifne2 = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/LazyLoadBase", "func_179280_b", "()Ljava/lang/Object;", false));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/util/LazyLoadBase", "field_179283_a", "Ljava/lang/Object;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/util/LazyLoadBase", "field_179282_b", "Z"));
        list.add(ifne2);
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new InsnNode(Opcodes.MONITOREXIT));
        list.add(endOne);
        LabelNode gotoInsn = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(handlerOne);
        list.add(startTwo);
        list.add(handlerTwo);
        list.add(new VarInsnNode(Opcodes.ASTORE, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new InsnNode(Opcodes.MONITOREXIT));
        list.add(endTwo);
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new InsnNode(Opcodes.ATHROW));
        list.add(ifne);
        list.add(gotoInsn);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/util/LazyLoadBase", "field_179283_a", "Ljava/lang/Object;"));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }
}
