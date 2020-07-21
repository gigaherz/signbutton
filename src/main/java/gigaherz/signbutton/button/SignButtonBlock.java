package gigaherz.signbutton.button;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gigaherz.signbutton.ModSignButton;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.block.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class SignButtonBlock extends AbstractSignBlock
{
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.FACE;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private final Map<BlockState, VoxelShape> cache = Maps.newConcurrentMap();

    public SignButtonBlock(Properties properties)
    {
        super(properties);
        this.setDefaultState(this.getStateContainer().getBaseState()
                .with(FACE, AttachFace.FLOOR)
                .with(FACING, Direction.NORTH)
                .with(POWERED, false)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACE, FACING, POWERED, WATERLOGGED);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn)
    {
        return new SignButtonTileEntity();
    }

    @Override
    public int tickRate(IWorldReader worldIn)
    {
        return 30;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
    {
        VoxelShape cached = cache.get(state);

        if (cached == null)
        {
            cached = VoxelShapes.empty();

            AttachFace face = state.get(FACE);
            Direction enumfacing = state.get(FACING);
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
            float w1 = w0 + thick;
            float t1 = 1 - w0;
            float t0 = 1 - w1;

            if (face == AttachFace.FLOOR)
            {
                switch (enumfacing)
                {
                    case NORTH:
                        cached = VoxelShapes.create(u0, w0, v0 + offset, u1, w1, v1 + offset);
                        break;
                    case SOUTH:
                        cached = VoxelShapes.create(u0, w0, v0 - offset, u1, w1, v1 - offset);
                        break;
                    case EAST:
                        cached = VoxelShapes.create(v0 - offset, w0, u0, v1 - offset, w1, u1);
                        break;
                    case WEST:
                        cached = VoxelShapes.create(v0 + offset, w0, u0, v1 + offset, w1, u1);
                        break;
                }
            }
            else if (face == AttachFace.CEILING)
            {
                switch (enumfacing)
                {
                    case NORTH:
                        cached = VoxelShapes.create(u0, t0, v0 - offset, u1, t1, v1 - offset);
                        break;
                    case SOUTH:
                        cached = VoxelShapes.create(u0, t0, v0 + offset, u1, t1, v1 + offset);
                        break;
                    case EAST:
                        cached = VoxelShapes.create(v0 + offset, t0, u0, v1 + offset, t1, u1);
                        break;
                    case WEST:
                        cached = VoxelShapes.create(v0 - offset, t0, u0, v1 - offset, t1, u1);
                        break;
                }
            }
            else
            {
                switch (enumfacing)
                {
                    case EAST:
                        cached = VoxelShapes.create(w0, v0 + offset, u0, w1, v1 + offset, u1);
                        break;
                    case WEST:
                        cached = VoxelShapes.create(t0, v0 + offset, u0, t1, v1 + offset, u1);
                        break;
                    case SOUTH:
                        cached = VoxelShapes.create(u0, v0 + offset, w0, u1, v1 + offset, w1);
                        break;
                    case NORTH:
                        cached = VoxelShapes.create(u0, v0 + offset, t0, u1, v1 + offset, t1);
                        break;
                }
            }

            cache.put(state, cached);
        }

        return cached;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = this.getDefaultState();
        IFluidState fluid = context.getWorld().getFluidState(context.getPos());
        IWorldReader world = context.getWorld();
        BlockPos pos = context.getPos();

        Direction clickedFace = context.getFace().getOpposite();
        List<Direction> directions = Lists.newArrayList(clickedFace);
        Arrays.stream(context.getNearestLookingDirections()).filter(f -> f != clickedFace).forEach(directions::add);

        ModSignButton.logger.warn(directions.stream().map(Direction::toString).collect(Collectors.joining(",")));

        for (int i = 0; i < directions.size(); i++)
        {
            Direction lookDirection = directions.get(i);
            Direction lookDirection2 = i + 1 >= directions.size() ? Direction.NORTH : directions.get(i + 1);

            AttachFace face;
            Direction facing;
            if (lookDirection == Direction.DOWN)
            {
                face = AttachFace.FLOOR;
                facing = (lookDirection2.getAxis() == Direction.Axis.Y ? Direction.NORTH : lookDirection2).getOpposite();
            }
            else if (lookDirection == Direction.UP)
            {
                face = AttachFace.CEILING;
                facing = (lookDirection2.getAxis() == Direction.Axis.Y ? Direction.NORTH : lookDirection2).getOpposite();
            }
            else
            {
                face = AttachFace.WALL;
                facing = lookDirection.getOpposite();
            }

            state = state.with(FACE, face).with(FACING, facing);
            if (state.isValidPosition(world, pos))
            {
                return state.with(WATERLOGGED, fluid.getFluid() == Fluids.WATER);
            }
        }

        return null;
    }

    @Deprecated
    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        return world.getBlockState(pos.offset(getEffectiveFacing(state).getOpposite())).getMaterial().isSolid();
    }

    private Direction getEffectiveFacing(BlockState state)
    {
        switch (state.get(FACE))
        {
            case FLOOR:
                return Direction.UP;
            case CEILING:
                return Direction.DOWN;
            default:
                return state.get(FACING);
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (hit.getFace() != state.get(FACING))
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);

        if (!state.get(POWERED))
        {
            worldIn.setBlockState(pos, state.with(POWERED, true));
            //worldIn.markForRerender(pos);
            worldIn.playSound(player, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
            notifyFacing(state, worldIn, pos);
            worldIn.getPendingBlockTicks().scheduleTick(new BlockPos(pos), this, this.tickRate(worldIn));
        }

        return true;
    }

    private void notifyFacing(BlockState state, World worldIn, BlockPos pos)
    {
        notifyNeighbors(worldIn, pos, getEffectiveFacing(state).getOpposite());
    }

    private void notifyNeighbors(World worldIn, BlockPos pos, Direction facing)
    {
        worldIn.notifyNeighborsOfStateChange(pos, this);
        worldIn.notifyNeighborsOfStateChange(pos.offset(facing), this);
    }

    @Deprecated
    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return blockState.get(POWERED) ? 15 : 0;
    }

    @Deprecated
    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return blockState.get(POWERED) && getEffectiveFacing(blockState) == side ? 15 : 0;
    }

    @Deprecated
    @Override
    public boolean canProvidePower(BlockState state)
    {
        return true;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return facing.getOpposite() == getEffectiveFacing(stateIn) && !stateIn.isValidPosition(worldIn, currentPos)
                ? Blocks.AIR.getDefaultState()
                : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Deprecated
    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!isMoving && state.getBlock() != newState.getBlock())
        {
            if (state.get(POWERED))
            {
                notifyFacing(state, worldIn, pos);
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Deprecated
    @Override
    public void randomTick(BlockState state, World worldIn, BlockPos pos, Random random)
    {
    }

    @Deprecated
    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, Random random)
    {
        if (!worldIn.isRemote)
        {
            if (state.get(POWERED))
            {
                worldIn.setBlockState(pos, state.with(POWERED, false));
                notifyFacing(state, worldIn, pos);
                //worldIn.markForRerender(pos);
                worldIn.playSound(null, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
            }
        }
    }

    @Deprecated
    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (!worldIn.isRemote && !state.get(POWERED))
        {
            this.checkArrows(state, worldIn, pos);
        }
    }

    private void checkArrows(BlockState state, World worldIn, BlockPos pos)
    {
        List<? extends Entity> list = worldIn.getEntitiesWithinAABB(AbstractArrowEntity.class, state.getShape(worldIn, pos).getBoundingBox().offset(pos));
        boolean arrowsPresent = !list.isEmpty();
        boolean currentlyPowered = state.get(POWERED);
        if (arrowsPresent != currentlyPowered)
        {
            worldIn.setBlockState(pos, state.with(POWERED, true));
            //worldIn.markForRerender(pos);
            notifyFacing(state, worldIn, pos);
            worldIn.playSound(null, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
        }

        if (arrowsPresent)
        {
            worldIn.getPendingBlockTicks().scheduleTick(new BlockPos(pos), this, this.tickRate(worldIn));
        }
    }
}
