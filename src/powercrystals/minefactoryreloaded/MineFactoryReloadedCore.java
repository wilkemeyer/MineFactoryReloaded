package powercrystals.minefactoryreloaded;

//this import brought to you by the department of redundancies department, the department that brought you this import
import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.*;

import org.apache.logging.log4j.Logger;

import cofh.core.CoFHProps;
import cofh.mod.BaseMod;
import cofh.updater.UpdateManager;
import cofh.util.RegistryUtils;
import cofh.world.WorldHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockSapling;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

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
import powercrystals.minefactoryreloaded.block.ItemBlockDetCord;
import powercrystals.minefactoryreloaded.block.ItemBlockFactory;
import powercrystals.minefactoryreloaded.block.ItemBlockFactoryLeaves;
import powercrystals.minefactoryreloaded.block.ItemBlockFactoryMachine;
import powercrystals.minefactoryreloaded.block.ItemBlockFactoryRoad;
import powercrystals.minefactoryreloaded.block.ItemBlockFactoryTree;
import powercrystals.minefactoryreloaded.block.ItemBlockRedNetLogic;
import powercrystals.minefactoryreloaded.block.ItemBlockRedNetPanel;
import powercrystals.minefactoryreloaded.block.ItemBlockVanillaIce;
import powercrystals.minefactoryreloaded.block.ItemBlockVineScaffold;
import powercrystals.minefactoryreloaded.block.decor.BlockDecorativeBricks;
import powercrystals.minefactoryreloaded.block.decor.BlockDecorativeStone;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryDecoration;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryGlass;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryGlassPane;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryPlastic;
import powercrystals.minefactoryreloaded.block.decor.BlockVanillaGlassPane;
import powercrystals.minefactoryreloaded.block.decor.BlockVanillaIce;
import powercrystals.minefactoryreloaded.block.fluid.BlockExplodingFluid;
import powercrystals.minefactoryreloaded.block.fluid.BlockFactoryFluid;
import powercrystals.minefactoryreloaded.block.fluid.BlockPinkSlimeFluid;
import powercrystals.minefactoryreloaded.block.transport.BlockConveyor;
import powercrystals.minefactoryreloaded.block.transport.BlockFactoryRoad;
import powercrystals.minefactoryreloaded.block.transport.BlockPlasticPipe;
import powercrystals.minefactoryreloaded.block.transport.BlockRailCargoDropoff;
import powercrystals.minefactoryreloaded.block.transport.BlockRailCargoPickup;
import powercrystals.minefactoryreloaded.block.transport.BlockRailPassengerDropoff;
import powercrystals.minefactoryreloaded.block.transport.BlockRailPassengerPickup;
import powercrystals.minefactoryreloaded.block.transport.BlockRedNetCable;
import powercrystals.minefactoryreloaded.entity.EntityFishingRod;
import powercrystals.minefactoryreloaded.entity.EntityNeedle;
import powercrystals.minefactoryreloaded.entity.EntityPinkSlime;
import powercrystals.minefactoryreloaded.entity.EntityRocket;
import powercrystals.minefactoryreloaded.entity.EntitySafariNet;
import powercrystals.minefactoryreloaded.gui.MFRGUIHandler;
import powercrystals.minefactoryreloaded.item.ItemCeramicDye;
import powercrystals.minefactoryreloaded.item.ItemFactory;
import powercrystals.minefactoryreloaded.item.ItemFactoryArmor;
import powercrystals.minefactoryreloaded.item.ItemFactoryBag;
import powercrystals.minefactoryreloaded.item.ItemFactoryBucket;
import powercrystals.minefactoryreloaded.item.ItemFactoryCup;
import powercrystals.minefactoryreloaded.item.ItemFactoryFood;
import powercrystals.minefactoryreloaded.item.ItemFactoryHammer;
import powercrystals.minefactoryreloaded.item.ItemFishingRod;
import powercrystals.minefactoryreloaded.item.ItemLaserFocus;
import powercrystals.minefactoryreloaded.item.ItemLogicUpgradeCard;
import powercrystals.minefactoryreloaded.item.ItemMilkBottle;
import powercrystals.minefactoryreloaded.item.ItemNeedleGun;
import powercrystals.minefactoryreloaded.item.ItemNeedlegunAmmoAnvil;
import powercrystals.minefactoryreloaded.item.ItemNeedlegunAmmoBlock;
import powercrystals.minefactoryreloaded.item.ItemNeedlegunAmmoFire;
import powercrystals.minefactoryreloaded.item.ItemNeedlegunAmmoStandard;
import powercrystals.minefactoryreloaded.item.ItemPlasticBoots;
import powercrystals.minefactoryreloaded.item.ItemPortaSpawner;
import powercrystals.minefactoryreloaded.item.ItemRedNetMemoryCard;
import powercrystals.minefactoryreloaded.item.ItemRedNetMeter;
import powercrystals.minefactoryreloaded.item.ItemRocket;
import powercrystals.minefactoryreloaded.item.ItemRocketLauncher;
import powercrystals.minefactoryreloaded.item.ItemRuler;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;
import powercrystals.minefactoryreloaded.item.ItemSafariNetLauncher;
import powercrystals.minefactoryreloaded.item.ItemSpyglass;
import powercrystals.minefactoryreloaded.item.ItemStraw;
import powercrystals.minefactoryreloaded.item.ItemSyringeCure;
import powercrystals.minefactoryreloaded.item.ItemSyringeGrowth;
import powercrystals.minefactoryreloaded.item.ItemSyringeHealth;
import powercrystals.minefactoryreloaded.item.ItemSyringeLiquid;
import powercrystals.minefactoryreloaded.item.ItemSyringeSlime;
import powercrystals.minefactoryreloaded.item.ItemSyringeZombie;
import powercrystals.minefactoryreloaded.item.ItemUpgrade;
import powercrystals.minefactoryreloaded.item.ItemXpExtractor;
import powercrystals.minefactoryreloaded.net.IMFRProxy;
import powercrystals.minefactoryreloaded.net.ServerPacketHandler;
import powercrystals.minefactoryreloaded.net.ServerPacketHandler.MFRMessage;
import powercrystals.minefactoryreloaded.setup.BehaviorDispenseSafariNet;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.MineFactoryReloadedFuelHandler;
import powercrystals.minefactoryreloaded.setup.recipe.ThermalExpansion;
import powercrystals.minefactoryreloaded.setup.recipe.Vanilla;
import powercrystals.minefactoryreloaded.setup.village.VillageCreationHandler;
import powercrystals.minefactoryreloaded.setup.village.VillageTradeHandler;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityUnifier;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetHistorian;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityConveyor;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe;
import powercrystals.minefactoryreloaded.world.MineFactoryReloadedWorldGen;

