package dev.gigaherz.signbutton.network;

import dev.gigaherz.signbutton.client.ClientUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class OpenSignButtonEditor implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation("signbutton","update_spell_sequence");

    public BlockPos pos;

    public OpenSignButtonEditor(BlockPos pos)
    {
        this.pos = pos;
    }

    public OpenSignButtonEditor(FriendlyByteBuf buf)
    {
        pos = buf.readBlockPos();
    }

    public void write(FriendlyByteBuf buf)
    {
        buf.writeBlockPos(pos);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public void handle(PlayPayloadContext context)
    {
        ClientUtils.openSignButtonGui(pos);
    }
}