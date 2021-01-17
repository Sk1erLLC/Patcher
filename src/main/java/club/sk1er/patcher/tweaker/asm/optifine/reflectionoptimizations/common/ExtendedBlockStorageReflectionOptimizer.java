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
            final String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("func_177484_a")) {
                final InsnList instructions = methodNode.instructions;
                final Iterator<AbstractInsnNode> iterator = instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("isInstance")) {
                        instructions.remove(node.getPrevious());
                        instructions.remove(node.getPrevious());
                        instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 4));
                        instructions.insertBefore(node, new TypeInsnNode(Opcodes.INSTANCEOF, "net/minecraftforge/common/property/IExtendedBlockState"));
                        instructions.remove(node);
                    } else if (node.getOpcode() == Opcodes.CHECKCAST && ((TypeInsnNode) node).desc.equals("net/minecraft/block/state/IBlockState")) {
                        instructions.remove(node.getPrevious());
                        instructions.remove(node.getPrevious());
                        instructions.remove(node.getPrevious());
                        instructions.remove(node.getPrevious());
                        instructions.insertBefore(node, new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraftforge/common/property/IExtendedBlockState"));
                        instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/common/property/IExtendedBlockState", "getClean", "()Lnet/minecraft/block/state/IBlockState;", false));
                        instructions.remove(node);
                        break;
                    }
                }
            }
        }
    }
}
