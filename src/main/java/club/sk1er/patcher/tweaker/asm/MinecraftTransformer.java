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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

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
                case "startGame":
                case "func_71384_a":
                    methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), toggleGLErrorChecking());
                    break;

                case "func_90020_K":
                case "getLimitFramerate":
                    methodNode.instructions.insert(changeActiveFramerate());
                    break;

                case "checkGLError":
                case "func_71361_d":
                    methodNode.instructions.insert(cancelGlCheck());
                    break;

                case "toggleFullscreen":
                case "func_71352_k": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();

                        if (node instanceof MethodInsnNode && ((MethodInsnNode) node).name.equals("setFullscreen")) {
                            methodNode.instructions.insert(node, resetScreenState());
                            break;
                        }
                    }
                    InsnList insnList = new InsnList();
                    LabelNode labelNode = new LabelNode();
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHooksPackage("MinecraftHook"), "fullscreen", "()Z", false));
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
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();

                        if (node instanceof MethodInsnNode && node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            String methodInsnName = mapMethodNameFromNode(node);
                            if (methodInsnName.equals("clearChatMessages") || methodInsnName.equals("func_146231_a")) {
                                LabelNode ifne = new LabelNode();
                                methodNode.instructions.insertBefore(node.getPrevious().getPrevious().getPrevious().getPrevious(), addConfigOption(ifne));
                                methodNode.instructions.insertBefore(node.getNext(), ifne);
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
                                        getHooksPackage("FallbackResourceManagerHook"),
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
                            methodNode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, getHooksPackage("MinecraftHook"), "updateKeyBindState", "()V", false));
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
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals("stream")) {
                            for (int i = 0; i < 33; ++i) {
                                methodNode.instructions.remove(next.getNext());
                            }

                            methodNode.instructions.remove(next.getPrevious().getPrevious());
                            methodNode.instructions.remove(next.getPrevious());
                            methodNode.instructions.remove(next);
                            break;
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

    private InsnList getKeybind(String keybindName) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/Patcher", "instance", "Lclub/sk1er/patcher/Patcher;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "club/sk1er/patcher/Patcher", keybindName, "()Lnet/minecraft/client/settings/KeyBinding;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/settings/KeyBinding", "getKeyCode", "()I", false));
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

    private InsnList cancelGlCheck() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "glErrorChecking", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }

    private InsnList toggleGLErrorChecking() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "glErrorChecking", "Z"));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD,
            "net/minecraft/client/Minecraft",
            "field_175619_R", // enableGLErrorChecking
            "Z"));
        return list;
    }

    private InsnList addConfigOption(LabelNode ifne) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "crossChat", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
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
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "fullscreenFix", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/Display", "setResizable", "(Z)V", false));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/Display", "setResizable", "(Z)V", false));
        list.add(ifeq);
        return list;
    }
}
