package com.bachduong.rpgclassselector;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.lang.reflect.Type;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class ClientHooks {
    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<ClassDefinition>>(){}.getType();

    private ClientHooks() {}

    public static void handleOpen(Networking.OpenSelectorPayload payload) {
        List<ClassDefinition> classes = GSON.fromJson(payload.classesJson(), LIST_TYPE);
        if (classes == null) classes = List.of();
        Minecraft.getInstance().setScreen(new ClassSelectionScreen(classes, payload.currentClass(), payload.required()));
    }
}
