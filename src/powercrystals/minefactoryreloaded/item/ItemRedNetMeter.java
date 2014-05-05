package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

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
		
		TileEntity te = world.getTileEntity(x, y, z);
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
					player.addChatMessage(new ChatComponentText(_colorNames[i])
							.appendText(": " + value));
					++foundNonZero;
				}
			}
			
			if(foundNonZero == 0)
			{
				player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.meter.cable.allzero"));
			}
			else if (foundNonZero < 16)
			{
				player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.meter.cable.restzero"));
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
					player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.meter.varprefix")
							.appendText(" " + i + ": " + value));
					++foundNonZero;
				}
			}
			
			if(foundNonZero == 0)
			{
				player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.meter.var.allzero"));
			}
			else if (foundNonZero < 16)
			{
				player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.meter.var.restzero"));
			}
			
			return true;
		}
		else if(world.getBlock(x, y, z).equals(Blocks.redstone_wire))
		{
			player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.meter.dustprefix")
					.appendText(": " + world.getBlockMetadata(x, y, z)));
		}
		return false;
	}
}
