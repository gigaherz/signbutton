package gigaherz.signbutton;

import gigaherz.signbutton.network.OpenSignButtonEditor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

import javax.annotation.Nullable;

public class ItemSignButton extends ItemBlock
{
    public ItemSignButton(Properties properties)
    {
        super(ModSignButton.signButton, properties);
    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable EntityPlayer player, ItemStack stack, IBlockState state) {
        boolean flag = super.onBlockPlaced(pos, worldIn, player, stack, state);
        if (!worldIn.isRemote && !flag && player != null)
        {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileSignButton)
            {
                ((TileSignButton)te).setPlayer(player);
                ModSignButton.channel.sendTo(new OpenSignButtonEditor(pos), ((EntityPlayerMP) player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
        return flag;
    }
}