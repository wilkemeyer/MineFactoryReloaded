package powercrystals.minefactoryreloaded.modhelpers.magicalcrops;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizerStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableCropPlant;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableCropPlant;

@Mod(modid = "MineFactoryReloaded|CompatMagicalCrops", name = "MFR Compat: Magical Crops", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:magicalcrops")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class MagicalCrops
{
	private static final String lastUpdated = "Magical Crops 2.1.2a, current release as of June 10 2013";
	
	@EventHandler
	public static void load(FMLInitializationEvent e)
	{
		if (e != null) return;
		if(!Loader.isModLoaded("magicalcrops"))
		{
			FMLLog.warning("Magical Crops missing - MFR Compat: Magical Crops not loading");
			return;
		}
		try
		{
			Class<?> mod = Class.forName("magicalcrops.mod_mCrops");
			
			Item magicalFertilizer = (Item)mod.getField("MagicFertilizer").get(null);
			if(magicalFertilizer != null)
			{
				MFRRegistry.registerFertilizer(new FertilizerStandard(magicalFertilizer.itemID, 0, FertilizerType.GrowMagicalCrop));
			}
			
			// the various plants are separated by type to make future changes easier (mostly considering magicFertilizer behavior)
			//String[] crops = {"Sberry", "Tomato", "Sweetcorn", "Cucum", "Melon", "Bberry", "Rberry", "Grape", "Chil"};
			String[] magicalCrops = { "Coal", "Redstone", "Glowstone", "Obsidian", "Dye", "Iron", "Gold",
									"Lapis", "Ender", "Nether", "XP", "Blaze", "Diamond", "Emerald" };
			String[] soulCrops = { "Cow", "Creeper", "Magma", "Skeleton", "Slime", "Spider", "Ghast" };
			
			int seedId;
			int blockId;
			Method fertilize;
			
			/*for(String crop : crops)
			{
				seedId = ((Item)mod.getField("seed" + crop).get(null)).itemID;
				blockId = ((Block)mod.getField("crop" + crop).get(null)).blockID;
				fertilize = Class.forName("magicalcrops.crop" + crop).getMethod("func_72272_c_", World.class, int.class, int.class, int.class);
				MFRRegistry.registerPlantable(new PlantableCropPlant(seedId, blockId));
				MFRRegistry.registerHarvestable(new HarvestableCropPlant(blockId, 7));
				MFRRegistry.registerFertilizable(new FertilizableCropReflection(blockId, fertilize, 7));
			}//*/
			
			for(String magicalCrop : magicalCrops)
			{
				seedId = ((Item)mod.getField("mSeeds" + magicalCrop).get(null)).itemID;
				blockId = ((Block)mod.getField("mCrop" + magicalCrop).get(null)).blockID;
				fertilize = Class.forName("magicalcrops.mCrop" + magicalCrop).getMethod("fertilize", World.class, int.class, int.class, int.class);
				MFRRegistry.registerPlantable(new PlantableCropPlant(seedId, blockId));
				MFRRegistry.registerHarvestable(new HarvestableCropPlant(blockId, 7));
				MFRRegistry.registerFertilizable(new FertilizableMagicalCropReflection(blockId, fertilize, 7));
			}
			
			for(String soulCrop : soulCrops)
			{
				seedId = ((Item)mod.getField("sSeeds" + soulCrop).get(null)).itemID;
				blockId = ((Block)mod.getField("soulCrop" + soulCrop).get(null)).blockID;
				fertilize = Class.forName("magicalcrops.soulCrop" + soulCrop).getMethod("fertilize", World.class, int.class, int.class, int.class);
				MFRRegistry.registerPlantable(new PlantableCropPlant(seedId, blockId));
				MFRRegistry.registerHarvestable(new HarvestableCropPlant(blockId, 7));
				MFRRegistry.registerFertilizable(new FertilizableMagicalCropReflection(blockId, fertilize, 7));
			}
			
		}
		catch (Exception x)
		{
			FMLLog.warning("Something went wrong in MFR Compat: Magical Crops. Probably Emy's fault.");
			System.out.println("Last updated for " + lastUpdated);
			x.printStackTrace();
		}
	}
}
