package dev.gigaherz.signbutton.client;

import dev.gigaherz.signbutton.ModSignButton;
import dev.gigaherz.signbutton.button.SignButtonBlockEntity;
import dev.gigaherz.signbutton.button.SignButtonRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientUtils
{
    public static void initClient()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientUtils::registerRenderers);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(ModSignButton.SIGN_BUTTON_BLOCK_ENTITY.get(), SignButtonRenderer::new);
    }

    public static void openSignButtonGui(BlockPos pos)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            Level world = mc.level;
            if (world != null && world.getBlockEntity(pos) instanceof SignButtonBlockEntity sign)
            {
                mc.setScreen(new SignEditScreen(sign, mc.isTextFilteringEnabled()));
            }
        });
    }
}