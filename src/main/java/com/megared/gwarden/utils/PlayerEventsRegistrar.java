package com.megared.gwarden.utils;

import com.megared.gwarden.database.collections.Player;
import com.megared.gwarden.database.managers.FactionManager;
import com.megared.gwarden.database.managers.PlayerManager;
import com.megared.gwarden.item.GwardenItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

public class PlayerEventsRegistrar implements EventsRegistrar {
    private final PlayerManager playerManager = PlayerManager.getInstance();
    private final FactionManager factionManager = FactionManager.getInstance();

    @Override
    public void register() {
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerEndTick);
    }

    @Override
    public void unregister() {}

    private void onServerEndTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!GwardenItems.CHUNK_CORE.equals(player.getMainHandStack().getItem())) {
                continue;
            }

            ServerWorld world = player.getServerWorld();
            ChunkPos chunkPos = player.getChunkPos();

            ParticleEffect particle = new DustParticleEffect(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F);

            int playerY = player.getBlockPos().getY();

            List<BlockPos> corners = Arrays.asList(
                    new BlockPos(chunkPos.getStartX(), playerY, chunkPos.getStartZ()),
                    new BlockPos(chunkPos.getStartX(), playerY, chunkPos.getEndZ() + 1),
                    new BlockPos(chunkPos.getEndX() + 1, playerY, chunkPos.getEndZ() + 1),
                    new BlockPos(chunkPos.getEndX() + 1, playerY, chunkPos.getStartZ()));

            int currIndex = 0;
            int yOffset = 16;

            for (BlockPos corner : corners) {
                int nextIndex = ++currIndex < corners.size() ? currIndex : 0;

                for (int j = -1; j <= 1; j++) {
                    ParticleSpawner.line(
                            player,
                            particle,
                            world,
                            corner.up(yOffset * j),
                            corners.get(nextIndex).up(yOffset * j),
                            Vec3d.ZERO,
                            1,
                            1);
                }

                ParticleSpawner.line(
                        player, particle, world, corner.down(yOffset), corner.up(yOffset), Vec3d.ZERO, 1, 1);
            }
        }
    }

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
