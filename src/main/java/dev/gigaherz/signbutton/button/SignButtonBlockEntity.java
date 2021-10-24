package dev.gigaherz.signbutton.button;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.ObjectHolder;

public class SignButtonBlockEntity extends SignBlockEntity
{
    @ObjectHolder("signbutton:sign_button")
    public static BlockEntityType<SignButtonBlockEntity> TYPE;

    public SignButtonBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType()
    {
        return TYPE;
    }

    public WoodType getWoodType()
    {
        if (getBlockState().getBlock() instanceof SignButtonBlock block)
            return block.type();
        return WoodType.OAK;
    }

}
