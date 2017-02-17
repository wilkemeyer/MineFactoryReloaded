package powercrystals.minefactoryreloaded;

//this import brought to you by the department of redundancies department, the department that brought you this import
import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.*;
import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

import cofh.CoFHCore;
import cofh.api.core.IInitializer;
import cofh.core.world.WorldHandler;
import com.google.common.collect.ImmutableList;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.CustomProperty;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import net.minecraftforge.fml.common.event.FMLModIdMappingEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.logging.log4j.Logger;

import powercrystals.minefactoryreloaded.block.BlockDetCord;
import powercrystals.minefactoryreloaded.block.BlockFactoryMachine;
import powercrystals.minefactoryreloaded.block.BlockFakeLaser;
import powercrystals.minefactoryreloaded.block.BlockFertileSoil;
import powercrystals.minefactoryreloaded.block.BlockRedNetLogic;
import powercrystals.minefactoryreloaded.block.BlockRedNetPanel;
import powercrystals.minefactoryreloaded.block.BlockRubberLeaves;
import powercrystals.minefactoryreloaded.block.BlockRubberSapling;
import powercrystals.minefactoryreloaded.block.BlockRubberWood;
import powercrystals.minefactoryreloaded.block.BlockVineScaffold;
import powercrystals.minefactoryreloaded.block.ItemBlockConveyor;
import powercrystals.minefactoryreloaded.block.ItemBlockDetCord;
import powercrystals.minefactoryreloaded.block.ItemBlockFactory;
import powercrystals.minefactoryreloaded.block.ItemBlockFactoryLeaves;
import powercrystals.minefactoryreloaded.block.ItemBlockFactoryMachine;
import powercrystals.minefactoryreloaded.block.ItemBlockFactoryRoad;
import powercrystals.minefactoryreloaded.block.ItemBlockFactoryTree;
import powercrystals.minefactoryreloaded.block.ItemBlockRedNetLogic;
import powercrystals.minefactoryreloaded.block.ItemBlockRedNetPanel;
import powercrystals.minefactoryreloaded.block.ItemBlockTank;
import powercrystals.minefactoryreloaded.block.ItemBlockVineScaffold;
import powercrystals.minefactoryreloaded.block.decor.BlockDecorativeBricks;
import powercrystals.minefactoryreloaded.block.decor.BlockDecorativeStone;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryDecoration;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryGlass;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryGlassPane;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryPlastic;
import powercrystals.minefactoryreloaded.block.decor.BlockPinkSlime;
import powercrystals.minefactoryreloaded.block.fluid.BlockExplodingFluid;
import powercrystals.minefactoryreloaded.block.fluid.BlockFactoryFluid;
import powercrystals.minefactoryreloaded.block.fluid.BlockPinkSlimeFluid;
import powercrystals.minefactoryreloaded.block.fluid.BlockTank;
import powercrystals.minefactoryreloaded.block.transport.BlockConveyor;
import powercrystals.minefactoryreloaded.block.transport.BlockFactoryRoad;
import powercrystals.minefactoryreloaded.block.transport.BlockPlasticPipe;
import powercrystals.minefactoryreloaded.block.transport.BlockRailCargoDropoff;
import powercrystals.minefactoryreloaded.block.transport.BlockRailCargoPickup;
import powercrystals.minefactoryreloaded.block.transport.BlockRailPassengerDropoff;
import powercrystals.minefactoryreloaded.block.transport.BlockRailPassengerPickup;
import powercrystals.minefactoryreloaded.block.transport.BlockRedNetCable;
import powercrystals.minefactoryreloaded.core.MFRDyeColor;
import powercrystals.minefactoryreloaded.core.ReflectionHelper;
import powercrystals.minefactoryreloaded.entity.DebugTracker;
import powercrystals.minefactoryreloaded.entity.EntityFishingRod;
import powercrystals.minefactoryreloaded.entity.EntityFlyingItem;
import powercrystals.minefactoryreloaded.entity.EntityNeedle;
import powercrystals.minefactoryreloaded.entity.EntityPinkSlime;
import powercrystals.minefactoryreloaded.entity.EntityRocket;
import powercrystals.minefactoryreloaded.entity.EntitySafariNet;
import powercrystals.minefactoryreloaded.farmables.MFRFarmables;
import powercrystals.minefactoryreloaded.gui.MFRGUIHandler;
import powercrystals.minefactoryreloaded.item.ItemCeramicDye;
import powercrystals.minefactoryreloaded.item.ItemFactoryBag;
import powercrystals.minefactoryreloaded.item.ItemFactoryCup;
import powercrystals.minefactoryreloaded.item.ItemLogicUpgradeCard;
import powercrystals.minefactoryreloaded.item.ItemMilkBottle;
import powercrystals.minefactoryreloaded.item.ItemPinkSlime;
import powercrystals.minefactoryreloaded.item.ItemPlasticBoots;
import powercrystals.minefactoryreloaded.item.ItemPortaSpawner;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;
import powercrystals.minefactoryreloaded.item.ItemUpgrade;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryArmor;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryBucket;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryColored;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryFood;
import powercrystals.minefactoryreloaded.item.gun.ItemNeedleGun;
import powercrystals.minefactoryreloaded.item.gun.ItemPotatoCannon;
import powercrystals.minefactoryreloaded.item.gun.ItemRocketLauncher;
import powercrystals.minefactoryreloaded.item.gun.ItemSafariNetLauncher;
import powercrystals.minefactoryreloaded.item.gun.ammo.ItemNeedlegunAmmoAnvil;
import powercrystals.minefactoryreloaded.item.gun.ammo.ItemNeedlegunAmmoBlock;
import powercrystals.minefactoryreloaded.item.gun.ammo.ItemNeedlegunAmmoFire;
import powercrystals.minefactoryreloaded.item.gun.ammo.ItemNeedlegunAmmoStandard;
import powercrystals.minefactoryreloaded.item.gun.ammo.ItemRocket;
import powercrystals.minefactoryreloaded.item.syringe.ItemSyringeCure;
import powercrystals.minefactoryreloaded.item.syringe.ItemSyringeGrowth;
import powercrystals.minefactoryreloaded.item.syringe.ItemSyringeHealth;
import powercrystals.minefactoryreloaded.item.syringe.ItemSyringeLiquid;
import powercrystals.minefactoryreloaded.item.syringe.ItemSyringeSlime;
import powercrystals.minefactoryreloaded.item.syringe.ItemSyringeZombie;
import powercrystals.minefactoryreloaded.item.tool.ItemFactoryHammer;
import powercrystals.minefactoryreloaded.item.tool.ItemFishingRod;
import powercrystals.minefactoryreloaded.item.tool.ItemRedNetMemoryCard;
import powercrystals.minefactoryreloaded.item.tool.ItemRedNetMeter;
import powercrystals.minefactoryreloaded.item.tool.ItemRuler;
import powercrystals.minefactoryreloaded.item.tool.ItemSpyglass;
import powercrystals.minefactoryreloaded.item.tool.ItemStraw;
import powercrystals.minefactoryreloaded.item.tool.ItemXpExtractor;
import powercrystals.minefactoryreloaded.net.CommonProxy;
import powercrystals.minefactoryreloaded.net.EntityHandler;
import powercrystals.minefactoryreloaded.net.MFRPacket;
import powercrystals.minefactoryreloaded.setup.*;
import powercrystals.minefactoryreloaded.setup.recipe.EnderIO;
import powercrystals.minefactoryreloaded.setup.recipe.Vanilla;
import powercrystals.minefactoryreloaded.setup.village.Zoologist;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityUnifier;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetHistorian;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;
import powercrystals.minefactoryreloaded.tile.tank.TileEntityTank;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityConveyor;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe;
import powercrystals.minefactoryreloaded.world.MineFactoryReloadedWorldGen;

