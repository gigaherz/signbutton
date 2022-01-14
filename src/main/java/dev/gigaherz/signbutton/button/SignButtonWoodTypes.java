package dev.gigaherz.signbutton.button;

import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class SignButtonWoodTypes
{
    public static final Set<WoodType> SUPPORTED_WOOD_TYPES = new HashSet<>();

    public static Stream<WoodType> supported()
    {
        return SUPPORTED_WOOD_TYPES.stream();
    }

    public static WoodType setSupported(WoodType woodType)
    {
        SUPPORTED_WOOD_TYPES.add(woodType);
        return woodType;
    }
}
