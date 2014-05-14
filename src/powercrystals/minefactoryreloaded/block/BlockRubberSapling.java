package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.rednet.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.world.WorldGenMassiveTree;
import powercrystals.minefactoryreloaded.world.WorldGenRubberTree;

public class BlockRubberSapling extends BlockSapling implements IRedNetNoConnection
{
	private static WorldGenRubberTree treeGen = new WorldGenRubberTree(true);
			
	public BlockRubberSapling()
	{
		setHardness(0.0F);
		setStepSound(soundTypeGrass);
		setBlockName("mfr.rubberwood.sapling");
		setCreativeTab(MFRCreativeTab.tab);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		blockIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}
	
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		return blockIcon;
	}
	
	@Override
	public void func_149878_d(World world, int x, int y, int z, Random rand)
	{
		if (world.isRemote)
			return;

		int meta = damageDropped(world.getBlockMetadata(x, y, z));
		world.setBlockToAir(x, y, z);
		
		if (meta == 1)
		{
			if (!new WorldGenMassiveTree().setSloped(true).generate(world, rand, x, y, z))
				world.setBlock(x, y, z, this, 1, 4);
		}
		else if (!treeGen.growTree(world, rand, x, y, z))
			world.setBlock(x, y, z, this, 0, 4);
	}

	@Override
	public int damageDropped(int par1)
	{
		return par1 & 3;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item blockId, CreativeTabs tab, List subBlocks)
	{
		subBlocks.add(new ItemStack(blockId, 1, 0));
	}
}
