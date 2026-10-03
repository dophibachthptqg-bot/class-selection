package com.bachduong.rpgclassselector;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class Networking {
    private Networking() {}

    public record OpenSelectorPayload(String classesJson, String currentClass, boolean required) implements CustomPacketPayload {
        public static final Type<OpenSelectorPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(RpgClassSelector.MOD_ID, "open_selector"));
        public static final StreamCodec<ByteBuf, OpenSelectorPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, OpenSelectorPayload::classesJson,
                ByteBufCodecs.STRING_UTF8, OpenSelectorPayload::currentClass,
                ByteBufCodecs.BOOL, OpenSelectorPayload::required,
                OpenSelectorPayload::new
        );
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record SelectClassPayload(String classId) implements CustomPacketPayload {
        public static final Type<SelectClassPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(RpgClassSelector.MOD_ID, "select_class"));
        public static final StreamCodec<ByteBuf, SelectClassPayload> STREAM_CODEC =
                StreamCodec.composite(ByteBufCodecs.STRING_UTF8, SelectClassPayload::classId, SelectClassPayload::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToClient(OpenSelectorPayload.TYPE, OpenSelectorPayload.STREAM_CODEC,
                (payload, context) -> ClientHooks.handleOpen(payload));
        registrar.playToServer(SelectClassPayload.TYPE, SelectClassPayload.STREAM_CODEC,
                (payload, context) -> {
                    if (context.player() instanceof ServerPlayer player) {
                        SelectionService.select(player, payload.classId(), PlayerClassData.get(player).isBlank());
                    }
                });
    }

    public static void open(ServerPlayer player, boolean required) {
        PacketDistributor.sendToPlayer(player,
                new OpenSelectorPayload(ClassRegistry.toJsonAvailable(), PlayerClassData.get(player), required));
    }

    public static void selectFromClient(String id) {
        PacketDistributor.sendToServer(new SelectClassPayload(id));
    }
}
