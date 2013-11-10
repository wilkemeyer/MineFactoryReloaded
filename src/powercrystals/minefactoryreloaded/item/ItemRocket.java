package powercrystals.minefactoryreloaded.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRocket extends ItemFactory
{
	public ItemRocket(int id)
	{
		super(id);
		setHasSubtypes(true);
		setMetaMax(1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister ir){}
	
	@Override
	public String getUnlocalizedName(ItemStack item)
	{
		if (item != null && item.getItemDamage() == 0)
		{
			return getUnlocalizedName() + ".smart";
		}
		return getUnlocalizedName();
	}
}
