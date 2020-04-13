package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
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

            switch (methodName) {
                case "getServerData":
                case "func_78850_a":
                    methodNode.instructions.clear();
                    methodNode.localVariables.clear();
                    methodNode.instructions.insert(getCleanServerData());
                    break;
                case "removeServerData":
                case "func_78851_b":
                    methodNode.instructions.clear();
                    methodNode.localVariables.clear();
                    methodNode.instructions.insert(removeCleanServerData());
                    break;
                case "addServerData":
                case "func_78849_a":
                    methodNode.instructions.clear();
                    methodNode.localVariables.clear();
                    methodNode.instructions.insert(addCleanServerData());
                    break;
                case "swapServers":
                case "func_78857_a":
                    methodNode.instructions.clear();
                    methodNode.localVariables.clear();
                    methodNode.instructions.insert(swapCleanServers());
                    break;
                case "func_147413_a":
                    methodNode.instructions.clear();
                    methodNode.localVariables.clear();
                    methodNode.instructions.insert(setClean());
                    break;
            }
        }
    }

    private InsnList setClean() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "club/sk1er/patcher/hooks/ServerListHook",
            "set",
            "(Lnet/minecraft/client/multiplayer/ServerList;ILnet/minecraft/client/multiplayer/ServerData;)V",
            false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList swapCleanServers() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new VarInsnNode(Opcodes.ILOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "club/sk1er/patcher/hooks/ServerListHook",
            "swapServers",
            "(Lnet/minecraft/client/multiplayer/ServerList;II)V",
            false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList addCleanServerData() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "club/sk1er/patcher/hooks/ServerListHook",
            "addServerData",
            "(Lnet/minecraft/client/multiplayer/ServerList;Lnet/minecraft/client/multiplayer/ServerData;)V",
            false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList removeCleanServerData() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "club/sk1er/patcher/hooks/ServerListHook",
            "removeServerData",
            "(Lnet/minecraft/client/multiplayer/ServerList;I)V",
            false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList getCleanServerData() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "club/sk1er/patcher/hooks/ServerListHook",
            "getServerData",
            "(Lnet/minecraft/client/multiplayer/ServerList;I)Lnet/minecraft/client/multiplayer/ServerData;",
            false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }
}
