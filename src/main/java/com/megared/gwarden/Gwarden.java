package com.megared.gwarden;

import com.megared.gwarden.block.GwardenBlocks;
import com.megared.gwarden.item.GwardenItems;
import com.megared.gwarden.utils.ChunkEventsRegistrar;
import com.megared.gwarden.utils.PlayerEventsRegistrar;
import net.fabricmc.api.ModInitializer;
import static com.megared.gwarden.utils.Data.LOGGER;

public class Gwarden implements ModInitializer {
    private final ChunkEventsRegistrar chunkEventsRegistrar = new ChunkEventsRegistrar();
    private final PlayerEventsRegistrar playerEventsRegistrar = new PlayerEventsRegistrar();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Gwarden...");

        LOGGER.info("Registering blocks and items...");

        GwardenBlocks.register();
        GwardenItems.register();

        LOGGER.info("Loading protected regions and registering events...");

        chunkEventsRegistrar.register();
        playerEventsRegistrar.register();

        LOGGER.info("Loading finished! Gwarden successfully initialized");
    }
}
