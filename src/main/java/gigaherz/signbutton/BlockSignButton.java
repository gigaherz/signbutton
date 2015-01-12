package gigaherz.signbutton;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockSignButton extends BlockSign {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockSignButton() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, Boolean.valueOf(false)));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileSignButton();
    }


    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, BlockPos pos) {
        return ModSignButton.itemSignButton;
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return null;
    }

    public int tickRate(World worldIn) {
        return 30;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return worldIn.isSideSolid(pos.offset(side.getOpposite()), side, true);
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing enumfacing : EnumFacing.values()) {
            if (worldIn.isSideSolid(pos.offset(enumfacing), enumfacing.getOpposite(), true)) {
                return true;
            }
        }

        return false;
    }

    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return worldIn.isSideSolid(pos.offset(facing.getOpposite()), facing, true) ?
                this.getDefaultState().withProperty(FACING, facing).withProperty(POWERED, false) :
                this.getDefaultState().withProperty(FACING, EnumFacing.DOWN).withProperty(POWERED, Boolean.valueOf(false));
    }

    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (this.checkForDrop(worldIn, pos, state)) {
            EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

            if (!worldIn.isSideSolid(pos.offset(enumfacing.getOpposite()), enumfacing, true)) {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canPlaceBlockAt(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return false;
        } else {
            return true;
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        this.updateBlockBounds(worldIn.getBlockState(pos));
    }

    private void updateBlockBounds(IBlockState state) {
        EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);
        boolean powered = (Boolean) state.getValue(POWERED);

        float width = 16 / 16.0f;
        float height = 8 / 16.0f;
        float thick = powered ? 1 / 16.0f : 2 / 16.0f;
        float down = 1 - thick;

        float left = (1 - width) / 2;
        float right = 1 - left;
        float bottom = (1 - height) / 2;
        float top = 1 - bottom;

        switch (SwitchEnumFacing.FACING_LOOKUP[enumfacing.ordinal()]) {
            case 1:
                this.setBlockBounds(0.0F, bottom, left,
                        thick, top, right);
                break;
            case 2:
                this.setBlockBounds(down, bottom, left,
                        1.0F, top, right);
                break;
            case 3:
                this.setBlockBounds(left, bottom, 0.0F,
                        right, top, thick);
                break;
            case 4:
                this.setBlockBounds(left, bottom, down,
                        right, top, 1.0F);
                break;
            case 5:
                this.setBlockBounds(left, 0.0F, bottom,
                        right, thick, top);
                break;
            case 6:
                this.setBlockBounds(left, down, bottom,
                        right, 1.0F, top);
        }
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if ((Boolean) state.getValue(POWERED)) {
            return true;
        } else {
            worldIn.setBlockState(pos, state.withProperty(POWERED, true), 3);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
            worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, "random.click", 0.3F, 0.6F);
            this.notifyNeighbors(worldIn, pos, (EnumFacing) state.getValue(FACING));
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            return true;
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if ((Boolean) state.getValue(POWERED)) {
            this.notifyNeighbors(worldIn, pos, (EnumFacing) state.getValue(FACING));
        }

        super.breakBlock(worldIn, pos, state);
    }

    public int isProvidingWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
        return (Boolean) state.getValue(POWERED) ? 15 : 0;
    }

    public int isProvidingStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
        return !(Boolean) state.getValue(POWERED) ? 0 : (state.getValue(FACING) == side ? 15 : 0);
    }

    public boolean canProvidePower() {
        return true;
    }

    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            if ((Boolean) state.getValue(POWERED)) {
                worldIn.setBlockState(pos, state.withProperty(POWERED, false));
                notifyNeighbors(worldIn, pos, (EnumFacing) state.getValue(FACING));
                worldIn.markBlockRangeForRenderUpdate(pos, pos);
                worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, "random.click", 0.3F, 0.5F);
            }
        }
    }

    public void setBlockBoundsForItemRender() {
        float f = 0.1875F;
        float f1 = 0.125F;
        float f2 = 0.125F;
        this.setBlockBounds(0.5F - f, 0.5F - f1, 0.5F - f2, 0.5F + f, 0.5F + f1, 0.5F + f2);
    }

    private void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing facing) {
        worldIn.notifyNeighborsOfStateChange(pos, this);
        worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this);
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing;

        switch (meta & 7) {
            case 0:
                enumfacing = EnumFacing.DOWN;
                break;
            case 1:
                enumfacing = EnumFacing.EAST;
                break;
            case 2:
                enumfacing = EnumFacing.WEST;
                break;
            case 3:
                enumfacing = EnumFacing.SOUTH;
                break;
            case 4:
                enumfacing = EnumFacing.NORTH;
                break;
            case 5:
            default:
                enumfacing = EnumFacing.UP;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, (meta & 8) > 0);
    }

    public int getMetaFromState(IBlockState state) {
        int i;

        switch (SwitchEnumFacing.FACING_LOOKUP[((EnumFacing) state.getValue(FACING)).ordinal()]) {
            case 1:
                i = 1;
                break;
            case 2:
                i = 2;
                break;
            case 3:
                i = 3;
                break;
            case 4:
                i = 4;
                break;
            case 5:
            default:
                i = 5;
                break;
            case 6:
                i = 0;
        }

        if ((Boolean) state.getValue(POWERED)) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, POWERED);
    }

    @Override
    public net.minecraft.item.Item getItemDropped(IBlockState state, Random rnd, int fortune) {
        return ModSignButton.itemSignButton;
    }

    static final class SwitchEnumFacing {
        static final int[] FACING_LOOKUP = new int[EnumFacing.values().length];

        static {
            try {
                FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 1;
            } catch (NoSuchFieldError var6) {
                ;
            }

            try {
                FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 2;
            } catch (NoSuchFieldError var5) {
                ;
            }

            try {
                FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 3;
            } catch (NoSuchFieldError var4) {
                ;
            }

            try {
                FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 4;
            } catch (NoSuchFieldError var3) {
                ;
            }

            try {
                FACING_LOOKUP[EnumFacing.UP.ordinal()] = 5;
            } catch (NoSuchFieldError var2) {
                ;
            }

            try {
                FACING_LOOKUP[EnumFacing.DOWN.ordinal()] = 6;
            } catch (NoSuchFieldError var1) {
                ;
            }
        }
    }
}
