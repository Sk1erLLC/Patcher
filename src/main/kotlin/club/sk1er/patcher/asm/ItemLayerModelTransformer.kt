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
import club.sk1er.patcher.hooks.ItemLayerModelHook
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class ItemLayerModelTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraftforge.client.model.ItemLayerModel")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.first { it.name == "getQuadsForSprite" }?.apply {
            instructions.insertBefore(instructions.first, optimizeModelGeneration(this))
        }
    }

    private fun optimizeModelGeneration(methodNode: MethodNode): InsnList {
        val list = InsnList()
        list.add(FieldInsnNode(Opcodes.GETSTATIC, patcherConfigClass, "optimizedModelGeneration", "Z"))
        val ifeq = LabelNode()
        list.add(JumpInsnNode(Opcodes.IFEQ, ifeq))
        list.add(getInstructions {
            of(ItemLayerModelHook::getQuadsForSprite)
            target(methodNode)
            before(methodNode.instructions.first)
            params(1, 2, 3, 4)
        })
        list.add(ifeq)
        return list
    }
}