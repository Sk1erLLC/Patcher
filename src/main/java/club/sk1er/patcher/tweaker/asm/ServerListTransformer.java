package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ServerListTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.multiplayer.ServerList"};
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

            if (methodName.equals("getServerData") || methodName.equals("func_78850_a")) {
                methodNode.instructions.clear();
                methodNode.localVariables.clear();
                methodNode.instructions.insert(getCleanServerData(methodNode));
            }

            if (methodName.equals("removeServerData") || methodName.equals("func_78851_b")) {
                methodNode.instructions.clear();
                methodNode.localVariables.clear();
                methodNode.instructions.insert(removeCleanServerData(methodNode));
            }

            if (methodName.equals("addServerData") || methodName.equals("func_78849_a")) {
                methodNode.instructions.clear();
                methodNode.localVariables.clear();
                methodNode.instructions.insert(addCleanServerData(methodNode));
            }

            if (methodName.equals("swapServers") || methodName.equals("func_78857_a")) {
                methodNode.instructions.clear();
                methodNode.localVariables.clear();
                methodNode.instructions.insert(swapCleanServers(methodNode));
            }

            if (methodName.equals("func_147413_a")) {
                methodNode.instructions.clear();
                methodNode.localVariables.clear();
                methodNode.instructions.insert(func_147413_aClean(methodNode));
            }
        }
    }

    private InsnList func_147413_aClean(MethodNode methodNode) {
        InsnList list = new InsnList();
        LabelNode l0 = new LabelNode();
        LabelNode l1 = new LabelNode();
        LabelNode l2 = new LabelNode();
        methodNode.tryCatchBlocks.add(new TryCatchBlockNode(l0, l1, l2, "java/lang/Exception"));
        list.add(l0);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ServerList", "field_78858_b", // servers
            "Ljava/util/List;"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "set", "(ILjava/lang/Object;)Ljava/lang/Object;", true));
        list.add(new InsnNode(Opcodes.POP));
        list.add(l1);
        LabelNode l3 = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, l3));
        list.add(l2);
        list.add(new VarInsnNode(Opcodes.ASTORE, 3));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ServerList", "field_147415_a", // logger
            "Lorg/apache/logging/log4j/Logger;"));
        list.add(new LdcInsnNode("Failed to set server data"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;Ljava/lang/Throwable;)V", true));
        list.add(l3);
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList swapCleanServers(MethodNode methodNode) {
        InsnList list = new InsnList();
        LabelNode l0 = new LabelNode();
        LabelNode l1 = new LabelNode();
        LabelNode l2 = new LabelNode();
        methodNode.tryCatchBlocks.add(new TryCatchBlockNode(l0, l1, l2, "java/lang/Exception"));
        list.add(l0);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/multiplayer/ServerList", "func_78850_a", // getServerData
            "(I)Lnet/minecraft/client/multiplayer/ServerData;", false));
        list.add(new VarInsnNode(Opcodes.ASTORE, 3));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ServerList", "field_78858_b", // servers
            "Ljava/util/List;"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/multiplayer/ServerList", "func_78850_a", // getServerData
            "(I)Lnet/minecraft/client/multiplayer/ServerData;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "set", "(ILjava/lang/Object;)Ljava/lang/Object;", true));
        list.add(new InsnNode(Opcodes.POP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ServerList", "field_78858_b", // servers
            "Ljava/util/List;"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "set", "(ILjava/lang/Object;)Ljava/lang/Object;", true));
        list.add(new InsnNode(Opcodes.POP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/multiplayer/ServerList", "func_78855_b", // saveServerList
            "()V", false));
        list.add(l1);
        LabelNode l6 = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, l6));
        list.add(l2);
        list.add(new VarInsnNode(Opcodes.ASTORE, 3));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ServerList", "field_147415_a", // logger
            "Lorg/apache/logging/log4j/Logger;"));
        list.add(new LdcInsnNode("Failed to swap servers"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;Ljava/lang/Throwable;)V", true));
        list.add(l6);
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList addCleanServerData(MethodNode methodNode) {
        InsnList list = new InsnList();
        LabelNode l0 = new LabelNode();
        LabelNode l1 = new LabelNode();
        LabelNode l2 = new LabelNode();
        methodNode.tryCatchBlocks.add(new TryCatchBlockNode(l0, l1, l2, "java/lang/Exception"));
        list.add(l0);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ServerList", "field_78858_b", // servers
            "Ljava/util/List;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true));
        list.add(new InsnNode(Opcodes.POP));
        list.add(l1);
        LabelNode l3 = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, l3));
        list.add(l2);
        list.add(new VarInsnNode(Opcodes.ASTORE, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ServerList", "field_147415_a", // logger
            "Lorg/apache/logging/log4j/Logger;"));
        list.add(new LdcInsnNode("Failed to remove server data"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;Ljava/lang/Throwable;)V", true));
        list.add(l3);
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList removeCleanServerData(MethodNode methodNode) {
        InsnList list = new InsnList();
        LabelNode l0 = new LabelNode();
        LabelNode l1 = new LabelNode();
        LabelNode l2 = new LabelNode();
        methodNode.tryCatchBlocks.add(new TryCatchBlockNode(l0, l1, l2, "java/lang/Exception"));
        list.add(l0);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ServerList", "field_78858_b", // servers
            "Ljava/util/List;"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "remove", "(I)Ljava/lang/Object;", true));
        list.add(new InsnNode(Opcodes.POP));
        list.add(l1);
        LabelNode l3 = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, l3));
        list.add(l2);
        list.add(new VarInsnNode(Opcodes.ASTORE, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ServerList", "field_147415_a", // logger
            "Lorg/apache/logging/log4j/Logger;"));
        list.add(new LdcInsnNode("Failed to remove server data"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;Ljava/lang/Throwable;)V", true));
        list.add(l3);
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList getCleanServerData(MethodNode methodNode) {
        InsnList list = new InsnList();
        LabelNode l0 = new LabelNode();
        LabelNode l1 = new LabelNode();
        LabelNode l2 = new LabelNode();
        methodNode.tryCatchBlocks.add(new TryCatchBlockNode(l0, l1, l2, "java/lang/Exception"));
        list.add(l0);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ServerList", "field_78858_b", // servers
            "Ljava/util/List;"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;", true));
        list.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/client/multiplayer/ServerData"));
        list.add(l1);
        list.add(new InsnNode(Opcodes.ARETURN));
        list.add(l2);
        list.add(new VarInsnNode(Opcodes.ASTORE, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ServerList", "field_147415_a", // logger
            "Lorg/apache/logging/log4j/Logger;"));
        list.add(new LdcInsnNode("Failed to get server data"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;Ljava/lang/Throwable;)V", true));
        list.add(new InsnNode(Opcodes.ACONST_NULL));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }
}
