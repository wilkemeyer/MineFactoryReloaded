
package powercrystals.minefactoryreloaded.modhelpers.thermalexpansion;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "MineFactoryReloaded|CompatThermalExpansion", name = "MFR Compat: ThermalExpansion", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:ThermalExpansion")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class ThermalExpansion
{
	@EventHandler
	public static void postInit(FMLPostInitializationEvent e)
	{
		if(!Loader.isModLoaded("ThermalExpansion"))
		{
			FMLLog.warning("ThermalExpansion missing - Thermal Expansion compat not loading");
			return;
		}
		try
		{
			NBTTagCompound emptyBucket = new NBTTagCompound();
			new ItemStack(Item.bucketEmpty).writeToNBT(emptyBucket);
			sendFill(emptyBucket, new ItemStack(Item.bucketMilk), "milk");
			sendFill(emptyBucket, new ItemStack(MineFactoryReloadedCore.sludgeBucketItem), "sludge");
			sendFill(emptyBucket, new ItemStack(MineFactoryReloadedCore.sewageBucketItem), "sewage");
			sendFill(emptyBucket, new ItemStack(MineFactoryReloadedCore.mobEssenceBucketItem), "essence");
			sendFill(emptyBucket, new ItemStack(MineFactoryReloadedCore.bioFuelBucketItem), "biofuel");
			sendFill(emptyBucket, new ItemStack(MineFactoryReloadedCore.meatBucketItem), "meat");
			sendFill(emptyBucket, new ItemStack(MineFactoryReloadedCore.sewageBucketItem), "sewage");
			sendFill(emptyBucket, new ItemStack(MineFactoryReloadedCore.pinkSlimeBucketItem), "pinkslime");
			sendFill(emptyBucket, new ItemStack(MineFactoryReloadedCore.chocolateMilkBucketItem), "chocolatemilk");
			sendFill(emptyBucket, new ItemStack(MineFactoryReloadedCore.mushroomSoupBucketItem), "mushroomsoup");
			emptyBucket = new NBTTagCompound();
			new ItemStack(Item.bowlEmpty).writeToNBT(emptyBucket);
			sendFill(emptyBucket, new ItemStack(Item.bowlSoup), "mushroomsoup");
			
			sendPulv(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 0), new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 2)); // Smooth Blackstone -> Cobble
			sendPulv(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 1), new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 3)); // Smooth Whitestone -> Cobble
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
	
	private static void sendPulv(ItemStack input, ItemStack output)
	{
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setInteger("energy", 320);
		toSend.setCompoundTag("input", new NBTTagCompound());
		toSend.setCompoundTag("primaryOutput", new NBTTagCompound());
		input.writeToNBT(toSend.getCompoundTag("input"));
		output.writeToNBT(toSend.getCompoundTag("primaryOutput"));
		sendComm("PulverizerRecipe", toSend);
	}
	
	private static void sendFill(NBTTagCompound input, ItemStack output, String fluid)
	{
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setInteger("energy", 80);
		toSend.setTag("input", input.copy());
		toSend.setCompoundTag("output", new NBTTagCompound());
		toSend.setCompoundTag("fluid", new NBTTagCompound());
		output.writeToNBT(toSend.getCompoundTag("output"));
		toSend.setBoolean("reversable", true);
		FluidRegistry.getFluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME).
			writeToNBT(toSend.getCompoundTag("fluid"));
		sendComm("TransposerFillRecipe", toSend);
	}
	
	private static void sendComm(String type, NBTTagCompound msg)
	{
		FMLInterModComms.sendMessage("ThermalExpansion", type, msg);
	}
}
//*/