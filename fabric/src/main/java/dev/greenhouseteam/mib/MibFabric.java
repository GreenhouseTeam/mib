package dev.greenhouseteam.mib;

import dev.greenhouseteam.mib.command.MibCommand;
import dev.greenhouseteam.mib.command.argument.NoteArgumentType;
import dev.greenhouseteam.mib.data.MibSoundSet;
import dev.greenhouseteam.mib.network.clientbound.PlaySingleNoteClientboundPacket;
import dev.greenhouseteam.mib.network.clientbound.StartPlayingClientboundPacket;
import dev.greenhouseteam.mib.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        registerContents();
        registerNetwork();
    }

    public static void registerContents() {
        MibDataComponents.registerAll(Registry::register);
        MibInstrumentAnimations.registerAll(Registry::register);
        MibItems.registerAll(Registry::register);
        MibSoundEvents.registerAll(Registry::register);

        DynamicRegistries.registerSynced(MibRegistries.SOUND_SET, MibSoundSet.DIRECT_CODEC);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            MibCommand.registerCommands(dispatcher);
        });
        ArgumentTypeRegistry.registerArgumentType(Mib.asResource("note"), NoteArgumentType.class, SingletonArgumentInfo.contextFree(NoteArgumentType::note));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            ItemStack goatHorn = ItemStack.EMPTY;
            for (ItemStack stack : entries.getDisplayStacks()) {
                if (stack.is(Items.GOAT_HORN))
                    goatHorn = stack;
            }
            entries.addAfter(goatHorn, MibItems.ACOUSTIC_GUITAR);
            entries.addAfter(MibItems.ACOUSTIC_GUITAR, MibItems.COPPER_GOAT_HORN);
            entries.addAfter(MibItems.COPPER_GOAT_HORN, MibItems.FANTASY_TRUMPET);
            entries.addAfter(MibItems.FANTASY_TRUMPET, MibItems.FLUTE);
            entries.addAfter(MibItems.FLUTE, MibItems.HARPSICHORD);
            entries.addAfter(MibItems.HARPSICHORD, MibItems.KEYBOARD);
            entries.addAfter(MibItems.KEYBOARD, MibItems.SAXOPHONE);
            entries.addAfter(MibItems.SAXOPHONE, MibItems.VIOLIN);
        });
    }

    public static void registerNetwork() {
        PayloadTypeRegistry.playS2C().register(PlaySingleNoteClientboundPacket.TYPE, PlaySingleNoteClientboundPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(StartPlayingClientboundPacket.TYPE, StartPlayingClientboundPacket.STREAM_CODEC);
    }
}
