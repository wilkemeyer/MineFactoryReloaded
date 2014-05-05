package powercrystals.minefactoryreloaded.item;

import cofh.oredict.ItemIdentifier;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.HashMap;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

public class ItemFactoryArmor extends ItemArmor
{
	public static final ItemArmor.ArmorMaterial PLASTIC_ARMOR = EnumHelper.
			addArmorMaterial("plastic", 3, new int[]{1, 2, 2, 1}, 7);

	protected HashMap<ItemIdentifier, Boolean> repariableItems = new HashMap<ItemIdentifier, Boolean>();
	protected String textureFile;

	public ItemFactoryArmor(ItemArmor.ArmorMaterial mat, int render, int type)
	{
		super(mat, render, type);
		setMaxStackSize(1);
		textureFile = MineFactoryReloadedCore.armorTextureFolder + mat.name().toLowerCase() +
				"_layer_" + (type == 2 ? 2 : 1) + ".png";
	}

	public ItemFactoryArmor addRepairableItem(Item item)
	{
		return addRepairableItem(new ItemStack(item));
	}

	public ItemFactoryArmor addRepairableItem(ItemStack item)
	{
		this.repariableItems.put(ItemIdentifier.fromItemStack(item), true);
		return this;
	}

	@Override
	public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
	{
		return repariableItems.containsKey(ItemIdentifier.fromItemStack(par2ItemStack)) ||
				super.getIsRepairable(par1ItemStack, par2ItemStack);
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

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
	{
		return textureFile;
	}
}
