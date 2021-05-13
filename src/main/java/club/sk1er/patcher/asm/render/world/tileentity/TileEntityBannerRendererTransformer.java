package club.sk1er.patcher.asm.render.world.tileentity;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class TileEntityBannerRendererTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("renderTileEntityAt") || methodName.equals("func_180535_a")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        final String methodInsnName = mapMethodNameFromNode(next);
                        if (methodInsnName.equals("getTotalWorldTime") || methodInsnName.equals("func_82737_E")) {
                            method.instructions.insertBefore(next.getNext(), fixTime());
                            break;
                        }
                    }
                }
            } else if (methodName.equals("func_178463_a")) {
                clearInstructions(method);
                method.instructions.insert(fixedBannerCache());
            }
        }
    }

    private InsnList fixedBannerCache() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("TileEntityBannerRendererHook"),
            "getPatternResourceLocation", "(Lnet/minecraft/tileentity/TileEntityBanner;)Lnet/minecraft/util/ResourceLocation;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }

    private InsnList fixTime() {
        InsnList list = new InsnList();
        list.add(new LdcInsnNode(100L));
        list.add(new InsnNode(Opcodes.LREM));
        return list;
    }
}
