package gigaherz.signbutton.button;

import gigaherz.signbutton.ModSignButton;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class SignButtonTileEntity extends SignTileEntity
{
    @ObjectHolder("signbutton:sign_button")
    public static TileEntityType<SignButtonTileEntity> TYPE;

    public SignButtonTileEntity()
    {
        super();
        ModSignButton.logger.warn("TILE CREATED!");
    }

    @Override
    public TileEntityType<?> getType()
    {
        return TYPE;
    }
}