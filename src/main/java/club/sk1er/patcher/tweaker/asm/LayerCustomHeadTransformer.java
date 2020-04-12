package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class LayerCustomHeadTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerCustomHead"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);
            if (methodName.equals("doRenderLayer") || methodName.equals("func_177141_a")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.INVOKESTATIC) {
                        String methodCallName = mapMethodNameFromNode((MethodInsnNode) node);
                        if (methodCallName.equals("readGameProfileFromNBT")
                            || methodCallName.equals("func_152459_a")) {
                            AbstractInsnNode next = node.getNext().getNext();
                            if (next instanceof JumpInsnNode) {
                                method.instructions.insertBefore(
                                    node, ifAlreadyCached(((JumpInsnNode) next).label));
                                method.instructions.insert(node, saveCache());
                                iterator.next();
                                iterator.remove();
                                break;
                            }
                        }
                    }
                }

                break;
            }
        }
    }

    private InsnList saveCache() {
        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ASTORE, 14));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 15));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 14));
        insnList.add(
            new FieldInsnNode(
                Opcodes.PUTFIELD,
                "net/minecraft/nbt/NBTTagCompound",
                "profile",
                "Lcom/mojang/authlib/GameProfile;"));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 15));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 18));
        insnList.add(
            new FieldInsnNode(
                Opcodes.PUTFIELD,
                "net/minecraft/nbt/NBTTagCompound",
                "compound",
                "Lnet/minecraft/nbt/NBTTagCompound;"));
        return insnList;
    }

    private InsnList ifAlreadyCached(LabelNode after) {
        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ASTORE, 18));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 18));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 15));
        insnList.add(
            new FieldInsnNode(
                Opcodes.GETFIELD,
                "net/minecraft/nbt/NBTTagCompound",
                "compound",
                "Lnet/minecraft/nbt/NBTTagCompound;"));
        insnList.add(
            new MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "net/minecraft/nbt/NBTTagCompound",
                "equals",
                "(Ljava/lang/Object;)Z",
                false));
        LabelNode labelNode = new LabelNode();
        insnList.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 15));
        insnList.add(
            new FieldInsnNode(
                Opcodes.GETFIELD,
                "net/minecraft/nbt/NBTTagCompound",
                "profile",
                "Lcom/mojang/authlib/GameProfile;"));
        insnList.add(new VarInsnNode(Opcodes.ASTORE, 14));
        insnList.add(new JumpInsnNode(Opcodes.GOTO, after));
        insnList.add(labelNode);
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 18));
        return insnList;
    }
}
