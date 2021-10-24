package dev.gigaherz.signbutton.button;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class DynamicSignButtonBlock extends SignButtonBlock
{
    public DynamicSignButtonBlock(Properties properties)
    {
        super(properties, WoodType.OAK);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new DynamicSignButtonBlockEntity(pos, state);
    }

    @Override
    public void fillItemCategory(CreativeModeTab p_49812_, NonNullList<ItemStack> p_49813_)
    {

    }

    public ItemStack makeStack(WoodType woodType)
    {
        var stack = new ItemStack(this);
        var tag = stack.getOrCreateTagElement("BlockEntityTag");
        tag.putString("WoodType", woodType.name());
        return stack;
    }
}
