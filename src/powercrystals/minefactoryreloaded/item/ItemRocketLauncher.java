package powercrystals.minefactoryreloaded.item;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.core.net.PacketWrapper;
import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.entity.EntityRocket;
import powercrystals.minefactoryreloaded.net.Packets;

public class ItemRocketLauncher extends ItemFactoryGun
{
	@Override
	protected boolean hasGUI(ItemStack stack)
	{
		return false;
	}

	@Override
	protected boolean openGUI(ItemStack stack, World world, EntityPlayer player)
	{
		return false;
	}

	@Override
	protected boolean fire(ItemStack stack, World world, EntityPlayer player)
	{
		int slot = -1, id = MineFactoryReloadedCore.rocketItem.itemID;
		ItemStack[] mainInventory = player.inventory.mainInventory;
		for (int j = 0, e = mainInventory.length; j < e; ++j)
			if (mainInventory[j] != null && mainInventory[j].itemID == id)
			{
				slot = j;
				break;
			}
		if(slot > 0)
		{
			int damage = mainInventory[slot].getItemDamage();
			if (!player.capabilities.isCreativeMode)
				if (--mainInventory[slot].stackSize <= 0)
					mainInventory[slot] = null;

			if (world.isRemote)
			{
				PacketDispatcher.sendPacketToServer(PacketWrapper.createPacket(
						MineFactoryReloadedCore.modNetworkChannel, Packets.RocketLaunchWithLock,
						new Object[] { player.entityId,
								damage == 0 ? MineFactoryReloadedClient.instance.getLockedEntity() : Integer.MIN_VALUE
						}));
			}
			else if (!(player instanceof EntityPlayerMP))
			{
				EntityRocket r = new EntityRocket(world, player, null);
				world.spawnEntityInWorld(r);
			}
			return true;
		}
		return false;
	}

	@Override
	protected int getDelay(ItemStack stack, boolean fired)
	{
		return fired ? 100 : 40;
	}

	@Override
	protected String getDelayTag(ItemStack stack)
	{
		return "mfr:SPAMRLaunched";
	}
}
