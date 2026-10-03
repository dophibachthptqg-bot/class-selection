package com.bachduong.rpgclassselector;

import com.mojang.brigadier.Command;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class GameEvents {
    private GameEvents() {}

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && PlayerClassData.get(player).isBlank()) {
            Networking.open(player, true);
        }
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("class")
            .executes(ctx -> {
                if (ctx.getSource().getPlayer() instanceof ServerPlayer player) {
                    String current = PlayerClassData.get(player);
                    ctx.getSource().sendSuccess(() -> Component.literal(current.isBlank() ? "No class selected." : "Current class: " + current), false);
                }
                return Command.SINGLE_SUCCESS;
            })
            .then(Commands.literal("open").executes(ctx -> {
                ServerPlayer player = ctx.getSource().getPlayerOrException();
                Networking.open(player, PlayerClassData.get(player).isBlank());
                return Command.SINGLE_SUCCESS;
            }))
            .then(Commands.literal("reload").requires(s -> s.hasPermission(2)).executes(ctx -> {
                ClassRegistry.loadOrCreate();
                ctx.getSource().sendSuccess(() -> Component.literal("RPG class definitions reloaded."), true);
                return Command.SINGLE_SUCCESS;
            }))
            .then(Commands.literal("reset").requires(s -> s.hasPermission(2)).executes(ctx -> {
                ServerPlayer player = ctx.getSource().getPlayerOrException();
                SelectionService.reset(player);
                Networking.open(player, true);
                return Command.SINGLE_SUCCESS;
            }))
        );
    }
}
