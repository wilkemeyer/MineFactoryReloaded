package powercrystals.minefactoryreloaded.item.base;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;

import powercrystals.minefactoryreloaded.MFRRegistry;

public class ItemFactoryFood extends ItemFood {

	public ItemFactoryFood(int foodRestored, float sustenance) {

		super(foodRestored, sustenance, false);
	}

	@Override
	public Item setUnlocalizedName(String name) {

		super.setUnlocalizedName(name);
		MFRRegistry.registerItem(this, getUnlocalizedName());
		return this;
	}

/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) {

		itemIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}
*/

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder(getClass().getName());
		b.append('@').append(System.identityHashCode(this)).append('{');
		b.append("l:").append(getUnlocalizedName());
		b.append('}');
		return b.toString();
	}

}
