package com.megared.gwarden.utils;

import com.megared.gwarden.database.collections.Player;
import com.megared.gwarden.database.managers.FactionManager;
import com.megared.gwarden.database.managers.PlayerManager;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerEventsRegistrar implements EventsRegistrar {
    private final PlayerManager playerManager = PlayerManager.getInstance();
    private final FactionManager factionManager = FactionManager.getInstance();

    @Override
    public void register() {
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
    }

    @Override
    public void unregister() {}

    private void onPlayerJoin(
            ServerPlayNetworkHandler serverPlayNetworkHandler,
            PacketSender packetSender,
            MinecraftServer minecraftServer) {
        ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
        String playerName = player.getName().getLiteralString();

        if (playerName == null) {
            return;
        }

        Player playerInDB = playerManager.getPlayer(playerName);

        if (playerInDB != null) {
            return;
        }

        try {
            playerManager.addPlayer(playerName);
            Player addedPlayerInDB = playerManager.getPlayer(playerName);
            factionManager.addFaction("%s's Faction".formatted(playerName), addedPlayerInDB.getId());
            addedPlayerInDB.setFactionId(
                    factionManager.getFactionByOwner(addedPlayerInDB.getId()).getId());
            playerManager.updatePlayer(addedPlayerInDB);
        } catch (PlayerManager.PlayerUsernameAlreadyExists | FactionManager.FactionNameAlreadyExists ignored) {
        }
    }
}
