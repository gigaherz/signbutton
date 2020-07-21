package gigaherz.signbutton.client;

import gigaherz.signbutton.button.SignButtonTileEntity;
import gigaherz.signbutton.button.SignButtonTileEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientUtils
{
    public static void setupClient()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(SignButtonTileEntity.class, new SignButtonTileEntityRenderer());
    }

    public static void openSignButtonGui(BlockPos pos)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            World world = mc.world;
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof SignButtonTileEntity)
            {
                mc.displayGuiScreen(new SignButtonEditScreen((SignButtonTileEntity) te));
            }
        });
    }
}