package gigaherz.signbutton.client;

import gigaherz.signbutton.button.SignButtonTileEntity;
import gigaherz.signbutton.button.SignButtonTileEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientUtils
{
    public static void initClient()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientUtils::stitchTextures);
    }

    public static void setupClient()
    {
        BlockEntityRenderers.register(SignButtonTileEntity.TYPE, SignButtonTileEntityRenderer::new);
    }

    public static void openSignButtonGui(BlockPos pos)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            Level world = mc.level;
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof SignButtonTileEntity)
            {
                mc.setScreen(new SignButtonEditScreen((SignButtonTileEntity) te, mc.isTextFilteringEnabled()));
            }
        });
    }

    public static void stitchTextures(TextureStitchEvent.Pre event)
    {
        if (event.getMap().location().equals(Sheets.SIGN_SHEET))
        {
            event.addSprite(new ResourceLocation("signbutton", "entity/sign_button"));
        }
    }
}