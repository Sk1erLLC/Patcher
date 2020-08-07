package club.sk1er.patcher.asm.forge

import club.sk1er.patcher.asm.utils.injectInstructionsWithTryCatchNodes
import club.sk1er.patcher.hooks.ASMDataTableHook
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import codes.som.anthony.koffee.insns.jvm.aload_0
import codes.som.anthony.koffee.insns.jvm.getfield
import com.google.common.collect.SetMultimap
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

class ASMDataTableTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraftforge.fml.common.discovery.ASMDataTable")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.version = Opcodes.V1_8

        classNode.methods.first {
            it.name == "getAnnotationsFor"
        }?.apply {
            clearInstructions(this)
            injectInstructionsWithTryCatchNodes {
                of(ASMDataTableHook::getAnnotationsFor)
                into(this@apply)
                param(1)
                param {
                    aload_0
                    getfield("net/minecraftforge/fml/common/discovery/ASMDataTable", "containers", List::class)
                }
                param {
                    aload_0
                    getfield(
                        "net/minecraftforge/fml/common/discovery/ASMDataTable",
                        "globalAnnotationData",
                        SetMultimap::class
                    )
                }
            }
        }
    }
}