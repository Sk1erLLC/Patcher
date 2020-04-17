package club.sk1er.patcher.tweaker.asm.optifine;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.lwjgl.input.Mouse;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Iterator;

// By LlamaLad7
public class OptifineEntityRendererTransformer implements PatcherTransformer {
    private static final float normalModifier = 4f;
    private static float currentModifier = 4f;
    public static boolean zoomed = false;

    public static float getModifier() {
        if (!PatcherConfig.scrollToZoom) {
            return normalModifier;
        }

        int moved = Mouse.getDWheel();

        if (moved > 0) {
            currentModifier += 0.25f * currentModifier;
        } else if (moved < 0) {
            currentModifier -= 0.25f * currentModifier;
        }

        if (currentModifier < 0.8) {
            currentModifier = 0.8f;
        }

        if (currentModifier > 600) {
            currentModifier = 600f;
        }

        return currentModifier;
    }

    public static void resetCurrent() {
        currentModifier = normalModifier;
    }

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.EntityRenderer"};
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

            if (methodName.equals("getFOVModifier") || methodName.equals("func_78481_a")) {
                int zoomActiveIndex = -1;

                for (LocalVariableNode var : methodNode.localVariables) {
                    if (var.name.equals("zoomActive")) {
                        zoomActiveIndex = var.index;
                        break;
                    }
                }

                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode thing = iterator.next();
                    if (checkNode(thing)) {
                        methodNode.instructions.insertBefore(thing, new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "normalZoomSensitivity", "Z")); // False instead of true
                        methodNode.instructions.insertBefore(thing, new InsnNode(Opcodes.ICONST_1));
                        methodNode.instructions.insertBefore(thing, new InsnNode(Opcodes.IXOR));
                        methodNode.instructions.insert(thing, callReset());
                        methodNode.instructions.remove(thing);
                    } else if (checkDivNode(thing)) {
                        methodNode.instructions.remove(thing.getPrevious());
                        methodNode.instructions.insertBefore(thing, getDivisor());
                    } else if (checkZoomActiveNode(thing, zoomActiveIndex)) {
                        methodNode.instructions.insertBefore(thing, setZoomed(zoomActiveIndex));
                    }
                }

                break;
            }
        }
    }

    private boolean checkNode(AbstractInsnNode node) {
        if (node.getNext() == null) return false;
        if (node.getOpcode() == Opcodes.ICONST_1) {
            AbstractInsnNode next = node.getNext();
            if (next.getOpcode() == Opcodes.PUTFIELD) {
                FieldInsnNode fieldInsn = (FieldInsnNode) next;
                return fieldInsn.name.equals("smoothCamera") || fieldInsn.name.equals("field_74326_T");
            }
        }
        return false;
    }

    private boolean checkDivNode(AbstractInsnNode node) {
        if (node.getOpcode() == Opcodes.FDIV) {
            if (node.getPrevious().getOpcode() == Opcodes.LDC) {
                LdcInsnNode prev = (LdcInsnNode) node.getPrevious();
                if (prev.cst instanceof Float) {
                    Float f = (Float) prev.cst;
                    return f.equals(4f);
                }
            }
        }
        return false;
    }

    private boolean checkZoomActiveNode(AbstractInsnNode node, int zoomActiveIndex) {
        if (node.getOpcode() == Opcodes.ILOAD) {
            VarInsnNode n = (VarInsnNode) node;
            if (n.var == zoomActiveIndex) {
                return node.getNext().getOpcode() == Opcodes.IFEQ;
            }
        }
        return false;
    }

    private InsnList getDivisor() {
        InsnList list = new InsnList();
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "club/sk1er/patcher/tweaker/asm/optifine/OptifineEntityRendererTransformer",
                "getModifier",
                "()F",
                false)); // Call my method
        return list;
    }

    private InsnList callReset() {
        InsnList list = new InsnList();
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "club/sk1er/patcher/tweaker/asm/optifine/OptifineEntityRendererTransformer",
                "resetCurrent",
                "()V",
                false)); // Call my method
        return list;
    }

    private InsnList setZoomed(int zoomActiveIndex) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ILOAD, zoomActiveIndex));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "club/sk1er/patcher/tweaker/asm/optifine/OptifineEntityRendererTransformer", "zoomed", "Z"));
        return list;
    }
}
