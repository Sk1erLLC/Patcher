package club.sk1er.patcher.asm.utils

import club.sk1er.patcher.asm.utils.HookInlining.Companion.storeOpcode
import codes.som.anthony.koffee.BlockAssembly
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import kotlin.jvm.internal.FunctionReference
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * Hook Inlining DSL
 * Detailed Example: <a href="https://gist.github.com/lego3708/46945201c27734e5131a05ef71059b08">Github Gist</a>
 * @author LlamaLad7
 * DM me if you have any questions :)
 */
class HookBuilder {
    private var target: MethodNode? = null
    private var params = mutableListOf<Any>()
    private var injectBefore: AbstractInsnNode? = null
    private var injectAfter: AbstractInsnNode? = null
    private var methodNode: MethodNode? = null
    private var shouldRemapReturns = false

    var instructions = InsnList()
    var tryCatchBlocks = mutableListOf<TryCatchBlockNode>()

    fun finalize() {
        val methodArgs = Type.getArgumentTypes(methodNode?.desc)
        val localVariableIndexes = mutableListOf<Int>()
        val paramsStoring = InsnList()
        var index = HookInlining.getSuggestedStartingIndex(target!!, injectBefore ?: injectAfter?.next)

        for ((i, param) in params.withIndex()) {
            when (param) {
                is Int -> localVariableIndexes.add(param)

                is AbstractInsnNode -> {
                    index++
                    localVariableIndexes.add(index)
                    paramsStoring.add(param)
                    paramsStoring.add(VarInsnNode(methodArgs[i].descriptor.storeOpcode, index))
                }

                is InsnList -> {
                    index++
                    localVariableIndexes.add(index)
                    paramsStoring.add(param)
                    paramsStoring.add(VarInsnNode(methodArgs[i].descriptor.storeOpcode, index))
                }
            }
        }

        val finalInstructionList = HookInlining.getMethodInstructions(
                methodNode!!,
                shouldRemapReturns,
                index - methodArgs.size + if (methodNode!!.access and Opcodes.ACC_STATIC == Opcodes.ACC_STATIC) 1 else 0,
                *localVariableIndexes.toIntArray()
        )
        finalInstructionList.insertBefore(finalInstructionList.first, paramsStoring)
        instructions.add(finalInstructionList)
        if (methodNode?.tryCatchBlocks?.isNotEmpty() == true) {
            tryCatchBlocks.addAll(methodNode?.tryCatchBlocks!!)
        }
    }

    fun inject() {
        when {
            injectBefore != null -> target?.instructions?.insertBefore(injectBefore, instructions)
            injectAfter != null -> target?.instructions?.insert(injectAfter, instructions)
            else -> target?.instructions?.add(instructions)
        }
    }

    fun injectTryCatchNodes() {
        if (tryCatchBlocks.isNotEmpty()) {
            target?.tryCatchBlocks?.addAll(tryCatchBlocks)
        }
    }

    infix fun of(hook: KFunction<*>) {
        methodNode = HookInlining.getMethodNode(((hook as FunctionReference).owner as KClass<*>).java, hook.name)
    }

    infix fun into(methodNode: MethodNode) {
        target = methodNode
    }

    infix fun target(methodNode: MethodNode) = into(methodNode)

    infix fun before(abstractInsnNode: AbstractInsnNode) {
        injectBefore = abstractInsnNode
    }

    infix fun after(abstractInsnNode: AbstractInsnNode) {
        injectAfter = abstractInsnNode
    }

    infix fun param(index: Int) {
        params.add(index)
    }

    fun params(vararg indexes: Int) {
        params.addAll(indexes.toTypedArray())
    }

    infix fun param(abstractInsnNode: AbstractInsnNode) {
        params.add(abstractInsnNode)
    }

    infix fun param(insnList: InsnList) {
        params.add(insnList)
    }

    fun param(routine: BlockAssembly.() -> Unit) {
        val blockAssembly = BlockAssembly(InsnList(), mutableListOf())
        routine(blockAssembly)
        assert(blockAssembly.tryCatchBlocks.size == 0)
        params.add(blockAssembly.instructions)
    }

    val remapReturns: Unit
        get() {
            shouldRemapReturns = true
        }
}

fun injectInstructions(routine: HookBuilder.() -> Unit) {
    val hookBuilder = HookBuilder()
    routine(hookBuilder)
    hookBuilder.finalize()
    hookBuilder.inject()
}

fun injectInstructionsWithTryCatchNodes(routine: HookBuilder.() -> Unit) {
    val hookBuilder = HookBuilder()
    routine(hookBuilder)
    hookBuilder.finalize()
    hookBuilder.inject()
    hookBuilder.injectTryCatchNodes()
}

fun getInstructions(routine: HookBuilder.() -> Unit): InsnList {
    val hookBuilder = HookBuilder()
    routine(hookBuilder)
    hookBuilder.finalize()
    return hookBuilder.instructions
}

fun getInstructionsWithTryCatchNodes(routine: HookBuilder.() -> Unit): Pair<InsnList, MutableList<TryCatchBlockNode>> {
    val hookBuilder = HookBuilder()
    routine(hookBuilder)
    hookBuilder.finalize()
    return Pair(hookBuilder.instructions, hookBuilder.tryCatchBlocks)
}