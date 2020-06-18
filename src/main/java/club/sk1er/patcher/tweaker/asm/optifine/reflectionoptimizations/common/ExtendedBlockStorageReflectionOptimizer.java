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

package club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.common;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class ExtendedBlockStorageReflectionOptimizer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.world.chunk.storage.ExtendedBlockStorage"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("func_177484_a")) {
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("isInstance")) {
                        methodNode.instructions.remove(node.getPrevious());
                        methodNode.instructions.remove(node.getPrevious());
                        methodNode.instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 4));
                        methodNode.instructions.insertBefore(node, new TypeInsnNode(Opcodes.INSTANCEOF, "net/minecraftforge/common/property/IExtendedBlockState"));
                        methodNode.instructions.remove(node);
                    }
                    else if (node.getOpcode() == Opcodes.CHECKCAST && ((TypeInsnNode) node).desc.equals("net/minecraft/block/state/IBlockState")) {
                        methodNode.instructions.remove(node.getPrevious());
                        methodNode.instructions.remove(node.getPrevious());
                        methodNode.instructions.remove(node.getPrevious());
                        methodNode.instructions.remove(node.getPrevious());
                        methodNode.instructions.insertBefore(node, new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraftforge/common/property/IExtendedBlockState"));
                        methodNode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/common/property/IExtendedBlockState", "getClean", "()Lnet/minecraft/block/state/IBlockState;", false));
                        methodNode.instructions.remove(node);
                        break;
                    }
                }
            }
        }
    }
}
