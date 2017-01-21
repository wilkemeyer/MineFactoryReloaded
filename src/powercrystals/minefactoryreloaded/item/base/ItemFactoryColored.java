package powercrystals.minefactoryreloaded.item.base;

import net.minecraft.item.EnumDyeColor;

public class ItemFactoryColored extends ItemMulti {

	public ItemFactoryColored() {

		String[] names = new String[16];
		for(EnumDyeColor color : EnumDyeColor.values()) {
			names[color.ordinal()] = color.getName();
		}
		setNames(names);
	}

}
