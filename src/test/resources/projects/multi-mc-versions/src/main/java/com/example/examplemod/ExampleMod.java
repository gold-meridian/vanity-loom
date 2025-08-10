package com.example.examplemod;

import net.minecraft.block.GrassBlock;
import net.minecraft.client.MinecraftClient;

import net.fabricmc.api.ModInitializer;

public class ExampleMod implements ModInitializer {
	@Override
	public void onInitialize() {
		// class_2372 grassBlock = (class_2372) class_2246.field_10219;
		GrassBlock grassBlock = (GrassBlock) net.minecraft.block.Blocks.GRASS_BLOCK;
		// class_310 mincecraft = class_310.method_1551();
		MinecraftClient minecraft = MinecraftClient.getInstance();

		System.out.println("Hello Fabric world!");
		System.out.println("This is a grass block: " + grassBlock);
		System.out.println("This is minecraft client: " + minecraft);
	}
}
