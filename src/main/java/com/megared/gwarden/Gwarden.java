package com.megared.gwarden;

import com.megared.gwarden.block.ChunkCoreBlock;
import com.megared.gwarden.database.collections.ChunkPosition;
import com.megared.gwarden.utils.ChunkEventsRegistrar;
import com.megared.gwarden.utils.PlayerEventsRegistrar;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.megared.gwarden.utils.Data.MOD_ID;
import static com.megared.gwarden.utils.Data.LOGGER;
import static org.dizitart.no2.common.util.ObjectUtils.deepEquals;
import static org.dizitart.no2.filters.FluentFilter.where;

public class Gwarden implements ModInitializer {
    private final ChunkEventsRegistrar chunkEventsRegistrar = new ChunkEventsRegistrar();
    private final PlayerEventsRegistrar playerEventsRegistrar = new PlayerEventsRegistrar();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Gwarden...");

        ChunkCoreBlock chunkCoreBlock = new ChunkCoreBlock(AbstractBlock.Settings.copy(Blocks.OBSIDIAN));

        LOGGER.info("Registering blocks...");

        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "chunk_core_block"), chunkCoreBlock);
        Registry.register(
                Registries.ITEM,
                Identifier.of(MOD_ID, "chunk_core_block"),
                new PolymerBlockItem(chunkCoreBlock, new Item.Settings(), Items.BEACON));

        LOGGER.info("Loading protected regions...");

        chunkEventsRegistrar.register();
        playerEventsRegistrar.register();

        LOGGER.info("Loading finished! Gwarden successfully initialized");
    }
}
