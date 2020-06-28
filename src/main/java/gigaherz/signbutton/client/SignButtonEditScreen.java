package gigaherz.signbutton.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import gigaherz.signbutton.button.SignButtonTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;

public class SignButtonEditScreen extends EditSignScreen
{
    SignButtonTileEntity tileSign;

    public SignButtonEditScreen(SignButtonTileEntity teSign)
    {
        super(teSign);
        this.tileSign = teSign;
    }

    @Override
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderHelper.setupGuiFlatDiffuseLighting();
        this.func_230446_a_(matrixStack);
        this.func_238472_a_(matrixStack, this.field_230712_o_, this.field_230704_d_, this.field_230708_k_ / 2, 40, 16777215);
        matrixStack.push();
        matrixStack.translate((double)(this.field_230708_k_ / 2), 0.0D, 50.0D);
        float f = 93.75F;
        matrixStack.scale(93.75F, -93.75F, 93.75F);
        matrixStack.translate(0.0D, -1.3125D, 0.0D);
        BlockState blockstate = this.tileSign.getBlockState();
        boolean flag = blockstate.getBlock() instanceof StandingSignBlock;
        if (!flag) {
            matrixStack.translate(0.0D, -0.3125D, 0.0D);
        }

        boolean flag1 = this.updateCounter / 6 % 2 == 0;
        float f1 = 0.6666667F;
        matrixStack.push();
        matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = this.field_230706_i_.getRenderTypeBuffers().getBufferSource();
        RenderMaterial rendermaterial = SignTileEntityRenderer.getMaterial(blockstate.getBlock());
        IVertexBuilder ivertexbuilder = rendermaterial.getBuffer(irendertypebuffer$impl, this.field_228191_a_::getRenderType);
        this.field_228191_a_.signBoard.render(matrixStack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY);
        if (flag) {
            this.field_228191_a_.signStick.render(matrixStack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY);
        }

        matrixStack.pop();
        float f2 = 0.010416667F;
        matrixStack.translate(0.0D, (double)0.33333334F, (double)0.046666667F);
        matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
        int i = this.tileSign.getTextColor().getTextColor();
        int j = this.textInputUtil.func_216896_c();
        int k = this.textInputUtil.func_216898_d();
        int l = this.editLine * 10 - this.field_238846_r_.length * 5;
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();

        for(int i1 = 0; i1 < this.field_238846_r_.length; ++i1) {
            String s = this.field_238846_r_[i1];
            if (s != null) {
                if (this.field_230712_o_.getBidiFlag()) {
                    s = this.field_230712_o_.bidiReorder(s);
                }

                float f3 = (float)(-this.field_230706_i_.fontRenderer.getStringWidth(s) / 2);
                this.field_230706_i_.fontRenderer.func_238411_a_(s, f3, (float)(i1 * 10 - this.field_238846_r_.length * 5), i, false, matrix4f, irendertypebuffer$impl, false, 0, 15728880, false);
                if (i1 == this.editLine && j >= 0 && flag1) {
                    int j1 = this.field_230706_i_.fontRenderer.getStringWidth(s.substring(0, Math.max(Math.min(j, s.length()), 0)));
                    int k1 = j1 - this.field_230706_i_.fontRenderer.getStringWidth(s) / 2;
                    if (j >= s.length()) {
                        this.field_230706_i_.fontRenderer.func_238411_a_("_", (float)k1, (float)l, i, false, matrix4f, irendertypebuffer$impl, false, 0, 15728880, false);
                    }
                }
            }
        }

        irendertypebuffer$impl.finish();

        for(int i3 = 0; i3 < this.field_238846_r_.length; ++i3) {
            String s1 = this.field_238846_r_[i3];
            if (s1 != null && i3 == this.editLine && j >= 0) {
                int j3 = this.field_230706_i_.fontRenderer.getStringWidth(s1.substring(0, Math.max(Math.min(j, s1.length()), 0)));
                int k3 = j3 - this.field_230706_i_.fontRenderer.getStringWidth(s1) / 2;
                if (flag1 && j < s1.length()) {
                    func_238467_a_(matrixStack, k3, l - 1, k3 + 1, l + 9, -16777216 | i);
                }

                if (k != j) {
                    int l3 = Math.min(j, k);
                    int l1 = Math.max(j, k);
                    int i2 = this.field_230706_i_.fontRenderer.getStringWidth(s1.substring(0, l3)) - this.field_230706_i_.fontRenderer.getStringWidth(s1) / 2;
                    int j2 = this.field_230706_i_.fontRenderer.getStringWidth(s1.substring(0, l1)) - this.field_230706_i_.fontRenderer.getStringWidth(s1) / 2;
                    int k2 = Math.min(i2, j2);
                    int l2 = Math.max(i2, j2);
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuffer();
                    RenderSystem.disableTexture();
                    RenderSystem.enableColorLogicOp();
                    RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    bufferbuilder.pos(matrix4f, (float)k2, (float)(l + 9), 0.0F).color(0, 0, 255, 255).endVertex();
                    bufferbuilder.pos(matrix4f, (float)l2, (float)(l + 9), 0.0F).color(0, 0, 255, 255).endVertex();
                    bufferbuilder.pos(matrix4f, (float)l2, (float)l, 0.0F).color(0, 0, 255, 255).endVertex();
                    bufferbuilder.pos(matrix4f, (float)k2, (float)l, 0.0F).color(0, 0, 255, 255).endVertex();
                    bufferbuilder.finishDrawing();
                    WorldVertexBufferUploader.draw(bufferbuilder);
                    RenderSystem.disableColorLogicOp();
                    RenderSystem.enableTexture();
                }
            }
        }

        matrixStack.pop();
        RenderHelper.setupGui3DDiffuseLighting();

        // copy of super.super.render
        for(int b = 0; b < this.field_230710_m_.size(); ++b) {
            this.field_230710_m_.get(b).func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
        }
    }
}
