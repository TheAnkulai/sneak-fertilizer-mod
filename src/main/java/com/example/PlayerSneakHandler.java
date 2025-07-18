package com.example;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSneakHandler {

    private static final Map<UUID, Boolean> wasSneaking = new HashMap<>();

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
        return block == Blocks.WHEAT
                || block == Blocks.CARROTS
                || block == Blocks.POTATOES
                || block == Blocks.BEETROOTS
                || block == Blocks.MELON_STEM
                || block == Blocks.PUMPKIN_STEM
                || block == Blocks.SWEET_BERRY_BUSH
                || block == Blocks.NETHER_WART
                || block == Blocks.OAK_SAPLING
                || block == Blocks.SPRUCE_SAPLING
                || block == Blocks.BIRCH_SAPLING
                || block == Blocks.JUNGLE_SAPLING
                || block == Blocks.ACACIA_SAPLING
                || block == Blocks.DARK_OAK_SAPLING
                || block == Blocks.MANGROVE_PROPAGULE
                || block == Blocks.CHERRY_SAPLING
                || block == Blocks.PALE_OAK_SAPLING
                || block == Blocks.BAMBOO_SAPLING
                || block == Blocks.BAMBOO
                || block == Blocks.WARPED_FUNGUS
                || block == Blocks.CRIMSON_FUNGUS;
    }
}
