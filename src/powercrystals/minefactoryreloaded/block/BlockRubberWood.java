package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetDecorative;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class BlockRubberWood extends BlockLog implements IRedNetDecorative
{
	private IIcon _iconLogTop;
	private IIcon _iconLogSide;

	public BlockRubberWood()
	{
		setHardness(2.0F);
		setStepSound(soundWoodFootstep);
		setUnlocalizedName("mfr.rubberwood.log");
		setCreativeTab(MFRCreativeTab.tab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		_iconLogSide = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".side");
		_iconLogTop = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected IIcon getSideIcon(int par1)
	{
		return _iconLogSide;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected IIcon getTopIcon(int par1)
	{
		return _iconLogTop;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		drops.add(new ItemStack(this, 1, 0));
		if((metadata & 3) == 1)
			drops.add(new ItemStack(MineFactoryReloadedCore.rawRubberItem,
					fortune <= 0 ? 1 : 1 + world.rand.nextInt(fortune)));

		return drops;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item blockId, CreativeTabs tab, List subBlocks)
	{
		subBlocks.add(new ItemStack(blockId, 1, 0));
		subBlocks.add(new ItemStack(blockId, 1, 1));
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}
}
