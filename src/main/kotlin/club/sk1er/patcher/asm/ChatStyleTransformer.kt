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

import club.sk1er.patcher.asm.utils.getInstructions
import club.sk1er.patcher.asm.utils.injectInstructions
import club.sk1er.patcher.hooks.ChatStyleHook
import club.sk1er.patcher.hooks.FarmHook
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class ChatStyleTransformer : PatcherTransformer {
    override fun getClassName(): Array<String> {
        return arrayOf("net.minecraft.util.ChatStyle")
    }

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.first {
            val methodName = mapMethodName(classNode, it)
            methodName == "getChatHoverEvent" || methodName == "func_150210_i"
        }?.apply {
            clearInstructions(this); // The one you're overwriting
            injectInstructions {
                of(ChatStyleHook::getChatHoverEvent)
                into(this@apply) // Same one as earlier
                param(0)
            }
        }

    }
}