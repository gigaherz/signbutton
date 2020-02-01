package gigaherz.signbutton.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.*;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class SignButtonTileEntityRenderer extends TileEntityRenderer<SignButtonTileEntity>
{
    public static final Material SIGN_BUTTON_OVERLAY_MATERIAL = new Material(Atlases.SIGN_ATLAS, new ResourceLocation("signbutton", "entity/sign_button"));

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
            Material material = SignTileEntityRenderer.getMaterial(blockstate.getBlock());
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

        for (int j1 = 0; j1 < 4; ++j1)
        {
            String s = tileEntityIn.getRenderText(j1, (p_212491_1_) -> {
                List<ITextComponent> list = RenderComponentsUtil.splitText(p_212491_1_, 90, fontrenderer, false, true);
                return list.isEmpty() ? "" : list.get(0).getFormattedText();
            });
            if (s != null)
            {
                float f3 = (float) (-fontrenderer.getStringWidth(s) / 2);
                fontrenderer.renderString(s, f3, (float) (j1 * 10 - tileEntityIn.signText.length * 5), adjustedColor, false, matrixStackIn.getLast().getPositionMatrix(), bufferIn, false, 0, combinedLightIn);
            }
        }

        matrixStackIn.pop();
    }

    private static class ButtonRenderTypes extends RenderType
    {
        private ButtonRenderTypes(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_)
        {
            super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
        }

        public static RenderType entityTranslucentUnsorted(ResourceLocation texture, boolean doOverlay) {
            RenderType.State rendertype$state = RenderType.State.builder()
                    .texture(new RenderState.TextureState(texture, false, false))
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
                    .alpha(DEFAULT_ALPHA).cull(CULL_DISABLED)
                    .lightmap(LIGHTMAP_ENABLED)
                    .overlay(OVERLAY_ENABLED)
                    .build(doOverlay);
            return get("entity_translucent_unsorted", DefaultVertexFormats.ITEM, 7, 256, true, false/*no sorting*/, rendertype$state);
        }

        public static RenderType entityTranslucentUnsorted(ResourceLocation texture) {
            return entityTranslucentUnsorted(texture, true);
        }
    }
}