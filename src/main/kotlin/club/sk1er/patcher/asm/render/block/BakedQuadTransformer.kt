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

package club.sk1er.patcher.asm.render.block

import club.sk1er.hookinjection.getInstructions
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import club.sk1er.patcher.util.world.render.block.BlockUtil
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

class BakedQuadTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraft.client.renderer.block.model.BakedQuad")

    override fun transform(classNode: ClassNode, name: String) {
        val equals = MethodNode(Opcodes.ACC_PUBLIC, "equals", "(Ljava/lang/Object;)Z", null, null)
        equals.instructions.add(getEquals(equals))
        classNode.methods.add(equals)
    }

    private fun getEquals(method: MethodNode) = getInstructions {
        of(BlockUtil::bakedQuadEquals)
        into(method)
        params(0, 1)
        keepReturns
    }
}