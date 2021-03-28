/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.tweaker.asm;

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

                case "startGame":
                case "func_71384_a":
                    methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), toggleGLErrorChecking());
                    break;

                case "func_71354_a":
                case "setDimensionAndSpawnPlayer": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("updateWatchedObjectsFromList") || methodInsnName.equals("func_75687_a")) {
                                methodNode.instructions.insertBefore(next.getNext(), fixAttributeMap());
                                break;
                            }
                        }
                    }
                    break;
                }

                case "func_90020_K":
                case "getLimitFramerate":
                    methodNode.instructions.insert(changeActiveFramerate());
                    break;

                case "func_147107_h":
                case "isFramerateLimitBelowMax":
                    methodNode.instructions.insert(useCustomFrameLimit());
                    break;

                case "toggleFullscreen":
                case "func_71352_k": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode node = iterator.next();
                        if (node instanceof MethodInsnNode && ((MethodInsnNode) node).name.equals("setFullscreen")) {
                            methodNode.instructions.insertBefore(node.getPrevious().getPrevious(), resetScreenState());
                            break;
                        }
                    }
                    InsnList insnList = new InsnList();
                    LabelNode labelNode = new LabelNode();
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("MinecraftHook"), "fullscreen", "()Z", false));
                    insnList.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
                    insnList.add(new InsnNode(Opcodes.RETURN));
                    insnList.add(labelNode);
                    methodNode.instructions.insert(insnList);
                    break;
                }

                case "loadWorld":
                case "func_71353_a":
                    if (methodDesc.equals("(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V")) {
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
                                    methodNode.instructions.insertBefore(prevNode, new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "keepShadersOnPerspectiveChange", "Z"));
                                    methodNode.instructions.insertBefore(prevNode, new JumpInsnNode(Opcodes.IFNE, ifne));
                                }
                            }
                        } else if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            MethodInsnNode methodInsnNode = (MethodInsnNode) node;
                            String methodInsnName = mapMethodNameFromNode(methodInsnNode);
                            switch (methodInsnName) {
                                case "loadEntityShader":
                                case "func_175066_a":
                                    if (!foundFirst) {
                                        foundFirst = true;
                                    } else {
                                        methodNode.instructions.insert(node, ifne);
                                    }
                                    break;

                                case "refreshResources":
                                case "func_110436_a":
                                    methodNode.instructions.insertBefore(methodInsnNode.getPrevious().getPrevious(), new MethodInsnNode(
                                        Opcodes.INVOKESTATIC,
                                        getHookClass("FallbackResourceManagerHook"),
                                        "clearCache",
                                        "()V",
                                        false
                                    ));
                                    break;
                            }
                        } else if (node instanceof IntInsnNode && node.getOpcode() == Opcodes.BIPUSH) {
                            final int operand = ((IntInsnNode) node).operand;
                            if (operand == 62) {
                                methodNode.instructions.insertBefore(node, getKeybind("getClearShaders"));
                                methodNode.instructions.remove(node);
                            } else if (operand == 59) {
                                methodNode.instructions.insertBefore(node, getKeybind("getHideScreen"));
                                methodNode.instructions.remove(node);
                            } else if (operand == 61 && !(node.getNext() instanceof MethodInsnNode)) {
                                methodNode.instructions.insertBefore(node, getKeybind("getDebugView"));
                                methodNode.instructions.remove(node);
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
                            methodNode.instructions.insertBefore(node, new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "newKeybindHandling", "Z"));
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

                case "dispatchKeypresses":
                case "func_152348_aa": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESTATIC && next.getNext().getOpcode() == Opcodes.GOTO) {
                            if (((MethodInsnNode) next).name.equals("getEventCharacter")) {
                                methodNode.instructions.insert(next, keybindFixer());
                                break;
                            }
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

                case "func_71371_a":
                case "launchIntegratedServer": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL && next.getPrevious().getOpcode() == Opcodes.CHECKCAST) {
                            String methodInsnName = mapMethodNameFromNode(next);

                            if (methodInsnName.equals("displayGuiScreen") || methodInsnName.equals("func_147108_a")) {
                                methodNode.instructions.remove(next.getPrevious());
                                methodNode.instructions.remove(next.getPrevious());
                                methodNode.instructions.insertBefore(next, displayWorkingScreen());
                                break;
                            }
                        }
                    }

                    break;
                }
            }
        }
    }

    private InsnList useCustomFrameLimit() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "customFpsLimit", "I"));
        LabelNode ifle = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFLE, ifle));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(ifle);
        return list;
    }

    private InsnList pushMetricsSample() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getHookClass("MinecraftHook"), "metricsData", "Lclub/sk1er/patcher/metrics/MetricsData;"));
        list.add(new VarInsnNode(Opcodes.LLOAD, 7));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_181543_z", "J"));
        list.add(new InsnNode(Opcodes.LSUB));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "club/sk1er/patcher/metrics/MetricsData", "pushSample", "(J)V", false));
        return list;
    }

    private InsnList createMetricsData() {
        InsnList list = new InsnList();
        list.add(new TypeInsnNode(Opcodes.NEW, "club/sk1er/patcher/metrics/MetricsData"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "club/sk1er/patcher/metrics/MetricsData", "<init>", "()V", false));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, getHookClass("MinecraftHook"), "metricsData", "Lclub/sk1er/patcher/metrics/MetricsData;"));
        return list;
    }

    private InsnList fixAttributeMap() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 4));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("MinecraftHook"), "fixAttributeMap", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false));
        return list;
    }

    private InsnList getKeybind(String keybindName) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/Patcher", "instance", "Lclub/sk1er/patcher/Patcher;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "club/sk1er/patcher/Patcher", keybindName, "()Lnet/minecraft/client/settings/KeyBinding;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/settings/KeyBinding", "func_151463_i", "()I", false));
        return list;
    }

    private InsnList changeActiveFramerate() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/Display", "isActive", "()Z", false));
        LabelNode label = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, label));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "unfocusedFPS", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, label));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "unfocusedFPSAmount", "I"));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(label);
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "customFpsLimit", "I"));
        LabelNode ifle = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFLE, ifle));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "customFpsLimit", "I"));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(ifle);
        return list;
    }

    private InsnList displayWorkingScreen() {
        InsnList list = new InsnList();
        list.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/client/gui/GuiScreenWorking"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/gui/GuiScreenWorking", "<init>", "()V", false));
        return list;
    }

    private InsnList keybindFixer() {
        InsnList list = new InsnList();
        list.add(new IntInsnNode(Opcodes.SIPUSH, 256));
        list.add(new InsnNode(Opcodes.IADD));
        return list;
    }

    private InsnList toggleGLErrorChecking() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD,
            "net/minecraft/client/Minecraft",
            "field_175619_R", // enableGLErrorChecking
            "Z"));
        return list;
    }

    private InsnList setSystemTime() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "instantWorldSwapping", "Z"));
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

    private InsnList resetScreenState() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "org/apache/commons/lang3/SystemUtils", "IS_OS_WINDOWS", "Z"));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "org/apache/commons/lang3/SystemUtils", "IS_OS_WINDOWS", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(ifne);
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/Display", "setResizable", "(Z)V", false));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/Display", "setResizable", "(Z)V", false));
        list.add(ifeq);
        return list;
    }
}
