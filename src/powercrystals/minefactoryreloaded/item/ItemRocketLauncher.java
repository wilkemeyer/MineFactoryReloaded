package powercrystals.minefactoryreloaded.item;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercrystals.core.net.PacketWrapper;
import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.entity.EntityRocket;
import powercrystals.minefactoryreloaded.net.Packets;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemRocketLauncher extends ItemFactory
{
	public ItemRocketLauncher(int id)
	{
		super(id);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		NBTTagCompound tag = player.getEntityData();
		if (tag.getLong("mfr:SPAMRLaunched") > world.getTotalWorldTime())
			return stack;
		tag.setLong("mfr:SPAMRLaunched", world.getTotalWorldTime() + 300);
		int slot = -1, id = MineFactoryReloadedCore.rocketItem.itemID;
		ItemStack[] mainInventory = player.inventory.mainInventory;
        for (int j = 0, e = mainInventory.length; j < e; ++j)
        {
            if (mainInventory[j] != null && mainInventory[j].itemID == id)
            {
                slot = j;
                break;
            }
        }
		if(slot > 0)
		{
			int damage = mainInventory[slot].getItemDamage();
			if (!player.capabilities.isCreativeMode)
				if (--mainInventory[slot].stackSize <= 0)
					mainInventory[slot] = null;
			
			if(world.isRemote)
			{
				PacketDispatcher.sendPacketToServer(PacketWrapper.createPacket(
					MineFactoryReloadedCore.modNetworkChannel, Packets.RocketLaunchWithLock,
					new Object[] { player.entityId,
					damage == 0 ? MineFactoryReloadedClient.instance.getLockedEntity() : Integer.MIN_VALUE
				}));
			}
			else if (player.getCommandSenderName().equals("[CoFH]"))
			{
				EntityRocket r = new EntityRocket(world, player, null);
				world.spawnEntityInWorld(r);
			}
		}
		return stack;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister ir)
	{
	}
}
