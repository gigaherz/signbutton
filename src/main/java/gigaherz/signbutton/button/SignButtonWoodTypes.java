package gigaherz.signbutton.button;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.stream.Stream;

public class SignButtonWoodTypes
{
    private static final ImmutableList<WoodType> supportedWoodTypes = ImmutableList.of(
            WoodType.OAK,
            WoodType.SPRUCE,
            WoodType.BIRCH,
            WoodType.ACACIA,
            WoodType.JUNGLE,
            WoodType.DARK_OAK,
            WoodType.CRIMSON,
            WoodType.WARPED
    );

    public static Stream<WoodType> values()
    {
        return supportedWoodTypes.stream();
    }
}
