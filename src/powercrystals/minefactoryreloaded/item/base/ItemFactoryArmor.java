package powercrystals.minefactoryreloaded.item.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.IdentityHashMap;
import java.util.LinkedList;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

public class ItemFactoryArmor extends ItemArmor {

	public static final ItemArmor.ArmorMaterial PLASTIC_ARMOR = EnumHelper.
			addArmorMaterial("plastic", 3, new int[]{1, 2, 2, 1}, 7);

	protected IdentityHashMap<Item, LinkedList<ItemStack>> repairableItems =
			new IdentityHashMap<Item, LinkedList<ItemStack>>();
	protected String textureFile;

	public ItemFactoryArmor(ItemArmor.ArmorMaterial mat, int render, int type) {
		super(mat, render, type);
		setMaxStackSize(1);
		textureFile = MineFactoryReloadedCore.armorTextureFolder + mat.name().toLowerCase() +
				"_layer_" + (type == 2 ? 2 : 1) + ".png";
	}

	public ItemFactoryArmor addRepairableItem(Item item) {
		return addRepairableItem(new ItemStack(item));
	}

	public ItemFactoryArmor addRepairableItem(ItemStack stack) {
		Item item = stack.getItem();
		if (!repairableItems.containsKey(item))
			repairableItems.put(item, new LinkedList<ItemStack>());
		repairableItems.get(item).add(stack);
		return this;
	}

	@Override
	public boolean getIsRepairable(ItemStack armor, ItemStack stack) {
		if (repairableItems.containsKey(stack.getItem()))
			if (repairableItems.get(stack.getItem()).contains(stack))
				return true;
		return super.getIsRepairable(armor, stack);
	}

	@Override
	public Item setUnlocalizedName(String name) {
		super.setUnlocalizedName(name);
		MFRRegistry.registerItem(this, getUnlocalizedName());
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return textureFile;
	}

}
