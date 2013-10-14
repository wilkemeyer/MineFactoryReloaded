package powercrystals.minefactoryreloaded.modhelpers.thaumcraft;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableStandard;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "MineFactoryReloaded|CompatThaumcraft", name = "MFR Compat: Thaumcraft", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:Thaumcraft")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class Thaumcraft
{
	@EventHandler
	public static void load(FMLPostInitializationEvent e)
	{
		if(!Loader.isModLoaded("Thaumcraft"))
		{
			FMLLog.warning("Thaumcraft missing - MFR Thaumcraft Compat not loading");
			return;
		}
		
		try
		{
			Block tcSapling = GameRegistry.findBlock("Thaumcraft", "blockCustomPlant");
			Block tcLog = GameRegistry.findBlock("Thaumcraft", "blockMagicalLog");
			Block tcLeaves = GameRegistry.findBlock("Thaumcraft", "blockMagicalLeaves");
			Block tcFibres = GameRegistry.findBlock("Thaumcraft", "blockTaintFibres");
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
			
			Class<?> Aspect = Class.forName("thaumcraft.api.aspects.Aspect");
			aspects = (LinkedHashMap<String, ? extends Object>)Aspect.
					getDeclaredField("aspects").get(null);
			Class<?> ThaumcraftApi = Class.forName("thaumcraft.api.ThaumcraftApi");
			AspectList = Class.forName("thaumcraft.api.aspects.AspectList");
			registerItem = ThaumcraftApi.getDeclaredMethod("registerObjectTag",
					Integer.class, Integer.class, AspectList);
			registerEntity = ThaumcraftApi.getDeclaredMethod("registerObjectTag",
					String.class, AspectList, NBTBase[].class);
			addAspect = AspectList.getDeclaredMethod("add", Aspect, Integer.class);
			
			//doAspects();
		}
		catch(Throwable x)
		{
			x.printStackTrace();
		}
	}
	
	private static LinkedHashMap<String, ? extends Object> aspects = null;
	private static Method registerItem = null;
	private static Method registerEntity = null;
	private static Class<?> AspectList = null;
	private static Method addAspect = null;
	
	private static void parseAspects(ItemStack item, String toadd) throws Throwable
	{
		String[] list = toadd.split(",");
		Object aspectList = AspectList.newInstance();
		for (int i = list.length; i --> 0; )
		{
			String[] temp = list[i].trim().split(" ");
			temp[1] = temp[1].toLowerCase();
			if (aspects.containsKey(temp[1]))
				addAspect.invoke(aspectList, aspects.get(temp[1]), Integer.parseInt(temp[0], 10));
		}
		registerItem.invoke(null, item.itemID, item.getItemDamage(), aspectList);
	}
	
	private static void parseAspects(String entity, String toadd) throws Throwable
	{
		String[] list = toadd.split(",");
		Object aspectList = AspectList.newInstance();
		for (int i = list.length; i --> 0; )
		{
			String[] temp = list[i].trim().split(" ");
			temp[1] = temp[1].toLowerCase();
			if (aspects.containsKey(temp[1]))
				addAspect.invoke(aspectList, aspects.get(temp[1]), Integer.parseInt(temp[0], 10));
		}
		registerEntity.invoke(null, entity, aspectList, null);
	}
	
	private static void parseAspects(Item item, String toadd) throws Throwable
	{
		parseAspects(new ItemStack(item, 1, 0), toadd);
	}
	
	private static void parseAspects(Block item, String toadd) throws Throwable
	{
		parseAspects(new ItemStack(item, 1, 0), toadd);
	}
	
	private static void parseAspects(Machine item, String toadd) throws Throwable
	{
		parseAspects(new ItemStack(item.getBlockId(), 1, item.getMeta()), toadd);
	}
	
	private static void doAspects() throws Throwable
	{
		parseAspects(Machine.AutoAnvil, "3 Permutatio, 5 Fabrico, 10 Metallum, 5 Machina");
		parseAspects(Machine.AutoBrewer, "4 ignis, 2 Fabrico, 2 Aqua, 2 Praecantio, 5 Machina");
		parseAspects(Machine.AutoDisenchanter, "4 Praecantio, 4 Permutatio, 5 Machina");
		parseAspects(Machine.AutoEnchanter, "8 Preacantio, 4 Cognitio, 4 Fabricio, 5 Machina");
		parseAspects(Machine.AutoJukebox, "4 Sensus, 4 Aer, 5 Machina");
		parseAspects(Machine.AutoSpawner, "4 Beastia, 4 Examinis, 2 Itor, 5 Machina");
		parseAspects(Machine.BioFuelGenerator, "5 Potentia, 3 Herba, 5 Machina");
		parseAspects(Machine.BioReactor, "4 Herba, 2 Potentia, 5 Machina");
		parseAspects(Machine.BlockBreaker, "4 Perfodio, 5 Machina, 5 Metalum, 3 Lucrum");
		parseAspects(Machine.BlockSmasher, "15 Perditio, 5 Machina");
		parseAspects(Machine.Breeder, "2 Beastia, 2 Famus, 5 Machina");
		parseAspects(Machine.Chronotyper, "3 Tempus, 3 Beastia, 5 Machina, 3 Sensus");
		parseAspects(Machine.Composter, "2 Beastia, 2 Aqua, 5 Machina");
		parseAspects(Machine.DeepStorageUnit, "20 Vacous, 5 Machina, 5 Alienis");
		parseAspects(Machine.Ejector, "4 Motus, 5 Machina");
		parseAspects(Machine.EnchantmentRouter, "1 Motus, 4 Itor, 2 Sensus, 5 Machina, 1 Praecantio");
		parseAspects(Machine.Fertilizer, "6 Herba, 5 Machina, 1 Vitreus, 3 Victus");
		parseAspects(Machine.Fisher, "3 Aqua, 5 Machina, 4 Metalum, 2 Instrumentum");
		parseAspects(Machine.FruitPicker, "2 Herba, 4 Meso, 5 Machina");
		parseAspects(Machine.Grinder, "10 Telum, 3 Mortus, 7 Meto, 5 Machina, 4 Metalum, 2 Lucrum");
		parseAspects(Machine.ItemCollector, "4 Vacous, 5 Machina, 2 Arbor, 4 Motus");
		parseAspects(Machine.ItemRouter, "2 Motus, 4 Itor, 2 Sensus,  5 Machina");
		parseAspects(Machine.LaserDrill, "5 Perfodio, 15 Lux, 5 Machina, 4 Victus");
		parseAspects(Machine.LaserDrillPrecharger, "4 Lux, 5 Machina, 5 Potentia, 2 Ligus");
		parseAspects(Machine.LavaFabricator, "4 ignis, 4 Saxum, 4 Fabrico, 5 Machina");
		parseAspects(Machine.LiquiCrafter, "5 Aqua, 5 Fabrico, 5 Machina");
		parseAspects(Machine.LiquidRouter, "1 Motus, 4 Itor, 2 Sensus, 5 Machina, 1 Aqua");
		parseAspects(Machine.MeatPacker, "2 Ordo, 2 Corpus, 2 Famus, 5 Machina");
		//parseAspects(Machine.OilFabricator, "Not Sure What Aspects Oil Is Getting. Wait On Other Mods I Recommend.");
		parseAspects(Machine.Planter, "4 Herba, 2 Granum, 4 Meso, 5 Machina");
		parseAspects(Machine.Rancher, "6 Meto, 5 Machina, 4 Metalum, 2 Instrumentum");
		parseAspects(Machine.RedNote, "4 Aer, 4 Sensus, 5 Machina");
		parseAspects(Machine.Sewer, "1 Venenum, 3 Aqua 5 Machina, 4 Beastia");
		parseAspects(Machine.Slaughterhouse, "12 Telum, 5 Mortus, 5 Machina, 12 Metalum, 6 Lucrum");
		parseAspects(Machine.SludgeBoiler, "3 Venenum, 3 Terra, 2 Aqua, 5 Machina, 2 ignis");
		parseAspects(Machine.Unifier, "5 Ordo, 2 Alienis, 5 Machina");
		parseAspects(Machine.Vet, "4 Sano, 5 Machina, 4 Beastia");
		parseAspects(Machine.WeatherCollector, " 4 Vacous, 5 Machina, 5 Metalum, 4 Tempestas");

		parseAspects("mfrEntityPinkSlime", "1 Aqua, 2 Limus, 1 Corpus, 1 Bestia");
		parseAspects(MineFactoryReloadedCore.bioFuelBucketItem, "2 Herba, 1 Potentia, 1 Aqua, 8 Metalum, 1 Vacuous");
		parseAspects(MineFactoryReloadedCore.biofuelLiquid, "4 Herba, 2 Potentia, 2 Aqua");
		parseAspects(MineFactoryReloadedCore.blankRecordItem, "4 Sensus, 4 Aer, 4 Lucrum, 4 Vacuous");
		parseAspects(MineFactoryReloadedCore.ceramicDyeItem, "1 Terra, 1 Aqua, 1 Sensus");
		parseAspects(MineFactoryReloadedCore.chocolateMilkBucketItem, "2 Fames, 1 Motus, 1 Potentia, 2 Aqua, 8 Metallum, 1 Vacuous");
		parseAspects(MineFactoryReloadedCore.chocolateMilkLiquid, "4 Fames, 2 Motus, 2 Potentia, 4 Aqua");
		parseAspects(MineFactoryReloadedCore.conveyorBlock, "3 Motus, 1 Itor, 1 Machina");
		parseAspects(MineFactoryReloadedCore.essenceLiquid, "4 Praecantio, 2 Cognito, 2 Aqua");
		parseAspects(MineFactoryReloadedCore.factoryGlassBlock, "1 Vitreus, 1 Sensus");
		parseAspects(MineFactoryReloadedCore.factoryHammerItem, "1 Instrumentum, 2 Fabrico, 2 ignis, 3 Ordo");
		parseAspects(MineFactoryReloadedCore.fertilizerItem, "1 Granum, 1 Herba, 1 Messis");
		parseAspects(MineFactoryReloadedCore.laserFocusItem, "1 Ordo, 1 Vitreus, 4 Lucrum");
		parseAspects(MineFactoryReloadedCore.machineBaseItem, "2 Fabrico, 2 Machina, 1 Saxum");
		parseAspects(MineFactoryReloadedCore.meatBucketItem, "3 Corpus, 1 Beastia, 1 Aqua, 8 Metalum, 1 Vacuous");
		parseAspects(MineFactoryReloadedCore.meatIngotCookedItem, "3 Corpus, 2 Famus, 1 Ignis");
		parseAspects(MineFactoryReloadedCore.meatIngotRawItem, "3 Corpus, 2 Famus, 1 Beastia");
		parseAspects(MineFactoryReloadedCore.meatLiquid, "6 Corpus, 2 Aqua, 2 Beastia");
		parseAspects(MineFactoryReloadedCore.meatNuggetCookedItem, "1 Famus");
		parseAspects(MineFactoryReloadedCore.meatNuggetRawItem, "1 Corpus");
		parseAspects(MineFactoryReloadedCore.milkBottleItem, "1 Fames, 1 Sano, 1 Victus, 1 Vitreus");
		parseAspects(MineFactoryReloadedCore.milkLiquid, "4 Fames, 4 Sano, 4 Aqua, 2 Victus");
		parseAspects(MineFactoryReloadedCore.mobEssenceBucketItem, "2 Praecantio, 1 Cognito, 1 Aqua, 8 Metalum, 1 Vacuous");
		parseAspects(MineFactoryReloadedCore.mushroomSoupBucketItem, "2 Famus, 2 Herba, 8 Metalum, 1 Vacuous");
		parseAspects(MineFactoryReloadedCore.mushroomSoupLiquid, "4 Famus, 4 Herba, 1 Aqua");
		parseAspects(MineFactoryReloadedCore.pinkSlimeballItem, "1 Limus, 1 Corpus");
		parseAspects(MineFactoryReloadedCore.pinkSlimeBucketItem, "2 Limus, 2 Corpus, 1 Aqua, 8 Metalum, 1 Vacuous");
		parseAspects(MineFactoryReloadedCore.pinkSlimeLiquid, "4 Limus, 4 Corpus, 2 Aqua");
		parseAspects(MineFactoryReloadedCore.plasticSheetItem, "1 Fabrico, 1 ignis, 2 Ordo");
		parseAspects(MineFactoryReloadedCore.portaSpawnerItem, "8 Alienis, 4 Beastia, 4 Examinis, 4 Itor, 8 Praecantio, 8 Permutatio");
		parseAspects(MineFactoryReloadedCore.rawPlasticItem, "1 Fabrico, 1 ignis, 1 Ordo, 1 Perditio");
		parseAspects(MineFactoryReloadedCore.rawRubberItem, "1 Limus, 2 Arbor");
		parseAspects(MineFactoryReloadedCore.rednetCableBlock, "1 Cognito, 1 Machina");
		parseAspects(MineFactoryReloadedCore.rednetLogicBlock, "10 Cogntio, 5 Machina");
		parseAspects(MineFactoryReloadedCore.rednetMemoryCardItem, "3 Cognito, 1 Machina");
		parseAspects(MineFactoryReloadedCore.rednetMeterItem, "1 Instrumentum, 1 Sensus, 1 Machina");
		parseAspects(MineFactoryReloadedCore.rednetPanelBlock, "2 Sensus, 2 Cognito, 2 Machina");
		parseAspects(MineFactoryReloadedCore.rubberBarItem, "1 Motus, 1 Arbor, 1 ignis");
		parseAspects(MineFactoryReloadedCore.rubberLeavesBlock, "1 Herba");
		parseAspects(MineFactoryReloadedCore.rubberSaplingBlock, "1 Arbor, 1 Herba, 1 Granum");
		parseAspects(MineFactoryReloadedCore.rubberWoodBlock, "3 Arbor, 1 Limus");
		parseAspects(MineFactoryReloadedCore.rulerItem, "1 Instrumentum, 1 Sensus");
		parseAspects(MineFactoryReloadedCore.safariNetItem, "4 Spiritus, 8 Alienis, 8 Itor, 4 Praecantio, 8 Vinculum, 4 Fabrico");
		parseAspects(MineFactoryReloadedCore.safariNetJailerItem, "6 Vinculum, 4 Lumus, 2 Metalum, 1 Fabrico");
		parseAspects(MineFactoryReloadedCore.safariNetLauncherItem, "2 Volatus, 2 Instrumentum");
		parseAspects(MineFactoryReloadedCore.safariNetSingleItem, "4 Vinculum, 4 Limus, 2 Instrumentum");
		parseAspects(MineFactoryReloadedCore.sewageBucketItem, "2 Venenum, 1 Beastia, 1 Aqua, 8 Metalum, 1 Vacuous");
		parseAspects(MineFactoryReloadedCore.sewageLiquid, "4 Venenum, 2 Beastia, 2 Aqua");
		parseAspects(MineFactoryReloadedCore.sludgeBucketItem, "2 Venenum, 1 Terra, 1 Aqua, 1 Vitium, 8 Metalum, 1 Vacuous");
		parseAspects(MineFactoryReloadedCore.sludgeLiquid, "4 Venenum, 2 Terra, 2 Aqua, 1 Vitium");
		parseAspects(MineFactoryReloadedCore.spyglassItem, "2 Victus, 6 Sensus");
		parseAspects(MineFactoryReloadedCore.strawItem, "1 Vacous, 4 Aqua, 4 Famus, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.sugarCharcoalItem, "2 Potentia, 2 ignis");
		parseAspects(MineFactoryReloadedCore.syringeCureItem, "2 Sano, 1 Exanimis, 1 Humanus, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.syringeEmptyItem, "1 Vacous, 1 Sano, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.syringeGrowthItem, "1 tempus, 2 Sano, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.syringeHealthItem, "2 Sano, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.syringeSlimeItem, "1 Sano, 1 Limus, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.syringeZombieItem, "1 tempus, 1 Sano, 1 Exanimis, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.vineScaffoldBlock, "1 Herba, 1 Fabrico");
		parseAspects(MineFactoryReloadedCore.xpExtractorItem, "1 Praecantio, 1 Permutatio, 1 Vacuous, 1 Instrumentum, 1 Meto");
		//parseAspects(Tracks, "Tracks Currently Have No Aspects.");
		
		/*
		parseAspects(MineFactoryReloadedCore.cookedMeatBlock, "10 Corpus, 7 Famus, 5 Fabrico");
		parseAspects(MineFactoryReloadedCore.glowstoneBricks, "1 Terra, 1 ignis, 2 Lux, 2 Sensus");
		parseAspects(MineFactoryReloadedCore.iceBricks, "1 Terra, 1 ignis, 2 Gelum");
		parseAspects(MineFactoryReloadedCore.lapisBricks, "1 Terra, 1 ignis, 4 Sensus");
		parseAspects(MineFactoryReloadedCore.large____Bricks, "See ___ Bricks -1 Terra, -1 ignis, + 2 Saxum");
		parseAspects(MineFactoryReloadedCore.obsidianBricks, "1 Terra, 3 ignis, 2 Saxum, 1 Tenebre");
		parseAspects(MineFactoryReloadedCore.pavedStoneBricks, "1 Terra, 1 Ingus, 2 Saxum");
		parseAspects(MineFactoryReloadedCore.programableRednetControllerHousing, "3 Congitio, 3 Machina");
		parseAspects(MineFactoryReloadedCore.rawMeatBlock, "10 Corpus, 7 Famus, 5 Beastia");
		parseAspects(MineFactoryReloadedCore.road, "1 Itor, 1 Saxum");
		parseAspects(MineFactoryReloadedCore.roadLight, "1 Itor, 1 Saxum, 1 Lux");
		parseAspects(MineFactoryReloadedCore.roadLightInverted, "1 Itor, 1 Saxum, 1 Lux");
		//*/
		
		/*
		parseAspects(MineFactoryReloadedCore.upgradeMaterial, "Material *2+ 2 Cogntio");
		parseAspects(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 0), "3 Cognitio");
		parseAspects(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 2), "5 Cognitio");
		parseAspects(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 3), "7 Cognitio");
		//*/
		
	}
}