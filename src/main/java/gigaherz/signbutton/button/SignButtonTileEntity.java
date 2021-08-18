package gigaherz.signbutton.button;

import gigaherz.signbutton.ModSignButton;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ObjectHolder;

public class SignButtonTileEntity extends SignBlockEntity
{
    @ObjectHolder("signbutton:sign_button")
    public static BlockEntityType<SignButtonTileEntity> TYPE;

    public SignButtonTileEntity(BlockPos pos, BlockState state)
    {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType()
    {
        return TYPE;
    }
}