package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class ItemFactoryGun extends ItemFactory
{
	public ItemFactoryGun()
	{
		setHasIcons(false);
	}

	protected abstract boolean hasGUI(ItemStack stack);

	protected abstract boolean openGUI(ItemStack stack, World world, EntityPlayer player);

	protected abstract boolean fire(ItemStack stack, World world, EntityPlayer player);

	protected abstract int getDelay(ItemStack stack, boolean fired);

	protected abstract String getDelayTag(ItemStack stack);

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());

		if (!(hasGUI(stack) && openGUI(stack, world, player)))
		{
			NBTTagCompound tag = player.getEntityData();
			String delayTag = getDelayTag(stack);
			if (tag.getLong(delayTag) < world.getTotalWorldTime())
				tag.setLong(delayTag, world.getTotalWorldTime() + getDelay(stack, fire(stack, world, player)));
		}
		return stack;
	}
}
