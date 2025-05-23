package dev.gigaherz.signbutton.button;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class SignButtonBlock extends SignBlock implements EntityBlock
{
    public static final MapCodec<StandingSignBlock> CODEC = RecordCodecBuilder.mapCodec(
            p_308840_ -> p_308840_.group(WoodType.CODEC.fieldOf("wood_type").forGetter(SignBlock::type), propertiesCodec())
                    .apply(p_308840_, StandingSignBlock::new)
    );

    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final int TICK_RATE = 30;

    private final Map<BlockState, VoxelShape> cache = Maps.newConcurrentMap();

    public SignButtonBlock(Properties properties, WoodType woodType)
    {
        super(woodType, properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACE, AttachFace.FLOOR)
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACE, FACING, POWERED, WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new SignButtonBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx)
    {
        VoxelShape cached = cache.get(state);

        if (cached == null)
        {
            cached = Shapes.empty();

            AttachFace face = state.getValue(FACE);
            Direction enumfacing = state.getValue(FACING);
            boolean powered = state.getValue(POWERED);

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
                        cached = Shapes.box(u0, w0, v0 + offset, u1, w1, v1 + offset);
                        break;
                    case SOUTH:
                        cached = Shapes.box(u0, w0, v0 - offset, u1, w1, v1 - offset);
                        break;
                    case EAST:
                        cached = Shapes.box(v0 - offset, w0, u0, v1 - offset, w1, u1);
                        break;
                    case WEST:
                        cached = Shapes.box(v0 + offset, w0, u0, v1 + offset, w1, u1);
                        break;
                }
            }
            else if (face == AttachFace.CEILING)
            {
                switch (enumfacing)
                {
                    case NORTH:
                        cached = Shapes.box(u0, t0, v0 - offset, u1, t1, v1 - offset);
                        break;
                    case SOUTH:
                        cached = Shapes.box(u0, t0, v0 + offset, u1, t1, v1 + offset);
                        break;
                    case EAST:
                        cached = Shapes.box(v0 + offset, t0, u0, v1 + offset, t1, u1);
                        break;
                    case WEST:
                        cached = Shapes.box(v0 - offset, t0, u0, v1 - offset, t1, u1);
                        break;
                }
            }
            else
            {
                switch (enumfacing)
                {
                    case EAST:
                        cached = Shapes.box(w0, v0 + offset, u0, w1, v1 + offset, u1);
                        break;
                    case WEST:
                        cached = Shapes.box(t0, v0 + offset, u0, t1, v1 + offset, u1);
                        break;
                    case SOUTH:
                        cached = Shapes.box(u0, v0 + offset, w0, u1, v1 + offset, w1);
                        break;
                    case NORTH:
                        cached = Shapes.box(u0, v0 + offset, t0, u1, v1 + offset, t1);
                        break;
                }
            }

            cache.put(state, cached);
        }

        return cached;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = this.defaultBlockState();
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        LevelReader world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        Direction clickedFace = context.getClickedFace().getOpposite();
        List<Direction> directions = Lists.newArrayList(clickedFace);
        Arrays.stream(context.getNearestLookingDirections()).filter(f -> f != clickedFace).forEach(directions::add);

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

            state = state.setValue(FACE, face).setValue(FACING, facing);
            if (state.canSurvive(world, pos))
            {
                return state.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
            }
        }

        return null;
    }

    @Deprecated
    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.relative(getEffectiveFacing(state).getOpposite())).isSolid();
    }

    private Direction getEffectiveFacing(BlockState state)
    {
        return switch (state.getValue(FACE))
        {
            case FLOOR -> Direction.UP;
            case CEILING -> Direction.DOWN;
            default -> state.getValue(FACING);
        };
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (!switch (state.getValue(FACE))
        {
            case WALL -> (hit.getDirection() == state.getValue(FACING));
            case FLOOR -> (hit.getDirection() == Direction.UP);
            case CEILING -> (hit.getDirection() == Direction.DOWN);
        })
        {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }

        if (!state.getValue(POWERED))
        {
            level.setBlockAndUpdate(pos, state.setValue(POWERED, true));
            //level.markForRerender(pos);
            level.playSound(player, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
            notifyFacing(state, level, pos);
            level.scheduleTick(new BlockPos(pos), this, TICK_RATE);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public float getYRotationDegrees(BlockState state)
    {
        return state.getValue(FACING).toYRot();
    }

    private void notifyFacing(BlockState state, Level level, BlockPos pos)
    {
        notifyNeighbors(level, pos, getEffectiveFacing(state).getOpposite());
    }

    private void notifyNeighbors(Level level, BlockPos pos, Direction facing)
    {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.relative(facing), this);
    }

    @Deprecated
    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
    {
        return blockState.getValue(POWERED) ? 15 : 0;
    }

    @Deprecated
    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
    {
        return blockState.getValue(POWERED) && getEffectiveFacing(blockState) == side ? 15 : 0;
    }

    @Deprecated
    @Override
    public boolean isSignalSource(BlockState state)
    {
        return true;
    }

    @Override
    protected MapCodec<? extends SignBlock> codec()
    {
        return CODEC;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess,
                                     BlockPos currentPos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource randomSource)
    {
        return facing.getOpposite() == getEffectiveFacing(state) && !state.canSurvive(level, currentPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, level, tickAccess, currentPos, facing, facingPos, facingState, randomSource);
    }


    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean something)
    {
        if (state.getValue(POWERED))
        {
            notifyFacing(state, level, pos);
        }

        super.affectNeighborsAfterRemoval(state, level, pos, something);
    }

    @Deprecated
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (!level.isClientSide)
        {
            if (state.getValue(POWERED))
            {
                level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
                notifyFacing(state, level, pos);
                //level.markForRerender(pos);
                level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.5F);
            }
        }
    }

    @Deprecated
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier)
    {
        if (!level.isClientSide && !state.getValue(POWERED))
        {
            this.checkArrows(state, level, pos);
        }
    }

    private void checkArrows(BlockState state, Level level, BlockPos pos)
    {
        List<? extends Entity> list = level.getEntitiesOfClass(AbstractArrow.class, state.getShape(level, pos).bounds().move(pos));
        boolean arrowsPresent = !list.isEmpty();
        boolean currentlyPowered = state.getValue(POWERED);
        if (arrowsPresent != currentlyPowered)
        {
            level.setBlockAndUpdate(pos, state.setValue(POWERED, true));
            //level.markForRerender(pos);
            notifyFacing(state, level, pos);
            level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
        }

        if (arrowsPresent)
        {
            level.scheduleTick(new BlockPos(pos), this, TICK_RATE);
        }
    }
}

