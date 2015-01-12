package gigaherz.signbutton;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSignButton extends Item {

    public ItemSignButton() {
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        pos = pos.offset(side);

        if (stack.stackSize == 0) {
            return false;
        } else if (!playerIn.canPlayerEdit(pos, side, stack)) {
            return false;
        } else if (!ModSignButton.signButton.canPlaceBlockAt(worldIn, pos)) {
            return false;
        } else if (worldIn.isRemote) {
            return true;
        } else {
            worldIn.setBlockState(pos, ModSignButton.signButton.getDefaultState().withProperty(BlockSignButton.FACING, side), 3);

            --stack.stackSize;
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileSignButton && !ItemBlock.setTileEntityNBT(worldIn, pos, stack)) {
                playerIn.openEditSign((TileSignButton) tileentity);
            }

            return true;
        }
    }
}