@Mod(modid = modId, name = modName, version = version, dependencies = dependencies,
		customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class MineFactoryReloadedCore extends BaseMod {

	static{FluidRegistry.enableUniversalBucket();}
	public static final String modId = "minefactoryreloaded";
	public static final String modName = "MineFactory Reloaded";
	public static final String version = "1.7.10R2.8.2B1";
	public static final String dependencies = CoFHCore.VERSION_GROUP;
	public static final String modNetworkChannel = "MFReloaded";

	@SidedProxy(clientSide = "powercrystals.minefactoryreloaded.net.ClientProxy",
			serverSide = "powercrystals.minefactoryreloaded.net.ServerProxy")
	public static CommonProxy proxy;

	public static SimpleNetworkWrapper networkWrapper = null;

	public static Object balance = "balance";

	public static final String prefix = "minefactoryreloaded:";
	public static final String textureFolder = prefix + "textures/";
	public static final String guiFolder = textureFolder + "gui/";
	public static final String hudFolder = textureFolder + "hud/";
	public static final String villagerFolder = textureFolder + "villager/";
	public static final String tileEntityFolder = textureFolder + "tileentity/";
	public static final String mobTextureFolder = textureFolder + "mob/";
	public static final String modelTextureFolder = textureFolder + "itemmodels/";
	public static final String armorTextureFolder = textureFolder + "armor/";
	public static final String modelFolder = prefix + "models/";

	public static int renderIdConveyor = 1000;
	public static int renderIdFactoryGlassPane = 1001;
	public static int renderIdFluidTank = 1002;
	public static int renderIdFluidClassic = 1003;
	public static int renderIdRedNetLogic = 1004;
	public static int renderIdVineScaffold = 1005;
	public static int renderIdRedNetPanel = 1006;
	public static int renderIdFactoryGlass = 1007;
	public static int renderIdDetCord = 1008;
	public static int renderIdRedNet = 1009;
	public static int renderIdPPipe = 1009;

	public static final ResourceLocation CHEST_GEN = new ResourceLocation("mfr:villageZoolologist");

	private static MineFactoryReloadedCore instance;
	private LinkedList<Vanilla> recipeSets = new LinkedList<Vanilla>();
	
	public static MineFactoryReloadedCore instance() {

		return instance;
	}

	public static Logger log() {

		return instance.getLogger();
	}

	public static void registerFluids() {

		FluidRegistry.enableUniversalBucket();

		milk = registerFluid("milk", 1050, EnumRarity.COMMON);
		sludge = registerFluid("sludge", 1700, EnumRarity.COMMON);
		sewage = registerFluid("sewage", 1200, EnumRarity.COMMON);
		essence = registerFluid("mob_essence", 400, 9, 310, EnumRarity.EPIC);
		biofuel = registerFluid("biofuel", 800, EnumRarity.UNCOMMON);
		meat = registerFluid("meat", 2000, EnumRarity.COMMON);
		pinkSlime = registerFluid("pink_slime", 3000, EnumRarity.RARE);
		chocolateMilk = registerFluid("chocolate_milk", 1100, EnumRarity.COMMON);
		mushroomSoup = registerFluid("mushroom_soup", 1500, EnumRarity.COMMON);
		steam = registerFluid("steam", -100, 0, 673, EnumRarity.COMMON);

		FluidRegistry.addBucketForFluid(milk);
		FluidRegistry.addBucketForFluid(sludge);
		FluidRegistry.addBucketForFluid(sewage);
		FluidRegistry.addBucketForFluid(essence);
		FluidRegistry.addBucketForFluid(biofuel);
		FluidRegistry.addBucketForFluid(meat);
		FluidRegistry.addBucketForFluid(pinkSlime);
		FluidRegistry.addBucketForFluid(chocolateMilk);
		FluidRegistry.addBucketForFluid(mushroomSoup);

		milkLiquid = new BlockFactoryFluid("milk");
		sludgeLiquid = new BlockFactoryFluid("sludge");
		sewageLiquid = new BlockFactoryFluid("sewage");
		essenceLiquid = new BlockFactoryFluid("mob_essence");
		biofuelLiquid = new BlockExplodingFluid("biofuel");
		meatLiquid = new BlockFactoryFluid("meat");
		pinkSlimeLiquid = new BlockPinkSlimeFluid("pink_slime");
		chocolateMilkLiquid = new BlockFactoryFluid("chocolate_milk");
		mushroomSoupLiquid = new BlockFactoryFluid("mushroom_soup");
		steamFluid = new BlockFactoryFluid("steam", BlockFactoryFluid.material);

		registerBlock(milkLiquid, new ItemBlock(milkLiquid));
		registerBlock(sludgeLiquid, new ItemBlock(sludgeLiquid));
		registerBlock(sewageLiquid, new ItemBlock(sewageLiquid));
		registerBlock(essenceLiquid, new ItemBlock(essenceLiquid));
		registerBlock(biofuelLiquid, new ItemBlock(biofuelLiquid));
		registerBlock(meatLiquid, new ItemBlock(meatLiquid));
		registerBlock(pinkSlimeLiquid, new ItemBlock(pinkSlimeLiquid));
		registerBlock(chocolateMilkLiquid, new ItemBlock(chocolateMilkLiquid));
		registerBlock(mushroomSoupLiquid, new ItemBlock(mushroomSoupLiquid));
		registerBlock(steamFluid, new ItemBlock(steamFluid));

	}

	public static Fluid registerFluid(String name, int density, EnumRarity rarity) {

		return registerFluid(name, density, -1, -1, rarity);
	}

	public static Fluid registerFluid(String name, int density, int lightValue, int temp, EnumRarity rarity) {

		name = name.toLowerCase(Locale.ENGLISH);
		Fluid fluid = new Fluid(name, new ResourceLocation("minefactoryreloaded:blocks/fluid/fluid.mfr." + name + ".still"), new ResourceLocation("minefactoryreloaded:blocks/fluid/fluid.mfr." + name + ".flowing"));
		if (!FluidRegistry.registerFluid(fluid))
			fluid = FluidRegistry.getFluid(name);
		if (density != 0) {
			fluid.setDensity(density);
			fluid.setViscosity(Math.abs(density)); // works for my purposes
		}
		if (lightValue >= 0)
			fluid.setLuminosity(lightValue);
		if (temp >= 0)
			fluid.setTemperature(temp);
		fluid.setUnlocalizedName("mfr." + name + ".still.name");
		fluid.setRarity(rarity);
		return fluid;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) throws IOException {

		instance = this;
		setConfigFolderBase(evt.getModConfigurationDirectory());

		MFRConfig.loadClientConfig(getClientConfig());
		MFRConfig.loadCommonConfig(getCommonConfig());

		registerFluids();

		MFRThings.preInit();

		if (MFRConfig.vanillaRecipes.getBoolean(true))
			recipeSets.add(new Vanilla());

/* TODO readd when there's TE
		if (MFRConfig.thermalExpansionRecipes.getBoolean(false))
			recipeSets.add(new ThermalExpansion());
*/

		if (MFRConfig.enderioRecipes.getBoolean(false))
			recipeSets.add(new EnderIO());

		Vanilla.registerOredict();

		loadLang();

		Blocks.FIRE.setFireInfo(biofuelLiquid, 300, 30);

/* TODO stack sizes for door have changed in 1.8, figure out what this is for and if it needs to be readded
		Items.WOODEN_DOOR.setMaxStackSize(8);
		Items.IRON_DOOR.setMaxStackSize(8);
*/

		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("milk",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(milkBottleItem), new ItemStack(Items.GLASS_BOTTLE)));

		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("milk",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Items.MILK_BUCKET), new ItemStack(Items.BUCKET)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("mushroom_soup",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Items.MUSHROOM_STEW), new ItemStack(Items.BOWL)));

		GameRegistry.registerFuelHandler(new MineFactoryReloadedFuelHandler());
		
		proxy.preInit();
	}

	private static void registerBlock(Block block, ItemBlock itemBlock) {
		
		MFRRegistry.registerBlock(block, itemBlock);		
	}

	@Deprecated
	private void registerBlock(Block block, Class<? extends ItemBlock> item, String[] args) {

		MFRRegistry.registerBlock(block, item, new Object[] { args });
	}

	@Deprecated
	private void registerBlock(Block block, Class<? extends ItemBlock> item, Object... args) {

		MFRRegistry.registerBlock(block, item, args);
	}

	@EventHandler
	public void missingMappings(FMLMissingMappingsEvent e) {

		List<MissingMapping> list = e.get();
		if (list.size() > 0) for (MissingMapping mapping : list) {
			String name = mapping.name;
			if (name.indexOf(':') >= 0)
				name = name.substring(name.indexOf(':') + 1);
			l: switch (mapping.type) {
			case BLOCK:
				Block block = MFRRegistry.remapBlock(name);
				if (block != null)
					mapping.remap(block);
				else if ("tile.null".equals(name))
					mapping.remap(fakeLaserBlock);
				else
					mapping.warn();
				break l;
			case ITEM:
				Item item = MFRRegistry.remapItem(name);
				if (item != null)
					mapping.remap(item);
				else
					mapping.warn();
				break l;
			default:
			}
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {

		MinecraftForge.EVENT_BUS.register(rednetCableBlock);
		MinecraftForge.EVENT_BUS.register(new EntityHandler());

		proxy.init();
		MFRFarmables.load();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MFRGUIHandler());

		MFRPacket.initialize();

		addDispenserBehavior();

		MFRLoot.init();

		Zoologist.init();

		WorldHandler.instance.registerFeature(new MineFactoryReloadedWorldGen());

		//UpdateManager.registerUpdater(new UpdateManager(this, null, CoFHProps.DOWNLOAD_URL));
	}

	private void addDispenserBehavior() {

		IBehaviorDispenseItem behavior = new BehaviorDispenseSafariNet();
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(safariNetItem, behavior);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(safariNetSingleItem, behavior);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(safariNetJailerItem, behavior);
	}

	private void addChestGenItems() {

/*  TODO add loot tables
		//{ Vanilla chests
		ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
			new WeightedRandomChestContent(new ItemStack(safariNetSingleItem), 1, 1, 25));
		ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
			new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock, 1, 2), 1, 4, 8));

		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(
			new WeightedRandomChestContent(new ItemStack(safariNetSingleItem), 1, 1, 25));
		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(
			new WeightedRandomChestContent(new ItemStack(safariNetJailerItem), 1, 1, 15));

		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(
			new WeightedRandomChestContent(new ItemStack(safariNetSingleItem), 1, 1, 25));

		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(
			new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock, 1, 2), 1, 4, 8));
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(
			new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock, 1, 1), 1, 2, 1));

		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_DISPENSER).addItem(
			new WeightedRandomChestContent(Zoologist.getHiddenNetStack(), 1, 1, 25));

		if (MFRConfig.enableMassiveTree.getBoolean(true)) {
			ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(
				new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock, 1, 3), 1, 1, 1));
		}
		//}

		final WeightedRandomChestContent saplings = new WeightedRandomChestContent(new ItemStack(Blocks.SAPLING, 2), 1, 16, 7) {

			@Override
			public ItemStack[] generateChestContent(Random random, IInventory newInventory) {

				ItemStack item = theItemId.copy();
				item.setItemDamage(random.nextInt(6));
				return ChestGenHooks.generateStacks(random, item, theMinimumChanceToGenerateItem, theMaximumChanceToGenerateItem);
			}
		};

		//{ Fishing
		FishingHooks.addJunk(new WeightedRandomFishable(new ItemStack(rubberSaplingBlock, 1, 0), 5));
		FishingHooks.addJunk(new WeightedRandomFishable(new ItemStack(plasticSheetItem, 1, 0), 10));
		FishingHooks.addTreasure(new WeightedRandomFishable(Zoologist.getHiddenNetStack(), 1));
		FishingHooks.addTreasure(new WeightedRandomFishable(new ItemStack(plasticBagItem, 1, 0), 1) {

			WeightedRandomChestContent[] loot = {
					new WeightedRandomChestContent(new ItemStack(safariNetSingleItem, 1), 1, 1, 35),
					new WeightedRandomChestContent(new ItemStack(Blocks.SAND, 4), 1, 16, 20),
					new WeightedRandomChestContent(new ItemStack(plasticSheetItem, 16), 1, 16, 16),
					new WeightedRandomChestContent(new ItemStack(plasticSheetItem, 23), 1, 23, 16),
					new WeightedRandomChestContent(new ItemStack(plasticSheetItem, 6), 1, 6, 16),
					new WeightedRandomChestContent(new ItemStack(Items.PAPER), 1, 16, 14),
					new WeightedRandomChestContent(new ItemStack(spyglassItem, 1), 1, 1, 7),
					saplings,
					new WeightedRandomChestContent(new ItemStack(strawItem), 1, 1, 5),
					new WeightedRandomChestContent(new ItemStack(Items.REEDS, 3), 1, 3, 2),
					new WeightedRandomChestContent(new ItemStack(Items.PUMPKIN_SEEDS, 1), 1, 1, 2),
					new WeightedRandomChestContent(new ItemStack(Items.MELON_SEEDS, 1), 1, 1, 2),
					new WeightedRandomChestContent(new ItemStack(Items.DYE, 1, 4), 1, 1, 2),
					new WeightedRandomChestContent(new ItemStack(Items.NETHERBRICK, 1), 1, 1, 1),
			};

			@Override
			public ItemStack func_150708_a(Random r) {

				ItemStack a = field_150711_b.copy();
				a.setTagInfo("loot", new NBTTagByte((byte) 1));
				InventoryContainerItemWrapper w = new InventoryContainerItemWrapper(a);
				WeightedRandomChestContent.generateChestContents(r, loot, w, 1);
				return w.getContainerStack();
			}
		});
		//}

		//{ DimensionalDoors chestgen compat
		// reference weights[iron: 160; coal: 120; gold: 80; golden apple: 10]
		ChestGenHooks.getInfo("dimensionalDungeonChest").addItem(
			new WeightedRandomChestContent(new ItemStack(safariNetJailerItem), 1, 1, 15));
		ChestGenHooks.getInfo("dimensionalDungeonChest").addItem(
			new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock), 1, 8, 70));
		ChestGenHooks.getInfo("dimensionalDungeonChest").addItem(
			new WeightedRandomChestContent(new ItemStack(pinkSlimeItem), 1, 1, 1));
		// tempting as a sacred sapling is, chests are too common with too few possible items
		// maybe as a custom dungeon for integration
		///}

		//{ Villager house loot chest
		ChestGenHooks.getInfo(CHEST_GEN).setMax(2 * 9);
		ChestGenHooks.getInfo(CHEST_GEN).setMin(5);
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(safariNetSingleItem), 1, 1, 35));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(safariNetJailerItem), 1, 1, 20));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(safariNetItem), 1, 1, 5));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(
			new WeightedRandomChestContent(ItemSafariNet.makeMysteryNet(new ItemStack(safariNetSingleItem)), 1, 1, 17));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(
			new WeightedRandomChestContent(ItemSafariNet.makeMysteryNet(new ItemStack(safariNetJailerItem)), 1, 1, 10));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(
			new WeightedRandomChestContent(ItemSafariNet.makeMysteryNet(new ItemStack(safariNetItem)), 1, 1, 2));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.LEAD), 1, 17, 10));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.NAME_TAG), 1, 14, 10));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(safariNetLauncherItem), 1, 1, 8));

		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.PAPER), 1, 16, 14));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.COAL, 1, 1), 1, 16, 14));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(blankRecordItem), 1, 1, 14));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.BOOK), 1, 5, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(spyglassItem), 1, 1, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(rulerItem), 1, 1, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(mobEssenceBucketItem), 1, 1, 6));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(syringeEmptyItem), 1, 4, 6));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(syringeHealthItem), 1, 1, 6));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(syringeGrowthItem), 1, 2, 6));

		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(factoryHammerItem), 1, 1, 25));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(plasticBootsItem), 1, 1, 25));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock), 1, 8, 20));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(vineScaffoldBlock), 1, 32, 20));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(plasticSheetItem), 1, 64, 16));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(plasticBagItem), 1, 24, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.REEDS), 1, 16, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.PUMPKIN_SEEDS), 1, 16, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.SNOWBALL), 1, 16, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(saplings);
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(strawItem), 1, 1, 5));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(portaSpawnerItem), 1, 1, 1));
		//}
*/
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {

		TileEntityUnifier.updateUnifierLiquids();

		String[] list = MFRConfig.rubberTreeBiomeWhitelist.getStringList();
		for (String biome : list) {
			MFRRegistry.registerRubberTreeBiome(biome);
		}

		list = MFRConfig.unifierBlacklist.getStringList();
		for (String entry : list) {
			MFRRegistry.registerUnifierBlacklist(entry);
		}

		list = MFRConfig.spawnerBlacklist.getStringList();
		for (String entry : list) {
			MFRRegistry.registerAutoSpawnerBlacklist(entry);
		}

		for (Vanilla e : recipeSets)
			e.registerRecipes();

		MFRFarmables.post();
	}

	@EventHandler
	public void handleIMC(IMCEvent e) {

		IMCHandler.processIMC(e.getMessages());
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent evt) {

		IMCHandler.processIMC(FMLInterModComms.fetchRuntimeMessages(this));

		// catch biomes whitelisted via IMC that are in the config blacklist
		String[] list = MFRConfig.rubberTreeBiomeBlacklist.getStringList();
		for (String biome : list) {
			MFRRegistry.getRubberTreeBiomes().remove(biome);
		}
		for (Property prop : MFRConfig.spawnerCustomization.values()) {
			MFRRegistry.setBaseSpawnCost(prop.getName(), prop.getInt(0));
		}
		list = MFRConfig.safarinetBlacklist.getStringList();
		for (String s : list) {
			Class<?> cl = (Class<?>) EntityList.NAME_TO_CLASS.get(s);
			if (cl != null)
				MFRRegistry.registerSafariNetBlacklist(cl);
		}

		powercrystals.minefactoryreloaded.core.OreDictionaryArbiter.initialize();
		_log.info("Load Complete.");
	}

	@EventHandler
	public void remap(FMLModIdMappingEvent evt) {

		powercrystals.minefactoryreloaded.core.OreDictionaryArbiter.bake();
	}

	@Override
	public String getModId() {

		return modId;
	}
}
