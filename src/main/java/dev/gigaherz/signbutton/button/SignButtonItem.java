package dev.gigaherz.signbutton.button;

import dev.gigaherz.signbutton.ModSignButton;
import dev.gigaherz.signbutton.network.OpenSignButtonEditor;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;

import javax.annotation.Nullable;
import java.util.List;

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
                ModSignButton.channel.sendTo(new OpenSignButtonEditor(pos), ((ServerPlayer) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
        return flag;
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, world, list, flag);

        CompoundTag stackNbt = itemstack.getOrCreateTag();
        if (stackNbt.contains("upgrade", Tag.TAG_STRING)) {
            list.add(Component.translatable(stackNbt.getString("upgrade")));
        }
    }
}

