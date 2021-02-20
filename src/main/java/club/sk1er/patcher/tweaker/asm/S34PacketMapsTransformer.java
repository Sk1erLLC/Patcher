package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class S34PacketMapsTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.network.play.server.S34PacketMaps"};
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
            if (methodName.equals("setMapdataTo") || methodName.equals("func_179734_a")) {
                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.GETFIELD) {
                        String fieldName = mapFieldNameFromNode(node);
                        if(fieldName.equals("mapMaxX") || fieldName.equals("field_179735_f")) {
                            methodNode.instructions.insertBefore(
                                node.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious(),
                                checkMapBytesLength()
                            );
                            break;
                        }
                    }
                }
            }
        }
    }

    public static InsnList checkMapBytesLength() {
        InsnList list = new InsnList();

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(
            Opcodes.GETFIELD,
            "net/minecraft/network/play/server/S34PacketMaps",
            "field_179741_h",
            "[B"
        ));
        list.add(new InsnNode(Opcodes.ARRAYLENGTH));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifne);

        return list;
    }
}
