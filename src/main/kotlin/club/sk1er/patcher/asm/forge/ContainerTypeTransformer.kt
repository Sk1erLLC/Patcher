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

import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode

class ContainerTypeTransformer : PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    override fun getClassName() = arrayOf("net.minecraftforge.fml.common.discovery.ContainerType")

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.first {
            it.name == "<clinit>"
        }?.apply {
            instructions.iterator().forEach { insn ->
                if (insn is LdcInsnNode) {
                    if (insn.cst.toString() == "Lnet/minecraftforge/fml/common/discovery/JarDiscoverer;") {
                        insn.cst = Type.getObjectType("club/sk1er/patcher/jar/JarDiscoverer")
                    }
                }
            }
        }
    }
}