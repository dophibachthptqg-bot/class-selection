package com.bachduong.rpgclassselector;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

public final class SelectionService {
    private SelectionService() {}

    public static boolean select(ServerPlayer player, String id, boolean force) {
        var target = ClassRegistry.getAvailable(id);
        if (target.isEmpty()) {
            player.sendSystemMessage(Component.literal("Class is unavailable on this server."));
            return false;
        }

        String oldId = PlayerClassData.get(player);
        if (!oldId.isBlank() && !force) {
            player.sendSystemMessage(Component.literal("You already selected " + oldId + ". Use /class reset if re-selection is allowed by the server."));
            return false;
        }

        if (!oldId.isBlank()) {
            ClassRegistry.getAny(oldId).ifPresent(old -> removeEffects(player, old));
        }

        ClassDefinition def = target.get();
        applyEffects(player, def);
        PlayerClassData.set(player, def.id);
        player.sendSystemMessage(Component.literal("Class selected: " + def.display_name));
        return true;
    }

    public static void reset(ServerPlayer player) {
        String oldId = PlayerClassData.get(player);
        if (!oldId.isBlank()) ClassRegistry.getAny(oldId).ifPresent(old -> removeEffects(player, old));
        PlayerClassData.clear(player);
        player.sendSystemMessage(Component.literal("Class selection reset."));
    }

    private static void applyEffects(ServerPlayer player, ClassDefinition def) {
        if (def.tags != null) def.tags.forEach(player::addTag);

        if (def.give_items != null) {
            for (String raw : def.give_items) give(player, raw);
        }
        if (def.spell_book != null && !def.spell_book.isBlank()) give(player, def.spell_book);

        runCommands(player, def.commands_on_select);
    }

    private static void removeEffects(ServerPlayer player, ClassDefinition def) {
        if (def.tags != null) def.tags.forEach(player::removeTag);
        runCommands(player, def.commands_on_remove);
    }

    private static void give(ServerPlayer player, String raw) {
        ResourceLocation id = ResourceLocation.tryParse(raw);
        if (id == null || !BuiltInRegistries.ITEM.containsKey(id)) return;
        Item item = BuiltInRegistries.ITEM.get(id);
        player.getInventory().add(new ItemStack(item));
    }

    private static void runCommands(ServerPlayer player, java.util.List<String> commands) {
        if (commands == null) return;
        for (String command : commands) {
            if (command == null || command.isBlank()) continue;
            String parsed = command.replace("{player}", player.getScoreboardName());
            if (parsed.startsWith("/")) parsed = parsed.substring(1);
            player.getServer().getCommands().performPrefixedCommand(
                    player.createCommandSourceStack().withPermission(2).withSuppressedOutput(), parsed);
        }
    }
}
