package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;

public class ExampleLib implements ModInitializer {
	public static void hello() {
		System.out.println("Hello Fabric world!");
	}

	@Override
	public void onInitialize() {
	}
}
