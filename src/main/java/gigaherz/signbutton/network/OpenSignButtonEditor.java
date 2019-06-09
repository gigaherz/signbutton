package gigaherz.signbutton.network;

import gigaherz.signbutton.client.ClientUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenSignButtonEditor
{
    public BlockPos pos;

    public OpenSignButtonEditor(BlockPos pos)
    {
        this.pos = pos;
    }

    public OpenSignButtonEditor(PacketBuffer buf)
    {
        pos = buf.readBlockPos();
    }

    public void encode(PacketBuffer buf)
    {
        buf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> context)
    {
        ClientUtils.openSignButtonGui(pos);
    }
}
