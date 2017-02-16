package powercrystals.minefactoryreloaded.item.base;

import cofh.api.core.IInitializer;
import cofh.api.core.IModelRegister;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ItemFactoryFood extends ItemFood implements IInitializer, IModelRegister {

	private String modelName;
	private String variant;

	public ItemFactoryFood(int foodRestored, float sustenance) {

		super(foodRestored, sustenance, false);
		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
	}

	@Override
	public Item setUnlocalizedName(String name) {

		super.setUnlocalizedName(name);
		return this;
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder(getClass().getName());
		b.append('@').append(System.identityHashCode(this)).append('{');
		b.append("l:").append(getUnlocalizedName());
		b.append('}');
		return b.toString();
	}

	public ItemFactoryFood setModelLocation(String modelName, String variant) {

		this.modelName = modelName;
		this.variant = variant;

		return this;
	}

	@Override
	public boolean preInit() {

		MFRRegistry.registerItem(this);
		return true;
	}

	@Override
	public boolean initialize() {

		return true;
	}

	@Override
	public boolean postInit() {

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, modelName, variant);
	}
}
