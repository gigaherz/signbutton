package gigaherz.signbutton.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.Direction;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;

import java.util.List;

public class SignButtonTileEntityRenderer extends TileEntityRenderer<SignButtonTileEntity>
{
    public static final RenderMaterial SIGN_BUTTON_OVERLAY_MATERIAL = new RenderMaterial(Atlases.SIGN_ATLAS, new ResourceLocation("signbutton", "entity/sign_button"));

    private final SignTileEntityRenderer.SignModel model = new SignTileEntityRenderer.SignModel();

    public SignButtonTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(SignButtonTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        final float scale = 0.6666667F;

        matrixStackIn.push();

        BlockState blockstate = tileEntityIn.getBlockState();

        boolean powered = blockstate.get(SignButtonBlock.POWERED);
        Direction facing = blockstate.get(SignButtonBlock.FACING);
        AttachFace face = blockstate.get(SignButtonBlock.FACE);

        int rotAroundX = 0;
        switch (face)
        {
            case CEILING:
                rotAroundX = 90;
                break;
            case FLOOR:
                rotAroundX = -90;
                break;
        }

        int rotAroundY = 0;
        switch (facing)
        {
            case SOUTH:
                break;
            case NORTH:
                rotAroundY = 180;
                break;
            case EAST:
                rotAroundY = 90;
                break;
            case WEST:
                rotAroundY = -90;
                break;
        }

        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rotAroundY));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(rotAroundX));
        matrixStackIn.translate(0.0, -0.3125, -0.4375D - (powered ? 0.035 : 0));
        this.model.signStick.showModel = false;

        matrixStackIn.push();
        matrixStackIn.scale(scale, -scale, -scale);
        {
            RenderMaterial material = SignTileEntityRenderer.getMaterial(blockstate.getBlock());
            IVertexBuilder ivertexbuilder = material.getBuffer(bufferIn, ButtonRenderTypes::entityTranslucentUnsorted);
            this.model.signBoard.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            this.model.signStick.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
        }
        {
            IVertexBuilder ivertexbuilder = SIGN_BUTTON_OVERLAY_MATERIAL.getBuffer(bufferIn, ButtonRenderTypes::entityTranslucentUnsorted);
            this.model.signBoard.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            this.model.signStick.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
        }
        matrixStackIn.pop();
        FontRenderer fontrenderer = this.renderDispatcher.getFontRenderer();
        matrixStackIn.translate(0.0D, (double) 0.33333334F, (double) 0.046666667F);
        matrixStackIn.scale(0.010416667F, -0.010416667F, 0.010416667F);

        int color = tileEntityIn.getTextColor().getTextColor();
        int red = (int) ((double) NativeImage.getRed(color) * 0.4D);
        int green = (int) ((double) NativeImage.getGreen(color) * 0.4D);
        int blue = (int) ((double) NativeImage.getBlue(color) * 0.4D);
        int adjustedColor = NativeImage.getCombined(0, blue, green, red);

        for (int line = 0; line < 4; ++line)
        {
            IReorderingProcessor ireorderingprocessor = tileEntityIn.func_242686_a(line, (text) -> {
                List<IReorderingProcessor> list = fontrenderer.trimStringToWidth(text, 90);
                return list.isEmpty() ? IReorderingProcessor.field_242232_a : list.get(0);
            });
            if (ireorderingprocessor != null) {
                float f3 = (float)(-fontrenderer.func_243245_a(ireorderingprocessor) / 2);
                fontrenderer.func_238416_a_(ireorderingprocessor, f3, (float)(line * 10 - 20), adjustedColor, false, matrixStackIn.getLast().getMatrix(), bufferIn, false, 0, combinedLightIn);
            }
        }

        matrixStackIn.pop();
    }

    private static class ButtonRenderTypes extends RenderType
    {
        private ButtonRenderTypes(String name, VertexFormat fmt, int glMode, int bufferSize, boolean useDelegate, boolean useSorting, Runnable setupRendering, Runnable cleanupRendering)
        {
            super(name, fmt, glMode, bufferSize, useDelegate, useSorting, setupRendering, cleanupRendering);
        }

        public static RenderType entityTranslucentUnsorted(ResourceLocation texture, boolean doOverlay) {
            RenderType.State rendertype$state = RenderType.State.getBuilder()
                    .texture(new RenderState.TextureState(texture, false, false))
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
                    .alpha(DEFAULT_ALPHA).cull(CULL_DISABLED)
                    .lightmap(LIGHTMAP_ENABLED)
                    .overlay(OVERLAY_ENABLED)
                    .build(doOverlay);
            return makeType("entity_translucent_unsorted", DefaultVertexFormats.ENTITY, 7, 256, true, false/*no sorting*/, rendertype$state);
        }

        public static RenderType entityTranslucentUnsorted(ResourceLocation texture) {
            return entityTranslucentUnsorted(texture, true);
        }
    }
}