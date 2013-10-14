package powercrystals.minefactoryreloaded.modhelpers.thaumcraft;

import net.minecraft.block.Block;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableStandard;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "MineFactoryReloaded|CompatThaumcraft", name = "MFR Compat: Thaumcraft", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:Thaumcraft")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class Thaumcraft
{
	@EventHandler
	public static void load(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("Thaumcraft"))
		{
			FMLLog.warning("Thaumcraft missing - MFR Thaumcraft Compat not loading");
			return;
		}
		
		try
		{
			Block tcSapling = GameRegistry.findBlock("Thaumcraft", "tile.blockCustomPlant");
			Block tcLog = GameRegistry.findBlock("Thaumcraft", "tile.blockMagicalLog");
			Block tcLeaves = GameRegistry.findBlock("Thaumcraft", "tile.blockMagicalLeaves");
			Block tcFibres = GameRegistry.findBlock("Thaumcraft", "tile.blockTaintFibres");
			Class<?> golem = Class.forName("thaumcraft.common.entities.golems.EntityGolemBase");
			
			MFRRegistry.registerHarvestable(new HarvestableStandard(tcLog.blockID, HarvestType.Tree));
			MFRRegistry.registerHarvestable(new HarvestableStandard(tcFibres.blockID, HarvestType.Normal));
			MFRRegistry.registerHarvestable(new HarvestableThaumcraftLeaves(tcLeaves.blockID, tcSapling.blockID));
			MFRRegistry.registerHarvestable(new HarvestableThaumcraftPlant(tcSapling.blockID));
			
			MFRRegistry.registerPlantable(new PlantableThaumcraftTree(tcSapling.blockID, tcSapling.blockID));
			
			MFRRegistry.registerAutoSpawnerBlacklistClass(golem);
			
			MFRRegistry.registerGrinderBlacklist(golem);
			
			if (MFRConfig.conveyorNeverCapturesTCGolems.getBoolean(false))
			{
				MFRRegistry.registerConveyerBlacklist(golem);
			}
		}
		catch(Exception x)
		{
			x.printStackTrace();
		}
	}
}