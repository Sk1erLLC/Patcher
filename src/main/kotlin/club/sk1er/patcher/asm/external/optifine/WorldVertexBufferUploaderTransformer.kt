package club.sk1er.patcher.asm.external.optifine

import club.sk1er.patcher.tweaker.ClassTransformer
import club.sk1er.patcher.tweaker.transform.PatcherTransformer
import codes.som.anthony.koffee.assembleBlock
import codes.som.anthony.koffee.insns.jvm.*
import codes.som.anthony.koffee.types.TypeLike
import org.lwjgl.opengl.GL11
import org.objectweb.asm.tree.ClassNode
import java.nio.ByteBuffer

class WorldVertexBufferUploaderTransformer : PatcherTransformer {
    override fun getClassName() = arrayOf("net.minecraft.client.renderer.WorldVertexBufferUploader")

    override fun transform(classNode: ClassNode, name: String) {
        classNode.methods.first {
            val methodName = mapMethodName(classNode, it)
            methodName == "draw" || methodName == "func_181679_a"
        }?.apply {
            clearInstructions(this)
            instructions.insert(removeReflectionCall().first)
        }
    }

    private val worldRenderer = "net/minecraft/client/renderer/WorldRenderer"
    private val vertexFormat: TypeLike = "net/minecraft/client/renderer/vertex/VertexFormat"
    private val vertexFormatElement = "net/minecraft/client/renderer/vertex/VertexFormatElement"
    private val oldOptifine = ClassTransformer.optifineVersion == "I7"
    private val sVertexBuilder = if (oldOptifine) "shadersmod/client/SVertexBuilder" else "net/optifine/shaders/SVertexBuilder"
    private val mapping = mapOf(
        "func_178989_h" to "getVertexCount",
        "func_178979_i" to "getDrawMode",
        "func_178973_g" to "getVertexFormat",
        "func_177338_f" to "getNextOffset",
        "func_178966_f" to "getByteBuffer",
        "func_177343_g" to "getElements",
        "func_177375_c" to "getUsage",
        "func_178965_a" to "reset"
    )

    private val String.environmentName: String
        inline get() = if (!isDevelopment) this else mapping.getOrElse(this) { throw IllegalArgumentException("No mapping for $this")}

    private fun removeReflectionCall() = assembleBlock {
        aload_1
        invokevirtual(worldRenderer, "func_178989_h".environmentName, int)
        ifle(L["1"])

        if (!oldOptifine) {
            aload_1
            invokevirtual(worldRenderer, "func_178979_i".environmentName, int)
            bipush(7)
            if_icmpne(L["3"])
            invokestatic("Config", "isQuadsToTriangles", boolean)
            ifeq(L["3"])
            aload_1
            invokevirtual(worldRenderer, "quadsToTriangles", void)
            +L["3"]
        }

        aload_1
        invokevirtual(worldRenderer, "func_178973_g".environmentName, vertexFormat)
        astore_2
        aload_2
        invokevirtual(vertexFormat, "func_177338_f".environmentName, int)
        istore_3
        aload_1
        invokevirtual(worldRenderer, "func_178966_f".environmentName, ByteBuffer::class)
        astore(4)
        aload_2
        invokevirtual(vertexFormat, "func_177343_g".environmentName, List::class)
        astore(5)
        iconst_0
        istore(6)
        +L["9"]
        iload(6)
        aload(5)
        invokeinterface(List::class, "size", int)
        if_icmpge(L["10"])
        aload(5)
        iload(6)
        invokeinterface(List::class, "get", Object::class, int)
        checkcast(vertexFormatElement)
        astore(7)
        aload(7)
        invokevirtual(vertexFormatElement, "func_177375_c".environmentName, "$vertexFormatElement\$EnumUsage" as TypeLike)
        aload(2)
        iload(6)
        iload(3)
        aload(4)
        invokevirtual("$vertexFormatElement\$EnumUsage", "preDraw", void, vertexFormat, int, int, ByteBuffer::class)
        iinc(6, 1)
        goto(L["9"])
        +L["10"]
        aload_1
        astore(6)
        aload(6)
        invokevirtual(worldRenderer, "isMultiTexture", boolean)
        ifeq(L["15"])
        aload(6)
        invokevirtual(worldRenderer, "drawMultiTexture", void)
        goto(L["17"])
        +L["15"]
        invokestatic("Config", "isShaders", boolean)
        ifeq(L["18"])
        aload_1
        invokevirtual(worldRenderer, "func_178979_i".environmentName, int)
        iconst_0
        aload_1
        invokevirtual(worldRenderer, "func_178989_h".environmentName, int)
        aload_1
        invokestatic(sVertexBuilder, "drawArrays", void, int, int, int, worldRenderer)
        goto(L["17"])
        +L["18"]
        aload_1
        invokevirtual(worldRenderer, "func_178979_i".environmentName, int)
        iconst_0
        aload_1
        invokevirtual(worldRenderer, "func_178989_h".environmentName, int)
        invokestatic(GL11::class, "glDrawArrays", void, int, int, int)
        +L["17"]
        iconst_0
        istore(7)
        aload(5)
        invokeinterface(List::class, "size", int)
        istore(8)
        +L["21"]
        iload(7)
        iload(8)
        if_icmpge(L["1"])
        aload(5)
        iload(7)
        invokeinterface(List::class, "get", Object::class, int)
        checkcast(vertexFormatElement)
        astore(9)
        aload(9)
        invokevirtual(vertexFormatElement, "func_177375_c".environmentName, "$vertexFormatElement\$EnumUsage" as TypeLike)
        aload_2
        iload(7)
        iload_3
        aload(4)
        invokevirtual("$vertexFormatElement\$EnumUsage", "postDraw", void, vertexFormat, int, int, ByteBuffer::class)
        iinc(7, 1)
        goto(L["21"])
        +L["1"]
        aload_1
        invokevirtual(worldRenderer, "func_178965_a".environmentName, void)
        _return
    }
}