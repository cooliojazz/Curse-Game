package com.up.cursegame.ritual;

/**
 *
 * @author Ricky
 */
public enum RitualGenerators {
	DEFAULT(null),
	DESERT_TEMPLE(new DesertTempleRitual()),
	END(new EndRitual()),
	VILLAGE(new VillageRitual()),
	OCEAN_MONUMENT(new OceanMonumentRitual()),
	BASTION(new BastionRitual()),
	SWAMP(new SwampRitual());
	
	private RitualGenerator generator;

	private RitualGenerators(RitualGenerator generator) {
		this.generator = generator;
	}

	public RitualGenerator getGenerator() {
		return generator;
	}
	
}
