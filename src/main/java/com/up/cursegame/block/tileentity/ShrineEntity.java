package com.up.cursegame.block.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.up.cursegame.CurseGameMod;
import com.up.cursegame.block.Shrine;
import com.up.cursegame.ritual.Ritual;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author Ricky
 */
public class ShrineEntity extends TileEntity implements ITickableTileEntity {
    
    public static final TileEntityType<ShrineEntity> TYPE = (TileEntityType<ShrineEntity>)TileEntityType.Builder.<ShrineEntity>of(ShrineEntity::new, Shrine.INSTANCE).build(null).setRegistryName(CurseGameMod.MOD_ID, "shrine");

	private Ritual cureRitual;
    
    public ShrineEntity(TileEntityType<?> type) {
        super(type);
    }
    
    public ShrineEntity() {
        super(TYPE);
    }

    @Override
    public void tick() {
//        if (!level.isClientSide) {
//			
//        }
    }

	public Ritual getCureRitual() {
		return cureRitual;
	}

	public void setCureRitual(Ritual cureRitual) {
		this.cureRitual = cureRitual;
	}
    
    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains("cureRitual")) cureRitual = Ritual.fromTag(tag.getCompound("cureRitual"));
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        if (cureRitual != null) tag.put("cureRitual", cureRitual.toTag());
        return super.save(tag);
    }

    // TODO: These should probably be optimized to only return changed data
    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), -1, save(new CompoundNBT()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(getBlockState(), pkt.getTag());
    }
    
    public static class Renderer extends TileEntityRenderer<ShrineEntity> {
        
        private IBakedModel model;
        
        public Renderer(TileEntityRendererDispatcher dispatcher) {
            super(dispatcher);
        }
        
        // TODO: Look into weird face ordering forge thing that is causing this to have weird lighting
        @Override
        public void render(ShrineEntity entity, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
            if (model == null) model = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(CurseGameMod.MOD_ID, "block/shrine"));
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(entity.getLevel(), model.getBakedModel(), entity.getBlockState(), entity.getBlockPos(), stack, bufferIn.getBuffer(RenderType.solid()), true, new Random(), entity.getBlockState().getSeed(entity.getBlockPos()), OverlayTexture.NO_OVERLAY, null);
        }

    }
    
}
