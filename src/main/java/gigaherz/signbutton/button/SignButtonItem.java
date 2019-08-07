package gigaherz.signbutton.button;

import gigaherz.signbutton.ModSignButton;
import gigaherz.signbutton.network.OpenSignButtonEditor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

import javax.annotation.Nullable;

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
}