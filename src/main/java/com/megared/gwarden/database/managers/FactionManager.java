package com.megared.gwarden.database.managers;

import com.megared.gwarden.database.collections.Faction;
import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.common.SortOrder;
import org.jetbrains.annotations.NotNull;

import static org.dizitart.no2.filters.FluentFilter.where;

public class FactionManager extends BasicDocumentManager {
    private static final FactionManager INSTANCE = new FactionManager();

    private FactionManager() {
        super();
    }

    public static FactionManager getInstance() {
        return INSTANCE;
    }

    public void addFaction(String name, long ownerId) throws FactionNameAlreadyExists {
        if (getFaction(name) != null) {
            throw new FactionNameAlreadyExists();
        }

        Faction faction = Faction.create(getCurrentBiggestId() + 1, name, ownerId);

        this.getNitriteManager().getFactionRepository().insert(faction);
    }

    public Faction getFaction(long id) {
        return this.getNitriteManager().getFactionRepository().getById(id);
    }

    public Faction getFactionByOwner(long ownerId) {
        return this.getNitriteManager()
                .getFactionRepository()
                .find(where("ownerId").eq(ownerId))
                .firstOrNull();
    }

    public Faction getFaction(String name) {
        return this.getNitriteManager()
                .getFactionRepository()
                .find(where("name").eq(name))
                .firstOrNull();
    }

    public void updateFaction(@NotNull Faction faction) throws FactionNameAlreadyExists {
        Faction prevFaction = getFaction(faction.getId());

        if (!prevFaction.getName().equals(faction.getName())
                && getFaction(faction.getName()).getId() != faction.getId()) {
            throw new FactionNameAlreadyExists();
        }

        this.getNitriteManager().getFactionRepository().update(faction);
    }

    public long getCurrentBiggestId() {
        Faction faction = this.getNitriteManager()
                .getFactionRepository()
                .find(FindOptions.orderBy("id", SortOrder.Descending))
                .firstOrNull();
        if (faction == null) return 0;

        return faction.getId();
    }

    public static class FactionNameAlreadyExists extends Exception {
        public FactionNameAlreadyExists() {
            super("Faction with this name already exists");
        }
    }
}
