package com.up.cursegame.block;

import com.up.cursegame.CurseGame;
import com.up.cursegame.block.tileentity.ShrineEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

/**
 *
 * @author Ricky
 */
public class Shrine extends Block {
    
    public static final Shrine INSTANCE = (Shrine)new Shrine().setRegistryName(CurseGame.MOD_ID, "shrine");

    public Shrine() {
        super(Properties.of(Material.HEAVY_METAL).strength(0));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ShrineEntity();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
    
    
}
