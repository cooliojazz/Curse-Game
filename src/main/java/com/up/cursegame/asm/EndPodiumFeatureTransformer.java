package com.up.cursegame.asm;

import com.up.cursegame.block.Shrine;
import com.up.cursegame.ritual.RitualGenerators;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.EndPodiumFeature;

/**
 *
 * @author Ricky
 */
public class EndPodiumFeatureTransformer {
	
	public static void postPlace(EndPodiumFeature feature, ISeedReader seedReader, BlockPos pos) {
		feature.setBlock(seedReader, pos.above(4), Shrine.INSTANCE.defaultBlockState().setValue(Shrine.TYPE_PROPERTY, RitualGenerators.END.ordinal()));
		feature.setBlock(seedReader, pos.above(1).north(), Blocks.END_PORTAL.defaultBlockState());
	}
	
}
