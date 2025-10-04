package dev.gigaherz.signbutton.client;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class OverlayRenderType
{
    public static RenderType of(ResourceLocation textureLocation) {
        return Internal.SUPPLIER.apply(textureLocation);
    }

    private static final class Internal
    {
        public static Function<ResourceLocation, RenderType> SUPPLIER = Util.memoize(Internal::of);

        private static RenderType of(ResourceLocation textureLocation)
        {
            var renderState = RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(textureLocation, false))
                    .setLightmapState(RenderType.LIGHTMAP)
                    .setOverlayState(RenderType.OVERLAY)
                    .createCompositeState(true);
            return RenderType.create("sign_button_overlay", 256, true, false, RenderPipelines.ENTITY_TRANSLUCENT, renderState);
        }
    }
}
