package net.fabricmc.example;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.util.shape.VoxelShape;
import techreborn.blocks.cable.CableShapeUtil;

import net.fabricmc.api.ModInitializer;

public class ExampleMod implements ModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		System.out.println("Hello Fabric world!");

		if (false) {
			// Just here to make sure it compiles as named, not to test it runs
			BlockState state = null;
			VoxelShape shape = new CableShapeUtil(null).getShape(state);

			// Interface is injected by another project that we are depending on.
			Blocks.AIR.newMethodThatDidNotExist();

			// Method has a transitive AW in the core project.
			BrewingRecipeRegistry.registerPotionType(Items.DIAMOND);
		}
	}
}
