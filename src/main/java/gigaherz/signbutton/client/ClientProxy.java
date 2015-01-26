package gigaherz.signbutton.client;

import gigaherz.signbutton.CommonProxy;
import gigaherz.signbutton.ModSignButton;
import gigaherz.signbutton.TileSignButton;
import gigaherz.signbutton.TileSignButtonRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

    // Client stuff
    public void registerPreRenderers() {
    }

    @Override
    public void registerRenderers() {
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().registerBuiltInBlocks(ModSignButton.signButton);
        ClientRegistry.bindTileEntitySpecialRenderer(TileSignButton.class, new TileSignButtonRenderer());

        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        renderItem.getItemModelMesher().register(ModSignButton.itemSignButton, 0, new ModelResourceLocation(ModSignButton.MODID + ":signButton", "inventory"));
        ModelBakery.addVariantName(ModSignButton.itemSignButton, ModSignButton.MODID + ":signButton");
    }
}
