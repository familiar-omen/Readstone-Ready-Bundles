package net.familiar_omen.redstone_ready_bundles;

import net.fabricmc.api.ModInitializer;

import net.minecraft.block.DispenserBlock;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.System.in;

public class RedstoneReadyBundles implements ModInitializer {
	public static final String MOD_ID = "redstone-ready-bundles";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        BundleDispenseBehavior behavior = new BundleDispenseBehavior();

        for (var bundle : BundleItem.getBundles()) {
            DispenserBlock.registerBehavior(bundle, behavior);
        }
	}
}