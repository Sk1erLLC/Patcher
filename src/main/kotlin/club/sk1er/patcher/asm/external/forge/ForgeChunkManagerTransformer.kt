package club.sk1er.patcher.asm.external.forge

import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import codes.som.anthony.koffee.assembleBlock
import codes.som.anthony.koffee.insns.jvm.*
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import java.util.*

class ForgeChunkManagerTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraftforge.common.ForgeChunkManager")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.forEach { method ->
            when (method.name) {
                "<clinit>" -> {
                    for (insn in method.instructions.iterator()) {
                        if (insn is FieldInsnNode && insn.name == "forcedChunks") {
                            for (i in 0..4) {
                                method.instructions.remove(insn.getPrevious())
                            }

                            val (assignForcedChunks, _) = assembleBlock {
                                new(WeakHashMap::class)
                                dup
                                invokespecial(WeakHashMap::class, "<init>", void)
                                invokestatic(Collections::class, "synchronizedMap", Map::class, Map::class)
                            }

                            method.instructions.insertBefore(insn, assignForcedChunks)
                            break
                        }
                    }
                }

                "unloadWorld" -> {
                    val (removeWorld, _) = assembleBlock {
                        getstatic("net/minecraftforge/common/ForgeChunkManager", "forcedChunks", Map::class)
                        aload_0
                        invokeinterface(Map::class, "remove", Object::class, Object::class)
                    }

                    method.instructions.insert(removeWorld)
                }

                "getPersistentChunksFor" -> {
                    clearInstructions(method)
                    val (getPersistentChunksFor, _) = assembleBlock {
                        aload_0
                        getstatic("net/minecraftforge/common/ForgeChunkManager", "forcedChunks", Map::class)
                        invokestatic(
                            getHookClass("ForgeChunkManagerHook"),
                            "getPersistentChunksFor",
                            "com/google/common/collect/ImmutableSetMultimap",
                            "net/minecraft/world/World",
                            Map::class
                        )
                        areturn
                    }

                    method.instructions.insert(getPersistentChunksFor)
                }
            }
        }
    }
}
