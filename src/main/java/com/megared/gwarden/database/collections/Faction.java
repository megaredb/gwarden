package com.megared.gwarden.database.collections;

import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;

@Entity(
        value = "factions",
        indices = {@Index(fields = "name")})
public class Faction {
    @Id
    private long id;

    private String name;
    private long ownerId;
    private long[] moderatorsIds;

    public static Faction create(long id, String name, long ownerId) {
        Faction faction = new Faction();

        faction.id = id;
        faction.name = name;
        faction.ownerId = ownerId;
        faction.moderatorsIds = new long[0];

        return faction;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long[] getModeratorsIds() {
        return moderatorsIds;
    }

    public void setModeratorsIds(long[] moderatorsIds) {
        this.moderatorsIds = moderatorsIds;
    }
}
