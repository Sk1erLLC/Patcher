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
package club.sk1er.patcher.asm

import club.sk1er.hookinjection.getInstructions
import club.sk1er.patcher.hooks.FarmHook
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class BlockPotatoTransformer : PatcherTransformer {
    // todo: since this is specific to hypixel, move to hytilities
    override fun getClassName() = arrayOf(
        "net.minecraft.block.BlockPotato",
        "net.minecraft.block.BlockCarrot"
    )

    override fun transform(classNode: ClassNode, name: String) {
        val getSelectionBoundingBox = MethodNode(Opcodes.ACC_PUBLIC, "func_180646_a", "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/util/AxisAlignedBB;", null, null)
        val list = InsnList()
        list.add(getInstructions {
            of(FarmHook::getBox)
            target(getSelectionBoundingBox)
            params(1, 2, 0)
        })
        list.add(VarInsnNode(Opcodes.ALOAD, 0))
        list.add(VarInsnNode(Opcodes.ALOAD, 1))
        list.add(VarInsnNode(Opcodes.ALOAD, 2))
        list.add(MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/block/Block", "func_180646_a", "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/util/AxisAlignedBB;", false))
        list.add(InsnNode(Opcodes.ARETURN))
        getSelectionBoundingBox.instructions.add(list)
        classNode.methods.add(getSelectionBoundingBox)
    }
}