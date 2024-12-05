package com.up.cursegame.asm;

import com.up.cursegame.block.Shrine;
import com.up.cursegame.ritual.RitualGenerators;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.structure.SwampHutPiece;

/**
 *
 * @author Ricky
 */
public class SwampHutPieceTransformer {
	
	public static void postPostProcess(SwampHutPiece piece, ISeedReader seedReader, MutableBoundingBox bounds) {
		piece.placeBlock(seedReader, Shrine.INSTANCE.defaultBlockState().setValue(Shrine.TYPE_PROPERTY, RitualGenerators.SWAMP.ordinal()), 2, 2, 3, bounds);
	}
	
}
