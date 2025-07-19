package com.example;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerSneakHandler {

    private static final Map<UUID, Boolean> wasSneaking = new HashMap<>();
    private static final Set<String> ALLOWED_IDS = Stream.of(
            "wheat",
            "carrots",
            "potatoes",
            "beetroots",
            "melon_stem",
            "pumpkin_stem",
            "sweet_berry_bush",
            "oak_sapling",
            "spruce_sapling",
            "birch_sapling",
            "jungle_sapling",
            "acacia_sapling",
            "dark_oak_sapling",
            "mangrove_propagule",
            "cherry_sapling",
            "bamboo_sapling",
            "bamboo",
            "warped_fungus",
            "crimson_fungus"
    ).collect(Collectors.toSet());

    public static void onPlayerTick(ServerPlayerEntity player) {
        UUID id = player.getUuid();
        boolean prev = wasSneaking.getOrDefault(id, false);
        boolean now  = player.isSneaking();

        if (!prev && now) {
            applyBonemeal(player);
        }

        wasSneaking.put(id, now);
    }

    private static void applyBonemeal(ServerPlayerEntity player) {
        ServerWorld world =(ServerWorld) player.getWorld();
        BlockPos center = player.getBlockPos();
        int radius = FertilizingMod.RADIUS;

        BlockPos.iterateOutwards(center, radius, 1, radius).forEach(pos -> {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (isAllowedPlant(block) && block instanceof Fertilizable fertilizable) {
                if (fertilizable.isFertilizable(world, pos, state) &&
                        fertilizable.canGrow(world, world.random, pos, state)) {

                    fertilizable.grow(world, world.random, pos, state);
                    world.syncWorldEvent(2005, pos, 0);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                }
            }
        });
    }

    private static boolean isAllowedPlant(Block block) {
        Identifier id = Registries.BLOCK.getId(block);
        String path = id.getPath();
        return ALLOWED_IDS.contains(path);
    }
}
