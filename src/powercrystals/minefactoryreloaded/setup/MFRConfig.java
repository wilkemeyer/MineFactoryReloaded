package powercrystals.minefactoryreloaded.setup;

import static net.minecraftforge.common.config.Configuration.*;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class MFRConfig
{
	// client config
	public static Property spyglassRange;
	public static Property brightRednetBand;
	
	// common config
	public static Property dropFilledContainers;
	
	public static Property machineBlock0Id;
	public static Property machineBlock1Id;
	public static Property machineBlock2Id;
	
	public static Property conveyorBlockId;
	
	public static Property factoryGlassBlockId;
	public static Property factoryGlassPaneBlockId;
	public static Property factoryRoadBlockId;
	public static Property factoryDecorativeBrickBlockId;
	public static Property factoryDecorativeStoneBlockId;
	
	public static Property rubberWoodBlockId;
	public static Property rubberLeavesBlockId;
	public static Property rubberSaplingBlockId;
	
	public static Property railPickupCargoBlockId;
	public static Property railDropoffCargoBlockId;
	public static Property railPickupPassengerBlockId;
	public static Property railDropoffPassengerBlockId;
	
	public static Property rednetCableBlockId;
	public static Property rednetLogicBlockId;
	public static Property rednetPanelBlockId;
	
	public static Property fakeLaserBlockId;
	
	public static Property vineScaffoldBlockId;
	
	public static Property detCordBlockId;
	
	public static Property milkStillBlockId;
	public static Property sludgeStillBlockId;
	public static Property sewageStillBlockId;
	public static Property essenceStillBlockId;
	public static Property biofuelStillBlockId;
	public static Property meatStillBlockId;
	public static Property pinkslimeStillBlockId;
	public static Property chocolateMilkStillBlockId;
	public static Property mushroomSoupStillBlockId;
	
	public static Property hammerItemId;
	public static Property milkItemId; //TODO: UNUSED
	public static Property sludgeItemId; //TODO: UNUSED
	public static Property sewageItemId; //TODO: UNUSED
	public static Property mobEssenceItemId; //TODO: UNUSED
	public static Property fertilizerItemId;
	public static Property plasticSheetItemId;
	public static Property rawPlasticItemId;
	public static Property rubberBarItemId;
	public static Property sewageBucketItemId;
	public static Property sludgeBucketItemId;
	public static Property mobEssenceBucketItemId;
	public static Property syringeEmptyItemId;
	public static Property syringeHealthItemId;
	public static Property syringeGrowthItemId;
	public static Property rawRubberItemId;
	public static Property machineBaseItemId;
	public static Property safariNetItemId;
	public static Property ceramicDyeItemId;
	public static Property blankRecordId;
	public static Property syringeZombieId;
	public static Property safariNetSingleItemId;
	public static Property bioFuelItemId; //TODO: UNUSED
	public static Property bioFuelBucketItemId;
	public static Property upgradeItemId;
	public static Property safariNetLauncherItemId;
	public static Property sugarCharcoalItemId;
	public static Property milkBottleItemId;
	public static Property spyglassItemId;
	public static Property portaSpawnerItemId;
	public static Property strawItemId;
	public static Property xpExtractorItemId;
	public static Property syringeSlimeItemId;
	public static Property syringeCureItemId;
	public static Property logicCardItemId;
	public static Property rednetMeterItemId;
	public static Property rednetMemoryCardItemId;
	public static Property rulerItemId;
	public static Property meatIngotRawItemId;
	public static Property meatIngotCookedItemId;
	public static Property meatNuggetRawItemId;
	public static Property meatNuggetCookedItemId;
	public static Property meatBucketItemId;
	public static Property pinkSlimeBucketItemId;
	public static Property pinkSlimeballItemId;
	public static Property safariNetJailerItemId;
	public static Property laserFocusItemId;
	public static Property chocolateMilkBucketItemId;
	public static Property mushroomSoupBucketItemId;
	public static Property needlegunItemId;
	public static Property needlegunAmmoEmptyItemId;
	public static Property needlegunAmmoStandardItemId;
	public static Property needlegunAmmoLavaItemId;
	public static Property needlegunAmmoSludgeItemId;
	public static Property needlegunAmmoSewageItemId;
	public static Property needlegunAmmoFireItemId;
	public static Property needlegunAmmoAnvilItemId;
	public static Property plasticCupItemId;
	public static Property rocketLauncherItemId;
	public static Property rocketItemId;
	public static Property plasticCellItemId;
	public static Property fishingRodItemId;
	public static Property bagItemId;
	public static Property plasticBootsItemId;
	
	public static Property zoolologistEntityId;
	
	public static Property colorblindMode;
	public static Property treeSearchMaxVertical;
	public static Property treeSearchMaxHorizontal;
	public static Property verticalHarvestSearchMaxVertical;
	public static Property enableBonemealFertilizing;
	public static Property conveyorCaptureNonItems;
	public static Property conveyorNeverCapturesPlayers;
	public static Property conveyorNeverCapturesTCGolems;
	public static Property playSounds;
	public static Property fruitTreeSearchMaxVertical;
	public static Property fruitTreeSearchMaxHorizontal;
	public static Property breederShutdownThreshold;
	public static Property autospawnerCostStandard;
	public static Property autospawnerCostExact;
	public static Property laserdrillCost;
	public static Property meatSaturation;
	public static Property fishingDropRate;
	
	public static Property vanillaOverrideGlassPane;
	public static Property vanillaOverrideIce;
	public static Property vanillaOverrideMilkBucket;
	
	public static Property enableCheapDSU;
	public static Property craftSingleDSU;
	public static Property enableMossyCobbleRecipe;
	public static Property enablePortaSpawner;
	public static Property enableSyringes;
	public static Property enableLiquidSyringe;
	public static Property enableGuns;
	public static Property enableNetLauncher;
	public static Property enableSPAMRExploding;
	public static Property enableFuelExploding;
	public static Property enableSpawnerCarts;
	public static Property enableMassiveTree;
	public static Property enableChunkLimitBypassing;
	public static Property enableChunkLoaderRequiresOwner;
	public static Property enableSmoothSlabRecipe;
	public static Property enableCheapCL;
	
	public static Property redNetDebug;
	
	public static Property rubberTreeWorldGen;
	public static Property mfrLakeWorldGen;
	public static Property mfrLakeSewageRarity;
	public static Property mfrLakeSludgeRarity;
	
	public static Property redNetConnectionBlacklist;
	public static Property rubberTreeBiomeWhitelist;
	public static Property rubberTreeBiomeBlacklist;
	public static Property worldGenDimensionBlacklist;
	public static Property unifierBlacklist;
	public static Property spawnerBlacklist;
	
	public static Property passengerRailSearchMaxHorizontal;
	public static Property passengerRailSearchMaxVertical;
	
	// recipes config
	public static Property vanillaRecipes;
	public static Property thermalExpansionRecipes;
	public static Property gregTechRecipes;
	
	public static String CATEGORY_ITEM = "item";
	
	public static void loadClientConfig(File configFile)
	{
		Configuration c = new Configuration(configFile);
		
		spyglassRange = c.get(CATEGORY_GENERAL, "SpyglassRange", 200);
		spyglassRange.comment = "The maximum number of blocks the spyglass and ruler can look to find something. This calculation is performed only on the client side.";
		brightRednetBand = c.get(CATEGORY_GENERAL, "BrightRedNetColors", true);
		brightRednetBand.comment = "If true, RedNet color bands will always be bright.";
		
		c.save();
	}
	
	private static Configuration config;
	
	public static void loadCommonConfig(File configFile)
	{
		Configuration c = new Configuration(configFile);
		c.load();
		config = c;
		zoolologistEntityId = c.get("Entity", "ID.Zoologist", 330);
		enableSpawnerCarts = c.get("Entity", "EnableSpawnerCarts", true);
		enableSpawnerCarts.comment = "If true, using a portaspawner on an empty minecart will make it into a spawner cart";
		
		colorblindMode = c.get(CATEGORY_GENERAL, "RedNet.EnableColorblindMode", false);
		colorblindMode.comment = "Set to true to enable the RedNet GUI's colorblind mode.";
		deleteEntry(CATEGORY_GENERAL, "SearchDistance.TreeMaxHoriztonal");
		treeSearchMaxHorizontal = c.get(CATEGORY_GENERAL, "SearchLimit.TreeMaxHorizontal", 200);
		treeSearchMaxHorizontal.comment = "When searching for parts of a tree, how far out to the sides (radius) to search";
		deleteEntry(CATEGORY_GENERAL, "SearchDistance.TreeMaxVertical");
		treeSearchMaxVertical = c.get(CATEGORY_GENERAL, "SearchLimit.TreeMaxVertical", 256);
		treeSearchMaxVertical.comment = "When searching for parts of a tree, how far up to search";
		verticalHarvestSearchMaxVertical = c.get(CATEGORY_GENERAL, "SearchDistance.StackingBlockMaxVertical", 5);
		verticalHarvestSearchMaxVertical.comment = "How far upward to search for members of \"stacking\" blocks, like cactus and sugarcane";
		passengerRailSearchMaxVertical = c.get(CATEGORY_GENERAL, "SearchDistance.PassengerRailMaxVertical", 2);
		passengerRailSearchMaxVertical.comment = "When searching for players or dropoff locations, how far up to search";
		passengerRailSearchMaxHorizontal = c.get(CATEGORY_GENERAL, "SearchDistance.PassengerRailMaxHorizontal", 3);
		passengerRailSearchMaxHorizontal.comment = "When searching for players or dropoff locations, how far out to the sides (radius) to search";
		rubberTreeWorldGen = c.get(CATEGORY_GENERAL, "WorldGen.RubberTree", true);
		rubberTreeWorldGen.comment = "Whether or not to generate rubber trees during map generation";
		mfrLakeWorldGen = c.get(CATEGORY_GENERAL, "WorldGen.MFRLakes", true);
		mfrLakeWorldGen.comment = "Whether or not to generate MFR lakes during map generation";
		enableBonemealFertilizing = c.get(CATEGORY_GENERAL, "Fertilizer.EnableBonemeal", false);
		enableBonemealFertilizing.comment = "If true, the fertilizer will use bonemeal as well as MFR fertilizer. Provided for those who want a less work-intensive farm.";
		conveyorCaptureNonItems = c.get(CATEGORY_GENERAL, "Conveyor.CaptureNonItems", true);
		conveyorCaptureNonItems.comment = "If false, conveyors will not grab non-item entities. Breaks conveyor mob grinders but makes them safe for golems, etc.";
		conveyorNeverCapturesPlayers = c.get(CATEGORY_GENERAL, "Conveyor.NeverCapturePlayers", false);
		conveyorNeverCapturesPlayers.comment = "If true, conveyors will NEVER capture players regardless of other settings.";
		conveyorNeverCapturesTCGolems = c.get(CATEGORY_GENERAL, "Conveyor.NeverCaptureTCGolems", false);
		conveyorNeverCapturesTCGolems.comment = "If true, conveyors will NEVER capture Thaumcraft golems regardless of other settings.";
		playSounds = c.get(CATEGORY_GENERAL, "PlaySounds", true);
		playSounds.comment = "Set to false to disable the harvester's sound when a block is harvested.";
		deleteEntry(CATEGORY_GENERAL, "Road.Slippery");
		fruitTreeSearchMaxHorizontal = c.get(CATEGORY_GENERAL, "SearchDistance.FruitTreeMaxHoriztonal", 5);
		fruitTreeSearchMaxHorizontal.comment = "When searching for parts of a fruit tree, how far out to the sides (radius) to search";
		fruitTreeSearchMaxVertical = c.get(CATEGORY_GENERAL, "SearchDistance.FruitTreeMaxVertical", 20);
		fruitTreeSearchMaxVertical.comment = "When searching for parts of a fruit tree, how far up to search";
		breederShutdownThreshold = c.get(CATEGORY_GENERAL, "Breeder.ShutdownThreshold", 50);
		breederShutdownThreshold.comment = "If the number of entities in the breeder's target area exceeds this value, the breeder will cease operating. This is provided to control server lag.";
		autospawnerCostExact = c.get(CATEGORY_GENERAL, "AutoSpawner.Cost.Exact", 50);
		autospawnerCostExact.comment = "The work required to generate a mob in exact mode.";
		autospawnerCostStandard = c.get(CATEGORY_GENERAL, "AutoSpawner.Cost.Standard", 15);
		autospawnerCostStandard.comment = "The work required to generate a mob in standard (non-exact) mode.";
		laserdrillCost = c.get(CATEGORY_GENERAL, "LaserDrill.Cost", 300);
		laserdrillCost.comment = "The work required by the drill to generate a single ore.";
		meatSaturation = c.get(CATEGORY_GENERAL, "Meat.IncreasedSaturation", false);
		meatSaturation.comment = "If true, meat will be worth steak saturation instead of cookie saturation.";
		fishingDropRate = c.get(CATEGORY_GENERAL, "FishDropRate", 5);
		fishingDropRate.comment = "The rate at which fish are dropped from the fishing rod. The drop rate is 1 / this number. Must be greater than 0.";
		
		vanillaOverrideGlassPane = c.get(CATEGORY_GENERAL, "VanillaOverride.GlassPanes", true);
		vanillaOverrideGlassPane.comment = "If true, allows vanilla glass panes to connect to MFR stained glass panes.";
		vanillaOverrideIce = c.get(CATEGORY_GENERAL, "VanillaOverride.Ice", true);
		vanillaOverrideIce.comment = "If true, enables MFR unmelting ice as well as vanilla ice.";
		vanillaOverrideMilkBucket = c.get(CATEGORY_GENERAL, "VanillaOverride.MilkBucket", true);
		vanillaOverrideMilkBucket.comment = "If true, replaces the vanilla milk bucket so milk can be placed in the world.";
		
		redNetDebug = c.get(CATEGORY_GENERAL, "RedNet.Debug", false);
		redNetDebug.comment = "If true, RedNet cables will dump a massive amount of data to the log file. You should probably only use this if PC tells you to.";
		
		rubberTreeBiomeWhitelist = c.get(CATEGORY_GENERAL, "WorldGen.RubberTreeBiomeWhitelist", "");
		rubberTreeBiomeWhitelist.comment = "A comma-separated list of biomes to allow rubber trees to spawn in. Does nothing if rubber tree worldgen is disabled.";
		rubberTreeBiomeBlacklist = c.get(CATEGORY_GENERAL, "WorldGen.RubberTreeBiomeBlacklist", "");
		rubberTreeBiomeBlacklist.comment = "A comma-separated list of biomes to disallow rubber trees to spawn in. Overrides any other biomes added.";
		redNetConnectionBlacklist = c.get(CATEGORY_GENERAL, "RedNet.ConnectionBlackList", "");
		redNetConnectionBlacklist.comment = "A comma-separated list of block IDs to prevent RedNet cables from connecting to.";
		worldGenDimensionBlacklist = c.get(CATEGORY_GENERAL, "WorldGen.DimensionBlacklist", "");
		worldGenDimensionBlacklist.comment = "A comma-separated list of dimension IDs to disable MFR worldgen in. By default, MFR will not attempt worldgen in dimensions where the player cannot respawn.";
		mfrLakeSludgeRarity = c.get(CATEGORY_GENERAL, "WorldGen.LakeRarity.Sludge", 32);
		mfrLakeSludgeRarity.comment = "Higher numbers make sludge lakes rarer. A value of one will be approximately one per chunk.";
		mfrLakeSewageRarity = c.get(CATEGORY_GENERAL, "WorldGen.LakeRarity.Sewage", 32);
		mfrLakeSewageRarity.comment = "Higher numbers make sewage lakes rarer. A value of one will be approximately one per chunk.";
		unifierBlacklist = c.get(CATEGORY_GENERAL, "Unifier.Blacklist", "dyeBlue,dyeWhite,dyeBrown,dyeBlack,listAllwater,listAllmilk");
		unifierBlacklist.comment = "A comma-separated list of ore dictionary entrys to disable unifying for. By default, MFR will not attempt to unify anything with more than one oredict name.";
		spawnerBlacklist = c.get(CATEGORY_GENERAL, "AutoSpawner.Blacklist", "");
		spawnerBlacklist.comment = "A comma-separated list of entity IDs (e.g.: CaveSpider,VillagerGolem,butterflyGE) to blacklist from the AutoSpawner.";
		
		vanillaRecipes = c.get("RecipeSets", "EnableVanillaRecipes", true);
		vanillaRecipes.comment = "If true, MFR will register its standard (vanilla-item-only) recipes.";
		thermalExpansionRecipes = c.get("RecipeSets", "EnableThermalExpansionRecipes", false);
		thermalExpansionRecipes.comment = "If true, MFR will register its Thermal Expansion-based recipes.";
		gregTechRecipes = c.get("RecipeSets", "EnableGregTechRecipes", false);
		gregTechRecipes.comment = "If true, MFR will register its GregTech-based recipes.";
		
		enableCheapDSU = loadLegacy(CATEGORY_ITEM, "Recipe.CheaperDSU",
				CATEGORY_GENERAL, "DSU.EnableCheaperRecipe", false);
		enableCheapDSU.comment = "If true, the DSU can be built out of chests instead of ender pearls. Does nothing if the recipe is disabled.";
		craftSingleDSU = loadLegacy(CATEGORY_ITEM, "Recipe.SingleDSU",
				CATEGORY_GENERAL, "DSU.CraftSingle", false);
		craftSingleDSU.comment = "DSU recipes will always craft one DSU. Does nothing for recipes that already only craft one DSU (cheap mode, GT recipes, etc).";
		enableMossyCobbleRecipe = loadLegacy(CATEGORY_ITEM, "Recipe.MossyCobble",
				CATEGORY_GENERAL, "EnableMossyCobbleRecipe", false);
		enableMossyCobbleRecipe.comment = "If true, mossy cobble can be crafted.";
		enablePortaSpawner = c.get(CATEGORY_ITEM, "Recipe.PortaSpawner", true);
		enablePortaSpawner.comment = "If true, the PortaSpawner will be craftable.";
		enableSyringes = c.get(CATEGORY_ITEM, "Recipe.Syringes", true);
		enableSyringes.comment = "If true, the Syringes will be craftable.";
		enableGuns = c.get(CATEGORY_ITEM, "Recipe.Guns", true);
		enableGuns.comment = "If true, the Guns will be craftable.";
		enableNetLauncher = c.get(CATEGORY_ITEM, "Recipe.NetLauncher", true);
		enableNetLauncher.comment = "If true, the safarinet launcher will be craftable.";
		enableLiquidSyringe = c.get(CATEGORY_GENERAL, "LiquidSyringes", true);
		enableLiquidSyringe.comment = "If true, Empty Syringes will be able to contain liquids and inject players.";
		enableSPAMRExploding = c.get(CATEGORY_ITEM, "SPAMR.Exploding", true);
		enableSPAMRExploding.comment = "If true, SPAMRs will explode when they run out of fuel.";
		enableFuelExploding = c.get(CATEGORY_GENERAL, "Biofuel.Exploding", true);
		enableFuelExploding.comment = "If true, biofuel will explode when in the nether.";
		enableMassiveTree = c.get(CATEGORY_GENERAL, "WorldGen.SacredRubberSapling", true);
		enableMassiveTree.comment = "If true, enable adding Sacred Rubber Sapling to jungle temple loot.";
		enableChunkLimitBypassing = c.get("Machine", Machine.ChunkLoader.getName() + ".IgnoreChunkLimit", false);
		enableChunkLimitBypassing.comment = "If true, the Chunk Loader will ignore forgeChunkLoading.cfg.";
		enableChunkLoaderRequiresOwner = c.get("Machine", Machine.ChunkLoader.getName() + ".RequiresOwnerOnline", false);
		enableChunkLoaderRequiresOwner.comment = "If true, the Chunk Loader will require that the player who placed it be online to function";
		enableSmoothSlabRecipe = c.get(CATEGORY_ITEM, "Recipe.SmoothSlab", true);
		enableSmoothSlabRecipe.comment = "If true, smooth double stone slabs can be craftable.";
		enableCheapCL = c.get(CATEGORY_ITEM, "Recipe.CheaperChunkLoader", false);
		enableCheapCL.comment = "If true, the ChunkLoader can be built out of cheaper materials. Does nothing if the recipe is disabled.";
		
		for(Machine machine : Machine.values())
		{
			machine.load(c);
		}
		
		// TODO: make this config per-player
		dropFilledContainers = c.get(CATEGORY_GENERAL, "Tanks.FillWithoutEmptySlots", true);
		dropFilledContainers.comment = "If true, when you have no empty slots in your inventory, you will continue filling buckets from tanks and drop them on the ground.";
		
		c.save();
	}
	
	private static Property loadLegacy(String category, String name,
						String oldCategory, String oldName, boolean def)
	{
		Property r = null;
		String old = null;
		
		if (config.hasKey(oldCategory, oldName))
		{
				r = config.get(oldCategory, oldName, def);
				old = r.getString();
				deleteEntry(oldCategory, oldName);
		}
		
		r = config.get(category, name, def);
		if (old != null)
			r.set(old);
		return r;
	}
	
	private static void deleteEntry(String category, String name)
	{
		config.getCategory(category).remove(name);
	}
}