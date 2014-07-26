package powercrystals.minefactoryreloaded.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.entity.EntitySafariNet;

public class ItemSafariNetLauncher extends ItemFactory
{
	@Override
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips)
	{
		super.addInfo(stack, player, infoList, advancedTooltips);
		infoList.add(StatCollector.translateToLocal("tip.info.mfr.safarinet.mode"));
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(player.isSneaking())
		{
			stack.setItemDamage(stack.getItemDamage() == 0 ? 1 : 0);
			if(world.isRemote)
			{
				if(isCaptureMode(stack))
				{
					player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.safarinet.capture"));
				}
				else
				{
					player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.safarinet.release"));
				}
			}
			return stack;
		}
		
		for(int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack ammo = player.inventory.getStackInSlot(i);
			if(ammo != null && ammo.getItem() instanceof ItemSafariNet)
			{
				if((ItemSafariNet.isEmpty(ammo) && isCaptureMode(stack)) || (!ItemSafariNet.isEmpty(ammo) && !isCaptureMode(stack)))
				{
					if(!world.isRemote)
					{
						EntitySafariNet esn = new EntitySafariNet(world, player, ammo);
						world.spawnEntityInWorld(esn);
						
						world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
					}
					player.inventory.setInventorySlotContents(i, null);
					break;
				}
			}
		}
		return stack;
	}
	
	private boolean isCaptureMode(ItemStack stack)
	{
		return stack != null && stack.getItemDamage() == 1;
	}
}
