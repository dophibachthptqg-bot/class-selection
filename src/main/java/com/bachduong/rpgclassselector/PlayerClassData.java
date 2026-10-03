package com.bachduong.rpgclassselector;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;

public final class PlayerClassData {
    private static final String ROOT = "rpgclassselector";
    private static final String CLASS = "selected_class";

    private PlayerClassData() {}

    public static String get(ServerPlayer player) {
        CompoundTag persistent = player.getPersistentData();
        if (!persistent.contains(ROOT)) return "";
        return persistent.getCompound(ROOT).getString(CLASS);
    }

    public static void set(ServerPlayer player, String id) {
        CompoundTag persistent = player.getPersistentData();
        CompoundTag root = persistent.contains(ROOT) ? persistent.getCompound(ROOT) : new CompoundTag();
        root.putString(CLASS, id);
        persistent.put(ROOT, root);
    }

    public static void clear(ServerPlayer player) {
        CompoundTag persistent = player.getPersistentData();
        if (persistent.contains(ROOT)) {
            CompoundTag root = persistent.getCompound(ROOT);
            root.remove(CLASS);
            persistent.put(ROOT, root);
        }
    }
}
