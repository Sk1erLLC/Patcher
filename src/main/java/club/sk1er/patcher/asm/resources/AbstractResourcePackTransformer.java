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

package club.sk1er.patcher.asm.resources;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class AbstractResourcePackTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.resources.AbstractResourcePack"};
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
            if (methodName.equals("getPackImage") || methodName.equals("func_110586_a")) {
                methodNode.instructions.insert(downscaleImageSize());
                break;
            }
        }
    }

    private InsnList downscaleImageSize() {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("downscalePackImages", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new LdcInsnNode("pack.png"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/resources/AbstractResourcePack", "func_110591_a", // getInputStreamByName
            "(Ljava/lang/String;)Ljava/io/InputStream;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/texture/TextureUtil", "func_177053_a", // readBufferedImage
            "(Ljava/io/InputStream;)Ljava/awt/image/BufferedImage;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("AbstractResourcePackHook"), "getPackImage", "(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        list.add(ifeq);
        return list;
    }
}
