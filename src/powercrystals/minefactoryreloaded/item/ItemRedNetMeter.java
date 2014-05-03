package powercrystals.minefactoryreloaded.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;

public class ItemRedNetMeter extends ItemFactory
{
	public static String[] _colorNames = new String[] { "White", "Orange", "Magenta", "LightBlue",
					"Yellow", "Lime", "Pink", "Gray", "LightGray", "Cyan", "Purple",
					"Blue", "Brown", "Green", "Red", "Black" };
	
	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world,
			int x, int y, int z, int side, float xOffset, float yOffset, float zOffset)
	{
		if(world.isRemote)
		{
			return true;
		}
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityRedNetCable)
		{
			int value;
			int foundNonZero = 0;
			for(int i = 0; i < 16; i++)
			{
				value = ((TileEntityRedNetCable)te).getNetwork().getPowerLevelOutput(i);
				
				if(value != 0)
				{
					// TODO: localize color names v
					player.sendChatToPlayer(new ChatMessageComponent()
							.addText(_colorNames[i]).addText(": " + value));
					++foundNonZero;
				}
			}
			
			if(foundNonZero == 0)
			{
				player.sendChatToPlayer(new ChatMessageComponent()
						.addKey("chat.info.mfr.rednet.meter.cable.allzero"));
			}
			else if (foundNonZero < 16)
			{
				player.sendChatToPlayer(new ChatMessageComponent()
						.addKey("chat.info.mfr.rednet.meter.cable.restzero"));
			}
			
			return true;
		}
		else if(te instanceof TileEntityRedNetLogic)
		{
			int value;
			int foundNonZero = 0;
			for(int i = 0; i < ((TileEntityRedNetLogic)te).getBufferLength(13); i++)
			{
				value = ((TileEntityRedNetLogic)te).getVariableValue(i);
				
				if(value != 0)
				{
					player.sendChatToPlayer(new ChatMessageComponent()
							.addKey("chat.info.mfr.rednet.meter.varprefix")
							.addText(" " + i + ": " + value));
					++foundNonZero;
				}
			}
			
			if(foundNonZero == 0)
			{
				player.sendChatToPlayer(new ChatMessageComponent()
						.addKey("chat.info.mfr.rednet.meter.var.allzero"));
			}
			else if (foundNonZero < 16)
			{
				player.sendChatToPlayer(new ChatMessageComponent()
						.addKey("chat.info.mfr.rednet.meter.var.restzero"));
			}
			
			return true;
		}
		else if(world.getBlockId(x, y, z) == Block.redstoneWire.blockID)
		{
			player.sendChatToPlayer(new ChatMessageComponent()
					.addKey("chat.info.mfr.rednet.meter.dustprefix")
					.addText(": " + world.getBlockMetadata(x, y, z)));
		}
		return false;
	}
}
