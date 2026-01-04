package dev.gigaherz.signbutton.client;

import dev.gigaherz.signbutton.SignButtonMod;
import dev.gigaherz.signbutton.button.SignButtonBlockEntity;
import dev.gigaherz.signbutton.button.SignButtonRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;

@Mod(value = SignButtonMod.MODID, dist = Dist.CLIENT)
public class SignButtonClientMod
{
    public SignButtonClientMod(IEventBus modBus)
    {
        modBus.addListener(this::registerRenderers);
        modBus.addListener(this::registerBuffers);
    }

    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(SignButtonMod.SIGN_BUTTON_BLOCK_ENTITY.get(), SignButtonRenderer::new);
    }

    private void registerBuffers(RegisterRenderBuffersEvent event)
    {
        event.registerRenderBuffer(RenderTypes.entityTranslucent(Sheets.SIGN_SHEET));
    }

    public static void openSignButtonGui(BlockPos pos)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            Level world = mc.level;
            if (world != null && world.getBlockEntity(pos) instanceof SignButtonBlockEntity sign)
            {
                mc.setScreen(new SignEditScreen(sign, true, mc.isTextFilteringEnabled()));
            }
        });
    }
}