package powercrystals.minefactoryreloaded.core;

import static net.minecraft.util.EnumChatFormatting.*;
import static org.lwjgl.input.Keyboard.*;

import buildcraft.api.tools.IToolWrench;

import cofh.api.item.IToolHammer;
import cofh.lib.util.helpers.StringHelper;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.input.Keyboard;

import powercrystals.minefactoryreloaded.api.IMFRHammer;
import powercrystals.minefactoryreloaded.setup.MFRThings;

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

	//
	public static final int[] COLORS_DYE = { 0x1e1e1e, 0xb62222, 0x47691D, 0x804020, 0x404080, 0x803880,
		0x54a69b, 0xa3a3a3, 0x505050, 0xd881a4, 0x49c14a, 0xd8b920, 0x8da7f6, 0xc730a5, 0xe36600, 0xf0f0f0 };
	public static final int[] COLORS; static {
		COLORS = new int[16];
		for (int i = 16, o = 0; i --> 0; ++o)
			COLORS[o] = COLORS_DYE[i];
	}

	public static String shiftForInfo()
	{
		return StringHelper.shiftForDetails();
	}

	public static String empty()
	{
		return ITALIC + "<" + localize("info.cofh.empty", true) + ">" + RESET;
	}

	public static String energy()
	{
		return localize("info.cofh.energy", true);
	}

	public static String getFluidName(FluidStack fluid)
	{
		return StringHelper.getFluidName(fluid);
	}

	public static String localize(String s)
	{
		return localize(s + ".name", false, s);
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
		if (!StatCollector.canTranslate(s))
			return exists ? def :
				StatCollector.canTranslate(def) ? localize(StatCollector.translateToLocal(def), true) : def;
		return StatCollector.translateToLocal(s);
	}

	public static String localize(String s, Object... data)
	{
		if (!StatCollector.canTranslate(s))
			return s;
		return StatCollector.translateToLocalFormatted(s, data);
	}

	public static final List<ForgeDirection> VALID_DIRECTIONS = Arrays.asList(ForgeDirection.VALID_DIRECTIONS);

	public static boolean isHoldingUsableTool(EntityPlayer player, int x, int y, int z)
	{
		if (player.inventory.getCurrentItem() == null)
		{
			return false;
		}
		Item currentItem = player.inventory.getCurrentItem().getItem();
		if (currentItem instanceof IToolHammer)
		{
			return ((IToolHammer)currentItem).isUsable(player.inventory.getCurrentItem(), player, x, y, z);
		}
		else if (currentItem instanceof IMFRHammer)
		{
			return true;
		}
		else if (bcWrenchExists && canHandleBCWrench(currentItem, player, x, y, z))
		{
			return true;
		}

		return false;
	}

	public static void usedWrench(EntityPlayer player, int x, int y, int z)
	{
		if (player.inventory.getCurrentItem() == null)
		{
			return;
		}
		Item currentItem = player.inventory.getCurrentItem().getItem();
		if (currentItem instanceof IToolHammer)
		{
			((IToolHammer)currentItem).toolUsed(player.inventory.getCurrentItem(), player, x, y, z);
		}
		else if (currentItem instanceof IMFRHammer)
		{
			;
		}
		else if (bcWrenchExists)
		{
			bcWrenchUsed(currentItem, player, x, y, z);
		}
	}

	private static boolean bcWrenchExists = false;
	static {
		try {
			Class.forName("buildcraft.api.tools.IToolWrench");
			bcWrenchExists = true;
		} catch(Throwable _) {}
	}
	private static boolean canHandleBCWrench(Item item, EntityPlayer p, int x, int y, int z)
	{
		return item instanceof IToolWrench && ((IToolWrench)item).canWrench(p, x, y, z);
	}
	private static void bcWrenchUsed(Item item, EntityPlayer p, int x, int y, int z)
	{
		if (item instanceof IToolWrench) ((IToolWrench)item).wrenchUsed(p, x, y, z);
	}

	public static boolean isHoldingHammer(EntityPlayer player)
	{
		if (player.inventory.getCurrentItem() == null)
		{
			return false;
		}
		Item currentItem = player.inventory.getCurrentItem().getItem();
		if (currentItem instanceof IMFRHammer)
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

	public static final TileEntity getTile(World world, int x, int y, int z)
	{
		return world.getChunkFromBlockCoords(x, z).getTileEntityUnsafe(x & 15, y, z & 15);
	}

	public static ForgeDirection[] directionsWithoutConveyors(World world, int x, int y, int z)
	{
		ArrayList<ForgeDirection> nonConveyors = new ArrayList<ForgeDirection>();
		Block id = MFRThings.conveyorBlock;

		for (int i = 0, e = ForgeDirection.VALID_DIRECTIONS.length; i < e; ++i)
		{
			ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[i];
			if (!world.getBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ).equals(id))
				nonConveyors.add(direction);
		}

		return nonConveyors.toArray(new ForgeDirection[nonConveyors.size()]);
	}

	public static void notifyNearbyBlocks(World world, int x, int y, int z, Block block)
	{
		ForgeDirection[] dirs = ForgeDirection.VALID_DIRECTIONS;
		if (world.blockExists(x, y, z))
		{
			for (int j = 0; j < 6; ++j)
			{
				ForgeDirection d2 = dirs[j];
				int x2 = x + d2.offsetX, y2 = y + d2.offsetY, z2 = z + d2.offsetZ;
				if (world.blockExists(x2, y2, z2))
					world.notifyBlockOfNeighborChange(x2, y2, z2, block);
			}
		}
	}

	public static void notifyNearbyBlocksExcept(World world, int x, int y, int z, Block block)
	{
		ForgeDirection[] dirs = ForgeDirection.VALID_DIRECTIONS;
		if (world.blockExists(x, y, z) && world.getBlock(x, y, z) != block)
		{
			world.notifyBlockOfNeighborChange(x, y, z, block);
			for (int j = 0; j < 6; ++j)
			{
				ForgeDirection d2 = dirs[j];
				int x2 = x + d2.offsetX, y2 = y + d2.offsetY, z2 = z + d2.offsetZ;
				if (world.blockExists(x2, y2, z2) && world.getBlock(x2, y2, z2) != block)
					world.notifyBlockOfNeighborChange(x2, y2, z2, block);
			}
		}
	}

	public static void wideNotifyNearbyBlocksExcept(World world, int X, int Y, int Z, Block block)
	{
		ForgeDirection[] dirs = ForgeDirection.VALID_DIRECTIONS;
		for (int i = 0; i < 6; ++i)
		{
			ForgeDirection d = dirs[i];
			int x = X + d.offsetX, y = Y + d.offsetY, z = Z + d.offsetZ;
			if (world.blockExists(x, y, z) && world.getBlock(x, y, z) != block)
			{
				world.notifyBlockOfNeighborChange(x, y, z, block);
				for (int j = 0; j < 6; ++j)
				{
					if ((j^1) == i)
						continue;
					ForgeDirection d2 = dirs[j];
					int x2 = x + d2.offsetX, y2 = y + d2.offsetY, z2 = z + d2.offsetZ;
					if (world.blockExists(x2, y2, z2) && world.getBlock(x2, y2, z2) != block)
						world.notifyBlockOfNeighborChange(x2, y2, z2, block);
				}
			}
		}
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
