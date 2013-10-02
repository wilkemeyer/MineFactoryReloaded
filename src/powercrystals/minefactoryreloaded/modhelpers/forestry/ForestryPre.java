package powercrystals.minefactoryreloaded.modhelpers.forestry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "MineFactoryReloaded|CompatForestryPre", name = "MFR Compat: Forestry (2)", version = MineFactoryReloadedCore.version, dependencies = "before:Forestry")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class ForestryPre
{
	@EventHandler
	public static void init(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("Forestry"))
		{
			return;
		}
		MineFactoryReloadedCore.registerFluid("milk", MineFactoryReloadedCore.milkLiquid);
		MineFactoryReloadedCore.registerFluid("sludge", MineFactoryReloadedCore.sludgeLiquid);
		MineFactoryReloadedCore.registerFluid("sewage", MineFactoryReloadedCore.sewageLiquid);
		MineFactoryReloadedCore.registerFluid("essence", MineFactoryReloadedCore.essenceLiquid);
		MineFactoryReloadedCore.registerFluid("biofuel", MineFactoryReloadedCore.biofuelLiquid);
		MineFactoryReloadedCore.registerFluid("meat", MineFactoryReloadedCore.meatLiquid);
		MineFactoryReloadedCore.registerFluid("pinkslime", MineFactoryReloadedCore.pinkSlimeLiquid);
		MineFactoryReloadedCore.registerFluid("chocolatemilk", MineFactoryReloadedCore.chocolateMilkLiquid);
		MineFactoryReloadedCore.registerFluid("mushroomsoup", MineFactoryReloadedCore.mushroomSoupLiquid);
		
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("milk", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Item.bucketMilk), new ItemStack(Item.bucketEmpty)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("sludge", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(MineFactoryReloadedCore.sludgeBucketItem), new ItemStack(Item.bucketEmpty)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("sewage", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(MineFactoryReloadedCore.sewageBucketItem), new ItemStack(Item.bucketEmpty)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("mobEssence", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(MineFactoryReloadedCore.mobEssenceBucketItem), new ItemStack(Item.bucketEmpty)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("biofuel", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(MineFactoryReloadedCore.bioFuelBucketItem), new ItemStack(Item.bucketEmpty)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("meat", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(MineFactoryReloadedCore.meatBucketItem), new ItemStack(Item.bucketEmpty)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("pinkslime", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(MineFactoryReloadedCore.pinkSlimeBucketItem), new ItemStack(Item.bucketEmpty)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("chocolatemilk", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(MineFactoryReloadedCore.chocolateMilkBucketItem), new ItemStack(Item.bucketEmpty)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("mushroomsoup", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(MineFactoryReloadedCore.mushroomSoupBucketItem), new ItemStack(Item.bucketEmpty)));
		
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("mushroomsoup", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Item.bowlSoup), new ItemStack(Item.bowlEmpty)));
	}
}
