package com.megared.gwarden.database.managers;

import com.megared.gwarden.database.collections.Chunk;
import com.megared.gwarden.database.collections.ChunkPosition;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.collection.FindPlan;
import org.dizitart.no2.common.SortOrder;
import org.dizitart.no2.exceptions.NotIdentifiableException;
import org.dizitart.no2.repository.Cursor;
import org.jetbrains.annotations.NotNull;

import static org.dizitart.no2.filters.FluentFilter.where;

public class ChunkManager extends BasicDocumentManager {
    private static final ChunkManager INSTANCE = new ChunkManager();

    private ChunkManager() {
        super();
    }

    public static ChunkManager getInstance() {
        return INSTANCE;
    }

    public void addChunk(long factionId, @NotNull ChunkPosition chunkPosition) throws ChunkAlreadyExists {
        if (getChunk(chunkPosition) != null) {
            throw new ChunkAlreadyExists(chunkPosition);
        }

        Chunk chunk = Chunk.create(getCurrentBiggestId() + 1, factionId, chunkPosition);

        this.getNitriteManager().getChunkRepository().insert(chunk);
    }

    public Chunk getChunk(long id) {
        return this.getNitriteManager().getChunkRepository().getById(id);
    }

    public Chunk getChunk(ChunkPosition chunkPosition) {
        return this.getNitriteManager()
                .getChunkRepository()
                .find(where("chunkPosition.x")
                        .eq(chunkPosition.getX())
                        .and(where("chunkPosition.z")
                                .eq(chunkPosition.getZ())
                                .and(where("chunkPosition.worldName").eq(chunkPosition.getWorldName()))))
                .firstOrNull();
    }

    public Chunk getChunk(@NotNull ServerWorld world, @NotNull BlockPos pos) {
        net.minecraft.world.chunk.Chunk worldChunk = world.getChunk(pos);

        ChunkPos worldChunkPos = worldChunk.getPos();

        ChunkPosition chunkPosition = ChunkPosition.create(
                worldChunkPos.x,
                worldChunkPos.z,
                world.getRegistryKey().getValue().toString());

        return this.getChunk(chunkPosition);
    }

    public Chunk getChunk(@NotNull World world, @NotNull BlockPos pos) {
        return getChunk((ServerWorld) world, pos);
    }

    public Cursor<Chunk> getChunks(long skip, long limit) {
        Cursor<Chunk> cursor = this.getNitriteManager()
                .getChunkRepository()
                .find(where("factionId").notEq(null), FindOptions.orderBy("id", SortOrder.Ascending));

        FindPlan findPlan = cursor.getFindPlan();
        findPlan.setSkip(skip);

        if (limit > 0) {
            findPlan.setLimit(limit);
        }

        return cursor;
    }

    public Cursor<Chunk> getChunks() {
        return this.getChunks(0, 0);
    }

    public void updateChunk(@NotNull Chunk chunk) throws ChunkAlreadyExists {
        Chunk prevChunk = getChunk(chunk.getId());

        if (!prevChunk.getChunkPosition().equals(chunk.getChunkPosition())
                && getChunk(chunk.getChunkPosition()).getId() != chunk.getId()) {
            throw new ChunkAlreadyExists(chunk.getChunkPosition());
        }

        this.getNitriteManager().getChunkRepository().update(chunk);
    }

    public void removeChunk(@NotNull Chunk chunk) throws NotIdentifiableException {
        this.getNitriteManager().getChunkRepository().remove(chunk);
    }

    public long getCurrentBiggestId() {
        Chunk chunk = this.getNitriteManager()
                .getChunkRepository()
                .find(FindOptions.orderBy("id", SortOrder.Descending))
                .firstOrNull();
        if (chunk == null) return 0;

        return chunk.getId();
    }

    public static class ChunkAlreadyExists extends Exception {
        public ChunkAlreadyExists(ChunkPosition chunkPosition) {
            super("Chunk at position %d, %d already exists.".formatted(chunkPosition.getX(), chunkPosition.getZ()));
        }
    }
}
