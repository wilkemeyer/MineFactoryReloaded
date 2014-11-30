package powercrystals.minefactoryreloaded.modhelpers.rp2;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.CustomProperty;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableWood;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableCropPlant;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;

@Mod(modid = "MineFactoryReloaded|CompatRP2", name = "MFR Compat: RP2", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:RedPowerWorld",
customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class RP2
{
	@EventHandler
	public static void load(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("RedPowerWorld"))
		{
			return;
		}
		try
		{
			Class<?> modClass = Class.forName("com.eloraam.redpower.RedPowerWorld");

			Block blockIdLeaves = ((Block)modClass.getField("blockLeaves").get(null));
			Block blockIdLogs = ((Block)modClass.getField("blockLogs").get(null));
			Block blockIdPlants = ((Block)modClass.getField("blockPlants").get(null));
			Block blockIdCrops = ((Block)modClass.getField("blockCrops").get(null));

			Item itemCropSeedId = ((Item)modClass.getField("itemSeeds").get(null));

			Method fertilizeMethod = Class.forName("com.eloraam.redpower.world.BlockCustomFlower").getMethod("growTree", World.class, int.class, int.class, int.class);

			MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(blockIdLeaves));
			MFRRegistry.registerHarvestable(new HarvestableWood(blockIdLogs));
			MFRRegistry.registerHarvestable(new HarvestableRedPowerPlant(blockIdPlants));
			MFRRegistry.registerHarvestable(new HarvestableRedPowerFlax(blockIdCrops));

			MFRRegistry.registerPlantable(new PlantableStandard(blockIdPlants, blockIdPlants));
			MFRRegistry.registerPlantable(new PlantableCropPlant(itemCropSeedId, blockIdCrops));

			MFRRegistry.registerFertilizable(new FertilizableRedPowerFlax(blockIdCrops));
			MFRRegistry.registerFertilizable(new FertilizableRedPowerRubberTree(blockIdPlants, fertilizeMethod));
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
}