@Mod(modid = modId, name = modName, version = version, dependencies = dependencies)
public class MineFactoryReloadedCore extends BaseMod
{
	//static{DepLoader.load();}
	public static final String modId = "MineFactoryReloaded";
	public static final String modName = "MineFactory Reloaded";
	public static final String version = "1.7.10R2.8.0RC1";
	public static final String dependencies = CoFHProps.DEPENDENCIES +
			";required-after:CoFHCore@[" + CoFHProps.VERSION + ",)";
	public static final String modNetworkChannel = "MFReloaded";

	@SidedProxy(clientSide = "powercrystals.minefactoryreloaded.net.ClientProxy",
				serverSide = "powercrystals.minefactoryreloaded.net.ServerProxy")
	public static IMFRProxy proxy;

	public static SimpleNetworkWrapper networkWrapper = null;

	public static final String prefix             = "minefactoryreloaded:";
	public static final String textureFolder      = prefix + "textures/";
	public static final String guiFolder          = textureFolder + "gui/";
	public static final String hudFolder          = textureFolder + "hud/";
	public static final String villagerFolder     = textureFolder + "villager/";
	public static final String tileEntityFolder   = textureFolder + "tileentity/";
	public static final String mobTextureFolder   = textureFolder + "mob/";
	public static final String modelTextureFolder = textureFolder + "itemmodels/";
	public static final String armorTextureFolder = textureFolder + "armor/";
	public static final String modelFolder        = prefix + "models/";

	public static int renderIdConveyor = 1000;
	public static int renderIdFactoryGlassPane = 1001;
	public static int renderIdFluidClassic = 1003;
	public static int renderIdRedNetLogic = 1004;
	public static int renderIdVineScaffold = 1005;
	public static int renderIdRedNetPanel = 1006;
	public static int renderIdFactoryGlass = 1007;
	public static int renderIdDetCord = 1008;
	public static int renderIdRedNet = 1009;
	public static int renderIdPPipe = 1009;

	public static Map<Integer, Block> machineBlocks = new HashMap<Integer, Block>();

	public static Block conveyorBlock;

	public static Block factoryGlassBlock;
	public static Block factoryGlassPaneBlock;
	public static Block factoryRoadBlock;
	public static Block factoryPlasticBlock;
	public static Block factoryDecorativeBrickBlock;
	public static Block factoryDecorativeStoneBlock;

	public static Block rubberWoodBlock;
	public static Block rubberLeavesBlock;
	public static BlockSapling rubberSaplingBlock;

	public static Item rubberWoodItem;
	public static Item rubberLeavesItem;
	public static Item rubberSaplingItem;

	public static Block railPickupCargoBlock;
	public static Block railDropoffCargoBlock;
	public static Block railPickupPassengerBlock;
	public static Block railDropoffPassengerBlock;

	public static BlockRedNetCable rednetCableBlock;
	public static BlockPlasticPipe plasticPipeBlock;

	public static BlockRedNetLogic rednetLogicBlock;
	public static BlockRedNetPanel rednetPanelBlock;

	public static BlockFactoryFluid milkLiquid;
	public static BlockFactoryFluid sludgeLiquid;
	public static BlockFactoryFluid sewageLiquid;
	public static BlockFactoryFluid essenceLiquid;
	public static BlockFactoryFluid biofuelLiquid;
	public static BlockFactoryFluid meatLiquid;
	public static BlockFactoryFluid pinkSlimeLiquid;
	public static BlockFactoryFluid chocolateMilkLiquid;
	public static BlockFactoryFluid mushroomSoupLiquid;
	public static BlockFactoryFluid steamFluid;

	public static Block fakeLaserBlock;

	public static Block vineScaffoldBlock;

	public static Block detCordBlock;

	public static Block fertileSoil;

	public static Block machineItem;
	public static Item machineBaseItem;

