package powercrystals.minefactoryreloaded.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class ItemFactoryBucket extends ItemBucket
{
	public ItemFactoryBucket(Block liquidBlock)
	{
		super(liquidBlock);
		setCreativeTab(MFRCreativeTab.tab);
		setMaxStackSize(1);
		setContainerItem(Items.bucket);
	}
	
	@Override
	public Item setUnlocalizedName(String name)
	{
		super.setUnlocalizedName(name);
		GameRegistry.registerItem(this, getUnlocalizedName());
		return this;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List subTypes)
	{
		subTypes.add(new ItemStack(item, 1, 0));
	}
}
