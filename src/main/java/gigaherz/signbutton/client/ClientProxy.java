package gigaherz.signbutton.client;

import gigaherz.signbutton.CommonProxy;
import gigaherz.signbutton.ModSignButton;
import gigaherz.signbutton.TileSignButton;
import gigaherz.signbutton.TileSignButtonRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy
{

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        //Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().registerBuiltInBlocks(ModSignButton.signButton);

        ClientRegistry.bindTileEntitySpecialRenderer(TileSignButton.class, new TileSignButtonRenderer());

        ModelLoader.setCustomModelResourceLocation(ModSignButton.itemSignButton, 0,
                new ModelResourceLocation(ModSignButton.itemSignButton.getRegistryName(), "inventory"));
    }
}
