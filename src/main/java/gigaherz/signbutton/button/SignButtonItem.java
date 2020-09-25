package gigaherz.signbutton.button;

import gigaherz.signbutton.ModSignButton;
import gigaherz.signbutton.network.OpenSignButtonEditor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkDirection;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class SignButtonItem extends BlockItem
{
    public SignButtonItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state)
    {
        boolean flag = super.onBlockPlaced(pos, worldIn, player, stack, state);
        if (!worldIn.isRemote && !flag && player != null)
        {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof SignButtonTileEntity)
            {
                ((SignButtonTileEntity) te).setPlayer(player);
                ModSignButton.channel.sendTo(new OpenSignButtonEditor(pos), ((ServerPlayerEntity) player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
        return flag;
    }

    @Override
    public void addInformation(ItemStack itemstack, World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(itemstack, world, list, flag);

        CompoundNBT stackNbt = itemstack.getOrCreateTag();
        if (stackNbt.contains("upgrade", Constants.NBT.TAG_STRING)) {
            list.add(new TranslationTextComponent(stackNbt.getString("upgrade")));
        }
    }
}