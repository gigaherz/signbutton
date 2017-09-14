package gigaherz.signbutton;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockSignButton extends BlockSign
{

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockSignButton()
    {
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, false));
        this.setTickRandomly(true);
        setHardness(0.5F);
        setSoundType(SoundType.WOOD);
        setRegistryName("sign_button");
        setUnlocalizedName("sign_button");
        this.setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileSignButton();
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ModSignButton.itemSignButton);
    }

    @Override
    public int tickRate(World worldIn)
    {
        return 30;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        EnumFacing enumfacing = state.getValue(FACING);
        boolean powered = state.getValue(POWERED);

        float width = 16 / 16.0f;
        float height = 8 / 16.0f;
        float thick = powered ? 1 / 16.0f : 2 / 16.0f;
        float down = 1 - thick;

        float left = (1 - width) / 2;
        float right = 1 - left;
        float bottom = (1 - height) / 2;
        float top = 1 - bottom;

        switch (enumfacing)
        {
            case WEST:
                return new AxisAlignedBB(0.0F, bottom, left, thick, top, right);
            case EAST:
                return new AxisAlignedBB(down, bottom, left, 1.0F, top, right);
            case NORTH:
                return new AxisAlignedBB(left, bottom, 0.0F, right, top, thick);
            case SOUTH:
                return new AxisAlignedBB(left, bottom, down, right, top, 1.0F);
            case DOWN:
                return new AxisAlignedBB(left, 0.0F, bottom, right, thick, top);
            case UP:
                return new AxisAlignedBB(left, down, bottom, right, 1.0F, top);
        }

        return NULL_AABB;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return worldIn.getBlockState(pos.offset(side.getOpposite())).getBlockFaceShape(worldIn, pos, side) == BlockFaceShape.SOLID;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        for (EnumFacing enumfacing : EnumFacing.values())
        {
            if (canPlaceBlockOnSide(worldIn, pos, enumfacing))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        if (canPlaceBlockOnSide(world, pos, facing))
        {
            return this.getDefaultState().withProperty(FACING, facing.getOpposite());
        }
        else
        {
            for (EnumFacing enumfacing : EnumFacing.values())
            {
                if (canPlaceBlockOnSide(world, pos, enumfacing))
                {
                    return this.getDefaultState().withProperty(FACING, enumfacing.getOpposite());
                }
            }

            return this.getDefaultState();
        }
    }

    @Deprecated
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (this.checkForDrop(worldIn, pos, state))
        {
            EnumFacing enumfacing = state.getValue(FACING);

            if (!canPlaceBlockOnSide(worldIn, pos, enumfacing.getOpposite()))
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.canPlaceBlockAt(worldIn, pos))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (state.getValue(POWERED))
        {
            return true;
        }
        else
        {
            worldIn.setBlockState(pos, state.withProperty(POWERED, true));
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
            worldIn.playSound(playerIn, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
            this.notifyNeighbors(worldIn, pos, state.getValue(FACING).getOpposite());
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            return true;
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (state.getValue(POWERED))
        {
            this.notifyNeighbors(worldIn, pos, state.getValue(FACING));
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Deprecated
    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return blockState.getValue(POWERED) ? 15 : 0;
    }

    @Deprecated
    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return !blockState.getValue(POWERED) ? 0 : (blockState.getValue(FACING) == side.getOpposite() ? 15 : 0);
    }

    @Deprecated
    @Override
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            if (state.getValue(POWERED))
            {
                worldIn.setBlockState(pos, state.withProperty(POWERED, false));
                notifyNeighbors(worldIn, pos, state.getValue(FACING).getOpposite());
                worldIn.markBlockRangeForRenderUpdate(pos, pos);
                worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
            }
        }
    }

    private void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing facing)
    {
        worldIn.notifyNeighborsOfStateChange(pos, this, false);
        worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this, false);
    }

    @Deprecated
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        int f = meta & 7;
        if (f > EnumFacing.VALUES.length)
            f = 0;
        EnumFacing enumfacing = EnumFacing.VALUES[f];

        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, (meta & 8) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = state.getValue(FACING).ordinal();

        if (state.getValue(POWERED))
        {
            i |= 8;
        }

        return i;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, POWERED);
    }

    @Override
    public net.minecraft.item.Item getItemDropped(IBlockState state, Random rnd, int fortune)
    {
        return ModSignButton.itemSignButton;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}
