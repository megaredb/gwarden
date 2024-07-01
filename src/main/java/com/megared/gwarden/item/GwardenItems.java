package com.megared.gwarden.item;

import com.megared.gwarden.block.GwardenBlocks;
import com.megared.gwarden.utils.Data;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GwardenItems {
    public static final Item CHUNK_CORE;

    static {
        CHUNK_CORE = register(
                Registries.BLOCK.getId(GwardenBlocks.CHUNK_CORE).getPath(),
                new ChunkCoreItem(GwardenBlocks.CHUNK_CORE, new Item.Settings()));
    }

    public static void register() {
        PolymerItemGroupUtils.registerPolymerItemGroup(
                Identifier.of(Data.MOD_ID, "a_group"),
                ItemGroup.create(ItemGroup.Row.BOTTOM, -1)
                        .icon(CHUNK_CORE::getDefaultStack)
                        .displayName(Text.of("Gwarden"))
                        .entries((context, entries) -> entries.add(CHUNK_CORE))
                        .build());
    }

    public static <T extends Item> Item register(String path, T item) {
        Registry.register(Registries.ITEM, Identifier.of(Data.MOD_ID, path), item);

        return item;
    }

    public static <T extends Block & PolymerBlock> PolymerBlockItem register(T block, Item virtualItem) {
        Identifier id = Registries.BLOCK.getId(block);
        PolymerBlockItem item = new PolymerBlockItem(block, new Item.Settings(), virtualItem);

        Registry.register(Registries.ITEM, id, item);

        return item;
    }
}
