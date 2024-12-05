package com.up.cursegame.block.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.up.cursegame.CurseGame;
import com.up.cursegame.block.Shrine;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;

/**
 *
 * @author Ricky
 */
public class ShrineEntity extends TileEntity implements ITickableTileEntity {
    
    public static final TileEntityType<ShrineEntity> TYPE = (TileEntityType<ShrineEntity>)TileEntityType.Builder.<ShrineEntity>of(ShrineEntity::new, Shrine.INSTANCE).build(null).setRegistryName(CurseGame.MOD_ID, "shrine");

    private float rotation;
    private float rotationV = 0f;
    
    public ShrineEntity(TileEntityType<?> type) {
        super(type);
    }
    
    public ShrineEntity() {
        super(TYPE);
    }

    @Override
    public void tick() {
//        if (!level.isClientSide) {
//			Vector3d centerPos = Vector3d.atCenterOf(worldPosition);
//			// Do targeting
//			if (target != null && target.getPosition(0).vectorTo(centerPos).length() > range) target = null;
//            if (target == null) {
////				for (EnergyEntity e : level.getEntitiesOfClass(EnergyEntity.class, new AxisAlignedBB(centerPos.subtract(range, range, range), centerPos.add(range, range, range)))) {
////					if (e.getPosition(0).vectorTo(centerPos).length() <= range) {
////						target = e;
////					}
////				}
//			}
//            if (target != null) {
//				// Point at target
////                Vector3d targetDir = Vector3d.atCenterOf(worldPosition).vectorTo(target.getPosition(0)).multiply(1, 0, 1).normalize();
//                Vector3d targetDirNext = Vector3d.atCenterOf(worldPosition).vectorTo(target.getPosition(0).add(target.getDeltaMovement())).multiply(1, 0, 1).normalize();
//                rotationV = ((float)MathHelper.atan2(targetDirNext.x, targetDirNext.z) - rotation) / 2f;
//                rotation += rotationV;
//                if (rotation > Math.PI) rotation -= (float)Math.PI * 2;
//                if (rotation < -Math.PI) rotation += (float)Math.PI * 2;
//
//                // Fire if have valid shot
//                if (Math.abs(rotationV) < 0.1 && level.getDayTime() % 10 == 0) {
//                    SnowballEntity shot = EntityType.SNOWBALL.create(level);
//                    shot.setPos(getBlockPos().getX() + 0.5 + MathHelper.sin(rotation), getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5 + MathHelper.cos(rotation));
//                    shot.setDeltaMovement(MathHelper.sin(rotation) / 2, 0, MathHelper.cos(rotation) / 2);
//                    level.addFreshEntity(shot);
//                }
//                
//                // Sync updates
//                setChanged();
//                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
//            }
//        }
    }
    
    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        rotation = tag.getFloat("rotation");
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putFloat("rotation", rotation);
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
        
        // TODO: Revisit if this is the best way to render these
        @Override
        public void render(ShrineEntity entity, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
            if (model == null) model = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(CurseGame.MOD_ID, "block/shrine"));
            
            float angle = (entity.rotation + entity.rotationV * partialTicks) / 2;
            stack.pushPose();
//            stack.translate(0.5, 0.5, 0.5);
            stack.mulPose(new Quaternion(0f, MathHelper.sin(angle), 0f, MathHelper.cos(angle)));
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(entity.getLevel(), model.getBakedModel(), entity.getBlockState(), entity.getBlockPos(), stack, bufferIn.getBuffer(RenderType.solid()), true, new Random(), entity.getBlockState().getSeed(entity.getBlockPos()), OverlayTexture.NO_OVERLAY, null);
            stack.popPose();
        }
        
        // https://pastebin.com/beV8gxCc ??
//        private static void renderQuads(MatrixStack matrixStack, IVertexBuilder vertexBuilder, List<BakedQuad> quads, int lightTexture, int overlayTexture) {
//            MatrixStack.Entry entry = matrixStack.last();
//            for (BakedQuad quad : quads) {
//                int tintColor = 0xFFFFFF;
//                if (quad.isTinted()) {
//                    tintColor = -1;
//                }
//                float red = (float) (tintColor >> 16 & 255) / 255.0F;
//                float green = (float) (tintColor >> 8 & 255) / 255.0F;
//                float blue = (float) (tintColor & 255) / 255.0F;
//                vertexBuilder.addVertexData(entry, quad, red, green, blue, lightTexture, overlayTexture, true);
//            }
//        }

    }
    
}
