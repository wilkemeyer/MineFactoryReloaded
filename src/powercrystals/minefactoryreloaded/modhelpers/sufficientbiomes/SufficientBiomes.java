package powercrystals.minefactoryreloaded.modhelpers.sufficientbiomes;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.CustomProperty;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

@Mod(modid = "MineFactoryReloaded|CompatSufficientBiomes", name = "MFR Compat: Sufficient Biomes", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:EmasherWorldGen",
customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class SufficientBiomes
{

	@EventHandler
	public static void load(FMLInitializationEvent ev)
	{
		if(!Loader.isModLoaded("EmasherWorldGen"))
		{
			return;
		}
		try
		{
			MFRRegistry.registerRubberTreeBiome("Hollow");
			MFRRegistry.registerRubberTreeBiome("Marsh");
			MFRRegistry.registerRubberTreeBiome("Foot Hills");
			MFRRegistry.registerRubberTreeBiome("Sand Forest");
			MFRRegistry.registerRubberTreeBiome("Prairie Forest");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}