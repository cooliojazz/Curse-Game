package com.up.cursegame.event;

import com.up.cursegame.CurseGame;
import com.up.cursegame.block.Shrine;
import com.up.cursegame.block.tileentity.ShrineEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 *
 * @author Ricky
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterEvents {
	
	@SubscribeEvent
	public static void bakeModels(ModelRegistryEvent event) {
//        ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(CurseGame.MOD_ID, "block/tower.obj"), ""));
        ModelLoader.addSpecialModel(new ResourceLocation(CurseGame.MOD_ID, "block/shrine"));
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(Shrine.INSTANCE);
	}
    
	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
		event.getRegistry().register(ShrineEntity.TYPE);
	}
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		
	}
}
