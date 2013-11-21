package powercrystals.minefactoryreloaded.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class ItemSyringeLiquid extends ItemSyringe implements IFluidContainerItem
{
	private boolean _prefix = false;
    @SideOnly(Side.CLIENT)
    protected Icon fillIcon;
	public ItemSyringeLiquid(int id)
	{
		super(id);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		this.fillIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".fill");
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if (getFluid(stack) != null)
			return getUnlocalizedName() + (_prefix ? ".prefix" : ".suffix");
		return getUnlocalizedName();
	}

	public String getLocalizedName(String str)
	{
		String name = getUnlocalizedName() + "." + str;
		if (StatCollector.func_94522_b(name))
			return StatCollector.translateToLocal(name);
		return null;
	}

	@Override
	public String getItemDisplayName(ItemStack item)
	{
		String ret = getFluidName(item), t = getLocalizedName(ret);
		if (t != null && !t.isEmpty())
			return EnumChatFormatting.RESET + t + EnumChatFormatting.RESET;
		if (ret == null)
		{
			return super.getItemDisplayName(item);
		}
		Fluid liquid = FluidRegistry.getFluid(ret);
		if (liquid != null)
		{
			ret = liquid.getLocalizedName();
		}
		_prefix = true;
		t = super.getItemDisplayName(item);
		_prefix = false;
		t = t != null ? t.trim() : "";
		ret = (t.isEmpty() ? "" : t + " ") + ret;
		t = super.getItemDisplayName(item);
		t = t != null ? t.trim() : "";
		ret += t.isEmpty() ? " Syringe" : " " + t;
		return ret;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(ItemStack stack, int pass) {
		switch (pass)
		{
		case 1:
			return this.fillIcon;
		case 0:
		default:
			return this.itemIcon;
		}
	}

	public String getFluidName(ItemStack container)
	{
		NBTTagCompound tag = container.getTagCompound();
		return tag == null || !tag.hasKey("fluidName") ? null :
							tag.getString("fluidName");
	}

	@Override
	public boolean canInject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		return getFluidName(syringe) != null && entity instanceof EntityPlayer;
	}

	@Override
	public boolean inject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		ILiquidDrinkHandler handler = MFRRegistry.getLiquidDrinkHandlers().
				get(getFluidName(syringe));
		if (handler != null)
		{
			handler.onDrink((EntityPlayer)entity);
			syringe.getTagCompound().removeTag("fluidName");
			return true;
		}
		return false;
	}

	@Override
	public FluidStack getFluid(ItemStack container)
	{
		NBTTagCompound tag = container.getTagCompound();
		return tag == null || !tag.hasKey("fluidName") ? null :
			FluidRegistry.getFluidStack(tag.getString("fluidName"), getCapacity(container));
	}

	@Override
	public int getCapacity(ItemStack container)
	{
		return FluidContainerRegistry.BUCKET_VOLUME;
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill)
	{
		if (resource == null)
			return 0;
		int capacity = getCapacity(container);
		String name = resource.getFluid().getName();
		if (name == null | resource.amount < capacity || (container.hasTagCompound() &&
				container.getTagCompound().hasKey("fluidName")))
			return 0;
		if (doFill)
		{
			NBTTagCompound tag = container.getTagCompound();
			if (tag == null) container.setTagCompound(tag = new NBTTagCompound());
			tag.setString("fluidName", name);
		}
		return capacity;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain)
	{
		int capacity = getCapacity(container);
		if (maxDrain < capacity)
			return null;
		if (!container.hasTagCompound() ||
				!container.getTagCompound().hasKey("fluidName"))
			return null;
		FluidStack ret = getFluid(container);
		if (doDrain)
			container.getTagCompound().removeTag("fluidName");
		return ret;
	}

}
