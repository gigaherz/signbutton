package dev.gigaherz.signbutton.button;

import dev.gigaherz.signbutton.network.OpenSignButtonEditor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

public class SignButtonItem extends BlockItem
{
    public SignButtonItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level worldIn, @Nullable Player player, ItemStack stack, BlockState state)
    {
        boolean flag = super.updateCustomBlockEntityTag(pos, worldIn, player, stack, state);
        if (!worldIn.isClientSide && !flag && player != null)
        {
            BlockEntity te = worldIn.getBlockEntity(pos);
            if (te instanceof SignButtonBlockEntity sbe)
            {
                sbe.setAllowedPlayerEditor(player.getUUID());
                PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenSignButtonEditor(pos));
            }
        }
        return flag;
    }
}
