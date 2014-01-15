package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

public class ItemFactoryBag extends ItemFactory {

	public ItemFactoryBag(int id)
	{
		super(id);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote)
			return stack;

		if (stack.stackSize != 1)
		{
			player.sendChatToPlayer(new ChatMessageComponent()
					.addKey("chat.info.mfr.bag.stacksize"));
			return stack;
		}
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());

		player.openGui(MineFactoryReloadedCore.instance(), 2, world, 0, 0, 0);
		return stack;
	}

}
