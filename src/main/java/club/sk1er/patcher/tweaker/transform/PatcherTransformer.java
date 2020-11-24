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

package club.sk1er.patcher.tweaker.transform;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public interface PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    String[] getClassName();

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    void transform(ClassNode classNode, String name);

    /**
     * Map the method name from notch names
     *
     * @param classNode  the transformed class node
     * @param methodNode the transformed classes method node
     * @return a mapped method name
     */
    default String mapMethodName(ClassNode classNode, MethodNode methodNode) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, methodNode.name, methodNode.desc);
    }

    /**
     * Map the field name from notch names
     *
     * @param classNode the transformed class node
     * @param fieldNode the transformed classes field node
     * @return a mapped field name
     */
    default String mapFieldName(ClassNode classNode, FieldNode fieldNode) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(classNode.name, fieldNode.name, fieldNode.desc);
    }

    /**
     * Map the class name from notch names
     *
     * @param className the notch name of the class
     * @return a deobfuscated name of the class
     */
    default String mapClassName(String className) {
        return FMLDeobfuscatingRemapper.INSTANCE.map(className);
    }

    /**
     * Map the method desc from notch names
     *
     * @param methodNode the transformed method node
     * @return a mapped method desc
     */
    default String mapMethodDesc(MethodNode methodNode) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(methodNode.desc);
    }

    /**
     * Map the method name from notch names
     *
     * @param node the transformed method insn node
     * @return a mapped insn method
     */
    default String mapMethodNameFromNode(AbstractInsnNode node) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) node;
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc);
    }

    /**
     * Map the field name from notch names
     *
     * @param node the transformed field insn node
     * @return a mapped insn field
     */
    default String mapFieldNameFromNode(AbstractInsnNode node) {
        FieldInsnNode fieldInsnNode = (FieldInsnNode) node;
        return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc);
    }

    default String mapMethodDescFromNode(AbstractInsnNode node) {
        MethodInsnNode methodInsnNode = (MethodInsnNode) node;
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(methodInsnNode.desc);
    }

    /**
     * Remove instructions to this method
     *
     * @param methodNode the method being cleared
     */
    default void clearInstructions(MethodNode methodNode) {
        methodNode.instructions.clear();

        // don't waste time clearing local variables if they're empty
        if (!methodNode.localVariables.isEmpty()) {
            methodNode.localVariables.clear();
        }

        // don't waste time clearing try-catches if they're empty
        if (!methodNode.tryCatchBlocks.isEmpty()) {
            methodNode.tryCatchBlocks.clear();
        }
    }

    /**
     * Link to {@link PatcherConfig} in transformers easily
     *
     * @return patcher config class
     */
    default String getPatcherConfigClass() {
        return "club/sk1er/patcher/config/PatcherConfig";
    }

    default boolean isDevelopment() {
        Object o = Launch.blackboard.get("fml.deobfuscatedEnvironment");
        return o != null && (boolean) o;
    }
}
