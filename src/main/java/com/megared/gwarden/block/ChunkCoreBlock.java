package com.megared.gwarden.block;

import com.megared.gwarden.database.collections.Chunk;
import com.megared.gwarden.database.collections.ChunkPosition;
import com.megared.gwarden.database.collections.Faction;
import com.megared.gwarden.database.collections.Player;
import com.megared.gwarden.database.managers.ChunkManager;
import com.megared.gwarden.database.managers.FactionManager;
import com.megared.gwarden.database.managers.PlayerManager;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.dizitart.no2.exceptions.NotIdentifiableException;
import org.jetbrains.annotations.Nullable;
import com.megared.gwarden.utils.Data;

public class ChunkCoreBlock extends Block implements PolymerBlock {
    private final Block polymerBlock;
    private final ChunkManager chunkManager = ChunkManager.getInstance();
    private final PlayerManager playerManager = PlayerManager.getInstance();
    private final FactionManager factionManager = FactionManager.getInstance();

    public ChunkCoreBlock(Settings settings) {
        super(settings.dropsNothing()
                .pistonBehavior(PistonBehavior.IGNORE)
                .strength(Blocks.OBSIDIAN.getHardness(), Blocks.OBSIDIAN.getBlastResistance())
                .requiresTool());

        this.polymerBlock = Blocks.BEACON;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return this.polymerBlock.getDefaultState();
    }

    @Override
    public void onPlaced(
            World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (world.isClient || placer == null || !placer.isPlayer()) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) placer;

        String playerName = player.getName().getLiteralString();

        if (playerName == null) {
            return;
        }

        Player playerInDB = playerManager.getPlayer(playerName);

        if (playerInDB == null) {
            Data.LOGGER.warn("Player that placed a chunk core was not found.");

            return;
        }

        Faction faction = factionManager.getFaction(playerInDB.getFactionId());

        Chunk chunkInDB = chunkManager.getChunk(world, pos);

        if (chunkInDB != null) {
            if (chunkInDB.getFactionId() == null) {
                chunkInDB.setFactionId(faction.getId());
                try {
                    chunkManager.updateChunk(chunkInDB);
                } catch (ChunkManager.ChunkAlreadyExists ignored) {
                }
            }

            return;
        }

        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1f, 1f);

        player.getWorld()
                .addParticle(
                        ParticleTypes.EXPLOSION_EMITTER,
                        pos.getX() + 0.5,
                        pos.getY() + 1,
                        pos.getZ() + 0.5,
                        0,
                        0.15,
                        0);

        ChunkPos chunkPos = world.getChunk(pos).getPos();

        try {
            chunkManager.addChunk(
                    playerInDB.getFactionId(),
                    ChunkPosition.create(
                            chunkPos.x,
                            chunkPos.z,
                            world.getRegistryKey().getValue().toString()));
        } catch (ChunkManager.ChunkAlreadyExists ignored) {
        }

        for (Chunk chunk1 : chunkManager.getChunks()) {
            Chunk chunk = chunkManager.getChunk(chunk1.getChunkPosition());

            System.out.println(chunk.getChunkPosition().getX() + " "
                    + chunk.getChunkPosition().getZ() + " "
                    + chunk.getChunkPosition().getWorldName());
        }

        System.out.println("----");
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockState blockState = super.onBreak(world, pos, state, player);

        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1f, 1f);
        world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1f, 0.2f);
        world.playSound(null, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.BLOCKS, 1f, 1f);

        String playerName = player.getName().getLiteralString();

        if (playerName == null) {
            return blockState;
        }

        //        Player playerInDB = playerManager.getPlayer(playerName);

        Chunk chunkInDB = chunkManager.getChunk(world, pos);

        if (chunkInDB == null) {
            return blockState;
        }

        try {
            chunkManager.removeChunk(chunkInDB);
        } catch (NotIdentifiableException ignored) {
        }

        return blockState;
    }
}
