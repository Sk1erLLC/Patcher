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

package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public class TexturedQuadHook {
    private final TexturedQuad parent;

    public TexturedQuadHook(TexturedQuad parent) {
        this.parent = parent;
    }

    public void draw(WorldRenderer renderer, float scale) {
        Vec3 xVertex = parent.vertexPositions[1].vector3D.subtractReverse(parent.vertexPositions[0].vector3D);
        Vec3 zVertex = parent.vertexPositions[1].vector3D.subtractReverse(parent.vertexPositions[2].vector3D);
        Vec3 crossVertex = zVertex.crossProduct(xVertex).normalize();
        float xCoord = (float) crossVertex.xCoord;
        float yCoord = (float) crossVertex.yCoord;
        float zCoord = (float) crossVertex.zCoord;

        if (parent.invertNormal) {
            xCoord = -xCoord;
            yCoord = -yCoord;
            zCoord = -zCoord;
        }

        boolean drawOnSelf = !renderer.isDrawing;
        if (drawOnSelf || !PatcherConfig.singleModelCall) {
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

        if (drawOnSelf || !PatcherConfig.singleModelCall) {
            Tessellator.getInstance().draw();
        }
    }
}
