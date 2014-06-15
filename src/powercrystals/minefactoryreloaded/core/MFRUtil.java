package powercrystals.minefactoryreloaded.core;

import static org.lwjgl.input.Keyboard.*;
import static net.minecraft.util.EnumChatFormatting.*;

import buildcraft.api.tools.IToolWrench;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
				localize("tip.info.mfr.holdShift1", true) + " " + YELLOW + ITALIC +
				localize("tip.info.mfr.holdShift2", true) + " " + RESET + GRAY +
				localize("tip.info.mfr.holdShift3", true) + RESET;
	}

	public static String localize(String s)
	{
		return localize(s + ".name", false);
	}

	public static String localize(String prefix, String s)
	{
		return localize(prefix + s + ".name", true, s);
	}

	public static String localize(String s, boolean exists)
	{
		return localize(s, exists, s);
	}

	public static String localize(String s, boolean exists, String def)
	{
		if (exists && !StatCollector.canTranslate(s))
			return def;
		return StatCollector.translateToLocal(s);
	}

	public static final List<ForgeDirection> VALID_DIRECTIONS = Arrays.asList(ForgeDirection.VALID_DIRECTIONS);

	public static boolean isHoldingUsableTool(EntityPlayer player, int x, int y, int z)
	{
		if (player.inventory.getCurrentItem() == null)
		{
			return false;
		}
		Item currentItem = player.inventory.getCurrentItem().getItem();
		if (currentItem instanceof IToolHammerAdvanced)
		{
			return ((IToolHammerAdvanced)currentItem).isActive(player.inventory.getCurrentItem());
		}
		else if (currentItem instanceof IToolHammer)
		{
			return true;
		}
		else if (wrenchExists && canHandleWrench(currentItem, player, x, y, z))
		{
			return true;
		}

		return false;
	}
	
	private static boolean wrenchExists = false;
	static {
		try {
			Class.forName("buildcraft.api.tools.IToolWrench");
			wrenchExists = true;
		} catch(Throwable _) {}
	}
	private static boolean canHandleWrench(Item item, EntityPlayer p, int x, int y, int z)
	{
		return item instanceof IToolWrench && ((IToolWrench)item).canWrench(p, x, y, z);
	}

	public static boolean isHoldingHammer(EntityPlayer player)
	{
		if (player.inventory.getCurrentItem() == null)
		{
			return false;
		}
		Item currentItem = player.inventory.getCurrentItem().getItem();
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
		Item currentItem = player.inventory.getCurrentItem().getItem();
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
		Block id = MineFactoryReloadedCore.conveyorBlock;

		for (int i = 0, e = ForgeDirection.VALID_DIRECTIONS.length; i < e; ++i)
		{
			ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[i];
			if (!world.getBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ).equals(id))
				nonConveyors.add(direction);
		}

		return nonConveyors.toArray(new ForgeDirection[nonConveyors.size()]);
	}

	public static NBTTagCompound writeModifierToNBT(String name, AttributeModifier modifier)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("AttributeName", name);
		tag.setString("Name", modifier.getName());
		tag.setDouble("Amount", modifier.getAmount());
		tag.setInteger("Operation", modifier.getOperation());
		tag.setLong("UUIDMost", modifier.getID().getMostSignificantBits());
		tag.setLong("UUIDLeast", modifier.getID().getLeastSignificantBits());
		return tag;
	}
}
