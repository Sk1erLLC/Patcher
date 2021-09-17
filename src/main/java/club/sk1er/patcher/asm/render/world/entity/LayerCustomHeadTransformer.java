package club.sk1er.patcher.asm.render.world.entity;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

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
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("doRenderLayer") || methodName.equals("func_177141_a")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.INVOKESTATIC) {
                        final String methodCallName = mapMethodNameFromNode(node);
                        if (methodCallName.equals("readGameProfileFromNBT") || methodCallName.equals("func_152459_a")) {
                            final AbstractInsnNode next = node.getNext().getNext();
                            if (next instanceof JumpInsnNode) {
                                method.instructions.insertBefore(node, ifAlreadyCached(((JumpInsnNode) next).label));
                                method.instructions.insert(node, saveCache());
                                iterator.next();
                                iterator.remove();
                            }
                        }
                    } else if (node instanceof VarInsnNode && node.getOpcode() == Opcodes.ILOAD && ((VarInsnNode) node).var == 12 && node.getNext().getOpcode() == Opcodes.IFNE) {
                        method.instructions.insertBefore(node, checkEntityType());
                        method.instructions.remove(node);
                    }
                }

                break;
            }
        }
    }

    private InsnList checkEntityType() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new TypeInsnNode(Opcodes.INSTANCEOF, "net/minecraft/entity/passive/EntityVillager"));
        return list;
    }

    private InsnList saveCache() {
        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ASTORE, 14));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 15));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 14));
        insnList.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/nbt/NBTTagCompound", "profile", "Lcom/mojang/authlib/GameProfile;"));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 15));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 18));
        insnList.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/nbt/NBTTagCompound", "compound", "Lnet/minecraft/nbt/NBTTagCompound;"));
        return insnList;
    }

    private InsnList ifAlreadyCached(LabelNode after) {
        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ASTORE, 18));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 18));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 15));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/nbt/NBTTagCompound", "compound", "Lnet/minecraft/nbt/NBTTagCompound;"));
        insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "equals", "(Ljava/lang/Object;)Z", false));
        LabelNode ifeq = new LabelNode();
        insnList.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 15));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/nbt/NBTTagCompound", "profile", "Lcom/mojang/authlib/GameProfile;"));
        insnList.add(new VarInsnNode(Opcodes.ASTORE, 14));
        insnList.add(new JumpInsnNode(Opcodes.GOTO, after));
        insnList.add(ifeq);
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 18));
        return insnList;
    }
}
