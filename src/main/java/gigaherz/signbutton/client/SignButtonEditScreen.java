package gigaherz.signbutton.client;

import com.mojang.blaze3d.platform.GlStateManager;
import gigaherz.signbutton.button.SignButtonTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.Direction;

import static net.minecraft.state.properties.BlockStateProperties.FACE;
import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class SignButtonEditScreen extends EditSignScreen
{
    SignButtonTileEntity tileSign;

    public SignButtonEditScreen(SignButtonTileEntity teSign)
    {
        super(teSign);
        this.tileSign = teSign;
    }

    private int updateCounter;

    public void tick()
    {
        ++this.updateCounter;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 16777215);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)(this.width / 2), 0.0F, 50.0F);
        GlStateManager.scalef(-93.75F, -93.75F, -93.75F);
        GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);


        BlockState state = this.tileSign.getBlockState();
        Direction facing = state.get(HORIZONTAL_FACING);
        AttachFace face = state.get(FACE);

        switch (face)
        {
            case CEILING:
                GlStateManager.rotatef(-90, 1.0F, 0.0F, 0.0F);
                break;
            case FLOOR:
                GlStateManager.rotatef(90, 1.0F, 0.0F, 0.0F);
                break;
            default:
                break;
        }

        switch (face)
        {
            case CEILING:
                GlStateManager.translated(0, -0.5, -1.3125F);
                break;
            case FLOOR:
                GlStateManager.translated(0, 0.5, 1.3125F);
                break;
            default:
                GlStateManager.translated(0, -1.0625F, 0);
                break;
        }

        switch (facing)
        {
            case SOUTH:
                break;
            case NORTH:
                GlStateManager.rotatef(180, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.rotatef(-90, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotatef(90, 0.0F, 1.0F, 0.0F);
                break;
        }

        if (this.updateCounter / 6 % 2 == 0)
        {
            this.tileSign.lineBeingEdited = this.editLine;
        }

        this.tileSign.func_214062_a(this.editLine, this.field_214267_d.func_216896_c(), this.field_214267_d.func_216898_d(), this.updateCounter / 6 % 2 == 0);
        TileEntityRendererDispatcher.instance.render(this.tileSign, -0.5D, -0.75D, -0.5D, 0.0F);
        this.tileSign.func_214063_g();

        GlStateManager.popMatrix();
        super.render(mouseX, mouseY, partialTicks);
    }
}
