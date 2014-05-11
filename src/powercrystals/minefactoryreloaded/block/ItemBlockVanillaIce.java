package powercrystals.minefactoryreloaded.block;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockVanillaIce extends ItemBlock
{
	private Item _ice;
	public ItemBlockVanillaIce(net.minecraft.block.Block blockId, Item ice)
	{
		super(blockId);
		setMaxDamage(0);
		setHasSubtypes(true);
		setUnlocalizedName("ice");
		_ice = ice;
	}
	
	@Override public int hashCode() { return _ice.hashCode(); }
	@Override
	public boolean equals(Object obj)
	{
		return obj == _ice | obj == this;
	}
	
	@Override
	public int getMetadata(int meta)
	{
		return meta;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if(stack.getItemDamage() == 1) return getUnlocalizedName() + ".unmelting";
		return getUnlocalizedName();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(net.minecraft.item.Item itemId, CreativeTabs creativeTab, List subTypes)
	{
		subTypes.add(new ItemStack(Blocks.ice, 1, 0));
		subTypes.add(new ItemStack(Blocks.ice, 1, 1));
	}
}
