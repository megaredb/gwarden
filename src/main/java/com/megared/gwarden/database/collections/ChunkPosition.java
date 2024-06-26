package com.megared.gwarden.database.collections;

import org.jetbrains.annotations.NotNull;

public class ChunkPosition {
    private Long x;
    private Long z;
    private String worldName;

    public static ChunkPosition create(long x, long z, String worldName) {
        ChunkPosition chunkPosition = new ChunkPosition();

        chunkPosition.setX(x);
        chunkPosition.setZ(z);
        chunkPosition.setWorldName(worldName);

        return chunkPosition;
    }

    public Long getX() {
        return x;
    }

    public void setX(@NotNull Long x) {
        this.x = x;
    }

    public Long getZ() {
        return z;
    }

    public void setZ(@NotNull Long z) {
        this.z = z;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(@NotNull String worldName) {
        this.worldName = worldName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ChunkPosition chunkPos)) return false;

        boolean worldNameEquals = (chunkPos.worldName == null && this.worldName == null)
                || (this.worldName != null && this.worldName.equals(chunkPos.worldName));

        return chunkPos.x.equals(this.x) && chunkPos.z.equals(this.z) && worldNameEquals;
    }

    @Override
    public final int hashCode() {
        return this.x.hashCode() + this.z.hashCode() + this.worldName.hashCode();
    }
}
