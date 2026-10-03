package com.bachduong.rpgclassselector;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class ClassSelectionScreen extends Screen {
    private static final int PER_PAGE = 6;
    private final List<ClassDefinition> classes;
    private final String currentClass;
    private final boolean required;
    private int page;

    public ClassSelectionScreen(List<ClassDefinition> classes, String currentClass, boolean required) {
        super(Component.translatable("rpgclassselector.title"));
        this.classes = classes;
        this.currentClass = currentClass == null ? "" : currentClass;
        this.required = required;
    }

    @Override
    protected void init() {
        rebuild();
    }

    private void rebuild() {
        clearWidgets();
        int start = page * PER_PAGE;
        int end = Math.min(start + PER_PAGE, classes.size());
        int boxWidth = Math.min(360, width - 30);
        int left = (width - boxWidth) / 2;
        int top = 52;

        for (int i = start; i < end; i++) {
            ClassDefinition def = classes.get(i);
            int y = top + (i - start) * 35;
            Button button = Button.builder(Component.literal(def.display_name), b -> {
                Networking.selectFromClient(def.id);
                onClose();
            }).bounds(left + 42, y, boxWidth - 42, 28).build();
            addRenderableWidget(button);
        }

        int pages = Math.max(1, (classes.size() + PER_PAGE - 1) / PER_PAGE);
        if (page > 0) {
            addRenderableWidget(Button.builder(Component.literal("<"), b -> { page--; rebuild(); })
                    .bounds(width / 2 - 70, height - 32, 30, 20).build());
        }
        if (page + 1 < pages) {
            addRenderableWidget(Button.builder(Component.literal(">"), b -> { page++; rebuild(); })
                    .bounds(width / 2 + 40, height - 32, 30, 20).build());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(font, title, width / 2, 18, 0xFFFFFF);
        Component current = currentClass.isBlank()
                ? Component.translatable("rpgclassselector.none")
                : Component.translatable("rpgclassselector.current", currentClass);
        graphics.drawCenteredString(font, current, width / 2, 32, 0xA0A0A0);

        int start = page * PER_PAGE;
        int end = Math.min(start + PER_PAGE, classes.size());
        int boxWidth = Math.min(360, width - 30);
        int left = (width - boxWidth) / 2;
        int top = 52;

        for (int i = start; i < end; i++) {
            ClassDefinition def = classes.get(i);
            int y = top + (i - start) * 35;
            ItemStack icon = icon(def.icon);
            if (!icon.isEmpty()) graphics.renderItem(icon, left + 10, y + 6);
            String desc = def.description == null ? "" : def.description;
            if (!desc.isBlank()) {
                graphics.drawString(font, font.plainSubstrByWidth(desc, boxWidth - 60),
                        left + 45, y + 18, 0xAAAAAA, false);
            }
        }

        if (classes.isEmpty()) {
            graphics.drawCenteredString(font, Component.literal("No available classes. Check required_mods in the config."),
                    width / 2, height / 2, 0xFF7777);
        }
    }

    private static ItemStack icon(String raw) {
        ResourceLocation id = ResourceLocation.tryParse(raw == null ? "" : raw);
        if (id == null || !BuiltInRegistries.ITEM.containsKey(id)) return ItemStack.EMPTY;
        return new ItemStack(BuiltInRegistries.ITEM.get(id));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !required;
    }

    @Override
    public void onClose() {
        if (!required || !currentClass.isBlank() || !classes.isEmpty()) {
            super.onClose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
