package powercrystals.minefactoryreloaded.block;

import static powercrystals.minefactoryreloaded.block.ItemBlockFactory.getName;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class BlockRubberLeaves extends BlockLeaves implements IRedNetNoConnection
{
	static String[] _names = {null, "dry"};
	private Icon[] _iconOpaque = new Icon[_names.length];
	private Icon[] _iconTransparent = new Icon[_names.length];

	public BlockRubberLeaves(int id)
	{
		super(id);
		setHardness(0.2F);
		setLightOpacity(1);
		setStepSound(soundGrassFootstep);
		setUnlocalizedName("mfr.rubberwood.leaves");
		setCreativeTab(MFRCreativeTab.tab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister ir)
	{
		String unlocalizedName = getUnlocalizedName();
		for (int i = _names.length; i --> 0; )
		{
			String name = getName(unlocalizedName, _names[i]);
			_iconOpaque[i] = ir.registerIcon("minefactoryreloaded:" + name + ".opaque");
			_iconTransparent[i] = ir.registerIcon("minefactoryreloaded:" + name + ".transparent");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side)
	{
		return getIcon(side, world.getBlockMetadata(x, y, z));
	}

	@Override
	public Icon getIcon(int side, int meta)
	{
		meta &= 3;
		return Block.leaves.graphicsLevel ? _iconTransparent[meta] : _iconOpaque[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int par1)
	{
		return par1 == 1 ? 0xFFFFFF : super.getRenderColor(par1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z)
	{
		int meta = iba.getBlockMetadata(x, y, z) & 3;
		int r = 0;
		int g = 0;
		int b = 0;

		for (int l1 = -1; l1 <= 1; ++l1)
			for (int i2 = -1; i2 <= 1; ++i2)
			{
				int j2 = iba.getBiomeGenForCoords(x + i2, z + l1).getBiomeFoliageColor();
				r += (j2 & 16711680) >> 16;
				g += (j2 & 65280) >> 8;
				b += j2 & 255;
			}
		
		r = (r / 9 & 255);
		g = (g / 9 & 255);
		b = (b / 9 & 255);
		if (meta == 1)
			 return (r / 4 << 16 | g / 4 << 8 | b / 4) + 0xc0c0c0;

		return r << 16 | g << 8 | b;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return !Block.leaves.graphicsLevel;
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return MineFactoryReloadedCore.rubberSaplingBlock.blockID;
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}

	@Override
	public void dropBlockAsItemWithChance(World par1World, int x, int y, int z, int meta, float chance, int fortune)
	{
		if (!par1World.isRemote)
		{
			ArrayList<ItemStack> items = getBlockDropped(par1World, x, y, z, meta, fortune);
			//chance = ForgeEventFactory.fireBlockHarvesting(items, par1World, this, x, y, z, meta, fortune, chance, false, harvesters.get());

			for (ItemStack item : items)
				if (par1World.rand.nextFloat() <= chance)
					this.dropBlockAsItem_do(par1World, x, y, z, item);
		}
	}
	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune)
	{
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		if (meta > 3)
			return ret;

		int chance = 20 + 15 * meta;

		if (fortune > 0)
			chance = Math.max(chance - (2 << fortune), 10);

		if (world.rand.nextInt(chance) == 0)
			ret.add(new ItemStack(idDropped(meta, world.rand, fortune), 1, 0));

		/* TODO: drop book (counts as fuel) with info on MFR
		chance = 100;

		if (fortune > 0)
			chance = Math.min(chance - (8 << fortune), 30);

		if (world.rand.nextInt(chance) == 0)
			ret.add(new ItemStack(Item.appleRed, 1, 0));
		//*/
		return ret;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		if (world.isRemote)
			return;
		int l = world.getBlockMetadata(x, y, z), meta = l & 3;
		if (meta == 0 & ((l & 4) == 0))
		{
			BiomeGenBase b = world.getBiomeGenForCoords(x, z);
			boolean decay = (l & 8) != 0;
			int chance = 15;
			if (b != null)
			{
				float temp = b.temperature;
				float rain = b.rainfall; // getFloatRainfall is client only!?
				boolean t;
				decay |= (t = rain <= 0.05f);
				if (t) chance -= 5;
				decay |= ((rain <= 0.2f) & temp >= 1.2f);
				decay |= (t = temp > 1.8f);
				if (t) chance -= 5;
				if (rain >= 0.4f & temp <= 1.4f)
					chance += 7;
				else if (temp < 0.8f)
					chance += 3;
			}
			if (decay && rand.nextInt(chance) == 0)
			{
				world.setBlockMetadataWithNotify(x, y, z, 1, 2);
				return;
			}
		}
		super.updateTick(world, x, y, z, rand);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side)
	{
		return Block.leaves.graphicsLevel ? true : super.shouldSideBeRendered(iba, x, y, z, side);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(int blockId, CreativeTabs creativeTab, List subTypes)
	{
		subTypes.add(new ItemStack(blockId, 1, 0));
		subTypes.add(new ItemStack(blockId, 1, 1));
	}
}
