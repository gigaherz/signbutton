package dev.gigaherz.signbutton.button;

import dev.gigaherz.signbutton.ModSignButton;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.ObjectHolder;

public class SignButtonBlockEntity extends SignBlockEntity
{
    public SignButtonBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModSignButton.SIGN_BUTTON_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public BlockEntityType<?> getType()
    {
        return ModSignButton.SIGN_BUTTON_BLOCK_ENTITY.get();
    }

    public WoodType getWoodType()
    {
        if (getBlockState().getBlock() instanceof SignButtonBlock block)
            return block.type();
        return WoodType.OAK;
    }
}
