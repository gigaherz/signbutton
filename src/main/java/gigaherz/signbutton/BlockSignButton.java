package gigaherz.signbutton;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.state.properties.BlockStateProperties.FACE;
import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.state.properties.BlockStateProperties.POWERED;

public class BlockSignButton extends BlockSign
{
    private final Map<IBlockState, VoxelShape> cache = Maps.newConcurrentMap();

    public BlockSignButton(Properties properties)
    {
        super(properties);
        this.setDefaultState(this.getStateContainer().getBaseState()
                .with(FACE, AttachFace.FLOOR)
                .with(HORIZONTAL_FACING, EnumFacing.NORTH)
                .with(POWERED, false)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
    {
        builder.add(FACE, HORIZONTAL_FACING, POWERED, WATERLOGGED);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn)
    {
        return new TileSignButton();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(ModSignButton.itemSignButton);
    }

    @Override
    public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune)
    {
        return ModSignButton.itemSignButton;
    }

    @Override
    public int tickRate(IWorldReaderBase worldIn)
    {
        return 30;
    }

    @Deprecated
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public VoxelShape getShape(final IBlockState state, final IBlockReader world, final BlockPos pos)
    {
        VoxelShape cached = cache.get(state);
        
        if(cached != null)
            return cached;

        cached =  VoxelShapes.empty();

        AttachFace face = state.get(FACE);
        EnumFacing enumfacing = state.get(HORIZONTAL_FACING);
        boolean powered = state.get(POWERED);

        // when placed in wall: left/right
        float u0 = 0.0f;
        float u1 = 1.0f;

        // when placed in wall: bottom/top
        float offset = +0.02f;
        float v0 = 0.25f;
        float v1 = 0.75f;

        // when placed in wall: wall/exposed
        float thick = 1.36f / 16.0f;
        float w0 = (powered ? -0.23f : 0.32f) / 16.0f;
        float w1 = w0+thick;
        float t1 = 1 - w0;
        float t0 = 1 - w1;

        if (face == AttachFace.FLOOR)
        {
            switch (enumfacing)
            {
                case NORTH:
                    cached = VoxelShapes.create(u0, w0, v0+offset, u1, w1, v1+offset);
                    break;
                case SOUTH:
                    cached = VoxelShapes.create(u0, w0, v0-offset, u1, w1, v1-offset);
                    break;
                case EAST:
                    cached = VoxelShapes.create(v0-offset, w0, u0, v1-offset, w1, u1);
                    break;
                case WEST:
                    cached = VoxelShapes.create(v0+offset, w0, u0, v1+offset, w1, u1);
                    break;
            }
        }
        else if (face == AttachFace.CEILING)
        {
            switch (enumfacing)
            {
                case NORTH:
                    cached = VoxelShapes.create(u0, t0, v0-offset, u1, t1, v1-offset);
                    break;
                case SOUTH:
                    cached = VoxelShapes.create(u0, t0, v0+offset, u1, t1, v1+offset);
                    break;
                case EAST:
                    cached = VoxelShapes.create(v0+offset, t0, u0, v1+offset, t1, u1);
                    break;
                case WEST:
                    cached = VoxelShapes.create(v0-offset, t0, u0, v1-offset, t1, u1);
                    break;
            }
        }
        else
        {
            switch (enumfacing)
            {
                case EAST:
                    cached = VoxelShapes.create(w0, v0+offset, u0, w1, v1+offset, u1);
                    break;
                case WEST:
                    cached = VoxelShapes.create(t0, v0+offset, u0, t1, v1+offset, u1);
                    break;
                case SOUTH:
                    cached = VoxelShapes.create(u0, v0+offset, w0, u1, v1+offset, w1);
                    break;
                case NORTH:
                    cached = VoxelShapes.create(u0, v0+offset, t0, u1, v1+offset, t1);
                    break;
            }
        }

        cache.put(state, cached);
        return cached;
    }

    @Nullable
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        IBlockState state = this.getDefaultState();
        IFluidState fluid = context.getWorld().getFluidState(context.getPos());
        IWorldReaderBase world = context.getWorld();
        BlockPos pos = context.getPos();

        EnumFacing clickedFace = context.getFace().getOpposite();
        List<EnumFacing> directions = Lists.newArrayList(clickedFace);
        Arrays.stream(context.getNearestLookingDirections()).filter(f -> f != clickedFace).forEach(directions::add);

        ModSignButton.logger.warn(directions.stream().map(EnumFacing::toString).collect(Collectors.joining(",")));

        for (int i = 0; i < directions.size(); i++)
        {
            EnumFacing lookDirection = directions.get(i);
            EnumFacing lookDirection2 = i+1 >= directions.size() ? EnumFacing.NORTH : directions.get(i+1);

            AttachFace face;
            EnumFacing facing;
            if(lookDirection == EnumFacing.DOWN)
            {
                face = AttachFace.FLOOR;
                facing = (lookDirection2.getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : lookDirection2).getOpposite();
            }
            else if(lookDirection == EnumFacing.UP)
            {
                face = AttachFace.CEILING;
                facing = (lookDirection2.getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : lookDirection2).getOpposite();
            }
            else
            {
                face = AttachFace.WALL;
                facing = lookDirection.getOpposite();
            }

            state = state.with(FACE, face).with(HORIZONTAL_FACING, facing);
            if (state.isValidPosition(world, pos))
            {
                return state.with(WATERLOGGED, fluid.getFluid() == Fluids.WATER);
            }
        }

        return null;
    }

