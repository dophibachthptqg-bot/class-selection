package com.bachduong.rpgclassselector;

import java.util.ArrayList;
import java.util.List;

public final class ClassDefinition {
    public String id = "";
    public String display_name = "";
    public String description = "";
    public String icon = "minecraft:book";
    public String category = "base";
    public String spell_book = "";
    public List<String> required_mods = new ArrayList<>();
    public List<String> tags = new ArrayList<>();
    public List<String> give_items = new ArrayList<>();
    public List<String> commands_on_select = new ArrayList<>();
    public List<String> commands_on_remove = new ArrayList<>();

    public ClassDefinition() {}

    public ClassDefinition(String id, String name, String description, String icon, String category, String... required) {
        this.id = id;
        this.display_name = name;
        this.description = description;
        this.icon = icon;
        this.category = category;
        this.required_mods = List.of(required);
    }
}
