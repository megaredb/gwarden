package com.megared.gwarden.database.collections;

import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Entity(
        value = "chunks",
        indices = {
            @Index(fields = "factionId", type = IndexType.NON_UNIQUE),
            @Index(fields = {"chunkPosition.x", "chunkPosition.z", "chunkPosition.worldName"})
        })
public class Chunk {
    @Id
    private Long id;

    private Long factionId;
    private ChunkPosition chunkPosition;
    private ProtectionInfo protectionInfo = new ProtectionInfo();

    public static Chunk create(long id, @NotNull Long factionId, @NotNull ChunkPosition chunkPosition) {
        Chunk chunk = new Chunk();

        chunk.id = id;
        chunk.factionId = factionId;
        chunk.chunkPosition = chunkPosition;

        return chunk;
    }

    public long getId() {
        return id;
    }

    public Long getFactionId() {
        return factionId;
    }

    public void setFactionId(Long factionId) {
        this.factionId = factionId;
    }

    public @NotNull ChunkPosition getChunkPosition() {
        return chunkPosition;
    }

    public void setChunkPosition(@NotNull ChunkPosition chunkPosition) {
        this.chunkPosition = chunkPosition;
    }

    public ProtectionInfo getProtectionInfo() {
        return protectionInfo;
    }

    public void setProtectionInfo(@NotNull ProtectionInfo protectionInfo) {
        this.protectionInfo = protectionInfo;
    }

    public boolean isProtected() {
        return getProtectionInfo().getWarExpireTime() < Instant.now().getEpochSecond();
    }
}