	public static Item factoryHammerItem;
	public static Item fertilizerItem;
	public static Item plasticSheetItem;
	public static Item rubberBarItem;
	public static Item rawPlasticItem;
	public static Item sewageBucketItem;
	public static Item sludgeBucketItem;
	public static Item mobEssenceBucketItem;
	public static Item syringeEmptyItem;
	public static Item syringeHealthItem;
	public static Item syringeGrowthItem;
	public static Item rawRubberItem;
	public static Item safariNetItem;
	public static Item ceramicDyeItem;
	public static Item blankRecordItem;
	public static Item syringeZombieItem;
	public static Item safariNetSingleItem;
	public static Item bioFuelBucketItem;
	public static Item upgradeItem;
	public static Item safariNetLauncherItem;
	public static Item sugarCharcoalItem;
	public static Item milkBottleItem;
	public static Item spyglassItem;
	public static Item portaSpawnerItem;
	public static Item strawItem;
	public static Item xpExtractorItem;
	public static Item syringeSlimeItem;
	public static Item syringeCureItem;
	public static Item logicCardItem;
	public static Item rednetMeterItem;
	public static Item rednetMemoryCardItem;
	public static Item rulerItem;
	public static Item meatIngotRawItem;
	public static Item meatIngotCookedItem;
	public static Item meatNuggetRawItem;
	public static Item meatNuggetCookedItem;
	public static Item meatBucketItem;
	public static Item pinkSlimeBucketItem;
	public static Item pinkSlimeballItem;
	public static Item safariNetJailerItem;
	public static Item laserFocusItem;
	public static Item chocolateMilkBucketItem;
	public static Item mushroomSoupBucketItem;
	public static Item needlegunItem;
	public static Item needlegunAmmoEmptyItem;
	public static Item needlegunAmmoStandardItem;
	public static Item needlegunAmmoLavaItem;
	public static Item needlegunAmmoSludgeItem;
	public static Item needlegunAmmoSewageItem;
	public static Item needlegunAmmoFireItem;
	public static Item needlegunAmmoAnvilItem;
	public static Item rocketLauncherItem;
	public static Item rocketItem;
	public static ItemFactoryCup plasticCupItem;
	public static Item plasticCellItem;
	public static Item fishingRodItem;
	public static Item plasticBagItem;
	public static ItemFactoryArmor plasticBootsItem;

	public static final String CHEST_GEN = "mfr:villageZoolologist";

	private static MineFactoryReloadedCore instance;
	private LinkedList<Vanilla> recipeSets = new LinkedList<Vanilla>();

	public static MineFactoryReloadedCore instance()
	{
		return instance;
	}

	public static Logger log()
	{
		return instance.getLogger();
	}

	public static void registerFluids()
	{
		registerFluid("milk",          1050,           EnumRarity.common);
		registerFluid("sludge",        1700,           EnumRarity.common);
		registerFluid("sewage",        1200,           EnumRarity.common);
		registerFluid("mobessence",     400,  9,  310, EnumRarity.epic);
		registerFluid("biofuel",        800,           EnumRarity.uncommon);
		registerFluid("meat",          2000,           EnumRarity.common);
		registerFluid("pinkslime",     3000,           EnumRarity.rare);
		registerFluid("chocolatemilk", 1100,           EnumRarity.common);
		registerFluid("mushroomsoup",  1500,           EnumRarity.common);
		registerFluid("steam",         -100,  0,  673, EnumRarity.common);
	}

	public static Fluid registerFluid(String name, int density, EnumRarity rarity)
	{
		return registerFluid(name, density, -1, -1, rarity);
	}

