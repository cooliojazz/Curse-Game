package com.up.cursegame.asm;

import com.up.cursegame.block.Shrine;
import com.up.cursegame.ritual.RitualGenerators;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.DesertWellsFeature;

/**
 *
 * @author Ricky
 */
public class DesertWellsFeatureTransformer {
	
	public static void postPlace(DesertWellsFeature feature, ISeedReader seedReader, BlockPos pos) {
		feature.setBlock(seedReader, pos.above(5), Shrine.INSTANCE.defaultBlockState().setValue(Shrine.TYPE_PROPERTY, RitualGenerators.DESERT_TEMPLE.ordinal()));
	}
	
}
