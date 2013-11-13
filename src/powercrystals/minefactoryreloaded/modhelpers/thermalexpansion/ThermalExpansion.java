package powercrystals.minefactoryreloaded.modhelpers.thermalexpansion;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.NetworkMod;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

@Mod(modid = "MineFactoryReloaded|CompatThermalExpansion", name = "MFR Compat: ThermalExpansion", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:ThermalExpansion")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class ThermalExpansion
{
	@EventHandler
	public static void init(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("ThermalExpansion"))
		{
			FMLLog.warning("ThermalExpansion missing - Thermal Expansion compat not loading");
			return;
		}
		try
		{
			// Smooth Blackstone -> Cobble
			sendPulv(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 0),
					new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 2));
			// Smooth Whitestone -> Cobble
			sendPulv(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 1),
					new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 3));
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
	
	private static void sendPulv(ItemStack input, ItemStack output)
	{
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setInteger("energy", 3200);
		toSend.setCompoundTag("input", input.writeToNBT(new NBTTagCompound()));
		toSend.setCompoundTag("primaryOutput", output.writeToNBT(new NBTTagCompound()));
		FMLLog.fine("Sending %s -> %s for pulverizer.", input, output);
		sendComm("PulverizerRecipe", toSend);
	}
	
	private static void sendComm(String type, NBTTagCompound msg)
	{
		FMLInterModComms.sendMessage("ThermalExpansion", type, msg);
	}
}
//*/