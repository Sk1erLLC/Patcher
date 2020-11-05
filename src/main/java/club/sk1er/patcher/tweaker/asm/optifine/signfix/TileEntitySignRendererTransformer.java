package club.sk1er.patcher.tweaker.asm.optifine.signfix;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class TileEntitySignRendererTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.tileentity.TileEntitySignRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("isRenderText")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                final LabelNode ifacmpeq = new LabelNode();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();

                    if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETFIELD) {
                        final String fieldInsnName = mapFieldNameFromNode(next);

                        if (fieldInsnName.equals("lineBeingEdited") || fieldInsnName.equals("field_145918_i")) {
                            method.instructions.insertBefore(next.getNext().getNext(), checkSign(ifacmpeq));
                        }
                    }
                }

                method.instructions.insertBefore(method.instructions.getLast().getPrevious().getPrevious(), ifacmpeq);
            }
        }
    }

    private InsnList checkSign(LabelNode ifacmpeq) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/gui/inventory/GuiEditSign", "currentlyEditedSign", "Lnet/minecraft/tileentity/TileEntitySign;"));
        list.add(new JumpInsnNode(Opcodes.IF_ACMPEQ, ifacmpeq));
        return list;
    }
}
