package club.sk1er.patcher.asm.client;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class MinecraftTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.Minecraft"};
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
            final String methodName = mapMethodName(classNode, methodNode);
            final String methodDesc = mapMethodDesc(methodNode);

            switch (methodName) {
                case "<init>":
                    methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), createMetricsData());
                    break;

                case "loadWorld":
                case "func_71353_a":
                    if (methodDesc.equals("(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V")) {
                        methodNode.instructions.insert(clearLoadedMaps());
                        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                        while (iterator.hasNext()) {
                            AbstractInsnNode node = iterator.next();

                            if (node instanceof MethodInsnNode && node.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) node).owner.equals("java/lang/System")) {
                                methodNode.instructions.insertBefore(node, setSystemTime());
                            } else if (node instanceof FieldInsnNode && node.getOpcode() == Opcodes.PUTFIELD) {
                                String fieldInsnName = mapFieldNameFromNode(node);

                                if (fieldInsnName.equals("theWorld") || fieldInsnName.equals("field_71441_e")) {
                                    methodNode.instructions.insertBefore(node.getNext(), new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "net/minecraftforge/client/MinecraftForgeClient",
                                        "clearRenderCache",
                                        "()V",
                                        false));
                                }
                            }
                        }
                    }
                    break;

                case "displayGuiScreen":
                case "func_147108_a": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("clearChatMessages") || methodInsnName.equals("func_146231_a")) {
                                for (int i = 0; i < 3; i++) {
                                    methodNode.instructions.remove(next.getPrevious());
                                }

                                methodNode.instructions.remove(next);
                                break;
                            }
                        }
                    }
                    break;
                }

                case "runTick":
                case "func_71407_l": {
                    boolean foundFirst = false;
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    LabelNode ifne = new LabelNode();
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == Opcodes.GETFIELD) {
                            FieldInsnNode fieldInsnNode = (FieldInsnNode) node;
                            String fieldInsnName = mapFieldNameFromNode(fieldInsnNode);
                            if (fieldInsnName.equals("thirdPersonView") || fieldInsnName.equals("field_74320_O")) {
                                if (node.getNext().getOpcode() == Opcodes.IFNE) {
                                    AbstractInsnNode prevNode = node.getPrevious().getPrevious();
                                    methodNode.instructions.insertBefore(prevNode, getPatcherSetting("keepShadersOnPerspectiveChange", "Z"));
                                    methodNode.instructions.insertBefore(prevNode, new JumpInsnNode(Opcodes.IFNE, ifne));
                                }
                            }
                        } else if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            String methodInsnName = mapMethodNameFromNode(node);
                            if ("loadEntityShader".equals(methodInsnName) || "func_175066_a".equals(methodInsnName)) {
                                if (!foundFirst) {
                                    foundFirst = true;
                                } else {
                                    methodNode.instructions.insert(node, ifne);
                                }
                            }
                        }
                    }
                    break;
                }

                case "setIngameFocus":
                case "func_71381_h": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == Opcodes.ICONST_1) {
                            LabelNode ifeq = new LabelNode();
                            methodNode.instructions.insertBefore(node, getPatcherSetting("newKeybindHandling", "Z"));
                            methodNode.instructions.insertBefore(node, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/Minecraft", "field_142025_a", "Z"));
                            methodNode.instructions.insertBefore(node, new InsnNode(Opcodes.ICONST_1));
                            methodNode.instructions.insertBefore(node, new InsnNode(Opcodes.IXOR));
                            methodNode.instructions.insertBefore(node, new InsnNode(Opcodes.IAND));
                            methodNode.instructions.insertBefore(node, new JumpInsnNode(Opcodes.IFEQ, ifeq));
                            methodNode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("MinecraftHook"), "updateKeyBindState", "()V", false));
                            methodNode.instructions.insertBefore(node, ifeq);
                            break;
                        }
                    }
                    break;
                }

                case "runGameLoop":
                case "func_71411_J": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals("stream")) {
                            for (int i = 0; i < 33; ++i) {
                                methodNode.instructions.remove(next.getNext());
                            }

                            methodNode.instructions.remove(next.getPrevious().getPrevious());
                            methodNode.instructions.remove(next.getPrevious());
                            methodNode.instructions.remove(next);
                        }
                    }

                    iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("addFrame") || methodInsnName.equals("func_181747_a")) {
                                methodNode.instructions.insertBefore(next.getNext().getNext(), pushMetricsSample());
                            }
                        }
                    }

                    break;
                }
            }
        }
    }

    private InsnList clearLoadedMaps() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71441_e", "Lnet/minecraft/client/multiplayer/WorldClient;"));
        LabelNode ifacmpeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ACMPEQ, ifacmpeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71460_t", "Lnet/minecraft/client/renderer/EntityRenderer;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/EntityRenderer", "func_147701_i", "()Lnet/minecraft/client/gui/MapItemRenderer;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/gui/MapItemRenderer", "func_148249_a", "()V", false));
        list.add(ifacmpeq);
        return list;
    }

    private InsnList pushMetricsSample() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getHookClass("MinecraftHook"), "metricsData", "Lclub/sk1er/patcher/screen/render/overlay/metrics/MetricsData;"));
        list.add(new VarInsnNode(Opcodes.LLOAD, 7));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_181543_z", "J"));
        list.add(new InsnNode(Opcodes.LSUB));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "club/sk1er/patcher/screen/render/overlay/metrics/MetricsData", "pushSample", "(J)V", false));
        return list;
    }

    private InsnList createMetricsData() {
        InsnList list = new InsnList();
        list.add(new TypeInsnNode(Opcodes.NEW, "club/sk1er/patcher/screen/render/overlay/metrics/MetricsData"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "club/sk1er/patcher/screen/render/overlay/metrics/MetricsData", "<init>", "()V", false));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, getHookClass("MinecraftHook"), "metricsData", "Lclub/sk1er/patcher/screen/render/overlay/metrics/MetricsData;"));
        return list;
    }

    private InsnList setSystemTime() {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("optimizedWorldSwapping", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.LCONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/Minecraft",
            "field_71423_H", // systemTime
            "J"));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }
}
