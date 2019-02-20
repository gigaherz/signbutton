package gigaherz.signbutton.client;

import gigaherz.signbutton.TileSignButton;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import static net.minecraft.state.properties.BlockStateProperties.FACE;
import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class GuiEditSignButton extends GuiEditSign
{
    TileSignButton tileSign;

    public GuiEditSignButton(TileSignButton teSign)
    {
        super(teSign);
        this.tileSign = teSign;
    }

    private int updateCounter;
    public void tick() {
        ++this.updateCounter;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format("sign.edit"), this.width / 2, 40, 16777215);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)(this.width / 2), 0.0F, 50.0F);
        GlStateManager.scalef(-93.75F, -93.75F, -93.75F);
        GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);

        IBlockState state = this.tileSign.getBlockState();
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

        GlStateManager.rotatef(-rotAroundY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(-rotAroundX, 1.0F, 0.0F, 0.0F);
        GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
        if (this.updateCounter / 6 % 2 == 0) {
            this.tileSign.lineBeingEdited = ObfuscationReflectionHelper.getPrivateValue(GuiEditSign.class, this, "field_146851_h"); // this.editLine;
        }

        TileEntityRendererDispatcher.instance.render(this.tileSign, -0.5D, -0.75D, -0.5D, 0.0F);
        this.tileSign.lineBeingEdited = -1;
        GlStateManager.popMatrix();

        for(int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(mouseX, mouseY, partialTicks);
        }

        for(int j = 0; j < this.labels.size(); ++j) {
            this.labels.get(j).render(mouseX, mouseY, partialTicks);
        }
    }
}
