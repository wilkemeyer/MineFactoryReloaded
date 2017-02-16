package powercrystals.minefactoryreloaded.item.base;

import cofh.lib.render.IFluidOverlayItem;
import cofh.lib.util.RegistryUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class ItemFactoryBucket extends ItemBucket implements IFluidOverlayItem {

	private boolean _register, _needsOverlay;

	public ItemFactoryBucket(Block liquidBlock) {

		this(liquidBlock, true);
	}

	public ItemFactoryBucket(Block liquidBlock, boolean reg) {

		super(liquidBlock);
		setCreativeTab(MFRCreativeTab.tab);
		setMaxStackSize(1);
		setContainerItem(Items.BUCKET);
		_register = reg;
	}

	@Override
	public Item setUnlocalizedName(String name) {

		super.setUnlocalizedName(name);
		return this;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List subTypes) {

		subTypes.add(new ItemStack(item, 1, 0));
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder(getClass().getName());
		b.append('@').append(System.identityHashCode(this)).append('{');
		b.append("l:").append(getUnlocalizedName()).append(", o:").append(_needsOverlay);
		//b.append(", b:").append(this.isFull);
		FluidStack stack = FluidContainerRegistry.getFluidForFilledItem(new ItemStack(this, 1, 0));
		Fluid fluid = stack == null ? null : stack.getFluid();
		b.append(", f:").append(fluid).append(", i:").append(fluid == null ? null : fluid.getStill());
		b.append(", c:").append(fluid == null ? null : fluid.getClass());
		b.append('}');
		return b.toString();
	}

}
