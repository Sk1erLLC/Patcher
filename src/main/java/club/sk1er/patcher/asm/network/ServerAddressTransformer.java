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
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ServerAddressTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.multiplayer.ServerAddress"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);

            if (methodName.equals("getIP") || methodName.equals("func_78861_a")) {
                clearInstructions(method);
                method.instructions.insert(getIpSafely(method));
            }
        }
    }

    private InsnList getIpSafely(MethodNode method) {
        InsnList list = new InsnList();
        LabelNode start = new LabelNode(), end = new LabelNode(), handler = new LabelNode();
        method.tryCatchBlocks.add(new TryCatchBlockNode(start, end, handler, "java/lang/IllegalArgumentException"));
        list.add(start);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ServerAddress", "field_78866_a", "Ljava/lang/String;"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/net/IDN", "toASCII", "(Ljava/lang/String;)Ljava/lang/String;", false));
        list.add(end);
        list.add(new InsnNode(Opcodes.ARETURN));
        list.add(handler);
        list.add(new VarInsnNode(Opcodes.ASTORE, 1));
        list.add(new LdcInsnNode(""));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }
}
