package com.up.cursegame.block;

import com.up.cursegame.CurseGameMod;
import com.up.cursegame.block.tileentity.ShrineEntity;
import com.up.cursegame.gui.HUD;
import com.up.cursegame.ritual.Ritual;
import com.up.cursegame.ritual.RitualGenerators;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author Ricky
 */
public class Shrine extends Block {
    
	public static final IntegerProperty TYPE_PROPERTY = IntegerProperty.create("type", 0, RitualGenerators.values().length - 1);
    public static final Shrine INSTANCE = (Shrine)new Shrine().setRegistryName(CurseGameMod.MOD_ID, "shrine");
	
    public Shrine() {
        super(Properties.of(Material.HEAVY_METAL).strength(Integer.MAX_VALUE).noOcclusion()); 
    }

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(TYPE_PROPERTY);
//		Properties.of(Material.HEAVY_METAL).strength(friction).noOcclusion();
	}

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        ShrineEntity entity = new ShrineEntity();
		//TODO: You broke it fucker for jsut the server (is it okay now?)
		if (CurseGameMod.game != null) {
			if (CurseGameMod.game.isStarted()) {
				DistExecutor.unsafeRunForDist(() -> () -> {
						if (!(world instanceof ClientWorld)) entity.setCureRitual(RitualGenerators.values()[state.getValue(TYPE_PROPERTY)].getGenerator().generate(CurseGameMod.game.getAllPlayers().size()));
						return null;
					}, () -> () -> {
						entity.setCureRitual(RitualGenerators.values()[state.getValue(TYPE_PROPERTY)].getGenerator().generate(CurseGameMod.game.getAllPlayers().size()));
						return null;
					});
			} else {
				CurseGameMod.game.initializeEntities.add(entity);
			}
		}
		return entity;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
		if (!world.isClientSide && hand == Hand.MAIN_HAND) {
			if (CurseGameMod.game.isStarted()) {
				ItemStack item = player.getItemInHand(Hand.MAIN_HAND);
				ShrineEntity shrine = (ShrineEntity)world.getBlockEntity(pos);
				Ritual ritual = shrine.getCureRitual();
				if (ritual.isComplete()) {
					player.sendMessage(new StringTextComponent("The " + ritual.getName() + " has already been completed.").withStyle(TextFormatting.BOLD).withStyle(TextFormatting.DARK_RED), Util.NIL_UUID);
					return ActionResultType.SUCCESS;
				} else {
					ItemStack requirement = ritual.getRequirement(item.getItem());
					if (requirement != null) {
						int consumed = Math.min(item.getCount(), ritual.getRemaining(requirement));
						if (consumed > 0) {
							player.sendMessage(new StringTextComponent("Consumed " + consumed + " " + item.getHoverName().getString() + " in " + ritual.getName()).withStyle(TextFormatting.BOLD).withStyle(TextFormatting.DARK_GREEN), Util.NIL_UUID);
							ritual.complete(new ItemStack(requirement.getItem(), consumed));
							item.setCount(item.getCount() - consumed);
							if (ritual.isComplete()) {
								player.sendMessage(new StringTextComponent("The " + ritual.getName() + " is complete! Curing the nearest " + correctPeople(ritual.getCureAmount())).withStyle(TextFormatting.BOLD).withStyle(TextFormatting.GOLD), Util.NIL_UUID);
								CurseGameMod.game.curePlayersAt(pos, 10, ritual.getCureAmount());
							} else {
								player.sendMessage(new StringTextComponent("The " + ritual.getName() + " still requires " + requirementText(ritual) + ".").withStyle(TextFormatting.BOLD).withStyle(TextFormatting.GRAY), Util.NIL_UUID);
							}
							return ActionResultType.CONSUME;
						}
					} else {
						player.sendMessage(new StringTextComponent("The " + ritual.getName() + " requires " + requirementText(ritual) + " to cure " + correctPeople(shrine.getCureRitual().getCureAmount()) + ".").withStyle(TextFormatting.BOLD).withStyle(TextFormatting.GRAY), Util.NIL_UUID);
						return ActionResultType.SUCCESS;
					}
				}
			}
		}
		return ActionResultType.PASS;
	}
	
	private String requirementText(Ritual ritual) {
		return ritual.getRequirements().stream()
				.map(is -> Pair.of(is, ritual.getRemaining(is)))
				.filter(p -> p.getRight() > 0)
				.map(p -> p.getRight() + "x " + p.getLeft().getHoverName().getString())
				.collect(Collectors.joining(", "));
	}
	
	private String correctPeople(int p) {
		return p + (p == 1 ? " person" : " people");
	}
	
}
