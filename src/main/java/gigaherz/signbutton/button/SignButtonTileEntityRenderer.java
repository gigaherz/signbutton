package gigaherz.signbutton.button;

import com.mojang.blaze3d.platform.GlStateManager;
import gigaherz.signbutton.ModSignButton;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.renderer.tileentity.model.SignModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

import static net.minecraft.state.properties.BlockStateProperties.*;

public class SignButtonTileEntityRenderer extends TileEntityRenderer<SignButtonTileEntity>
{
    private static final ResourceLocation acaciaSignTexture = new ResourceLocation("textures/entity/signs/acacia.png");
    private static final ResourceLocation birchSignTexture = new ResourceLocation("textures/entity/signs/birch.png");
    private static final ResourceLocation dark_oakSignTexture = new ResourceLocation("textures/entity/signs/dark_oak.png");
    private static final ResourceLocation jungleSignTexture = new ResourceLocation("textures/entity/signs/jungle.png");
    private static final ResourceLocation oakSignTexture = new ResourceLocation("textures/entity/signs/oak.png");
    private static final ResourceLocation spruceSignTexture = new ResourceLocation("textures/entity/signs/spruce.png");
    private static final ResourceLocation signTexture = ModSignButton.location("textures/entity/sign_button.png");
    private final SignModel model = new SignModel();

    public SignButtonTileEntityRenderer()
    {
        model.getSignStick().isHidden = true;
    }

    public ResourceLocation getSignTexture(Block block)
    {
        if (block == ModSignButton.Blocks.ACACIA_SIGN_BUTTON) return acaciaSignTexture;
        if (block == ModSignButton.Blocks.BIRCH_SIGN_BUTTON) return birchSignTexture;
        if (block == ModSignButton.Blocks.DARK_OAK_SIGN_BUTTON) return dark_oakSignTexture;
        if (block == ModSignButton.Blocks.JUNGLE_SIGN_BUTTON) return jungleSignTexture;
        if (block == ModSignButton.Blocks.SPRUCE_SIGN_BUTTON) return spruceSignTexture;
        return oakSignTexture;
    }

    @Override
    public void render(SignButtonTileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();

        BlockState state = te.getBlockState();

        boolean powered = state.get(POWERED);
        Direction facing = state.get(HORIZONTAL_FACING);
        AttachFace face = state.get(FACE);

        int rotAroundY = 0;
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

        GlStateManager.translatef((float) x + 0.5F, (float) y + 0.75F * 2.0f / 3.0f, (float) z + 0.5F);
        GlStateManager.rotatef(rotAroundY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(rotAroundX, 1.0F, 0.0F, 0.0F);
        GlStateManager.translatef(0.0F, -0.3125F, -0.4375F - (powered ? 0.035F : 0));

        if (destroyStage >= 0)
        {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0F, 2.0F, 1.0F);
            GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
        else
        {
            this.bindTexture(getSignTexture(te.getBlockState().getBlock()));
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.6666667F, -0.6666667F, -0.6666667F);
        this.model.renderSign();

        if (destroyStage < 0)
        {
            GlStateManager.enableBlend();
            GlStateManager.color4f(1,1,1,1);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.bindTexture(signTexture);
            this.model.renderSign();
            GlStateManager.disableBlend();
        }

        GlStateManager.popMatrix();
        FontRenderer fontrenderer = this.getFontRenderer();
        float rot = 0.015625F * 0.6666667F;
        GlStateManager.translatef(0.0F, 0.5F * 0.6666667F, 0.07F * 0.6666667F);
        GlStateManager.scalef(rot, -rot, rot);
        GlStateManager.normal3f(0.0F, 0.0F, -1.0F * rot);
        GlStateManager.depthMask(false);
        byte b0 = 0;

        if (destroyStage < 0)
        {
            for (int j = 0; j < te.signText.length; ++j)
            {
                if (te.signText[j] != null)
                {
                    ITextComponent ichatcomponent = te.signText[j];
                    List<ITextComponent> list = RenderComponentsUtil.splitText(ichatcomponent, 90, fontrenderer, false, true);
                    String s = list != null && list.size() > 0 ? list.get(0).getFormattedText() : "";

                    if (j == te.lineBeingEdited)
                    {
                        s = "> " + s + " <";
                    }
                    fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - te.signText.length * 5, b0);
                }
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        if (destroyStage >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}