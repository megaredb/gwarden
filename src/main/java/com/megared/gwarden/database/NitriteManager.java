package com.megared.gwarden.database;

import com.megared.gwarden.database.collections.Chunk;
import com.megared.gwarden.database.collections.Faction;
import com.megared.gwarden.database.collections.Player;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.common.mapper.JacksonMapperModule;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.repository.ObjectRepository;

import java.io.File;
import java.nio.file.Files;

import static com.megared.gwarden.utils.Data.LOGGER;
import static com.megared.gwarden.utils.Data.MOD_ID;

public class NitriteManager {
    private static final NitriteManager INSTANCE = new NitriteManager();
    private static Nitrite db;

    private NitriteManager() {}

    public static NitriteManager getInstance() {
        return INSTANCE;
    }

    private void openDb() {
        if (db != null) {
            db.close();
        }

        File directory = new File("./%s".formatted(MOD_ID));

        if (!directory.exists()) {
            if (!directory.mkdir()) {
                LOGGER.error("Failed to create mod directory.");
            }
        }

        MVStoreModule storeModule = MVStoreModule.withConfig()
                .filePath("./%s/database.db".formatted(MOD_ID))
                //                .compress(true)
                .autoCommit(true)
                .build();

        db = Nitrite.builder()
                .loadModule(storeModule)
                .loadModule(new JacksonMapperModule())
                .openOrCreate();
    }

    private boolean isDbOpened() {
        return db != null;
    }

    private void openDbIfClosed() {
        if (isDbOpened()) {
            return;
        }

        openDb();
    }

    public ObjectRepository<Player> getPlayerRepository() {
        openDbIfClosed();

        return db.getRepository(Player.class);
    }

    public ObjectRepository<Faction> getFactionRepository() {
        openDbIfClosed();

        return db.getRepository(Faction.class);
    }

    public ObjectRepository<Chunk> getChunkRepository() {
        openDbIfClosed();

        return db.getRepository(Chunk.class);
    }

    public void save() {
        if (!isDbOpened()) {
            return;
        }

        db.commit();
    }
}
