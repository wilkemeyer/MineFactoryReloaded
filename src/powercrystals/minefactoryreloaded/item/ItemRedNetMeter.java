package powercrystals.minefactoryreloaded.item;

import cofh.api.block.IBlockDebug;
import cofh.api.block.IBlockInfo;
import cofh.api.tileentity.ITileInfo;
import cofh.util.ServerHelper;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

public class ItemRedNetMeter extends ItemMulti
{
	public static String[] _colorNames = new String[] { "White", "Orange", "Magenta", "LightBlue",
					"Yellow", "Lime", "Pink", "Gray", "LightGray", "Cyan", "Purple",
					"Blue", "Brown", "Green", "Red", "Black" };
	public ItemRedNetMeter()
	{
		setNames(null, "info", "debug");
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity)
	{
		if (player.worldObj.isRemote)
			return true;
		player.addChatMessage(new ChatComponentText("ID: " + EntityList.getEntityString(entity)));
		return true;
	}
	
	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world,
			int x, int y, int z, int side, float xOffset, float yOffset, float zOffset)
	{
		if (world.isRemote)
		{
			return true;
		}
		switch (itemstack.getItemDamage())
		{
		case 2:
			Block block = world.getBlock(x, y, z);
			ArrayList<String> info = new ArrayList<String>();
			if (player.isSneaking() && block instanceof IBlockDebug) {
				((IBlockDebug) (block)).debugBlock(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[side], player);
				return true;
			} else if (block instanceof IBlockInfo) {
				if (ServerHelper.isClientWorld(world)) {
					info.add("-Client-");
				} else {
					info.add("-Server-");
				}
				((IBlockInfo) (block)).getBlockInfo(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[side], player, info, true);
				for (int i = 0; i < info.size(); i++) {
					player.addChatMessage(new ChatComponentText(info.get(i)));
				}
				return true;
			} else {
				TileEntity theTile = world.getTileEntity(x, y, z);
				if (theTile instanceof ITileInfo) {
					if (ServerHelper.isServerWorld(world)) {
						((ITileInfo) theTile).getTileInfo(info, ForgeDirection.UNKNOWN, player, player.isSneaking());
						for (int i = 0; i < info.size(); i++) {
							player.addChatMessage(new ChatComponentText(info.get(i)));
						}
					}
					return true;
				}
			}
			return false;
		case 1:
			if (ServerHelper.isClientWorld(world)) {
				return false;
			}
			block = world.getBlock(x, y, z);
			info = new ArrayList<String>();
			if (block instanceof IBlockInfo) {
				((IBlockInfo) (block)).getBlockInfo(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[side], player, info, false);
				for (int i = 0; i < info.size(); i++) {
					player.addChatMessage(new ChatComponentText(info.get(i)));
				}
				return true;
			} else {
				TileEntity theTile = world.getTileEntity(x, y, z);
				if (theTile instanceof ITileInfo) {
					if (ServerHelper.isServerWorld(world)) {
						((ITileInfo) theTile).getTileInfo(info, ForgeDirection.UNKNOWN, player, false);
						for (int i = 0; i < info.size(); i++) {
							player.addChatMessage(new ChatComponentText(info.get(i)));
						}
					}
					return true;
				}
			}
			return false;
		case 0:
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
		return false;
	}
}
