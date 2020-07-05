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

import club.sk1er.patcher.asm.utils.injectInstructions
import club.sk1er.patcher.hooks.ModDiscovererHook
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import codes.som.anthony.koffee.insns.jvm.aload_0
import codes.som.anthony.koffee.insns.jvm.getfield
import org.objectweb.asm.tree.ClassNode

class ModDiscovererTransformer : PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    override fun getClassName(): Array<String> {
        return arrayOf("net.minecraftforge.fml.common.discovery.ModDiscoverer")
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.first {
            it.name == "identifyMods"
        }?.apply {
            clearInstructions(this)
            injectInstructions {
                of(ModDiscovererHook::identifyModsAsync)
                into(this@apply)
                param {
                    aload_0
                    getfield("net/minecraftforge/fml/common/discovery/ModDiscoverer", "candidates", List::class)
                }
                param {
                    aload_0
                    getfield("net/minecraftforge/fml/common/discovery/ModDiscoverer", "dataTable", "net/minecraftforge/fml/common/discovery/ASMDataTable")
                }
                param {
                    aload_0
                    getfield("net/minecraftforge/fml/common/discovery/ModDiscoverer", "nonModLibs", List::class)
                }
            }
        }
    }
}