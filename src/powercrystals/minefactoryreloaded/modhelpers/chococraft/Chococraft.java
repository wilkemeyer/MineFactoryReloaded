package powercrystals.minefactoryreloaded.modhelpers.chococraft;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.CustomProperty;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableCropPlant;

@Mod(modid="MineFactoryReloaded|CompatChococraft", name = "MFR Compat: Chococraft",
version = MineFactoryReloadedCore.version,
dependencies = "required-after:MineFactoryReloaded;after:chococraft",
customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class Chococraft
{
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		if (!Loader.isModLoaded("chococraft"))
		{
			return;
		}

		try
		{
			Class<?> blocks = Class.forName("chococraft.common.config.ChocoCraftBlocks");

			FMLLog.info("Registering Gysahls for Planter/Harvester/Fertilizer");
			Block blockId = ((Block)(blocks.getField("gysahlStemBlock").get(null)));
			
			Class<?> items = Class.forName("chococraft.common.config.ChocoCraftItems");
			Item seedId = ((Item)(items.getField("gysahlSeedsItem").get(null)));

			MFRRegistry.registerPlantable(new PlantableCropPlant(seedId, blockId));
			MFRRegistry.registerHarvestable(new HarvestableChococraft(blockId));
			MFRRegistry.registerFertilizable(new FertilizableChococraft(blockId));
		}
		catch (ClassNotFoundException e)
		{
			FMLLog.warning("Unable to load support for Chococraft");
			e.printStackTrace();
		}
		catch (Exception e)
		{
			FMLLog.warning("Clienthax prob screwed up support for mfr, email him at clienthax@gmail.com");
			e.printStackTrace();
		}
	}
}
