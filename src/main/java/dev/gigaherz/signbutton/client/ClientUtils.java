package dev.gigaherz.signbutton.client;

import dev.gigaherz.signbutton.button.SignButtonBlockEntity;
import dev.gigaherz.signbutton.button.SignButtonRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientUtils
{
    public static void initClient()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientUtils::stitchTextures);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientUtils::registerRenderers);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(SignButtonBlockEntity.TYPE, SignButtonRenderer::new);
    }

    public static void openSignButtonGui(BlockPos pos)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            Level world = mc.level;
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof SignButtonBlockEntity)
            {
                mc.setScreen(new SignButtonEditScreen((SignButtonBlockEntity) te, mc.isTextFilteringEnabled()));
            }
        });
    }

    public static void stitchTextures(TextureStitchEvent.Pre event)
    {
        if (event.getAtlas().location().equals(Sheets.SIGN_SHEET))
        {
            event.addSprite(new ResourceLocation("signbutton", "entity/sign_button"));
        }
    }
}