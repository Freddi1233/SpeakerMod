package de.example.speakermod;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SpeakerMod implements ModInitializer {
    public static final String MOD_ID = "speakermod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Dispenser, die gerade geladen/platziert wurden. Der Name wird erst kurz nach dem
    // Platzieren gesetzt, deshalb pruefen wir sie erst am Ende des Ticks.
    private static final List<BlockEntity> PENDING = new ArrayList<>();

    @Override
    public void onInitialize() {
        ModBlocks.initialize();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS)
                .register(entries -> entries.accept(ModBlocks.SPEAKER.asItem()));

        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) -> {
            if (blockEntity instanceof DispenserBlockEntity
                    && blockEntity.getBlockState().is(Blocks.DISPENSER)) {
                PENDING.add(blockEntity);
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> processPending());

        LOGGER.info("Speaker Mod geladen. Benenne einen Dispenser am Amboss in 'Speaker' um.");
    }

    private static void processPending() {
        if (PENDING.isEmpty()) {
            return;
        }
        List<BlockEntity> work = new ArrayList<>(PENDING);
        PENDING.clear();

        for (BlockEntity be : work) {
            if (be.isRemoved() || !(be instanceof DispenserBlockEntity dispenser)) {
                continue;
            }
            Level level = be.getLevel();
            if (level == null) {
                continue;
            }
            BlockState state = be.getBlockState();
            if (!state.is(Blocks.DISPENSER)) {
                continue;
            }
            Component name = dispenser.getCustomName();
            if (name == null || !name.getString().trim().equalsIgnoreCase("speaker")) {
                continue;
            }
            BlockState speaker = ModBlocks.SPEAKER.defaultBlockState()
                    .setValue(SpeakerBlock.FACING, state.getValue(DispenserBlock.FACING));
            level.setBlock(be.getBlockPos(), speaker, Block.UPDATE_ALL);
        }
    }
}
