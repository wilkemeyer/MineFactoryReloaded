package powercrystals.minefactoryreloaded.block;

import static powercrystals.minefactoryreloaded.item.ItemMulti.getName;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemBlockFactory extends ItemBlock
{
	protected String[] _names = {null};
	
	public ItemBlockFactory(Block p_i45328_1_)
	{
		super(p_i45328_1_);
	}

	protected void setNames(String[] names)
	{
		_names = names;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage)
	{
		return field_150939_a.getIcon(2, damage);
	}

	@Override
	public int getMetadata(int meta)
	{
		return meta;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		String str = _names[Math.min(stack.getItemDamage(), _names.length - 1)];
		return getName(getUnlocalizedName(), str);
	}

	public void getSubItems(Item itemId, List<ItemStack> subTypes)
	{
		for(int i = 0; i < _names.length; i++)
		{
			subTypes.add(new ItemStack(itemId, 1, i));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item itemId, CreativeTabs creativeTab, List subTypes)
	{
		getSubItems(itemId, subTypes);
	}
}
