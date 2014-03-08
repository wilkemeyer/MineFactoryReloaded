package powercrystals.minefactoryreloaded.core;

import static org.lwjgl.input.Keyboard.*;
import static net.minecraft.util.EnumChatFormatting.*;

import buildcraft.api.tools.IToolWrench;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.input.Keyboard;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.IToolHammer;
import powercrystals.minefactoryreloaded.api.IToolHammerAdvanced;

public class MFRUtil
{
	@SideOnly(Side.CLIENT)
	public static boolean isAltKeyDown()
	{
		return Keyboard.isKeyDown(KEY_LMENU) || Keyboard.isKeyDown(KEY_RMENU);
	}

	@SideOnly(Side.CLIENT)
	public static boolean isCtrlKeyDown()
	{ // logic lifted from net.minecraft.client.gui.GuiScreen.isCtrlKeyDown()
		if (Minecraft.isRunningOnMac)
			return Keyboard.isKeyDown(KEY_LMETA) || Keyboard.isKeyDown(KEY_RMETA);
		return Keyboard.isKeyDown(KEY_LCONTROL) || Keyboard.isKeyDown(KEY_RCONTROL);
	}

	@SideOnly(Side.CLIENT)
	public static boolean isShiftKeyDown()
	{ // logic lifted from net.minecraft.client.gui.GuiScreen.isCtrlKeyDown()
		return Keyboard.isKeyDown(KEY_LSHIFT) || Keyboard.isKeyDown(KEY_RSHIFT);
	}

	public static String shiftForInfo()
	{
		return GRAY +
				StatCollector.translateToLocal("tip.info.mfr.holdShift1") + " " + YELLOW + ITALIC +
				StatCollector.translateToLocal("tip.info.mfr.holdShift2") + " " + RESET + GRAY +
				StatCollector.translateToLocal("tip.info.mfr.holdShift3") + RESET;
	}

	public static final List<ForgeDirection> VALID_DIRECTIONS = Arrays.asList(ForgeDirection.VALID_DIRECTIONS);

	public static boolean isHoldingUsableTool(EntityPlayer player, int x, int y, int z)
	{
		if (player.inventory.getCurrentItem() == null)
		{
			return false;
		}
		Item currentItem = Item.itemsList[player.inventory.getCurrentItem().itemID];
		if (currentItem instanceof IToolHammerAdvanced)
		{
			return ((IToolHammerAdvanced)currentItem).isActive(player.inventory.getCurrentItem());
		}
		else if (currentItem instanceof IToolHammer)
		{
			return true;
		}
		else if (currentItem instanceof IToolWrench)
		{
			return ((IToolWrench)currentItem).canWrench(player, x, y, z);
		}

		return false;
	}

	public static boolean isHoldingHammer(EntityPlayer player)
	{
		if (player.inventory.getCurrentItem() == null)
		{
			return false;
		}
		Item currentItem = Item.itemsList[player.inventory.getCurrentItem().itemID];
		if (currentItem instanceof IToolHammerAdvanced)
		{
			return ((IToolHammerAdvanced)currentItem).isActive(player.inventory.getCurrentItem());
		}
		else if (currentItem instanceof IToolHammer)
		{
			return true;
		}

		return false;
	}

	public static boolean isHolding(EntityPlayer player, Class<? extends Item> itemClass)
	{
		if(player.inventory.getCurrentItem() == null)
		{
			return false;
		}
		Item currentItem = Item.itemsList[player.inventory.getCurrentItem().itemID];
		if(currentItem != null && itemClass.isAssignableFrom(currentItem.getClass()))
		{
			return true;
		}
		return false;
	}

	public static <V extends Entity, T extends Class<V>> V prepareMob(T entity, World world)
	{
		try
		{
			V e = entity.getConstructor(World.class).newInstance(world);
			return e;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static ForgeDirection[] directionsWithoutConveyors(World world, int x, int y, int z)
	{
		ArrayList<ForgeDirection> nonConveyors = new ArrayList<ForgeDirection>();
		int id = MineFactoryReloadedCore.conveyorBlock.blockID;

		for (int i = 0, e = ForgeDirection.VALID_DIRECTIONS.length; i < e; ++i)
		{
			ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[i];
			if (id != world.getBlockId(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ))
				nonConveyors.add(direction);
		}

		return nonConveyors.toArray(new ForgeDirection[nonConveyors.size()]);
	}
}
