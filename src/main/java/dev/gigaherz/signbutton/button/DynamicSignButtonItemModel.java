package dev.gigaherz.signbutton.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DynamicSignButtonItemModel implements BakedModel
{
    private final List<BakedQuad> overlayQuads;
    private final boolean ambientOcclusion;
    private final boolean isGui3d;
    private final boolean useBlockLight;
    private final TextureAtlasSprite particleTexture;
    private final ItemTransforms transforms;
    private final List<BakedQuad> baseItemQuads;
    private final ItemOverrides overrides;
    private List<BakedQuad> modelQuads;

    public DynamicSignButtonItemModel(List<BakedQuad> overlayQuads, boolean ambientOcclusion, boolean isGui3d, boolean useBlockLight, TextureAtlasSprite particleTexture, ItemTransforms transforms, ItemOverrides overrides, List<BakedQuad> baseItemQuads)    {
        this.overlayQuads = overlayQuads;
        this.ambientOcclusion = ambientOcclusion;
        this.isGui3d = isGui3d;
        this.useBlockLight = useBlockLight;
        this.particleTexture = particleTexture;
        this.transforms = transforms;
        this.baseItemQuads = baseItemQuads;
        this.overrides = new ItemOverrides(){
            @Nullable
            @Override
            public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int number)
            {
                var out = overrides.resolve(originalModel, stack, level, entity, number);
                if (out != originalModel)
                    return out;
                var tag = stack.getTagElement("WoodType");
                var woodType = SignButtonWoodTypes.get(tag != null ? tag.getString("WoodType") : null);
                var itemName = new ResourceLocation(woodType.name() + "_sign");
                var item = ForgeRegistries.ITEMS.getValue(itemName);
                if (item == null) item = Items.OAK_SIGN;
                var actualStack = new ItemStack(item);
                BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(actualStack, level, entity, number);
                var rand = new Random(42);
                var itemQuads = itemModel.getQuads(null, null, rand);
                return new DynamicSignButtonItemModel(overlayQuads, ambientOcclusion, isGui3d, useBlockLight, itemModel.getParticleIcon(), transforms, overrides, itemQuads);
            }
        };
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, Random rand)
    {
        if (direction == null)
        {
            if (modelQuads == null)
            {
                this.modelQuads = new ArrayList<>();
                modelQuads.addAll(baseItemQuads);
                modelQuads.addAll(overlayQuads);
            }
            return modelQuads;
        }
        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return ambientOcclusion;
    }

    @Override
    public boolean isGui3d()
    {
        return isGui3d;
    }

    @Override
    public boolean usesBlockLight()
    {
        return useBlockLight;
    }

    @Override
    public boolean isCustomRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return particleTexture;
    }

    @Override
    public ItemTransforms getTransforms()
    {
        return transforms;
    }

    @Override
    public ItemOverrides getOverrides()
    {
        return overrides;
    }
}
