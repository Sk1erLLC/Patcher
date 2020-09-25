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
import club.sk1er.patcher.hooks.EnchantmentHook
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class EnchantmentTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraft.enchantment.Enchantment")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.first {
            val methodName = mapMethodName(classNode, it)
            methodName == "getTranslatedName" || methodName == "func_77316_c"
        }?.apply {
            instructions.insertBefore(instructions.first, getNumericalName(this))
        }
    }

    private fun getNumericalName(methodNode: MethodNode): InsnList {
        val list = InsnList()
        list.add(FieldInsnNode(Opcodes.GETSTATIC, patcherConfigClass, "romanNumerals", "Z"))
        val labelNode = LabelNode()
        list.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
        list.add(getInstructions { // get the instructions
            of(EnchantmentHook::getNumericalName) // of the method "getNumericalName"
            target(methodNode) // targeting the method we're injecting (getTranslatedName)
            before(methodNode.instructions.first) // place here
            params(0, 1) // insert "this" and "level" into parameters
            keepReturns
        }) // add the instructions from "getNumericalName" directly instead of using a hook
        list.add(labelNode)
        return list
    }
}