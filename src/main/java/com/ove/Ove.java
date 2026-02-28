package com.ove;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ove implements ModInitializer {
	public static final String MOD_ID = "ove";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// 注册避雷针右键事件处理器
		EndRodHandler.register();
		
		// 注册毙命附魔
		EnchantmentRegistry.register();
		
		// 注册毙命附魔事件处理器
		InstantKillHandler.register();
		
		LOGGER.info("Ove mod initialized successfully!");
	}
}