package powercrystals.minefactoryreloaded.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemFactory extends Item
{
	protected int _metaMax = 0;
	protected boolean _hasIcons = true;
	
	public ItemFactory()
	{
		setCreativeTab(MFRCreativeTab.tab);
	}
	
	@Override
	public Item setUnlocalizedName(String name)
	{
		super.setUnlocalizedName(name);
		GameRegistry.registerItem(this, getUnlocalizedName());
		return this;
	}
	
	public Item setHasIcons(boolean icons)
	{
		_hasIcons = icons;
		return this;
	}
	
	protected void setMetaMax(int max)
	{
		_metaMax = max;
	}

	public void getSubItems(Item item, List<ItemStack> subTypes)
	{
		for(int meta = 0; meta <= _metaMax; meta++)
		{
			subTypes.add(new ItemStack(item, 1, meta));
		}
	}
	
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips)
	{
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean advancedTooltips)
	{
		super.addInformation(stack, player, infoList, advancedTooltips);
		addInfo(stack, player, infoList, advancedTooltips);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		if (_hasIcons)
			this.itemIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List subTypes)
	{
		getSubItems(item, subTypes);
	}
}
