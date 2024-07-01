package com.megared.gwarden.utils;

import com.megared.gwarden.block.GwardenBlocks;
import com.megared.gwarden.database.collections.Chunk;
import com.megared.gwarden.database.collections.Player;
import com.megared.gwarden.database.managers.ChunkManager;
import com.megared.gwarden.database.managers.PlayerManager;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.stimuli.EventSource;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventListenerMap;
import xyz.nucleoid.stimuli.event.block.BlockBreakEvent;
import xyz.nucleoid.stimuli.event.block.BlockPlaceEvent;
import xyz.nucleoid.stimuli.event.block.BlockUseEvent;
import xyz.nucleoid.stimuli.event.block.FluidPlaceEvent;
import xyz.nucleoid.stimuli.filter.EventFilter;
import xyz.nucleoid.stimuli.selector.SimpleListenerSelector;

class ChunkFilter implements EventFilter {
    private final ChunkManager chunkManager = ChunkManager.getInstance();
    private boolean inverted = false;

    public ChunkFilter() {
        super();
    }

    public ChunkFilter(boolean inverted) {
        super();

        this.inverted = inverted;
    }

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

        if (inverted) {
            return chunk == null;
        }

        return chunk != null && chunk.getFactionId() != null && chunk.isProtected();
    }
}

public class ChunkEventsRegistrar implements EventsRegistrar {
    private final EventListenerMap protectionListeners = new EventListenerMap();
    private final SimpleListenerSelector protectionListenerSelector =
            new SimpleListenerSelector(new ChunkFilter(), protectionListeners);

    private final PlayerManager playerManager = PlayerManager.getInstance();
    private final ChunkManager chunkManager = ChunkManager.getInstance();

    @Override
    public void register() {
        protectionListeners.listen(BlockBreakEvent.EVENT, this::chunkBlockBreakHandler);
        protectionListeners.listen(BlockPlaceEvent.BEFORE, this::chunkBlockPlaceHandler);
        protectionListeners.listen(BlockUseEvent.EVENT, this::chunkBlockUseHandler);
        protectionListeners.listen(FluidPlaceEvent.EVENT, this::chunkFluidPlaceHandler);
        // protectionListeners.listen(ItemUseEvent.EVENT, this::chunkItemUseHandler);

        Stimuli.registerSelector(protectionListenerSelector);
    }

    @Override
    public void unregister() {
        Stimuli.unregisterSelector(protectionListenerSelector);
    }

    private ActionResult chunkBlockBreakHandler(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
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

    private ActionResult chunkBlockPlaceHandler(
            ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockState state, ItemUsageContext context) {
        if (GwardenBlocks.CHUNK_CORE.equals(state.getBlock())) {
            return ActionResult.FAIL;
        }

        return chunkBlockBreakHandler(player, world, pos);
    }

    private ActionResult chunkBlockUseHandler(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        ItemStack itemInHand = player.getStackInHand(hand);
        Item item = itemInHand.getItem();

        if (item instanceof BlockItem) {
            Block block = player.getServerWorld()
                    .getBlockState(hitResult.getBlockPos())
                    .getBlock();

            if (!(block instanceof BlockEntityProvider || Blocks.RESPAWN_ANCHOR.equals(block))
                    || player.shouldCancelInteraction()) {
                return ActionResult.PASS;
            }
        }

        ActionResult result = chunkBlockBreakHandler(player, player.getServerWorld(), hitResult.getBlockPos());

        if (result.equals(ActionResult.SUCCESS)) {
            result = ActionResult.PASS;
        }

        return result;
    }

    private ActionResult chunkFluidPlaceHandler(
            ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity player, @Nullable BlockHitResult hitResult) {
        return chunkBlockBreakHandler(player, world, pos);
    }

    //    private TypedActionResult<ItemStack> chunkItemUseHandler(ServerPlayerEntity player, Hand hand) {
    //        if (player == null) {
    //            return TypedActionResult.pass(ItemStack.EMPTY);
    //        }
    //
    //        ItemStack itemInHand = player.getStackInHand(hand);
    //
    //        if (itemInHand == null) {
    //            itemInHand = ItemStack.EMPTY;
    //        }
    //
    //        String playerName = player.getName().getLiteralString();
    //
    //        if (playerName == null) {
    //            return TypedActionResult.pass(itemInHand);
    //        }
    //
    //        Player playerInDB = playerManager.getPlayer(playerName);
    //        Chunk chunkInDB = chunkManager.getChunk(player.getServerWorld(), player.getBlockPos());
    //
    //        Item item = itemInHand.getItem();
    //
    //        boolean blockedItem = item.equals(Items.FLINT_AND_STEEL) || item.equals(Items.FIRE_CHARGE);
    //
    //        if (!blockedItem
    //                || (playerInDB != null
    //                        && chunkInDB != null
    //                        && chunkInDB.getFactionId() != null
    //                        && playerInDB.getFactionId().equals(chunkInDB.getFactionId()))) {
    //            return TypedActionResult.success(itemInHand);
    //        }
    //
    //        return TypedActionResult.fail(itemInHand);
    //    }
}
