package com.megared.gwarden.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static com.megared.gwarden.utils.Data.MOD_ID;

public class GwardenBlocks {
    private static final List<Block> BLOCKS = new ArrayList<>();

    public static final ChunkCoreBlock CHUNK_CORE;

    static {
        CHUNK_CORE = register(
                "chunk_core_block",
                new ChunkCoreBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.OBSIDIAN)));
    }

    public static void register() {}

    public static <T extends Block> T register(String path, T item) {
        BLOCKS.add(item);

        return Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, path), item);
    }
}
