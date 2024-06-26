package com.megared.gwarden.utils;

import com.megared.gwarden.database.collections.Chunk;
import com.megared.gwarden.database.collections.Player;
import com.megared.gwarden.database.managers.ChunkManager;
import com.megared.gwarden.database.managers.PlayerManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import xyz.nucleoid.stimuli.EventSource;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventListenerMap;
import xyz.nucleoid.stimuli.event.block.BlockBreakEvent;
import xyz.nucleoid.stimuli.event.block.BlockPlaceEvent;
import xyz.nucleoid.stimuli.filter.EventFilter;
import xyz.nucleoid.stimuli.selector.SimpleListenerSelector;

class ChunkFilter implements EventFilter {
    private final ChunkManager chunkManager = ChunkManager.getInstance();

    private Chunk getEventChunk(
            @NotNull Entity entity, @NotNull BlockPos eventPos, @NotNull RegistryKey<World> worldRegistry) {
        MinecraftServer server = entity.getServer();

        if (server == null) {
            return null;
        }

        ServerWorld world = server.getWorld(worldRegistry);

        if (world == null) {
            return null;
        }

        return chunkManager.getChunk(world, eventPos);
    }

    @Override
    public boolean accepts(EventSource source) {
        Entity entity = source.getEntity();

        if (entity == null || !entity.isPlayer() || source.getPos() == null || source.getDimension() == null) {
            return false;
        }

        Chunk chunk = getEventChunk(entity, source.getPos(), source.getDimension());

        return chunk != null && chunk.getFactionId() != null && chunk.isProtected();
    }
}

public class ChunkEventsRegistrar implements EventsRegistrar {
    private final EventListenerMap eventListenerMap = new EventListenerMap();
    private final SimpleListenerSelector listenerSelector =
            new SimpleListenerSelector(new ChunkFilter(), eventListenerMap);

    private final PlayerManager playerManager = PlayerManager.getInstance();
    private final ChunkManager chunkManager = ChunkManager.getInstance();

    @Override
    public void register() {
        eventListenerMap.listen(BlockBreakEvent.EVENT, this::chunkBlockBreakHandler);
        eventListenerMap.listen(BlockPlaceEvent.BEFORE, this::chunkBlockPlaceHandler);

        Stimuli.registerSelector(listenerSelector);
    }

    @Override
    public void unregister() {
        Stimuli.unregisterSelector(listenerSelector);
    }

    public ActionResult chunkBlockBreakHandler(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        if (player == null || world == null || pos == null) {
            return ActionResult.PASS;
        }

        String playerName = player.getName().getLiteralString();

        if (playerName == null) {
            return ActionResult.PASS;
        }

        Player playerInDB = playerManager.getPlayer(playerName);
        Chunk chunkInDB = chunkManager.getChunk(world, pos);

        if (playerInDB != null
                && chunkInDB != null
                && chunkInDB.getFactionId() != null
                && playerInDB.getFactionId().equals(chunkInDB.getFactionId())) {

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    public ActionResult chunkBlockPlaceHandler(
            ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockState state, ItemUsageContext context) {
        return chunkBlockBreakHandler(player, world, pos);
    }
}
