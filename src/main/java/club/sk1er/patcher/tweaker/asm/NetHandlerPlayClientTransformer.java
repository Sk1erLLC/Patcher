package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class NetHandlerPlayClientTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.network.NetHandlerPlayClient"};
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
                case "handleResourcePack":
                case "func_175095_a":
                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), cancelIfNotSafe());
                    break;
                case "handleJoinGame":
                case "func_147282_a":
                case "handleRespawn":
                case "func_147280_a":
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode) {
                            String methodInsnName = mapMethodNameFromNode((MethodInsnNode) next);
                            if (methodInsnName.equals("displayGuiScreen") || methodInsnName.equals("func_147108_a")) {
                                for (int i = 0; i < 4; ++i) {
                                    methodNode.instructions.remove(next.getPrevious());
                                }

                                methodNode.instructions.insertBefore(next, new InsnNode(Opcodes.ACONST_NULL));
                                break;
                            }
                        }
                    }
                    break;
                case "func_147235_a":
                case "handleSpawnObject":
                    S0EPacketSpawnObjectTransformer.changeJumpNode(methodNode);
                    break;
            }
        }
    }

    private InsnList cancelIfNotSafe() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/network/play/server/S48PacketResourcePackSend",
            "func_179784_b", "()Ljava/lang/String;", false));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/network/play/server/S48PacketResourcePackSend",
            "func_179783_a", "()Ljava/lang/String;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "club/sk1er/patcher/hooks/NetHandlerPlayClientHook",
            "validateResourcePackUrl",
            "(Lnet/minecraft/client/network/NetHandlerPlayClient;Ljava/lang/String;Ljava/lang/String;)Z",
            false));
        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, labelNode));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(labelNode);
        return list;
    }
}
