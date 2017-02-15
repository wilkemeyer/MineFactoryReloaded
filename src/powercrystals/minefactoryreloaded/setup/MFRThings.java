package powercrystals.minefactoryreloaded.setup;

import cofh.api.core.IInitializer;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import javafx.fxml.Initializable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.*;
import powercrystals.minefactoryreloaded.block.decor.*;
import powercrystals.minefactoryreloaded.block.fluid.BlockFactoryFluid;
import powercrystals.minefactoryreloaded.block.fluid.BlockTank;
import powercrystals.minefactoryreloaded.block.transport.*;
import powercrystals.minefactoryreloaded.core.MFRDyeColor;
import powercrystals.minefactoryreloaded.core.ReflectionHelper;
import powercrystals.minefactoryreloaded.entity.*;
import powercrystals.minefactoryreloaded.item.*;
import powercrystals.minefactoryreloaded.item.base.*;
import powercrystals.minefactoryreloaded.item.gun.ItemNeedleGun;
import powercrystals.minefactoryreloaded.item.gun.ItemPotatoCannon;
import powercrystals.minefactoryreloaded.item.gun.ItemRocketLauncher;
import powercrystals.minefactoryreloaded.item.gun.ItemSafariNetLauncher;
import powercrystals.minefactoryreloaded.item.gun.ammo.*;
import powercrystals.minefactoryreloaded.item.syringe.*;
import powercrystals.minefactoryreloaded.item.tool.*;
import powercrystals.minefactoryreloaded.setup.recipe.EnderIO;
import powercrystals.minefactoryreloaded.setup.recipe.Vanilla;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetHistorian;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;
import powercrystals.minefactoryreloaded.tile.tank.TileEntityTank;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityConveyor;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe;

import java.util.ArrayList;
import java.util.LinkedList;

public class MFRThings
{
	public static TIntObjectMap<Block> machineBlocks = new TIntObjectHashMap<Block>();

	private static ArrayList<IInitializer> initList = new ArrayList<>();

	public static void registerInitializer(IInitializer init) {

		initList.add(init);
	}

	public static Block conveyorBlock;

	public static Block factoryGlassBlock;
	public static Block factoryGlassPaneBlock;
	public static Block factoryRoadBlock;
	public static Block factoryPlasticBlock;
	public static Block factoryDecorativeBrickBlock;
	public static Item factoryDecorativeBrickItem;
	public static Block factoryDecorativeStoneBlock;
	public static Block pinkSlimeBlock;
	public static Item pinkSlimeBlockItem;

	public static Block rubberWoodBlock;
	public static Block rubberLeavesBlock;
	public static BlockRubberSapling rubberSaplingBlock;

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

	public static Fluid milk;
	public static Fluid sludge;
	public static Fluid sewage;
	public static Fluid essence;
	public static Fluid biofuel;
	public static Fluid meat;
	public static Fluid pinkSlime;
	public static Fluid chocolateMilk;
	public static Fluid mushroomSoup;
	public static Fluid steam;
	
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

	public static Block plasticTank;
	public static Item plasticTankItem;

	public static Block fakeLaserBlock;

	public static Block vineScaffoldBlock;

	public static Block detCordBlock;

	public static Block fertileSoil;

	public static Block machineBlock;
	public static Item machineBaseItem;

	public static Item factoryHammerItem;
	public static Item fertilizerItem;
	public static Item plasticSheetItem;
	public static Item rubberBarItem;
	public static Item rawPlasticItem;
	public static Item syringeEmptyItem;
	public static Item syringeHealthItem;
	public static Item syringeGrowthItem;
	public static Item rawRubberItem;
	public static Item safariNetItem;
	public static ItemMulti ceramicDyeItem;
	public static Item blankRecordItem;
	public static Item syringeZombieItem;
	public static Item safariNetSingleItem;
	public static ItemMulti upgradeItem;
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
	public static Item pinkSlimeItem;
	public static Item safariNetJailerItem;
	public static ItemMulti laserFocusItem;
	public static Item needlegunItem;
	public static Item needlegunAmmoEmptyItem;
	public static Item needlegunAmmoStandardItem;
	public static Item needlegunAmmoPierceItem;
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
	public static ItemFactoryArmor plasticGlasses;
	public static ItemFactoryArmor plasticHelmetItem;
	public static ItemFactoryArmor plasticChestplateItem;
	public static ItemFactoryArmor plasticLeggingsItem;
	public static ItemFactoryArmor plasticBootsItem;
	public static Item safariNetFancyJailerItem;
	public static Item potatoLauncherItem;


