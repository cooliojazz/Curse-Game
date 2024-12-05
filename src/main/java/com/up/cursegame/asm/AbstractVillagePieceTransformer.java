package com.up.cursegame.asm;

import com.up.cursegame.block.Shrine;
import com.up.cursegame.ritual.RitualGenerators;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;

/**
 *
 * @author Ricky
 */
public class AbstractVillagePieceTransformer {
	
	private static final Set<String> villageTemplates = new HashSet<String>(Arrays.asList(
			"village/plains/houses/plains_temple_3",
			"village/plains/houses/plains_temple_4",
			"village/desert/houses/desert_temple_1",
			"village/desert/houses/desert_temple_2",
			"village/savanna/houses/savanna_temple_1",
			"village/savanna/houses/savanna_temple_2",
			"village/taiga/houses/taiga_temple_1",
			"village/snowy/houses/snowy_temple_1"));
	
	private static final Set<String> bastionTemplates = new HashSet<String>(Arrays.asList(
			"bastion/bridge/bridge_pieces/bridge",
			"bastion/treasure/ramparts/lava_basin_main"));
	
	public static void postPostProcess(AbstractVillagePiece piece, ISeedReader seedReader, MutableBoundingBox bounds) {
		if (piece.getElement() instanceof SingleJigsawPiece) {
			ResourceLocation template = ((SingleJigsawPiece)piece.getElement()).template.left().orElse(null);
			if (template != null) {
				if (villageTemplates.contains(template.getPath())) {
					if (template.getPath().contains("desert")) {
						seedReader.setBlock(piece.getPosition().above(1), Shrine.INSTANCE.defaultBlockState().setValue(Shrine.TYPE_PROPERTY, RitualGenerators.VILLAGE.ordinal()), 2);
					} else {
						seedReader.setBlock(piece.getPosition(), Shrine.INSTANCE.defaultBlockState().setValue(Shrine.TYPE_PROPERTY, RitualGenerators.VILLAGE.ordinal()), 2);
					}
					seedReader.getChunk(piece.getPosition()).markPosForPostprocessing(piece.getPosition());
				}
				if (bastionTemplates.contains(template.getPath())) {
					if (template.getPath().contains("bridge")) {
						seedReader.setBlock(piece.getPosition().above(14).south(5).west(10), Shrine.INSTANCE.defaultBlockState().setValue(Shrine.TYPE_PROPERTY, RitualGenerators.BASTION.ordinal()), 2);
					} else if (template.getPath().contains("ramparts")) {
						seedReader.setBlock(piece.getPosition().above(30), Shrine.INSTANCE.defaultBlockState().setValue(Shrine.TYPE_PROPERTY, RitualGenerators.BASTION.ordinal()), 2);
					}
					seedReader.getChunk(piece.getPosition()).markPosForPostprocessing(piece.getPosition());
				}
			}
		}
	}
	
}
