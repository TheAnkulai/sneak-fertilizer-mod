package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class FertilizerMod implements ModInitializer {
    public static final String MOD_ID = "fertilizermod";
    public static int RADIUS = 5;

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(this::onEndServerTick);
    }

    private void onEndServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PlayerSneakHandler.onPlayerTick(player);
        }
    }
}
