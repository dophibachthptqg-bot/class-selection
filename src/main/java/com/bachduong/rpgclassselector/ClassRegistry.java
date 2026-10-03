package com.bachduong.rpgclassselector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class ClassRegistry {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_TYPE = new TypeToken<List<ClassDefinition>>(){}.getType();
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("rpg_class_selector/classes.json");
    private static List<ClassDefinition> all = new ArrayList<>();

    private ClassRegistry() {}

    public static synchronized void loadOrCreate() {
        try {
            Files.createDirectories(FILE.getParent());
            if (!Files.exists(FILE)) {
                all = defaults();
                Files.writeString(FILE, GSON.toJson(all));
            } else {
                List<ClassDefinition> parsed = GSON.fromJson(Files.readString(FILE), LIST_TYPE);
                all = parsed == null ? defaults() : parsed;
            }
        } catch (IOException e) {
            all = defaults();
            RpgClassSelectorLog.LOGGER.error("Failed to load class definitions", e);
        }
    }

    public static synchronized List<ClassDefinition> available() {
        return all.stream().filter(ClassRegistry::modsPresent).toList();
    }

    public static synchronized Optional<ClassDefinition> getAvailable(String id) {
        return available().stream().filter(c -> c.id.equals(id)).findFirst();
    }

    public static synchronized Optional<ClassDefinition> getAny(String id) {
        return all.stream().filter(c -> c.id.equals(id)).findFirst();
    }

    private static boolean modsPresent(ClassDefinition c) {
        return c.required_mods == null || c.required_mods.stream().allMatch(m -> m == null || m.isBlank() || ModList.get().isLoaded(m));
    }

    public static String toJsonAvailable() {
        return GSON.toJson(available());
    }

    private static List<ClassDefinition> defaults() {
        List<ClassDefinition> list = new ArrayList<>();
        list.add(new ClassDefinition("warrior","Warrior","Front-line melee specialist.","minecraft:iron_sword","base","rogues"));
        list.add(new ClassDefinition("rogue","Rogue","Fast melee class focused on agility.","minecraft:iron_dagger","base","rogues"));
        list.add(new ClassDefinition("archer","Archer","Ranged physical damage specialist.","minecraft:bow","base","archers"));
        list.add(new ClassDefinition("wizard","Wizard","Arcane spell caster.","minecraft:book","base","wizards"));
        list.add(new ClassDefinition("paladin","Paladin","Holy melee and support.","minecraft:golden_sword","base","paladins"));
        list.add(new ClassDefinition("priest","Priest","Holy caster and healer.","minecraft:glistering_melon_slice","base","paladins"));
        list.add(new ClassDefinition("berserker","Berserker","Aggressive heavy melee class.","minecraft:netherite_axe","plus","berserker_rpg"));
        list.add(new ClassDefinition("druid","Druid","Nature-focused spell caster.","minecraft:oak_sapling","plus","druids"));
        list.add(new ClassDefinition("earth_wizard","Earth Wizard","Elemental earth magic.","minecraft:stone","plus","elemental_wizards_rpg"));
        list.add(new ClassDefinition("water_wizard","Water Wizard","Elemental water magic.","minecraft:water_bucket","plus","elemental_wizards_rpg"));
        list.add(new ClassDefinition("wind_wizard","Wind Wizard","Elemental wind magic.","minecraft:feather","plus","elemental_wizards_rpg"));
        list.add(new ClassDefinition("forcemaster","Forcemaster","Force-based magical combat.","minecraft:ender_eye","plus","forcemaster_rpg"));
        return list;
    }
}
