package dev.gigaherz.signbutton.network;

import dev.gigaherz.signbutton.client.ClientUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenSignButtonEditor
{
    public BlockPos pos;

    public OpenSignButtonEditor(BlockPos pos)
    {
        this.pos = pos;
    }

    public OpenSignButtonEditor(FriendlyByteBuf buf)
    {
        pos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> context)
    {
        ClientUtils.openSignButtonGui(pos);
    }
}