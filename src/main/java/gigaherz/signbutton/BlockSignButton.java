package gigaherz.signbutton;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockSignButton extends BlockSign {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockSignButton() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, false));
        this.setTickRandomly(true);
        setHardness(0.5F);
        setSoundType(SoundType.WOOD);
        setRegistryName("signButton");
        setUnlocalizedName("signButton");
        this.setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileSignButton();
    }

    @Override
    public Item getItem(World worldIn, BlockPos pos) {
        return ModSignButton.itemSignButton;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return null;
    }

    @Override
    public int tickRate(World worldIn) {
        return 30;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return worldIn.isSideSolid(pos.offset(side.getOpposite()), side, true);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing enumfacing : EnumFacing.values()) {
            if (worldIn.isSideSolid(pos.offset(enumfacing), enumfacing.getOpposite(), true)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return worldIn.isSideSolid(pos.offset(facing.getOpposite()), facing, true) ?
                this.getDefaultState().withProperty(FACING, facing).withProperty(POWERED, false) :
                this.getDefaultState().withProperty(FACING, EnumFacing.DOWN).withProperty(POWERED, false);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (this.checkForDrop(worldIn, pos, state)) {
            EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

            if (!worldIn.isSideSolid(pos.offset(enumfacing.getOpposite()), enumfacing, true)) {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    @Override
    private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canPlaceBlockAt(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        this.updateBlockBounds(worldIn.getBlockState(pos));
    }

    @Override
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

    @Override
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

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if ((Boolean) state.getValue(POWERED)) {
            this.notifyNeighbors(worldIn, pos, (EnumFacing) state.getValue(FACING));
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
        return (Boolean) state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
        return !(Boolean) state.getValue(POWERED) ? 0 : (state.getValue(FACING) == side ? 15 : 0);
    }

    @Override
    public boolean canProvidePower() {
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
            if ((Boolean) state.getValue(POWERED))
            {
                worldIn.setBlockState(pos, state.withProperty(POWERED, false));
                notifyNeighbors(worldIn, pos, (EnumFacing) state.getValue(FACING));
                worldIn.markBlockRangeForRenderUpdate(pos, pos);
                worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, "random.click", 0.3F, 0.5F);
            }
        }
    }

    @Override
    public void setBlockBoundsForItemRender()
    {
        float f = 0.1875F;
        float f1 = 0.125F;
        float f2 = 0.125F;
        this.setBlockBounds(0.5F - f, 0.5F - f1, 0.5F - f2, 0.5F + f, 0.5F + f1, 0.5F + f2);
    }

    @Override
    private void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing facing)
    {
        worldIn.notifyNeighborsOfStateChange(pos, this);
        worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        int f = meta & 7;
        if(f > EnumFacing.VALUES.length)
            f = 0;
        EnumFacing enumfacing = EnumFacing.VALUES[f];

        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, (meta & 8) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = state.getValue(FACING).ordinal();

        if (state.getValue(POWERED)) {
            i |= 8;
        }

        return i;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, POWERED);
    }

    @Override
    public net.minecraft.item.Item getItemDropped(IBlockState state, Random rnd, int fortune) {
        return ModSignButton.itemSignButton;
    }
}
