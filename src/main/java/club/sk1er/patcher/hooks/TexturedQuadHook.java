package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.mixins.accessors.TexturedQuadAccessor;
import club.sk1er.patcher.mixins.accessors.WorldRendererAccessor;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class TexturedQuadHook {

    // todo: convert into a better hook instead of overwriting
    public static void draw(TexturedQuad parent, WorldRenderer renderer, float scale) {
        TexturedQuadAccessor parentAccessor = (TexturedQuadAccessor) parent;
        WorldRendererAccessor worldRendererAccessor = (WorldRendererAccessor) renderer;
        Vec3 xVertex = parent.vertexPositions[1].vector3D.subtractReverse(parent.vertexPositions[0].vector3D);
        Vec3 zVertex = parent.vertexPositions[1].vector3D.subtractReverse(parent.vertexPositions[2].vector3D);
        Vec3 crossVertex = zVertex.crossProduct(xVertex).normalize();
        float xCoord = (float) crossVertex.xCoord;
        float yCoord = (float) crossVertex.yCoord;
        float zCoord = (float) crossVertex.zCoord;

        if (parentAccessor.isInvertNormal()) {
            xCoord = -xCoord;
            yCoord = -yCoord;
            zCoord = -zCoord;
        }

        boolean drawOnSelf = !worldRendererAccessor.isDrawing();
        if (drawOnSelf || !PatcherConfig.batchModelRendering) {
            renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        }

        for (int i = 0; i < 4; ++i) {
            PositionTextureVertex vertex = parent.vertexPositions[i];
            renderer
                .pos(vertex.vector3D.xCoord * (double) scale, vertex.vector3D.yCoord * (double) scale, vertex.vector3D.zCoord * (double) scale)
                .tex(vertex.texturePositionX, vertex.texturePositionY)
                .normal(xCoord, yCoord, zCoord)
                .endVertex();
        }

        if (drawOnSelf || !PatcherConfig.batchModelRendering) {
            Tessellator.getInstance().draw();
        }
    }
}
