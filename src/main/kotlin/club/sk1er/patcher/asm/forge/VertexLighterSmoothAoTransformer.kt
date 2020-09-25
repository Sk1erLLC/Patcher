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
package club.sk1er.patcher.asm.forge

import club.sk1er.hookinjection.injectInstructions
import club.sk1er.patcher.hooks.VertexLighterSmoothAoHook
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import codes.som.anthony.koffee.assembleBlock
import codes.som.anthony.koffee.insns.jvm.*
import org.objectweb.asm.tree.ClassNode

class VertexLighterSmoothAoTransformer : PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     */
    override fun getClassName() = arrayOf("net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo")

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    override fun transform(classNode: ClassNode, name: String) {
        for (methodNode in classNode.methods) {
            if (methodNode.name == "calcLightmap") {
                clearInstructions(methodNode)
                methodNode.desc = "([FFFF)V"
                injectInstructions {
                    of(VertexLighterSmoothAoHook::fastCalcLightmap)
                    into(methodNode)
                    params(0, 1, 2, 3, 4)
                    keepReturns
                }
            } else if (methodNode.name == "updateLightmap") {
                clearInstructions(methodNode)
                methodNode.instructions.insert(assembleBlock {
                    aload_0
                    aload_2
                    fload_3
                    fload(4)
                    fload(5)
                    invokevirtual(
                        "net/minecraftforge/client/model/pipeline/VertexLighterSmoothAo",
                        "calcLightmap",
                        void,
                        FloatArray::class,
                        float,
                        float,
                        float
                    )
                    _return
                }.first)
            }
        }
    }
}