    @Deprecated
    @Override
    public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
        EnumFacing enumfacing = getEffectiveFacing(state);
        BlockPos otherPos = pos.offset(enumfacing.getOpposite());
        IBlockState otherState = worldIn.getBlockState(otherPos);
        return otherState.getBlockFaceShape(worldIn, otherPos, enumfacing) == BlockFaceShape.SOLID
                && !isExceptBlockForAttachWithPiston(otherState.getBlock());
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (state.get(POWERED))
        {
            return true;
        }
        else
        {
            worldIn.setBlockState(pos, state.with(POWERED, true));
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
            worldIn.playSound(player, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
            notifyFacing(state, worldIn, pos);
            worldIn.getPendingBlockTicks().scheduleTick(new BlockPos(pos), this, this.tickRate(worldIn));
            return true;
        }
    }

    private void notifyFacing(IBlockState state, World worldIn, BlockPos pos)
    {
        this.notifyNeighbors(worldIn, pos, getEffectiveFacing(state).getOpposite());
    }

    private void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing facing)
    {
        worldIn.notifyNeighborsOfStateChange(pos, this);
        worldIn.notifyNeighborsOfStateChange(pos.offset(facing), this);
    }

    private EnumFacing getEffectiveFacing(IBlockState state)
    {
        switch(state.get(FACE))
        {
            case FLOOR:
                return EnumFacing.UP;
            case CEILING:
                return EnumFacing.DOWN;
            default:
                return state.get(HORIZONTAL_FACING);
        }
    }


    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return facing.getOpposite() == getEffectiveFacing(stateIn) && !stateIn.isValidPosition(worldIn, currentPos)
                ? Blocks.AIR.getDefaultState()
                : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }


    @Override
    public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
        if (!isMoving && state.getBlock() != newState.getBlock()) {
            if (state.get(POWERED)) {
                notifyFacing(state, worldIn, pos);
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Deprecated
    @Override
    public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
        return blockState.get(POWERED) ? 15 : 0;
    }

    @Deprecated
    @Override
    public int getStrongPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
        return blockState.get(POWERED) && getEffectiveFacing(blockState) == side ? 15 : 0;
    }

    @Deprecated
    @Override
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    @Deprecated
    @Override
    public void randomTick(IBlockState state, World worldIn, BlockPos pos, Random random)
    {
    }

    @Deprecated
    @Override
    public void tick(IBlockState state, World worldIn, BlockPos pos, Random random)
    {
        if (!worldIn.isRemote)
        {
            if (state.get(POWERED))
            {
                worldIn.setBlockState(pos, state.with(POWERED, false));
                notifyFacing(state, worldIn, pos);
                worldIn.markBlockRangeForRenderUpdate(pos, pos);
                worldIn.playSound(null, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
            }
        }
    }

    public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isRemote && !state.get(POWERED)) {
            this.checkArrows(state, worldIn, pos);
        }
    }

    private void checkArrows(IBlockState state, World worldIn, BlockPos pos) {
        List<? extends Entity> list = worldIn.getEntitiesWithinAABB(EntityArrow.class, state.getShape(worldIn, pos).getBoundingBox().offset(pos));
        boolean arrowsPresent = !list.isEmpty();
        boolean currentlyPowered = state.get(POWERED);
        if (arrowsPresent != currentlyPowered) {
            worldIn.setBlockState(pos, state.with(POWERED, true));
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
            notifyFacing(state, worldIn, pos);
            worldIn.playSound(null, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
        }

        if (arrowsPresent) {
            worldIn.getPendingBlockTicks().scheduleTick(new BlockPos(pos), this, this.tickRate(worldIn));
        }

    }

}
