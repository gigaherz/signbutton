package dev.gigaherz.signbutton.button;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.state.properties.WoodType;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class SignButtonWoodTypes
{
    public static final ImmutableList<WoodType> SUPPORTED_WOOD_TYPES = ImmutableList.of(
            WoodType.OAK,
            WoodType.SPRUCE,
            WoodType.BIRCH,
            WoodType.ACACIA,
            WoodType.JUNGLE,
            WoodType.DARK_OAK,
            WoodType.CRIMSON,
            WoodType.WARPED
    );

    public static final Set<WoodType> FOUND_WOOD_TYPES = new HashSet<>(SUPPORTED_WOOD_TYPES);

    public static Stream<WoodType> stock()
    {
        return FOUND_WOOD_TYPES.stream();
    }

    public static Stream<WoodType> found()
    {
        return FOUND_WOOD_TYPES.stream();
    }

    public static void visit(WoodType woodType)
    {
        FOUND_WOOD_TYPES.add(woodType);
    }

    public static WoodType get(@Nullable String woodType)
    {
        if (woodType == null)
            return WoodType.OAK;
        return FOUND_WOOD_TYPES.stream().filter(wt -> wt.name().equals(woodType)).findFirst().orElse(WoodType.OAK);
    }
}
