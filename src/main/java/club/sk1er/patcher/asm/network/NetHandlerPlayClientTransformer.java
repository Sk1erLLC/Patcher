package club.sk1er.patcher.asm.network;

import club.sk1er.patcher.asm.network.packet.S0EPacketSpawnObjectTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

import static org.objectweb.asm.Opcodes.*;

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
                //#if MC==10809
                case "func_147235_a":
                case "handleSpawnObject":
                    S0EPacketSpawnObjectTransformer.changeJumpNode(methodNode);
                    break;
                //#endif

                case "func_147237_a":
                case "handleSpawnPlayer":
                    this.fixHandleSpawnPlayerNPE(methodNode);
                    break;

                case "func_147240_a":
                case "handleCustomPayload": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == INVOKEVIRTUAL) {
                            String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("release")) {
                                LabelNode ifeq = new LabelNode();
                                methodNode.instructions.insertBefore(next.getPrevious(), createList(ifeq));
                                methodNode.instructions.insertBefore(next.getNext().getNext(), ifeq);
                            }
                        }
                    }

                    break;
                }
            }
        }
    }

    private void fixHandleSpawnPlayerNPE(MethodNode methodNode) {
        final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        while (iterator.hasNext()) {
            final AbstractInsnNode insnNode = iterator.next();
            if (insnNode instanceof MethodInsnNode && insnNode.getOpcode() == INVOKEVIRTUAL) {
                String methodInsnName = mapMethodNameFromNode(insnNode);
                if (methodInsnName.equals("getPlayerInfo") || methodInsnName.equals("func_175102_a")) {
                    final InsnList list = new InsnList();
                    list.add(new InsnNode(DUP));
                    LabelNode label = new LabelNode();
                    list.add(new JumpInsnNode(IFNONNULL, label));
                    list.add(new InsnNode(RETURN));
                    list.add(label);
                    methodNode.instructions.insert(insnNode, list);
                }
            }
        }
    }

    public static InsnList createList(LabelNode ifeq) {
        InsnList list = new InsnList();
        list.add(new InsnNode(ICONST_0));
        list.add(new JumpInsnNode(IFEQ, ifeq));
        return list;
    }
}
