package com.megared.gwarden.database.collections;

import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;
import org.jetbrains.annotations.NotNull;

@Entity(
        value = "players",
        indices = {@Index(fields = "username"), @Index(fields = "factionId", type = IndexType.NON_UNIQUE)})
public class Player {
    @Id
    private long id;

    private String username;
    private Long factionId;

    public static Player create(long id, @NotNull String username, Long factionId) {
        Player player = new Player();

        player.id = id;
        player.username = username;
        player.factionId = factionId;

        return player;
    }

    public long getId() {
        return id;
    }

    public @NotNull String getUsername() {
        return username;
    }

    public Long getFactionId() {
        return factionId;
    }

    public void setFactionId(long factionId) {
        this.factionId = factionId;
    }
}