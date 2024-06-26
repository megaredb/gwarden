package com.megared.gwarden.database.managers;

import com.megared.gwarden.database.collections.Player;
import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.collection.FindPlan;
import org.dizitart.no2.common.SortOrder;
import org.dizitart.no2.repository.Cursor;
import org.jetbrains.annotations.NotNull;

import static org.dizitart.no2.filters.FluentFilter.where;

public class PlayerManager extends BasicDocumentManager {
    private static final PlayerManager INSTANCE = new PlayerManager();

    private PlayerManager() {
        super();
    }

    public static PlayerManager getInstance() {
        return INSTANCE;
    }

    public void addPlayer(@NotNull String username) throws PlayerUsernameAlreadyExists {
        if (getPlayer(username) != null) {
            throw new PlayerUsernameAlreadyExists();
        }

        Player player = Player.create(getCurrentBiggestId() + 1, username, null);

        this.getNitriteManager().getPlayerRepository().insert(player);
    }

    public Player getPlayer(long id) {
        return this.getNitriteManager().getPlayerRepository().getById(id);
    }

    public Player getPlayer(@NotNull String username) {
        return this.getNitriteManager()
                .getPlayerRepository()
                .find(where("username").eq(username))
                .firstOrNull();
    }

    public Cursor<Player> getFactionPlayers(long factionId) {
        return this.getNitriteManager()
                .getPlayerRepository()
                .find(where("factionId").eq(factionId), FindOptions.orderBy("id", SortOrder.Ascending));
    }

    public Cursor<Player> getFactionPlayers(long factionId, long skip, long limit) {
        Cursor<Player> cursor = this.getNitriteManager()
                .getPlayerRepository()
                .find(where("factionId").eq(factionId), FindOptions.orderBy("id", SortOrder.Ascending));

        FindPlan findPlan = cursor.getFindPlan();
        findPlan.setSkip(skip);
        findPlan.setLimit(limit);

        return cursor;
    }

    public void updatePlayer(@NotNull Player player) throws PlayerUsernameAlreadyExists {
        Player prevPlayer = this.getNitriteManager().getPlayerRepository().getById(player.getId());

        if (!prevPlayer.getUsername().equals(player.getUsername())
                && getPlayer(player.getUsername()).getId() != player.getId()) {
            throw new PlayerUsernameAlreadyExists();
        }

        this.getNitriteManager().getPlayerRepository().update(player);
    }

    public long getCurrentBiggestId() {
        Player player = this.getNitriteManager()
                .getPlayerRepository()
                .find(FindOptions.orderBy("id", SortOrder.Descending))
                .firstOrNull();
        if (player == null) return 0;

        return player.getId();
    }

    public static class PlayerUsernameAlreadyExists extends Exception {
        public PlayerUsernameAlreadyExists() {
            super("Player username already exists");
        }
    }
}
