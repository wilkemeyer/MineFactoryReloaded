package powercrystals.minefactoryreloaded.item.base;

import java.util.Arrays;

import net.minecraft.item.ItemStack;

public class ItemMulti extends ItemFactory {

	protected String[] _names;

	public ItemMulti() {

		setHasSubtypes(true);
		setMaxDamage(0);
	}

	protected void setNames(String... names) {

		_names = names;
		setMetaMax(_names.length - 1);
	}
	
	@Override
	public int getMetadata(int meta) {

		return meta;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return getName(getUnlocalizedName(), _names[Math.min(stack.getItemDamage(), _metaMax)]);
	}

	public static String getName(String name, String postfix) {

		return name + (postfix != null ? "." + postfix : "");
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder(getClass().getName());
		b.append('@').append(System.identityHashCode(this)).append('{');
		b.append("l:").append(getUnlocalizedName()).append(", n:");
		b.append(Arrays.toString(_names)).append('}');
		return b.toString();
	}

}
