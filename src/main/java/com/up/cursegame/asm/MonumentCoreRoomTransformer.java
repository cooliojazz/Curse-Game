package com.up.cursegame.asm;

import com.up.cursegame.block.Shrine;
import com.up.cursegame.ritual.RitualGenerators;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.structure.OceanMonumentPieces;

/**
 *
 * @author Ricky
 */
public class MonumentCoreRoomTransformer {
	
	public static void postPostProcess(OceanMonumentPieces.MonumentCoreRoom piece, ISeedReader seedReader, MutableBoundingBox bounds) {
		piece.placeBlock(seedReader, Shrine.INSTANCE.defaultBlockState().setValue(Shrine.TYPE_PROPERTY, RitualGenerators.OCEAN_MONUMENT.ordinal()), 7, 1, 4, bounds);
	}
	
}
