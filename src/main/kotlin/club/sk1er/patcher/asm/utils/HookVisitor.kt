package club.sk1er.patcher.asm.utils

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodNode


class HookVisitor(private val hookName: String): ClassVisitor(Opcodes.ASM5) {
    var hookNode: MethodNode? = null
    override fun visitMethod(access: Int, name: String?,
                             desc: String?, signature: String?, exceptions: Array<String?>?): MethodVisitor? {

        if (name == hookName) {
            val methodNode = MethodNode(access, name, desc, signature, exceptions)
            hookNode = methodNode
            return hookNode
        }
        return null
    }
}