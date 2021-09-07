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

package club.sk1er.patcher.asm.render.item;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ItemRendererTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.ItemRenderer"};
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
            if (methodName.equals("renderFireInFirstPerson") || methodName.equals("func_78442_d")) {
                methodNode.instructions.insert(changeHeightAndFixOverlay());
                methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "net/minecraft/client/renderer/GlStateManager",
                    "func_179121_F",
                    "()V",
                    false));
                break;
            }
        }
    }

    private InsnList changeHeightAndFixOverlay() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ItemRenderer", "field_78455_a", "Lnet/minecraft/client/Minecraft;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/Minecraft", "func_147117_R", "()Lnet/minecraft/client/renderer/texture/TextureMap;", false));
        list.add(new LdcInsnNode("minecraft:blocks/fire_layer_1"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/texture/TextureMap", "func_110572_b", "(Ljava/lang/String;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/texture/TextureAtlasSprite", "func_110970_k", "()I", false));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifne);
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179094_E", "()V", false));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(getPatcherSetting("fireOverlayHeight", "F"));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179109_b", "(FFF)V", false));
        return list;
    }
}
