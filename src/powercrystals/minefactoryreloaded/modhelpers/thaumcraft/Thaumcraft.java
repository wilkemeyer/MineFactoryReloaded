package powercrystals.minefactoryreloaded.modhelpers.thaumcraft;

import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.oredict.OreDictionary;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableCocoa;
import powercrystals.minefactoryreloaded.farmables.fruits.FruitCocoa;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableCocoa;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableWood;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableCocoa;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;

@Mod(modid = "MineFactoryReloaded|CompatThaumcraft", name = "MFR Compat: Thaumcraft", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:Thaumcraft")
public class Thaumcraft
{
	@EventHandler
	public static void load(FMLPostInitializationEvent e)
	{
		if(!Loader.isModLoaded("Thaumcraft"))
		{
			return;
		}

		try
		{
			final Block tcSapling = GameRegistry.findBlock("Thaumcraft", "blockCustomPlant");
			final Block tcLog = GameRegistry.findBlock("Thaumcraft", "blockMagicalLog");
			final Block tcLeaves = GameRegistry.findBlock("Thaumcraft", "blockMagicalLeaves");
			final Block tcFibres = GameRegistry.findBlock("Thaumcraft", "blockTaintFibres");
			final Block tcPod = GameRegistry.findBlock("Thaumcraft", "blockManaPod");
			final Item tcBean = GameRegistry.findItem("Thaumcraft", "ItemManaBean");
			Class<? extends EntityLivingBase> golem = (Class<? extends EntityLivingBase>) Class.
					forName("thaumcraft.common.entities.golems.EntityGolemBase");
			Class<? extends EntityLivingBase> trunk = (Class<? extends EntityLivingBase>) Class.
					forName("thaumcraft.common.entities.golems.EntityTravelingTrunk");
			Class<? extends EntityLivingBase> pech = (Class<? extends EntityLivingBase>) Class.
					forName("thaumcraft.common.entities.monster.EntityPech");

			MFRRegistry.registerAutoSpawnerBlacklistClass(golem);
			MFRRegistry.registerAutoSpawnerBlacklistClass(trunk);

			MFRRegistry.registerSpawnHandler(new SpawnablePech(pech));

			MFRRegistry.registerGrinderBlacklist(golem);
			MFRRegistry.registerGrinderBlacklist(trunk);

			if (MFRConfig.conveyorNeverCapturesTCGolems.getBoolean(false))
			{
				MFRRegistry.registerConveyerBlacklist(golem);
				MFRRegistry.registerConveyerBlacklist(trunk);
			}

			MFRRegistry.registerHarvestable(new HarvestableWood(tcLog));
			MFRRegistry.registerHarvestable(new HarvestableStandard(tcFibres, HarvestType.Normal));
			MFRRegistry.registerHarvestable(new HarvestableThaumcraftLeaves(tcLeaves,
					Item.getItemFromBlock(tcSapling)));
			MFRRegistry.registerHarvestable(new HarvestableThaumcraftPlant(tcSapling));
			MFRRegistry.registerHarvestable(new HarvestableCocoa(tcPod));

			MFRRegistry.registerPlantable(new PlantableThaumcraftTree(tcSapling));
			MFRRegistry.registerPlantable(new PlantableCocoa(tcBean, tcPod) {
				@Override
				protected boolean isNextToAcceptableLog(World world, int x, int y, int z)
				{
					boolean isMagic = false;
					BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
					if (biome != null)
						isMagic = BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MAGICAL);
					return isMagic &&
							isGoodLog(world, x+1, y, z) ||
							isGoodLog(world, x-1, y, z) ||
							isGoodLog(world, x, y, z+1) ||
							isGoodLog(world, x, y, z-1);
				}

				@Override
				protected boolean isGoodLog(World world, int x, int y, int z)
				{
					Block id = world.getBlock(x, y, z);
					return id == tcLog || id.equals(Blocks.log);
				}
			});

			MFRRegistry.registerFruitLogBlock(tcLog);
			MFRRegistry.registerFruit(new FruitCocoa(tcPod));

			MFRRegistry.registerFertilizable(new FertilizableCocoa(tcPod, FertilizerType.GrowMagicalCrop));
			MFRRegistry.registerFertilizable(new FertilizableTCSapling(tcSapling));

			Class<?> Aspect = Class.forName("thaumcraft.api.aspects.Aspect");
			aspects = (LinkedHashMap<String, ? extends Object>)Aspect.
					getDeclaredField("aspects").get(null);
			Class<?> ThaumcraftApi = Class.forName("thaumcraft.api.ThaumcraftApi");
			AspectList = Class.forName("thaumcraft.api.aspects.AspectList");
			registerItem = ThaumcraftApi.getDeclaredMethod("registerObjectTag", ItemStack.class, AspectList);
			Class<?> EntityTagsNBT = Class.forName("[Lthaumcraft.api.ThaumcraftApi$EntityTagsNBT");
			registerEntity = ThaumcraftApi.getDeclaredMethod("registerEntityTag",
					String.class, AspectList, EntityTagsNBT);
			addAspect = AspectList.getDeclaredMethod("add", Aspect, int.class);
			newAspectList = AspectList.getDeclaredConstructor(ItemStack.class);

			doAspects();
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
	private static Constructor<?> newAspectList = null;
	private static Method addAspect = null;

	private static void parseAspects(ItemStack item, String toadd, boolean craftedAspects) throws Throwable
	{
		Object aspectList;
		if (craftedAspects)
			aspectList = newAspectList.newInstance(item);
		else
			aspectList = AspectList.newInstance();
		if (!toadd.trim().isEmpty())
		{
			String[] list = toadd.split(",");
			for (int i = list.length; i --> 0; )
			{
				String[] temp = list[i].trim().split(" ");
				if (aspects.containsKey(temp[1]))
					addAspect.invoke(aspectList, aspects.get(temp[1]), Integer.parseInt(temp[0], 10));
				else
					FMLLog.severe("%s aspect missing.", temp[1]);
			}
		}
		registerItem.invoke(null, item, aspectList);
	}

	private static void parseAspects(String entity, String toadd) throws Throwable
	{
		String[] list = toadd.split(",");
		Object aspectList = AspectList.newInstance();
		for (int i = list.length; i --> 0; )
		{
			String[] temp = list[i].trim().split(" ");
			if (aspects.containsKey(temp[1]))
				addAspect.invoke(aspectList, aspects.get(temp[1]), Integer.parseInt(temp[0], 10));
		}
		registerEntity.invoke(null, entity, aspectList, null);
	}

	private static void parseAspects(Item item, String toadd) throws Throwable
	{
		parseAspects(item, OreDictionary.WILDCARD_VALUE, toadd, true);
	}

	private static void parseAspects(Item item, int meta, String toadd) throws Throwable
	{
		parseAspects(item, meta, toadd, true);
	}

	private static void parseAspects(Item item, int meta, String toadd, boolean craftedAspects) throws Throwable
	{
		parseAspects(new ItemStack(item, 1, meta), toadd, craftedAspects);
	}

	private static void parseAspects(Block item, int meta, String toadd) throws Throwable
	{
		parseAspects(new ItemStack(item, 1, meta), toadd, true);
	}

	private static void parseAspects(Block item, String toadd) throws Throwable
	{
		parseAspects(item, OreDictionary.WILDCARD_VALUE, toadd);
	}

	private static void parseAspects(Block item, int meta, String toadd, boolean craftedAspects) throws Throwable
	{
		parseAspects(new ItemStack(item, 1, meta), toadd, craftedAspects);
	}

	private static void parseAspects(Block item, String toadd, boolean craftedAspects) throws Throwable
	{
		parseAspects(item, OreDictionary.WILDCARD_VALUE, toadd, craftedAspects);
	}

	private static void parseAspects(Machine item, String toadd) throws Throwable
	{
		parseAspects(new ItemStack(item.getBlock(), 1, item.getMeta()), toadd, true);
	}

	private static void doAspects() throws Throwable
	{
		parseAspects(Machine.AutoAnvil, "3 permutatio, 5 fabrico, 10 metallum, 5 machina");
		parseAspects(Machine.AutoBrewer, "4 ignis, 2 fabrico, 2 aqua, 2 praecantatio, 5 machina");
		parseAspects(Machine.AutoDisenchanter, "4 praecantatio, 4 permutatio, 5 machina");
		parseAspects(Machine.AutoEnchanter, "8 praecantatio, 4 cognitio, 4 fabrico, 5 machina");
		parseAspects(Machine.AutoJukebox, "4 sensus, 4 aer, 5 machina");
		parseAspects(Machine.AutoSpawner, "4 bestia, 4 exanimis, 7 praecantatio, 4 alienis, 5 machina, 10 permutatio");
		parseAspects(Machine.BioFuelGenerator, "15 potentia, 3 herba, 5 machina, 3 permutatio");
		parseAspects(Machine.BioReactor, "4 herba, 2 potentia, 5 machina, 5 permutatio");
		parseAspects(Machine.BlockBreaker, "15 perfodio, 5 machina, 5 metallum, 3 lucrum");
		parseAspects(Machine.BlockPlacer, "1 motus, 1 ordo, 5 machina, 5 metallum, 3 lucrum");
		parseAspects(Machine.BlockSmasher, "5 perditio, 5 machina, 3 permutatio, 3 praecantatio");
		parseAspects(Machine.Breeder, "2 bestia, 2 fames, 5 machina");
		parseAspects(Machine.Chronotyper, "3 tempus, 3 bestia, 5 machina, 3 sensus");
		parseAspects(Machine.ChunkLoader, "100 potentia, 30 alienis, 20 praecantatio, 10 iter, 10 vacuos, 5 machina, 5 instrumentum");
		parseAspects(Machine.Composter, "2 bestia, 2 aqua, 5 machina");
		parseAspects(Machine.DeepStorageUnit, "4971027 vacuos, 5 machina, 15 alienis, 1 praecantatio");
		parseAspects(Machine.Ejector, "4 motus, 5 machina");
		parseAspects(Machine.EnchantmentRouter, "1 motus, 4 iter, 2 sensus, 5 machina, 1 praecantatio");
		parseAspects(Machine.Fertilizer, "6 herba, 5 machina, 1 vitreus, 3 victus");
		parseAspects(Machine.Fisher, "3 aqua, 5 machina, 4 metallum, 2 instrumentum");
		parseAspects(Machine.Fountain, "10 aqua, 5 machina, 3 fabrico, 1 iter");
		parseAspects(Machine.FruitPicker, "2 herba, 4 meto, 5 machina");
		parseAspects(Machine.Grinder, "10 telum, 6 mortuus, 7 meto, 5 machina, 4 metallum, 2 lucrum");
		parseAspects(Machine.ItemCollector, "4 vacuos, 5 machina, 2 arbor, 4 motus");
		parseAspects(Machine.ItemRouter, "2 motus, 4 iter, 2 sensus,  5 machina");
		parseAspects(Machine.LaserDrill, "30 perfodio, 15 lux, 5 machina, 4 victus");
		parseAspects(Machine.LaserDrillPrecharger, "4 lux, 5 machina, 25 potentia, 2 victus");
		parseAspects(Machine.LavaFabricator, "4 ignis, 4 saxum, 4 fabrico, 5 machina");
		parseAspects(Machine.LiquiCrafter, "5 aqua, 5 fabrico, 5 machina");
		parseAspects(Machine.LiquidRouter, "1 motus, 4 iter, 2 sensus, 5 machina, 1 aqua");
		parseAspects(Machine.MeatPacker, "2 ordo, 2 corpus, 2 fames, 5 machina");
		parseAspects(Machine.MobCounter, "5 ordo, 5 machina, 5 cognitio");
		parseAspects(Machine.MobRouter, "3 ordo, 3 beastia, 5 machina, 3 sensus");
		parseAspects(Machine.Planter, "4 herba, 2 granum, 4 messis, 5 machina");
		parseAspects(Machine.Rancher, "6 meto, 5 machina, 4 metallum, 2 instrumentum");
		parseAspects(Machine.RedNote, "4 aer, 4 sensus, 5 machina");
		parseAspects(Machine.Sewer, "1 venenum, 3 aqua, 5 machina, 4 bestia");
		parseAspects(Machine.Slaughterhouse, "12 telum, 10 mortuus, 5 machina, 12 metallum, 6 lucrum");
		parseAspects(Machine.SludgeBoiler, "3 ordo, 3 venenum, 3 terra, 2 aqua, 5 machina, 2 ignis");
		parseAspects(Machine.SteamBoiler, "3 fabrico, 7 aqua, 5 machina, 10 ignis, 3 perditio");
		parseAspects(Machine.SteamTurbine, "3 vacuos, 3 aqua, 5 machina, 10 potentia, 1 fabrico, 3 ordo");
		parseAspects(Machine.Unifier, "5 ordo, 2 alienis, 5 machina");
		parseAspects(Machine.Vet, "4 sano, 5 machina, 4 bestia");
		parseAspects(Machine.WeatherCollector, " 4 vacuos, 5 machina, 5 metallum, 4 tempestas");

		parseAspects("mfrEntityPinkSlime", "1 aqua, 2 limus, 1 corpus, 1 bestia");

		parseAspects(machineItem, 0, "2 fabrico, 2 machina, 1 saxum"); // factory machine block
		parseAspects(machineItem, 1, "1 cognitio, 3 machina"); // PRC housing
		parseAspects(rubberBarItem, "1 motus, 1 arbor, 1 ignis"); // rubber bar
		parseAspects(rubberLeavesBlock, "1 herba"); // rubber leaves
		parseAspects(rubberSaplingBlock, "1 arbor, 1 herba, 1 granum"); // rubber sapling
		parseAspects(rubberWoodBlock, 0, "3 arbor"); // rubber wood
		parseAspects(rubberWoodBlock, 1, "3 arbor, 1 limus"); // rubber wood
		parseAspects(rawRubberItem, "2 limus, 1 arbor"); // raw rubber
		parseAspects(rawPlasticItem, "1 fabrico, 1 ignis, 1 ordo, 1 perditio"); // raw plastic
		parseAspects(plasticSheetItem, "1 fabrico, 1 ignis, 2 ordo"); // plastic sheet
		parseAspects(factoryPlasticBlock, "1 iter, 1 fabrico, 1 sensus"); // plastic block
		parseAspects(bioFuelBucketItem, "2 herba, 1 potentia, 1 aqua, 8 metallum, 1 vacuos");
		parseAspects(biofuelLiquid, "4 herba, 2 potentia, 2 aqua");
		parseAspects(blankRecordItem, "4 sensus, 4 aer, 4 lucrum, 4 vacuos");
		parseAspects(ceramicDyeItem, "1 terra, 1 aqua, 1 sensus");
		parseAspects(chocolateMilkBucketItem, "2 fames, 1 motus, 1 potentia, 2 aqua, 8 metallum, 1 vacuos");
		parseAspects(chocolateMilkLiquid, "4 fames, 2 motus, 2 potentia, 4 aqua");
		parseAspects(conveyorBlock, "3 motus, 1 iter, 1 machina");
		parseAspects(essenceLiquid, "4 praecantatio, 2 cognitio, 2 aqua");
		parseAspects(factoryGlassBlock, "1 vitreus, 1 sensus");
		parseAspects(factoryHammerItem, "1 instrumentum, 2 fabrico, 2 ignis, 3 ordo");
		parseAspects(fertilizerItem, "1 granum, 1 herba, 1 messis, 1 sensus");
		parseAspects(laserFocusItem, "1 ordo, 1 vitreus, 4 lucrum");
		parseAspects(meatBucketItem, "3 corpus, 1 bestia, 1 aqua, 8 metallum, 1 vacuos");
		parseAspects(meatIngotCookedItem, "3 corpus, 2 fames, 1 ignis");
		parseAspects(meatIngotRawItem, "3 corpus, 2 fames, 1 bestia");
		parseAspects(meatLiquid, "6 corpus, 2 aqua, 2 bestia");
		parseAspects(meatNuggetCookedItem, "1 fames");
		parseAspects(meatNuggetRawItem, "1 corpus");
		parseAspects(milkBottleItem, "1 fames, 1 sano, 1 victus, 1 vitreus");
		parseAspects(milkLiquid, "4 fames, 4 sano, 4 aqua, 2 victus");
		parseAspects(mobEssenceBucketItem, "2 praecantatio, 1 cognitio, 1 aqua, 8 metallum, 1 vacuos");
		parseAspects(mushroomSoupBucketItem, "2 fames, 2 herba, 8 metallum, 1 vacuos");
		parseAspects(mushroomSoupLiquid, "4 fames, 4 herba, 1 aqua");
		parseAspects(pinkSlimeItem, "1 limus, 1 corpus");
		parseAspects(pinkSlimeBlock, "9 limus, 9 corpus, 2 aqua");
		parseAspects(pinkSlimeBucketItem, "2 limus, 2 corpus, 1 aqua, 8 metallum, 1 vacuos");
		parseAspects(pinkSlimeLiquid, "4 limus, 4 corpus, 2 aqua");
		parseAspects(portaSpawnerItem, "8 alienis, 4 bestia, 4 exanimis, 4 iter, 8 praecantatio, 8 permutatio");
		parseAspects(rednetCableBlock, "1 cognitio, 1 machina", true);
		parseAspects(rednetLogicBlock, "15 cognitio, 5 machina", true);
		parseAspects(rednetMemoryCardItem, "3 cognitio, 1 machina");
		parseAspects(rednetMeterItem, 0, "1 instrumentum, 1 sensus, 1 machina");
		parseAspects(rednetMeterItem, 1, "4 instrumentum, 1 sensus, 2 machina");
		parseAspects(rednetPanelBlock, "2 sensus, 2 cognitio, 2 machina");
		parseAspects(rulerItem, "1 instrumentum, 1 sensus");
		parseAspects(safariNetItem, "4 spiritus, 8 alienis, 8 iter, 4 praecantatio, 8 vinculum, 4 fabrico");
		parseAspects(safariNetJailerItem, "10 vinculum, 1 praecantatio, 4 spiritus, 2 metallum, 1 fabrico");
		parseAspects(safariNetLauncherItem, "2 volatus, 2 instrumentum");
		parseAspects(safariNetSingleItem, "4 vinculum, 4 spiritus, 2 instrumentum");
		parseAspects(sewageBucketItem, "2 venenum, 1 bestia, 1 aqua, 8 metallum, 1 vacuos");
		parseAspects(sewageLiquid, "4 venenum, 2 bestia, 2 aqua");
		parseAspects(sludgeBucketItem, "2 venenum, 1 terra, 1 aqua, 1 vitium, 8 metallum, 1 vacuos");
		parseAspects(sludgeLiquid, "4 venenum, 2 terra, 2 aqua, 1 vitium");
		parseAspects(spyglassItem, "2 victus, 6 sensus");
		parseAspects(strawItem, "1 vacuos, 4 aqua, 4 fames, 1 instrumentum");
		parseAspects(sugarCharcoalItem, "2 potentia, 2 ignis");
		parseAspects(syringeCureItem, "2 sano, 1 exanimis, 1 humanus, 1 instrumentum");
		parseAspects(syringeEmptyItem, "1 vacuos, 1 sano, 1 instrumentum");
		parseAspects(syringeGrowthItem, "1 tempus, 2 sano, 1 instrumentum");
		parseAspects(syringeHealthItem, "2 sano, 1 instrumentum");
		parseAspects(syringeSlimeItem, "1 sano, 1 limus, 1 instrumentum");
		parseAspects(syringeZombieItem, "1 tempus, 1 sano, 1 exanimis, 1 instrumentum");
		parseAspects(vineScaffoldBlock, "1 herba, 1 fabrico");
		parseAspects(xpExtractorItem, "1 praecantatio, 1 permutatio, 1 vacuos, 1 instrumentum, 1 meto");
		//parseAspects(Tracks, "Tracks Currently Have No Aspects.");

		parseAspects(upgradeItem, "2 cognitio");
		for (int i = 0, n = 10; i <= n; ++i)
			parseAspects(upgradeItem, i, "2 cognitio", true);
		parseAspects(upgradeItem, 11, "1 cognitio", true);
		parseAspects(logicCardItem, 0, "4 cognitio", true);
		parseAspects(logicCardItem, 1, "7 cognitio", true);
		parseAspects(logicCardItem, 2, "10 cognitio", true);

		parseAspects(factoryDecorativeBrickBlock,  0, "2 gelum, 1 terra"); // ice
		parseAspects(factoryDecorativeBrickBlock,  1, "1 terra, 3 lux, 2 sensus"); // glowstone
		parseAspects(factoryDecorativeBrickBlock,  2, "2 terra, 4 sensus"); // lapis
		parseAspects(factoryDecorativeBrickBlock,  3, "1 terra, 3 ignis, 1 saxum, 1 tenebrae"); // obsidian
		parseAspects(factoryDecorativeBrickBlock,  4, "2 terra, 1 saxum"); // paved stone
		parseAspects(factoryDecorativeBrickBlock,  5, "1 gelum, 1 terra"); // snow
		parseAspects(factoryDecorativeBrickBlock,  6, "2 gelum, 1 saxum"); // ice large
		parseAspects(factoryDecorativeBrickBlock,  7, "1 saxum, 3 lux, 2 sensus"); // glowstone large
		parseAspects(factoryDecorativeBrickBlock,  8, "2 saxum, 4 sensus"); // lapis large
		parseAspects(factoryDecorativeBrickBlock,  9, "3 ignis, 2 saxum, 1 tenebrae"); // obsidian large
		parseAspects(factoryDecorativeBrickBlock, 10, "3 saxum"); // pavedstone large
		parseAspects(factoryDecorativeBrickBlock, 12, "1 gelum, 1 saxum"); // snow large
		parseAspects(factoryDecorativeBrickBlock, 12, "3 corpus, 2 fames, 1 bestia", true); // raw meat block
		parseAspects(factoryDecorativeBrickBlock, 13, "3 corpus, 2 fames, 1 ignis", true); // cooked meat block
		parseAspects(factoryDecorativeBrickBlock, 14, "2 saxum"); // brick large
		parseAspects(factoryDecorativeBrickBlock, 15, "10 ignis"); // sugar charcoal

		parseAspects(factoryDecorativeStoneBlock, 0, "2 saxum, 1 tenebrae");
		parseAspects(factoryDecorativeStoneBlock, 1, "2 saxum, 1 victus");
		parseAspects(factoryDecorativeStoneBlock, 2, "1 perditio, 1 saxum, 1 tenebrae");
		parseAspects(factoryDecorativeStoneBlock, 3, "1 perditio, 1 saxum, 1 victus");
		parseAspects(factoryDecorativeStoneBlock, 4, "2 saxum, 1 tenebrae");
		parseAspects(factoryDecorativeStoneBlock, 5, "2 saxum, 1 victus");
		parseAspects(factoryDecorativeStoneBlock, 6, "2 terra, 1 saxum, 1 tenebrae");
		parseAspects(factoryDecorativeStoneBlock, 7, "2 terra, 1 saxum, 1 victus");
		parseAspects(factoryDecorativeStoneBlock, 8, "1 terra, 1 saxum, 1 tenebrae");
		parseAspects(factoryDecorativeStoneBlock, 9, "1 terra, 1 saxum, 1 victus");
		parseAspects(factoryDecorativeStoneBlock, 10, "3 saxum, 1 tenebrae");
		parseAspects(factoryDecorativeStoneBlock, 11, "3 saxum, 1 victus");
		//parseAspects(factoryDecorativeStoneBlock, 12, "2 saxum, 1 tenebrae");
		//parseAspects(factoryDecorativeStoneBlock, 13, "2 saxum, 1 victus");

		parseAspects(factoryRoadBlock, 0, "3 iter, 1 saxum, 1 sensus");
		parseAspects(factoryRoadBlock, 1, "3 iter, 1 saxum, 1 sensus, 3 lux"); // road light (off)
		parseAspects(factoryRoadBlock, 2, "3 iter, 1 saxum, 1 sensus, 3 lux"); // road light (on)
		parseAspects(factoryRoadBlock, 3, "3 iter, 1 saxum, 1 sensus, 3 lux"); // road light inverted (off)
		parseAspects(factoryRoadBlock, 4, "3 iter, 1 saxum, 1 sensus, 3 lux"); // road light inverted (on)
	}
}