package powercrystals.minefactoryreloaded.block.decor;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.render.IconOverlay;

public class BlockVanillaGlassPane extends BlockFactoryGlassPane
{
	private IIcon _iconPane;

	private Block _pane;
	public BlockVanillaGlassPane()
	{
		super(false);
		setBlockName("thinGlass");
		_pane = Blocks.glass_pane;
		ObfuscationReflectionHelper.setPrivateValue(ItemBlock.class,
				(ItemBlock)Item.getItemFromBlock(_pane), this, "field_150939_a");
	}

	@Override public int hashCode() { return _pane.hashCode(); }
	@Override
	public boolean equals(Object obj)
	{
		return obj == _pane | obj == this;
	}

	@Override
	public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour)
	{
		return false;
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		return getIcon(side, 1);
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		meta /= 16;
		if (meta > 0)
			return new IconOverlay(BlockFactoryGlass._texture, 8, 8, 0, 7);
		return _iconPane;
	}

	@Override
	public IIcon func_150097_e()
	{
		return _iconSide;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister ir)
	{
		_iconPane = ir.registerIcon("glass");
		_iconSide = ir.registerIcon("glass_pane_top");
	}
}
