package dev.gigaherz.signbutton.network;

import dev.gigaherz.signbutton.client.ClientUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenSignButtonEditor(BlockPos pos) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation("signbutton","update_spell_sequence");

    public static final Type<OpenSignButtonEditor> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, OpenSignButtonEditor> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, OpenSignButtonEditor::pos,
            OpenSignButtonEditor::new
    );

    public void handle(IPayloadContext context)
    {
        ClientUtils.openSignButtonGui(pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}