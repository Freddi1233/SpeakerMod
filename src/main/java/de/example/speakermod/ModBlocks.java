package de.example.speakermod;

import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {
    public static final Block SPEAKER = register(
            "speaker",
            SpeakerBlock::new,
            BlockBehaviour.Properties.of().strength(3.5F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory,
                                  BlockBehaviour.Properties properties) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK,
                Identifier.fromNamespaceAndPath(SpeakerMod.MOD_ID, name));
        Block block = factory.apply(properties.setId(blockKey));

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(SpeakerMod.MOD_ID, name));
        BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    public static void initialize() {
    }
}
