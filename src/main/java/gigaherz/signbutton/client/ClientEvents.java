package gigaherz.signbutton.client;

import gigaherz.signbutton.TileSignButton;
import gigaherz.signbutton.TileSignButtonRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents
{
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        //Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().registerBuiltInBlocks(ModSignButton.signButton);

        ClientRegistry.bindTileEntitySpecialRenderer(TileSignButton.class, new TileSignButtonRenderer());

        /*ModelLoader.setCustomModelResourceLocation(ModSignButton.itemSignButton, 0,
                new ModelResourceLocation(ModSignButton.itemSignButton.getRegistryName(), "inventory"));*/
    }

    public static void openSignButtonGui(BlockPos pos)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.addScheduledTask(() -> {
            World world = mc.world;
            TileEntity te = world.getTileEntity(pos);
            if(te instanceof TileSignButton)
            {
                mc.displayGuiScreen(new GuiEditSignButton((TileSignButton)te));
            }
        });
    }
}
