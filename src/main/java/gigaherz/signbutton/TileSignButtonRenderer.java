package gigaherz.signbutton;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelSign;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

import static net.minecraft.state.properties.BlockStateProperties.FACE;
import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.state.properties.BlockStateProperties.POWERED;

public class TileSignButtonRenderer extends TileEntityRenderer<TileSignButton>
{
    private static final ResourceLocation signTexture = new ResourceLocation("signbutton", "textures/entity/sign.png");
    private final ModelSign model = new ModelSign();

    public TileSignButtonRenderer()
    {
        model.getSignStick().isHidden = true;
    }

    @Override
    public void render(TileSignButton te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();

        IBlockState state = te.getBlockState();

        boolean powered = state.get(POWERED);
        EnumFacing facing = state.get(HORIZONTAL_FACING);
        AttachFace face = state.get(FACE);

        int rotAroundY = 0;
        int rotAroundX = 0;

        switch(face)
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
            this.bindTexture(signTexture);
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.6666667F, -0.6666667F, -0.6666667F);
        this.model.renderSign();
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
                    List<ITextComponent> list = GuiUtilRenderComponents.splitText(ichatcomponent, 90, fontrenderer, false, true);
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