	public static Fluid registerFluid(String name, int density, int lightValue, int temp, EnumRarity rarity)
	{
		name = name.toLowerCase(Locale.ENGLISH);
		Fluid fluid = new Fluid(name);
		if (!FluidRegistry.registerFluid(fluid))
			fluid = FluidRegistry.getFluid(name);
		if (density != 0)
		{
			fluid.setDensity(density);
			fluid.setViscosity(Math.abs(density)); // works for my purposes
		}
		if (lightValue >= 0)
			fluid.setLuminosity(lightValue);
		if (temp >= 0)
			fluid.setTemperature(temp);
		fluid.setUnlocalizedName("mfr.liquid." + name + ".still.name");
		fluid.setRarity(rarity);
		return fluid;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) throws IOException
	{
		instance = this;
		setConfigFolderBase(evt.getModConfigurationDirectory());

		machineBlocks.put(0, new BlockFactoryMachine(0));
		machineBlocks.put(1, new BlockFactoryMachine(1));
		machineBlocks.put(2, new BlockFactoryMachine(2));

		MFRConfig.loadCommonConfig(getCommonConfig());
		MFRConfig.loadClientConfig(getClientConfig());

		loadLang();

		networkWrapper = new SimpleNetworkWrapper(modNetworkChannel);
		networkWrapper.registerMessage(ServerPacketHandler.class, MFRMessage.class, 0, Side.SERVER);

		float meatNuggetSaturation = MFRConfig.meatSaturation.getBoolean(false) ? 0.1F : 0.2F;
		float meatIngotSaturation = MFRConfig.meatSaturation.getBoolean(false) ? 0.2F : 0.8F;

		conveyorBlock = new BlockConveyor();
		factoryGlassBlock = new BlockFactoryGlass();
		factoryGlassPaneBlock = new BlockFactoryGlassPane();
		factoryRoadBlock = new BlockFactoryRoad();
		factoryPlasticBlock = new BlockFactoryPlastic();
		factoryDecorativeBrickBlock = new BlockDecorativeBricks();
		factoryDecorativeStoneBlock = new BlockDecorativeStone();
		rubberWoodBlock = new BlockRubberWood();
		rubberLeavesBlock = new BlockRubberLeaves();
		rubberSaplingBlock = new BlockRubberSapling();
		railDropoffCargoBlock = new BlockRailCargoDropoff();
		railPickupCargoBlock = new BlockRailCargoPickup();
		railDropoffPassengerBlock = new BlockRailPassengerDropoff();
		railPickupPassengerBlock = new BlockRailPassengerPickup();
		rednetCableBlock = new BlockRedNetCable();
		rednetLogicBlock = new BlockRedNetLogic();
		rednetPanelBlock = new BlockRedNetPanel();
		fakeLaserBlock = new BlockFakeLaser();
		vineScaffoldBlock = new BlockVineScaffold();
		detCordBlock = new BlockDetCord();
		plasticPipeBlock = new BlockPlasticPipe();
		
		fertileSoil = new BlockFertileSoil();
		
		machineItem = new BlockFactoryDecoration();

		registerFluids();

		milkLiquid = new BlockFactoryFluid("milk");
		sludgeLiquid = new BlockFactoryFluid("sludge");
		sewageLiquid = new BlockFactoryFluid("sewage");
		essenceLiquid = new BlockFactoryFluid("mobessence");
		biofuelLiquid = new BlockExplodingFluid("biofuel");
		meatLiquid = new BlockFactoryFluid("meat");
		pinkSlimeLiquid = new BlockPinkSlimeFluid("pinkslime");
		chocolateMilkLiquid = new BlockFactoryFluid("chocolatemilk");
		mushroomSoupLiquid = new BlockFactoryFluid("mushroomsoup");
		steamFluid = new BlockFactoryFluid("steam", BlockFactoryFluid.material);

		factoryHammerItem = (new ItemFactoryHammer()).setUnlocalizedName("mfr.hammer").setMaxStackSize(1);
		plasticBootsItem = new ItemPlasticBoots();

		rawRubberItem = (new ItemFactory()).setUnlocalizedName("mfr.rubber.raw");
		rubberBarItem = (new ItemFactory()).setUnlocalizedName("mfr.rubber.bar");

		rawPlasticItem = (new ItemFactory()).setUnlocalizedName("mfr.plastic.raw");
		plasticSheetItem = (new ItemFactory()).setUnlocalizedName("mfr.plastic.sheet").setMaxStackSize(127);

		plasticBootsItem.addRepairableItem(plasticSheetItem).addRepairableItem(rawPlasticItem);

		upgradeItem = (new ItemUpgrade()).setUnlocalizedName("mfr.upgrade.radius").setMaxStackSize(1);

		rednetMeterItem = (new ItemRedNetMeter()).setUnlocalizedName("mfr.rednet.meter").setMaxStackSize(1);
		rednetMemoryCardItem = (new ItemRedNetMemoryCard()).setUnlocalizedName("mfr.rednet.memorycard").setMaxStackSize(1);
		logicCardItem = (new ItemLogicUpgradeCard()).setUnlocalizedName("mfr.upgrade.logic").setMaxStackSize(1);

		meatIngotRawItem = (new ItemFactoryFood( 4, meatIngotSaturation)).setUnlocalizedName("mfr.meat.ingot.raw");
		meatIngotCookedItem = (new ItemFactoryFood( 10, meatIngotSaturation)).setUnlocalizedName("mfr.meat.ingot.cooked");
		meatNuggetRawItem = (new ItemFactoryFood( 1, meatNuggetSaturation)).setUnlocalizedName("mfr.meat.nugget.raw");
		meatNuggetCookedItem = (new ItemFactoryFood( 4, meatNuggetSaturation)).setUnlocalizedName("mfr.meat.nugget.cooked");
		pinkSlimeballItem = (new ItemFactory()).setUnlocalizedName("mfr.pinkslimeball");

		if (MFRConfig.enableLiquidSyringe.getBoolean(true))
			syringeEmptyItem = (new ItemSyringeLiquid()).setUnlocalizedName("mfr.syringe.empty");
		else
			syringeEmptyItem = (new ItemFactory()).setUnlocalizedName("mfr.syringe.empty");
		syringeHealthItem = (new ItemSyringeHealth()).setUnlocalizedName("mfr.syringe.health").setContainerItem(syringeEmptyItem);
		syringeGrowthItem = (new ItemSyringeGrowth()).setUnlocalizedName("mfr.syringe.growth").setContainerItem(syringeEmptyItem);
		syringeZombieItem = (new ItemSyringeZombie()).setUnlocalizedName("mfr.syringe.zombie").setContainerItem(syringeEmptyItem);
		syringeSlimeItem = (new ItemSyringeSlime()).setUnlocalizedName("mfr.syringe.slime").setContainerItem(syringeEmptyItem);
		syringeCureItem = (new ItemSyringeCure()).setUnlocalizedName("mfr.syringe.cure").setContainerItem(syringeEmptyItem);

		safariNetLauncherItem = (new ItemSafariNetLauncher()).setUnlocalizedName("mfr.safarinet.launcher").setMaxStackSize(1);
		safariNetItem = (new ItemSafariNet()).setUnlocalizedName("mfr.safarinet.reusable");
		safariNetSingleItem = (new ItemSafariNet()).setUnlocalizedName("mfr.safarinet.singleuse");
		safariNetJailerItem = (new ItemSafariNet()).setUnlocalizedName("mfr.safarinet.jailer");

		portaSpawnerItem = (new ItemPortaSpawner()).setUnlocalizedName("mfr.portaspawner").setMaxStackSize(1);

		xpExtractorItem = (new ItemXpExtractor()).setUnlocalizedName("mfr.xpextractor").setMaxStackSize(1);
		strawItem = (new ItemStraw()).setUnlocalizedName("mfr.straw").setMaxStackSize(1);
		milkBottleItem = (new ItemMilkBottle()).setUnlocalizedName("mfr.milkbottle").setMaxStackSize(16);
		plasticCupItem = (ItemFactoryCup)new ItemFactoryCup(24, 16).setUnlocalizedName("mfr.bucket.plasticcup");
		/*
		CarbonContainer.cell = new CarbonContainer(MFRConfig.plasticCellItemId.getInt(), 64, "mfr.bucket.plasticcell", false);
		CarbonContainer.cell.setFilledItem(CarbonContainer.cell).setEmptyItem(CarbonContainer.cell);

		MinecraftForge.EVENT_BUS.register(new LiquidRegistry(_configFolder,
				Loader.instance().activeModContainer()));//*/
		//plasticCellItem = CarbonContainer.cell;
		plasticBagItem = (new ItemFactoryBag()).setUnlocalizedName("mfr.plastic.bag").setMaxStackSize(24);

		sugarCharcoalItem = (new ItemFactory()).setUnlocalizedName("mfr.sugarcharcoal");
		fertilizerItem = (new ItemFactory()).setUnlocalizedName("mfr.fertilizer");

		ceramicDyeItem = (new ItemCeramicDye()).setUnlocalizedName("mfr.ceramicdye");
		laserFocusItem = (new ItemLaserFocus()).setUnlocalizedName("mfr.laserfocus").setMaxStackSize(1);

		blankRecordItem = (new ItemFactory()).setUnlocalizedName("mfr.record.blank").setMaxStackSize(1);
		spyglassItem = (new ItemSpyglass()).setUnlocalizedName("mfr.spyglass").setMaxStackSize(1);
		rulerItem = (new ItemRuler()).setUnlocalizedName("mfr.ruler").setMaxStackSize(1);
		fishingRodItem = (new ItemFishingRod());

		needlegunItem = (new ItemNeedleGun()).setUnlocalizedName("mfr.needlegun").setMaxStackSize(1);
		needlegunAmmoEmptyItem = (new ItemFactory()).setUnlocalizedName("mfr.needlegun.ammo.empty");
		needlegunAmmoStandardItem = (new ItemNeedlegunAmmoStandard()).setUnlocalizedName("mfr.needlegun.ammo.standard");
		needlegunAmmoLavaItem = (new ItemNeedlegunAmmoBlock(Blocks.flowing_lava, 3)).setUnlocalizedName("mfr.needlegun.ammo.lava");
		needlegunAmmoSludgeItem = (new ItemNeedlegunAmmoBlock(sludgeLiquid, 6)).setUnlocalizedName("mfr.needlegun.ammo.sludge");
		needlegunAmmoSewageItem = (new ItemNeedlegunAmmoBlock(sewageLiquid, 6)).setUnlocalizedName("mfr.needlegun.ammo.sewage");
		needlegunAmmoFireItem = (new ItemNeedlegunAmmoFire()).setUnlocalizedName("mfr.needlegun.ammo.fire");
		needlegunAmmoAnvilItem = (new ItemNeedlegunAmmoAnvil()).setUnlocalizedName("mfr.needlegun.ammo.anvil");

		rocketLauncherItem = (new ItemRocketLauncher()).setUnlocalizedName("mfr.rocketlauncher").setMaxStackSize(1);
		rocketItem = (new ItemRocket()).setUnlocalizedName("mfr.rocket").setMaxStackSize(16);

		registerBlock(conveyorBlock, ItemBlockFactory.class, new Object[] {BlockConveyor._names});
		registerBlock(machineItem, ItemBlockFactory.class, new Object[] {BlockFactoryDecoration._names});
		machineBaseItem = Item.getItemFromBlock(machineItem);

		for (Entry<Integer, Block> machine : machineBlocks.entrySet())
		{
			registerBlock(machine.getValue(), ItemBlockFactoryMachine.class);
		}
		
		registerBlock(plasticPipeBlock, ItemBlockFactory.class);

		registerBlock(rednetCableBlock, ItemBlockFactory.class, new Object[] {BlockRedNetCable._names});
		registerBlock(rednetLogicBlock, ItemBlockRedNetLogic.class);
		registerBlock(rednetPanelBlock, ItemBlockRedNetPanel.class);

		registerBlock(railPickupCargoBlock, ItemBlock.class);
		registerBlock(railDropoffCargoBlock, ItemBlock.class);
		registerBlock(railPickupPassengerBlock, ItemBlock.class);
		registerBlock(railDropoffPassengerBlock, ItemBlock.class);

		registerBlock(rubberSaplingBlock, ItemBlockFactoryTree.class);
		registerBlock(rubberWoodBlock, ItemBlock.class);
		registerBlock(rubberLeavesBlock, ItemBlockFactoryLeaves.class);
		rubberSaplingItem = Item.getItemFromBlock(rubberSaplingBlock);
		rubberWoodItem = Item.getItemFromBlock(rubberWoodBlock);
		rubberLeavesItem = Item.getItemFromBlock(rubberLeavesBlock);

		registerBlock(factoryGlassBlock, ItemBlockFactory.class, new Object[] {BlockFactoryGlass._names});
		registerBlock(factoryGlassPaneBlock, ItemBlockFactory.class, new Object[] {BlockFactoryGlass._names});
		registerBlock(factoryRoadBlock, ItemBlockFactoryRoad.class);
		registerBlock(factoryPlasticBlock, ItemBlockFactory.class);
		registerBlock(factoryDecorativeBrickBlock, ItemBlockFactory.class, new Object[] {BlockDecorativeBricks._names});
		registerBlock(factoryDecorativeStoneBlock, ItemBlockFactory.class, new Object[] {BlockDecorativeStone._names});

		registerBlock(vineScaffoldBlock, ItemBlockVineScaffold.class);
		registerBlock(fertileSoil, ItemBlockFactory.class);

		registerBlock(detCordBlock, ItemBlockDetCord.class);

		registerBlock(fakeLaserBlock, null);

		sewageBucketItem = (new ItemFactoryBucket(sewageLiquid)).setUnlocalizedName("mfr.bucket.sewage");
		sludgeBucketItem = (new ItemFactoryBucket(sludgeLiquid)).setUnlocalizedName("mfr.bucket.sludge");
		mobEssenceBucketItem = (new ItemFactoryBucket(essenceLiquid)).setUnlocalizedName("mfr.bucket.essence");
		bioFuelBucketItem = (new ItemFactoryBucket(biofuelLiquid)).setUnlocalizedName("mfr.bucket.biofuel");
		meatBucketItem = (new ItemFactoryBucket(meatLiquid)).setUnlocalizedName("mfr.bucket.meat");
		pinkSlimeBucketItem = (new ItemFactoryBucket(pinkSlimeLiquid)).setUnlocalizedName("mfr.bucket.pinkslime");
		chocolateMilkBucketItem = (new ItemFactoryBucket(chocolateMilkLiquid)).setUnlocalizedName("mfr.bucket.chocolatemilk");
		mushroomSoupBucketItem = (new ItemFactoryBucket(mushroomSoupLiquid)).setUnlocalizedName("mfr.bucket.mushroomsoup");

		GameRegistry.registerBlock(milkLiquid, milkLiquid.getUnlocalizedName());
		GameRegistry.registerBlock(sludgeLiquid, sludgeLiquid.getUnlocalizedName());
		GameRegistry.registerBlock(sewageLiquid, sewageLiquid.getUnlocalizedName());
		GameRegistry.registerBlock(essenceLiquid, essenceLiquid.getUnlocalizedName());
		GameRegistry.registerBlock(biofuelLiquid, biofuelLiquid.getUnlocalizedName());
		GameRegistry.registerBlock(meatLiquid, meatLiquid.getUnlocalizedName());
		GameRegistry.registerBlock(pinkSlimeLiquid, pinkSlimeLiquid.getUnlocalizedName());
		GameRegistry.registerBlock(chocolateMilkLiquid, chocolateMilkLiquid.getUnlocalizedName());
		GameRegistry.registerBlock(mushroomSoupLiquid, mushroomSoupLiquid.getUnlocalizedName());
		GameRegistry.registerBlock(steamFluid, steamFluid.getUnlocalizedName());

		Blocks.fire.setFireInfo(rubberWoodBlock, 30, 15);
		Blocks.fire.setFireInfo(rubberLeavesBlock, 80, 25);
		Blocks.fire.setFireInfo(rubberSaplingBlock, 40, 20);
		Blocks.fire.setFireInfo(detCordBlock, 100, 20);
		Blocks.fire.setFireInfo(biofuelLiquid, 300, 30);

		if (MFRConfig.vanillaOverrideGlassPane.getBoolean(true))
		{
			Blocks.glass_pane = new BlockVanillaGlassPane();
			RegistryUtils.overwriteEntry(Block.blockRegistry, "minecraft:glass_pane", Blocks.glass_pane);
		}
		if (MFRConfig.vanillaOverrideIce.getBoolean(true))
		{
			Item ice = Item.getItemFromBlock(Blocks.ice);
			Blocks.ice = new BlockVanillaIce();
			RegistryUtils.overwriteEntry(Block.blockRegistry, "minecraft:ice", Blocks.ice);
			RegistryUtils.overwriteEntry(Item.itemRegistry, "minecraft:ice", new ItemBlockVanillaIce(Blocks.ice, ice));
		}
		if (MFRConfig.vanillaOverrideMilkBucket.getBoolean(true))
		{
			final Item milkBucket = Items.milk_bucket;
			Items.milk_bucket = new ItemFactoryBucket(milkLiquid, false) {
				@Override public int hashCode() { return milkBucket.hashCode(); }
				@Override
				public boolean equals(Object obj)
				{
					return obj == milkBucket || obj == this;
				}
			}.setUnlocalizedName("mfr.bucket.milk").setTextureName("minecraft:bucket_milk").
			setCreativeTab(CreativeTabs.tabMisc);
			RegistryUtils.overwriteEntry(Item.itemRegistry, "minecraft:milk_bucket", Items.milk_bucket);
		}

		if (MFRConfig.vanillaRecipes.getBoolean(true))
			recipeSets.add(new Vanilla());

		if (MFRConfig.thermalExpansionRecipes.getBoolean(false))
			recipeSets.add(new ThermalExpansion());

		GameRegistry.registerTileEntity(TileEntityConveyor.class, "factoryConveyor");
		GameRegistry.registerTileEntity(TileEntityRedNetCable.class, "factoryRedstoneCable");
		GameRegistry.registerTileEntity(TileEntityRedNetLogic.class, "factoryRednetLogic");
		GameRegistry.registerTileEntity(TileEntityRedNetHistorian.class, "factoryRednetHistorian");
		GameRegistry.registerTileEntity(TileEntityRedNetEnergy.class, "factoryRedstoneCableEnergy");
		GameRegistry.registerTileEntity(TileEntityPlasticPipe.class, "factoryPlasticPipe");

		EntityRegistry.registerModEntity(EntitySafariNet.class, "entitySafariNet", 0, instance, 160, 5, true);
		EntityRegistry.registerModEntity(EntityPinkSlime.class, "mfrEntityPinkSlime", 1, instance, 160, 5, true);
		EntityRegistry.registerModEntity(EntityNeedle.class, "mfrEntityNeedle", 2, instance, 160, 5, true);
		EntityRegistry.registerModEntity(EntityRocket.class, "mfrEntityRocket", 3, instance, 160, 1, true);
		EntityRegistry.registerModEntity(EntityFishingRod.class, "mfrEntityFishingRod", 4, instance, 80, 3, true);

		OreDictionary.registerOre("itemRubber", MineFactoryReloadedCore.rubberBarItem);
		OreDictionary.registerOre("itemRawRubber", MineFactoryReloadedCore.rawRubberItem);
		OreDictionary.registerOre("woodRubber", MineFactoryReloadedCore.rubberWoodBlock);
		OreDictionary.registerOre("leavesRubber", MineFactoryReloadedCore.rubberLeavesBlock);
		OreDictionary.registerOre("blockPlastic", MineFactoryReloadedCore.factoryPlasticBlock);
		OreDictionary.registerOre("sheetPlastic", MineFactoryReloadedCore.plasticSheetItem);
		OreDictionary.registerOre("dustPlastic", MineFactoryReloadedCore.rawPlasticItem);
		OreDictionary.registerOre("ingotMeat", MineFactoryReloadedCore.meatIngotCookedItem);
		OreDictionary.registerOre("ingotMeatRaw", MineFactoryReloadedCore.meatIngotRawItem);
		OreDictionary.registerOre("nuggetMeat", MineFactoryReloadedCore.meatNuggetCookedItem);
		OreDictionary.registerOre("nuggetMeatRaw", MineFactoryReloadedCore.meatNuggetRawItem);
		OreDictionary.registerOre("blockMeat",
				new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 13));
		OreDictionary.registerOre("blockMeatRaw",
				new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 12));
		OreDictionary.registerOre("itemCharcoalSugar", MineFactoryReloadedCore.sugarCharcoalItem);
		OreDictionary.registerOre("cableRedNet", new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 1, 0));
		OreDictionary.registerOre("cableRedNet", new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 1, 1));
		OreDictionary.registerOre("cableRedNetEnergy",
				new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 1, 2));
		OreDictionary.registerOre("cableRedNetEnergy",
				new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 1, 3));
		OreDictionary.registerOre("slimeball", MineFactoryReloadedCore.pinkSlimeballItem);
		OreDictionary.registerOre("dyeBrown", MineFactoryReloadedCore.fertilizerItem);
		OreDictionary.registerOre("fertilizerOrganic", MineFactoryReloadedCore.fertilizerItem);
		OreDictionary.registerOre("wireExplosive", MineFactoryReloadedCore.detCordBlock);
		OreDictionary.registerOre("listAllmilk", MineFactoryReloadedCore.milkBottleItem);

		for (Vanilla e : recipeSets)
			e.registerOredictEntries();

		// vanilla items
		OreDictionary.registerOre("listAllmilk", Items.milk_bucket);

		Items.wooden_door.setMaxStackSize(8);
		Items.iron_door.setMaxStackSize(8);

		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("milk", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(milkBottleItem), new ItemStack(Items.glass_bottle)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("sludge", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(sludgeBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("sewage", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(sewageBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("mobessence", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(mobEssenceBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("biofuel", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(bioFuelBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("meat", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(meatBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("pinkslime", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(pinkSlimeBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("chocolatemilk", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(chocolateMilkBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("mushroomsoup", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(mushroomSoupBucketItem), new ItemStack(Items.bucket)));

		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("milk", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Items.milk_bucket), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("mushroomsoup", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Items.mushroom_stew), new ItemStack(Items.bowl)));

	}

	@EventHandler
	public void init(FMLInitializationEvent evt)
	{
		MinecraftForge.EVENT_BUS.register(instance);
		MinecraftForge.EVENT_BUS.register(proxy);
		MinecraftForge.EVENT_BUS.register(rednetCableBlock);
		MinecraftForge.EVENT_BUS.register(plasticPipeBlock);

		GameRegistry.registerFuelHandler(new MineFactoryReloadedFuelHandler());

		proxy.init();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MFRGUIHandler());

		IBehaviorDispenseItem behavior = new BehaviorDispenseSafariNet();
		BlockDispenser.dispenseBehaviorRegistry.putObject(safariNetItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(safariNetSingleItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(safariNetJailerItem, behavior);

		behavior = (IBehaviorDispenseItem)BlockDispenser.dispenseBehaviorRegistry.getObject(Items.water_bucket);
		BlockDispenser.dispenseBehaviorRegistry.putObject(sewageBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(sludgeBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(mobEssenceBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(bioFuelBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(meatBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(pinkSlimeBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(chocolateMilkBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(mushroomSoupBucketItem, behavior);

		addChestGenItems();

		VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandler());
		VillagerRegistry.instance().registerVillagerId(MFRConfig.zoolologistEntityId.getInt());
		VillagerRegistry.instance().registerVillageTradeHandler(MFRConfig.zoolologistEntityId.getInt(),
				new VillageTradeHandler());

		WorldHandler.instance.registerFeature(new MineFactoryReloadedWorldGen());

		UpdateManager.registerUpdater(new UpdateManager(this));
	}

	private void registerBlock(Block block, Class<? extends ItemBlock> item, Object... args)
	{
		GameRegistry.registerBlock(block, item, block.getUnlocalizedName(), args);
	}

	private void addChestGenItems()
	{
		//{ Vanilla chests
		ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(safariNetSingleItem), 1, 1, 25));
		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new WeightedRandomChestContent(new ItemStack(safariNetSingleItem), 1, 1, 25));
		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new WeightedRandomChestContent(new ItemStack(safariNetJailerItem), 1, 1, 15));
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(safariNetSingleItem), 1, 1, 25));
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_DISPENSER).addItem(new WeightedRandomChestContent(VillageTradeHandler.getHiddenNetStack(), 1, 1, 25));
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock, 1, 2), 1, 4, 8));
		ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock, 1, 2), 1, 4, 8));
		ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock, 1, 1), 1, 2, 1));
		if (MFRConfig.enableMassiveTree.getBoolean(true))
		{
			ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock, 1, 3), 1, 1, 1));
		}
		//}

		//{ DimensionalDoors chestgen compat
		// reference weights[iron: 160; coal: 120; gold: 80; golden apple: 10]
		ChestGenHooks.getInfo("dimensionalDungeonChest").addItem(new WeightedRandomChestContent(new ItemStack(safariNetJailerItem), 1, 1, 15));
		ChestGenHooks.getInfo("dimensionalDungeonChest").addItem(new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock), 1, 8, 70));
		ChestGenHooks.getInfo("dimensionalDungeonChest").addItem(new WeightedRandomChestContent(new ItemStack(pinkSlimeballItem), 1, 1, 5));
		// tempting as a sacred sapling is, chests are too common with too few possible items
		// maybe as a custom dungeon for integration 
		///}

		//{ Villager house loot chest
		ChestGenHooks.getInfo(CHEST_GEN).setMax(2 * 9);
		ChestGenHooks.getInfo(CHEST_GEN).setMin(5);
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(safariNetSingleItem), 1, 1, 35));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(safariNetJailerItem), 1, 1, 20));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(safariNetItem), 1, 1, 5));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(ItemSafariNet.makeMysteryNet(new ItemStack(safariNetSingleItem)), 1, 1, 17));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(ItemSafariNet.makeMysteryNet(new ItemStack(safariNetJailerItem)), 1, 1, 10));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(ItemSafariNet.makeMysteryNet(new ItemStack(safariNetItem)), 1, 1, 2));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.lead), 1, 17, 10));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.name_tag), 1, 14, 10));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(safariNetLauncherItem), 1, 1, 8));

		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.paper), 1, 16, 14));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.coal, 1, 1), 1, 16, 14));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(blankRecordItem), 1, 1, 14));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.book), 1, 5, 7));
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
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.reeds), 1, 16, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.pumpkin_seeds), 1, 16, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.snowball), 1, 16, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Blocks.sapling), 1, 16, 7) {
			@Override
			protected ItemStack[] generateChestContent(Random random, IInventory newInventory)
			{
				ItemStack item = theItemId.copy();
				item.setItemDamage(random.nextInt(6));
				return ChestGenHooks.generateStacks(random, item, theMinimumChanceToGenerateItem, theMaximumChanceToGenerateItem);
			}
		});
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(strawItem), 1, 1, 5));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(portaSpawnerItem), 1, 1, 1));
		//}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt)
	{
		TileEntityUnifier.updateUnifierLiquids();

		String[] list = MFRConfig.rubberTreeBiomeWhitelist.getStringList();
		for (String biome : list)
		{
			MFRRegistry.registerRubberTreeBiome(biome);
		}

		list = MFRConfig.unifierBlacklist.getStringList();
		for (String entry : list)
		{
			MFRRegistry.registerUnifierBlacklist(entry);
		}

		list = MFRConfig.spawnerBlacklist.getStringList();
		for (String entry : list)
		{
			MFRRegistry.registerAutoSpawnerBlacklist(entry);
		}

		for (Vanilla e : recipeSets)
			e.registerRecipes();
	}

	@EventHandler
	public void handleIMC(IMCEvent e)
	{
		IMCHandler.processIMC(e.getMessages());
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent evt)
	{
		IMCHandler.processIMC(FMLInterModComms.fetchRuntimeMessages(this));

		// catch biomes whitelisted via IMC that are in the config blacklist
		String[] list = MFRConfig.rubberTreeBiomeBlacklist.getStringList();
		for (String biome : list)
		{
			MFRRegistry.getRubberTreeBiomes().remove(biome);
		}
		_log.info("Load Complete.");
	}

	@SubscribeEvent
	public void onBonemeal(BonemealEvent e)
	{
		if (!e.world.isRemote && e.world.getBlock(e.x, e.y, e.z).equals(rubberSaplingBlock))
		{
			MineFactoryReloadedCore.rubberSaplingBlock.func_149879_c(e.world, e.x, e.y, e.z, e.world.rand);
			e.setResult(Result.ALLOW);
		}
	}

	@SubscribeEvent
	public void onMinecartInteract(MinecartInteractEvent e)
	{
		if (e.player.worldObj.isRemote)
			return;
		if (!MFRConfig.enableSpawnerCarts.getBoolean(true))
			return;
		if (e.minecart != null && !e.minecart.isDead)
		{
			ItemStack item = e.player.getCurrentEquippedItem();
			if (item != null && item.getItem().equals(portaSpawnerItem) &
					e.minecart.ridingEntity == null &
					e.minecart.riddenByEntity == null)
			{
				if (e.minecart.getMinecartType() == 0)
				{
					if (ItemPortaSpawner.hasData(item))
					{
						e.setCanceled(true);
						NBTTagCompound tag = ItemPortaSpawner.getSpawnerTag(item);
						e.player.destroyCurrentEquippedItem();
						e.minecart.writeToNBT(tag);
						e.minecart.setDead();
						EntityMinecartMobSpawner ent = new EntityMinecartMobSpawner(e.minecart.worldObj);
						ent.readFromNBT(tag);
						ent.worldObj.spawnEntityInWorld(ent);
						ent.worldObj.playAuxSFXAtEntity(null, 2004, // particles 
								(int)ent.posX, (int)ent.posY, (int)ent.posZ, 0);
					}
				}
				else if (e.minecart.getMinecartType() == 4)
				{
					// maybe
				}
			}
		}
	}

	@SubscribeEvent
	public void onItemExpire(ItemExpireEvent e)
	{
		ItemStack stack = e.entityItem.getEntityItem();
		if (stack.getItem().equals(rubberLeavesBlock) && stack.getItemDamage() == 0)
		{
			e.setCanceled(true);
			e.extraLife = 0;
			e.entityItem.age = 0;
			e.entityItem.setEntityItemStack(new ItemStack(stack.getItem(), stack.stackSize, 1));
		}
	}

	@Override
	public String getModId()
	{
		return modId;
	}

	@Override
	public String getModName()
	{
		return modName;
	}

	@Override
	public String getModVersion()
	{
		return version;
	}
}
