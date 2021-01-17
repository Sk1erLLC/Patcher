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

public class ResourcePackRepositoryTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.resources.ResourcePackRepository"};
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

            switch (methodName) {
                case "deleteOldServerResourcesPacks":
                case "func_183028_i":
                    methodNode.instructions.insert(createDirectory());
                    break;
                case "setResourcePackInstance":
                case "func_177319_a":
                    methodNode.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC,
                        getHooksPackage("FallbackResourceManagerHook"),
                        "clearCache",
                        "()V",
                        false));
                    break;
                case "func_148529_f":
                case "clearResourcePack": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            final String mappedMethodName = mapMethodNameFromNode(next);
                            if (mappedMethodName.equals("scheduleResourcesRefresh") || mappedMethodName.equals("func_175603_A")) {
                                methodNode.instructions.insertBefore(next.getPrevious().getPrevious(), new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    getHooksPackage("FallbackResourceManagerHook"),
                                    "clearCache",
                                    "()V",
                                    false
                                ));
                                break;
                            }
                        }
                    }
                    break;
                }

                case "func_110611_a":
                case "updateRepositoryEntriesAll": {
                    methodNode.instructions.insert(getFasterSearching());
                    break;
                }
            }
        }
    }

    private InsnList getFasterSearching() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "optimizedResourcePackDiscovery", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHooksPackage("ResourcePackRepositoryHook"), "updateRepositoryEntriesAll", "(Lnet/minecraft/client/resources/ResourcePackRepository;)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }

    private InsnList createDirectory() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/ResourcePackRepository", "field_148534_e", // dirServerResourcepacks
            "Ljava/io/File;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/File", "exists", "()Z", false));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/ResourcePackRepository", "field_148534_e", // dirServerResourcepacks
            "Ljava/io/File;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/File", "mkdirs", "()Z", false));
        list.add(new InsnNode(Opcodes.POP));
        list.add(ifne);
        return list;
    }
}
