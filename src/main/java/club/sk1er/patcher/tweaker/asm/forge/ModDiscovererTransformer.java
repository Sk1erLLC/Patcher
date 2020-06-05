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

package club.sk1er.patcher.tweaker.asm.forge;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ModDiscovererTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.fml.common.discovery.ModDiscoverer"};
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
            if (methodNode.name.equals("identifyMods")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(identifyModsAsync());
                break;
            }
        }
    }

    private InsnList identifyModsAsync() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/fml/common/discovery/ModDiscoverer", "candidates", "Ljava/util/List;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/fml/common/discovery/ModDiscoverer", "dataTable", "Lnet/minecraftforge/fml/common/discovery/ASMDataTable;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/fml/common/discovery/ModDiscoverer", "nonModLibs", "Ljava/util/List;"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/hooks/ModDiscovererHook", "identifyModsAsync", "(Ljava/util/List;Lnet/minecraftforge/fml/common/discovery/ASMDataTable;Ljava/util/List;)Ljava/util/List;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }
}
