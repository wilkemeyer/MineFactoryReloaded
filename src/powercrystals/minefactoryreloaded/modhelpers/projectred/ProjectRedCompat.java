package powercrystals.minefactoryreloaded.modhelpers.projectred;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSapling;

@Mod(modid = "MineFactoryReloaded|CompatProjRed", name = "MFR Compat: ProjectRed", version = MineFactoryReloadedCore.version, 
	 dependencies = "after:MineFactoryReloaded;after:ProjRed|Core;after:ProjRed|Exploration")
public class ProjectRedCompat
{
	@EventHandler
	public static void postInit(FMLPostInitializationEvent e)
	{
		if(!Loader.isModLoaded("ProjRed|Exploration"))
		{
			return;
		}
		try
		{
			Block stainedLeaf = GameRegistry.findBlock("ProjRed|Exploration", "projectred.exploration.dyeleaf");
			Block stainedSapling = GameRegistry.findBlock("ProjRed|Exploration", "projectred.exploration.dyesapling");
			
			MFRRegistry.registerPlantable(new PlantableSapling(stainedSapling));
			MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(stainedLeaf));
			MFRRegistry.registerFertilizable(new FertilizableStandard((IGrowable)stainedSapling));				
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}