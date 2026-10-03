package com.bachduong.rpgclassselector;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(RpgClassSelector.MOD_ID)
public final class RpgClassSelector {
    public static final String MOD_ID = "rpgclassselector";

    public RpgClassSelector(IEventBus modBus) {
        modBus.addListener(Networking::register);
        NeoForge.EVENT_BUS.register(GameEvents.class);
        ClassRegistry.loadOrCreate();
    }
}
