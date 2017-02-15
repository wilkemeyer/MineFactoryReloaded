package powercrystals.minefactoryreloaded.item.base;

import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.render.ModelHelper;

public class ItemFactoryColored extends ItemMulti {

	public ItemFactoryColored() {

		String[] names = new String[16];
		for(EnumDyeColor color : EnumDyeColor.values()) {
			names[color.ordinal()] = color.getName();
		}
		setNames(names);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerColoredItemModels(this, modelName);
	}
}
