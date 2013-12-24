package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.Machine;

import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDetCord extends BlockContainer
{
	public BlockDetCord(int par1)
	{
		super(par1, Machine.MATERIAL);
		setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(soundSnowFootstep);
		setUnlocalizedName("mfr.detcord");
		setCreativeTab(MFRCreativeTab.tab);
	}
	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return null;
	}

    @Override
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return false; // temporary
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		blockIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}


	@Override
	public int getRenderType()
	{
		return MineFactoryReloadedCore.renderIdDetCord;
	}
}
