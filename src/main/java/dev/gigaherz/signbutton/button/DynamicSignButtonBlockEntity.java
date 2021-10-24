package dev.gigaherz.signbutton.button;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.ObjectHolder;

public class DynamicSignButtonBlockEntity extends SignButtonBlockEntity
{
    @ObjectHolder("signbutton:dynamic_sign_button")
    public static BlockEntityType<DynamicSignButtonBlockEntity> TYPE;

    public WoodType woodType;

    public DynamicSignButtonBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType()
    {
        return TYPE;
    }

    @Override
    public WoodType getWoodType()
    {
        return woodType;
    }

    public void setWoodType(WoodType woodType)
    {
        this.woodType = woodType;
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
        tag = super.save(tag);
        tag.putString("WoodType", woodType.name());
        return tag;
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        woodType = SignButtonWoodTypes.get(tag.getString("WoodType"));
    }
}
