package gigaherz.signbutton.client;

import gigaherz.signbutton.ModSignButton;
import gigaherz.signbutton.button.TileSignButton;
import gigaherz.signbutton.button.TileSignButtonRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

public class ClientUtils
{
    public static void registerTESR()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileSignButton.class, new TileSignButtonRenderer());
    }

    public static void openSignButtonGui(BlockPos pos)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            World world = mc.world;
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileSignButton)
            {
                mc.displayGuiScreen(new EditSignButtonScreen((TileSignButton) te));
            }
        });
    }
}