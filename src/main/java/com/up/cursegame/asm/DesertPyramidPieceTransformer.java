package com.up.cursegame.asm;

import com.up.cursegame.block.Shrine;
import com.up.cursegame.ritual.RitualGenerators;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.structure.DesertPyramidPiece;

/**
 *
 * @author Ricky
 */
public class DesertPyramidPieceTransformer {
	
	public static void postPostProcess(DesertPyramidPiece piece, ISeedReader seedReader, MutableBoundingBox bounds) {
		piece.placeBlock(seedReader, Shrine.INSTANCE.defaultBlockState().setValue(Shrine.TYPE_PROPERTY, RitualGenerators.DESERT_TEMPLE.ordinal()), 10, 1, 10, bounds);
	}
	
}
