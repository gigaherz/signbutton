package gigaherz.signbutton;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class TileSignButtonRenderer extends TileEntitySpecialRenderer<TileSignButton>
{
    private static final ResourceLocation signTexture = new ResourceLocation("signbutton", "textures/entity/sign.png");
    private final ModelSign model = new ModelSign();

    public TileSignButtonRenderer()
    {
        model.signStick.isHidden = true;
    }

    @Override
    public void render(TileSignButton te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GlStateManager.pushMatrix();
        int meta = te.getBlockMetadata();

        boolean powered = (meta & 8) != 0;

        int rotAroundY = 0;
        int rotAroundX = 0;

        switch (meta & 7)
        {
            case 0:
                rotAroundX = -90;
                break;
            case 1:
                rotAroundX = 90;
                break;
            case 2:
                break;
            case 3:
                rotAroundY = 180;
                break;
            case 4:
                rotAroundY = 90;
                break;
            case 5:
                rotAroundY = -90;
                break;
        }

        float f1 = 0.6666667F;
        GlStateManager.translate((float) x + 0.5F, (float) y + 0.75F * f1, (float) z + 0.5F);
        GlStateManager.rotate(rotAroundY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rotAroundX, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, -0.3125F, -0.4375F - (powered ? 0.035F : 0));

        if (destroyStage >= 0)
        {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 2.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
        else
        {
            this.bindTexture(signTexture);
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.scale(f1, -f1, -f1);
        this.model.renderSign();
        GlStateManager.popMatrix();
        FontRenderer fontrenderer = this.getFontRenderer();
        float rot = 0.015625F * f1;
        GlStateManager.translate(0.0F, 0.5F * f1, 0.07F * f1);
        GlStateManager.scale(rot, -rot, rot);
        GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * rot);
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
                        fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - te.signText.length * 5, b0);
                    }
                    else
                    {
                        fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - te.signText.length * 5, b0);
                    }
                }
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        if (destroyStage >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}