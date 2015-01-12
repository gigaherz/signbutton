package gigaherz.signbutton;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class TileSignButtonRenderer extends TileEntitySpecialRenderer {
    private static final ResourceLocation signTexture = new ResourceLocation("textures/entity/sign.png");
    private final ModelSign model = new ModelSign();

    public TileSignButtonRenderer() {
        model.signStick.isHidden = true;
    }

    public void renderTileEntityAt(TileEntity tileEntity, double posX, double posY, double posZ, float p_180535_8_, int p_180535_9_) {
        GlStateManager.pushMatrix();
        int meta = tileEntity.getBlockMetadata();

        boolean powered = (meta & 8) != 0;

        int rotAroundY = 0;
        int rotAroundX = 0;

        switch (meta & 7) {
            case 0:
                rotAroundX = 90;
                break;
            case 1:
                rotAroundY = 90;
                break;
            case 2:
                rotAroundY = -90;
                break;
            case 4:
                rotAroundY = 180;
                break;
            case 5:
                rotAroundX = -90;
                break;
        }

        float f1 = 0.6666667F;
        GlStateManager.translate((float) posX + 0.5F, (float) posY + 0.75F * f1, (float) posZ + 0.5F);
        GlStateManager.rotate(rotAroundY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rotAroundX, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, -0.3125F, -0.4375F - (powered ? 0.035F : 0));

        if (p_180535_9_ >= 0) {
            this.bindTexture(DESTROY_STAGES[p_180535_9_]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 2.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else {
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
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * rot);
        GlStateManager.depthMask(false);
        byte b0 = 0;

        TileSignButton teSignButton = (TileSignButton) tileEntity;
        if (p_180535_9_ < 0) {
            for (int j = 0; j < teSignButton.signText.length; ++j) {
                if (teSignButton.signText[j] != null) {
                    IChatComponent ichatcomponent = teSignButton.signText[j];
                    List list = GuiUtilRenderComponents.func_178908_a(ichatcomponent, 90, fontrenderer, false, true);
                    String s = list != null && list.size() > 0 ? ((IChatComponent) list.get(0)).getFormattedText() : "";

                    if (j == teSignButton.lineBeingEdited) {
                        s = "> " + s + " <";
                        fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - teSignButton.signText.length * 5, b0);
                    } else {
                        fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - teSignButton.signText.length * 5, b0);
                    }
                }
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        if (p_180535_9_ >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}