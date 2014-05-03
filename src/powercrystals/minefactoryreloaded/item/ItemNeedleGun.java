package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.entity.EntityNeedle;

public class ItemNeedleGun extends ItemFactoryGun
{
	@Override
	protected boolean hasGUI(ItemStack stack)
	{
		return true;
	}

	@Override
	protected boolean openGUI(ItemStack stack, World world, EntityPlayer player)
	{
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag("ammo");
		boolean needsAmmo = tag == null || tag.hasNoTags();
		if (needsAmmo & !world.isRemote)
			player.openGui(MineFactoryReloadedCore.instance(), 1, world, 0, 0, 0);

		return needsAmmo;
	}

	@Override
	protected boolean fire(ItemStack stack, World world, EntityPlayer player)
	{
		ItemStack ammo = ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("ammo"));
		boolean reloaded = false, creative = player.capabilities.isCreativeMode;

		if (!world.isRemote)
		{
			EntityNeedle needle = new EntityNeedle(world, player, ammo, 1.0F);
			world.spawnEntityInWorld(needle);
			world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 2.0F);
		}

		NBTTagCompound t = new NBTTagCompound();
		if (!creative)
			ammo.setItemDamage(ammo.getItemDamage() + 1);
		if(ammo.getItemDamage() <= ammo.getMaxDamage())
			ammo.writeToNBT(t);
		else
		{
			ItemStack[] inv = player.inventory.mainInventory;
			for (int i = 0, e = inv.length; i < e; ++i)
			{
				ItemStack item = inv[i];
				if (ammo.getItem().equals(item.getItem()))
				{
					if (!creative && --inv[i].stackSize <= 0) inv[i] = null;
					ammo.setItemDamage(0);
					ammo.writeToNBT(t);
					reloaded = true;
					break;
				}
			}

			if (!(world.isRemote | creative))
				player.dropItem(MineFactoryReloadedCore.needlegunAmmoEmptyItem, 1);
		}
		stack.getTagCompound().setTag("ammo", t);
		return reloaded;
	}

	@Override
	protected int getDelay(ItemStack stack, boolean fired)
	{
		return fired ? 27 : 7;
	}

	@Override
	protected String getDelayTag(ItemStack stack)
	{
		return "mfr:NeedleLaunched";
	}
}