	public static void preInit() {

		MFRThings.machineBlocks.put(0, new BlockFactoryMachine(0));
		MFRThings.machineBlocks.put(1, new BlockFactoryMachine(1));
		MFRThings.machineBlocks.put(2, new BlockFactoryMachine(2));

		conveyorBlock = new BlockConveyor();
		factoryGlassBlock = new BlockFactoryGlass();
		factoryGlassPaneBlock = new BlockFactoryGlassPane();
		factoryRoadBlock = new BlockFactoryRoad();
		factoryPlasticBlock = new BlockFactoryPlastic();
		factoryDecorativeBrickBlock = new BlockDecorativeBricks();
		factoryDecorativeStoneBlock = new BlockDecorativeStone();
		pinkSlimeBlock = new BlockPinkSlime();
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
		machineBlock = new BlockFactoryDecoration();
		plasticTank = new BlockTank();

		factoryHammerItem = new ItemFactoryHammer();
		plasticHelmetItem = new ItemFactoryArmor(ItemFactoryArmor.PLASTIC_ARMOR, EntityEquipmentSlot.HEAD);
		plasticChestplateItem = new ItemFactoryArmor(ItemFactoryArmor.PLASTIC_ARMOR, EntityEquipmentSlot.CHEST);
		plasticLeggingsItem = new ItemFactoryArmor(ItemFactoryArmor.PLASTIC_ARMOR, EntityEquipmentSlot.LEGS);
		plasticBootsItem = new ItemPlasticBoots();
		plasticGlasses = new ItemFactoryArmor(ItemFactoryArmor.GLASS_ARMOR, EntityEquipmentSlot.HEAD);

		rawRubberItem = (new ItemFactory()).setUnlocalizedName("mfr.rubber.raw");
		rubberBarItem = (new ItemFactory()).setUnlocalizedName("mfr.rubber.bar");

		rawPlasticItem = (new ItemFactory()).setUnlocalizedName("mfr.plastic.raw");
		plasticSheetItem = (new ItemFactory()).setUnlocalizedName("mfr.plastic.sheet").setMaxStackSize(96);
		{
			int i = MFRConfig.armorStacks.getBoolean(false) ? 4 : 1;
			plasticHelmetItem.setRepairIngot("itemPlastic").setUnlocalizedName("mfr.plastic.armor.helm").setMaxStackSize(i);
			plasticChestplateItem.setRepairIngot("itemPlastic").setUnlocalizedName("mfr.plastic.armor.chest").setMaxStackSize(i);
			plasticLeggingsItem.setRepairIngot("itemPlastic").setUnlocalizedName("mfr.plastic.armor.legs").setMaxStackSize(i);
			plasticBootsItem.setRepairIngot("itemPlastic").setMaxStackSize(i);
			plasticGlasses.setRepairIngot("itemPlastic").setUnlocalizedName("mfr.glass.armor.helm");
		}

		upgradeItem = new ItemUpgrade();

		rednetMeterItem = new ItemRedNetMeter();
		rednetMemoryCardItem = new ItemRedNetMemoryCard();
		logicCardItem = (new ItemLogicUpgradeCard());

		float meatNuggetSaturation = MFRConfig.meatSaturation.getBoolean(false) ? 0.1F : 0.2F;
		float meatIngotSaturation = MFRConfig.meatSaturation.getBoolean(false) ? 0.2F : 0.8F;
		meatIngotRawItem = (new ItemFactoryFood(4, meatIngotSaturation)).setUnlocalizedName("mfr.meat.ingot.raw");
		meatIngotCookedItem = (new ItemFactoryFood(10, meatIngotSaturation)).setUnlocalizedName("mfr.meat.ingot.cooked");
		meatNuggetRawItem = (new ItemFactoryFood(1, meatNuggetSaturation)).setUnlocalizedName("mfr.meat.nugget.raw");
		meatNuggetCookedItem = (new ItemFactoryFood(4, meatNuggetSaturation)).setUnlocalizedName("mfr.meat.nugget.cooked");
		pinkSlimeItem = new ItemPinkSlime();

		if (MFRConfig.enableLiquidSyringe.getBoolean(true))
			syringeEmptyItem = new ItemSyringeLiquid();
		else
			syringeEmptyItem = (new ItemFactory()).setUnlocalizedName("mfr.syringe.empty");
		syringeHealthItem = new ItemSyringeHealth();
		syringeGrowthItem = new ItemSyringeGrowth();
		syringeZombieItem = new ItemSyringeZombie();
		syringeSlimeItem = new ItemSyringeSlime();
		syringeCureItem = new ItemSyringeCure();

		safariNetLauncherItem = new ItemSafariNetLauncher();
		safariNetItem = (new ItemSafariNet(0, true)).setUnlocalizedName("mfr.safarinet.reusable");
		safariNetSingleItem = (new ItemSafariNet(0)).setUnlocalizedName("mfr.safarinet.singleuse");
		safariNetJailerItem = (new ItemSafariNet(1)).setUnlocalizedName("mfr.safarinet.jailer");
		safariNetFancyJailerItem = (new ItemSafariNet(3)).setUnlocalizedName("mfr.safarinet.jailer.fancy");

		portaSpawnerItem = new ItemPortaSpawner();

		xpExtractorItem = new ItemXpExtractor();
		strawItem = new ItemStraw();
		milkBottleItem = new ItemMilkBottle();
		plasticCupItem = new ItemFactoryCup(24, 16);

		/*
		CarbonContainer.cell = new CarbonContainer(MFRConfig.plasticCellItemId.getInt(), 64, "mfr.bucket.plasticcell", false);
		CarbonContainer.cell.setFilledItem(CarbonContainer.cell).setEmptyItem(CarbonContainer.cell);

		MinecraftForge.EVENT_BUS.register(new LiquidRegistry(_configFolder,
				Loader.instance().activeModContainer()));//*/
		//plasticCellItem = CarbonContainer.cell;
		plasticBagItem = new ItemFactoryBag();

		sugarCharcoalItem = (new ItemFactory()).setUnlocalizedName("mfr.sugarcharcoal");
		fertilizerItem = (new ItemFactory()).setUnlocalizedName("mfr.fertilizer");

		ceramicDyeItem = new ItemCeramicDye();
		(laserFocusItem = new ItemFactoryColored()).setUnlocalizedName("mfr.laserfocus").setMaxStackSize(1);

		blankRecordItem = (new ItemFactory()).setUnlocalizedName("mfr.record.blank").setMaxStackSize(1);
		spyglassItem = new ItemSpyglass();
		rulerItem = new ItemRuler();
		fishingRodItem = new ItemFishingRod();

		potatoLauncherItem = new ItemPotatoCannon();

		needlegunItem = new ItemNeedleGun();
		needlegunAmmoEmptyItem = (new ItemFactory()).setUnlocalizedName("mfr.needlegun.ammo.empty");
		needlegunAmmoStandardItem = (new ItemNeedlegunAmmoStandard()).setUnlocalizedName("mfr.needlegun.ammo.standard");
		needlegunAmmoPierceItem = (new ItemNeedlegunAmmoStandard(16, 2f, 8)).setUnlocalizedName("mfr.needlegun.ammo.pierce");
		needlegunAmmoLavaItem = (new ItemNeedlegunAmmoBlock(Blocks.FLOWING_LAVA.getDefaultState(), 3))
				.setUnlocalizedName("mfr.needlegun.ammo.lava");
		needlegunAmmoSludgeItem = (new ItemNeedlegunAmmoBlock(sludgeLiquid.getDefaultState(), 6)).setUnlocalizedName("mfr.needlegun.ammo.sludge");
		needlegunAmmoSewageItem = (new ItemNeedlegunAmmoBlock(sewageLiquid.getDefaultState(), 6)).setUnlocalizedName("mfr.needlegun.ammo.sewage");
		needlegunAmmoFireItem = (new ItemNeedlegunAmmoFire()).setUnlocalizedName("mfr.needlegun.ammo.fire");
		needlegunAmmoAnvilItem = (new ItemNeedlegunAmmoAnvil()).setUnlocalizedName("mfr.needlegun.ammo.anvil");

		rocketLauncherItem = new ItemRocketLauncher();
		rocketItem = new ItemRocket();

		if (MFRConfig.vanillaOverrideMilkBucket.getBoolean(true)) {
			final Item milkBucket = Items.MILK_BUCKET;
			ReflectionHelper.setFinalValue(Items.class, null, new ItemFactoryBucket(milkLiquid, false) {

				@Override
				public int hashCode() {

					return milkBucket.hashCode();
				}

				@Override
				public boolean equals(Object obj) {

					return obj == milkBucket || obj == this;
				}
			}.setUnlocalizedName("mfr.bucket.milk").setCreativeTab(CreativeTabs.MISC), "field_151117_aB", "MILK_BUCKET");;
			//RegistryUtils.overwriteEntry(Item.REGISTRY, new ResourceLocation("minecraft:milk_bucket"), Items.MILK_BUCKET); TODO readd vanilla bucket replacement
		}

		EntityRegistry.registerModEntity(EntityPinkSlime.class, "mfrEntityPinkSlime", 1, MineFactoryReloadedCore.instance(), 160, 5, true);
		LootTableList.register(EntityPinkSlime.PINK_SLIME);

		EntityRegistry.registerModEntity(DebugTracker.class, "DebugTracker", 99, MineFactoryReloadedCore.instance(), 250, 10, true);

		for(IInitializer init : initList) {
			init.preInit();
		}

		machineBaseItem = Item.getItemFromBlock(machineBlock);
		plasticTankItem = Item.getItemFromBlock(plasticTank);

		rubberSaplingItem = Item.getItemFromBlock(rubberSaplingBlock);
		rubberWoodItem = Item.getItemFromBlock(rubberWoodBlock);
		rubberLeavesItem = Item.getItemFromBlock(rubberLeavesBlock);

		factoryDecorativeBrickItem = Item.getItemFromBlock(factoryDecorativeBrickBlock);
		pinkSlimeBlockItem = Item.getItemFromBlock(pinkSlimeBlock);
	}

	public static void initialize() {

		for(IInitializer init : initList) {
			init.initialize();
		}
	}

	public static void postInit() {

		for(IInitializer init : initList) {
			init.postInit();
		}
	}
}
