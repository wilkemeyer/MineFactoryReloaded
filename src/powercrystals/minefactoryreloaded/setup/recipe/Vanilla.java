package powercrystals.minefactoryreloaded.setup.recipe;

import static cofh.util.ItemHelper.*;
import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.*;
import static net.minecraftforge.oredict.OreDictionary.registerOre;
import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.*;
import static powercrystals.minefactoryreloaded.setup.Machine.*;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import powercrystals.minefactoryreloaded.block.ItemBlockRedNetLogic;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.setup.recipe.handler.ShapelessMachineTinker;

public class Vanilla
{
	// prevent derived recipe sets from double-registering this one if multiple sets are enabled
	private static boolean _registeredMachines;
	private static boolean _registeredMachineUpgrades;
	private static boolean _registeredMachineTinkers;
	private static boolean _registeredConveyors;
	private static boolean _registeredDecorative;
	private static boolean _registeredSyringes;
	private static boolean _registeredPlastics;
	private static boolean _registeredMiscItems;
	private static boolean _registeredSafariNets;
	private static boolean _registeredSmelting;
	private static boolean _registeredVanillaImprovements;
	private static boolean _registeredRails;
	private static boolean _registeredGuns;
	private static boolean _registeredRedNet;
	private static boolean _registeredRedNetManual;

	protected static String[] DYES = { "Black", "Red", "Green", "Brown",
		"Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime",
		"Yellow", "LightBlue", "Magenta", "Orange", "White" }; // order copied from forge 

	public final void registerRecipes()
	{
		gatherItems();
		registerConveyors();
		registerMachines();
		registerMachineUpgrades();
		registerMachineTinkers();
		registerPlastics();
		registerDecorative();
		registerMiscItems();
		registerSmelting();
		registerVanillaImprovements();
		registerSafariNets();
		registerRails();
		if (MFRConfig.enableSyringes.getBoolean(true))
			registerSyringes();
		if (MFRConfig.enableGuns.getBoolean(true))
			registerGuns();
		registerRedNet();
		registerRedNetManual();
	}

	public void registerOredictEntries()
	{
		{ // GLASS:
			String pane = "paneGlass", glass = "blockGlass";
			ItemStack glassStack = stack2(factoryGlassBlock, 1);
			ItemStack paneStack = stack2(factoryGlassPaneBlock, 1);
			registerOre(glass, glassStack.copy());
			registerOre(pane, paneStack.copy());
			for (int i = 0; i < 16; i++)
			{
				ItemStack ceramicDye = stack(ceramicDyeItem, 1, i);
				glassStack = stack(factoryGlassBlock, 1, i);
				paneStack = stack(factoryGlassPaneBlock, 1, i);
				String dye = DYES[15 - i];
				String dye2 = "dyeCeramic" + dye;
				registerOre(glass + dye, glassStack.copy());
				registerOre(pane + dye, paneStack.copy());
				registerOre(dye2, ceramicDye.copy());
			}
		}

		registerOre("stone", stack(factoryDecorativeStoneBlock, 8, 0));
		registerOre("stone", stack(factoryDecorativeStoneBlock, 8, 1));
		registerOre("cobblestone", stack(factoryDecorativeStoneBlock, 1, 2));
		registerOre("cobblestone", stack(factoryDecorativeStoneBlock, 1, 3));
	}

	protected void gatherItems()
	{
	}

	protected void registerMachines()
	{
		if(_registeredMachines)
		{
			return;
		}
		_registeredMachines = true;

		addRecipe(ShapedRecipe(stack(machineBaseItem, 3), new Object[]
				{
			"PPP",
			"SSS",
			'P', "sheetPlastic",
			'S', "stone",
				} ));

		// regex: if\s*\((Machine\.\w+)[^\n]+\n[^\n]+\n[^\n]+\n\s+(\{[^}]+\} \))[^\n]+\n[^\n]+

		registerMachine(Planter, new Object[]
				{
				"GGG",
				"CPC",
				" M ",
				'G', "sheetPlastic",
				'P', piston,
				'C', Items.flower_pot,
				'M', machineBaseItem,
				} );

		registerMachine(Fisher, new Object[]
				{
				"GGG",
				"RRR",
				"BMB",
				'G', "sheetPlastic",
				'R', fishing_rod,
				'B', bucket,
				'M', machineBaseItem,
				} );

		registerMachine(Harvester, new Object[]
				{
				"GGG",
				"SXS",
				" M ",
				'G', "sheetPlastic",
				'X', golden_axe,
				'S', shears,
				'M', machineBaseItem,
				} );

		registerMachine(Rancher, new Object[]
				{
				"GGG",
				"SBS",
				" M ",
				'G', "sheetPlastic",
				'B', bucket,
				'S', shears,
				'M', machineBaseItem,
				} );

		registerMachine(Fertilizer, new Object[]
				{
				"GGG",
				"LBL",
				" M ",
				'G', "sheetPlastic",
				'L', leather,
				'B', glass_bottle,
				'M', machineBaseItem,
				} );

		registerMachine(Vet, new Object[]
				{
				"GGG",
				"SSS",
				"EME",
				'G', "sheetPlastic",
				'E', spider_eye,
				'S', syringeEmptyItem,
				'M', machineBaseItem,
				} );

		registerMachine(ItemCollector, 8, new Object[]
				{
				"GGG",
				" C ",
				" M ",
				'G', "sheetPlastic",
				'C', chest,
				'M', machineBaseItem,
				} );

		registerMachine(BlockBreaker, new Object[]
				{
				"GGG",
				"PHS",
				" M ",
				'G', "sheetPlastic",
				'P', golden_pickaxe,
				'H', factoryHammerItem,
				'S', golden_shovel,
				'M', machineBaseItem,
				} );

		registerMachine(WeatherCollector, new Object[]
				{
				"GGG",
				"BBB",
				"UMU",
				'G', "sheetPlastic",
				'B', iron_bars,
				'U', bucket,
				'M', machineBaseItem,
				} );

		registerMachine(SludgeBoiler, new Object[]
				{
				"GGG",
				"FFF",
				" M ",
				'G', "sheetPlastic",
				'F', furnace,
				'M', machineBaseItem,
				} );

		registerMachine(Sewer, 4, new Object[]
				{
				"GGG",
				"BUB",
				"BMB",
				'G', "sheetPlastic",
				'B', brick,
				'U', bucket,
				'M', machineBaseItem,
				} );

		registerMachine(Composter, new Object[]
				{
				"GGG",
				"PFP",
				" M ",
				'G', "sheetPlastic",
				'P', piston,
				'F', furnace,
				'M', machineBaseItem,
				} );

		registerMachine(Breeder, new Object[]
				{
				"GGG",
				"CAC",
				"PMP",
				'G', "sheetPlastic",
				'P', "dyePurple",
				'C', golden_carrot,
				'A', golden_apple,
				'M', machineBaseItem,
				} );

		registerMachine(Grinder, new Object[]
				{
				"GGG",
				"BSP",
				" M ",
				'G', "sheetPlastic",
				'P', piston,
				'B', book,
				'S', golden_sword,
				'M', machineBaseItem,
				} );

		registerMachine(AutoEnchanter, new Object[]
				{
				"GGG",
				"BBB",
				"DMD",
				'G', "sheetPlastic",
				'B', book,
				'D', diamond,
				'M', machineBaseItem,
				} );

		registerMachine(Chronotyper, new Object[]
				{
				"GGG",
				"EEE",
				"PMP",
				'G', "sheetPlastic",
				'E', emerald,
				'P', "dyePurple",
				'M', machineBaseItem,
				} );

		registerMachine(Ejector, 8, new Object[]
				{
				"GGG",
				" D ",
				"RMR",
				'G', "sheetPlastic",
				'D', dropper,
				'R', "dustRedstone",
				'M', machineBaseItem,
				} );

		registerMachine(ItemRouter, 8, new Object[]
				{
				"GGG",
				"RCR",
				" M ",
				'G', "sheetPlastic",
				'C', chest,
				'R', repeater,
				'M', machineBaseItem,
				} );

		registerMachine(LiquidRouter, 8, new Object[]
				{
				"GGG",
				"RBR",
				"BMB",
				'G', "sheetPlastic",
				'B', bucket,
				'R', repeater,
				'M', machineBaseItem,
				} );

		int dsuCount = MFRConfig.craftSingleDSU.getBoolean(false) ? 1 : 4;
		registerMachine(DeepStorageUnit, dsuCount, new Object[]
				{
				"GGG",
				"PPP",
				"EME",
				'G', "sheetPlastic",
				'P', ender_pearl,
				'E', ender_eye,
				'M', machineBaseItem,
				} );

		if (MFRConfig.enableCheapDSU.getBoolean(false))
		{
			registerMachine(DeepStorageUnit, new Object[]
					{
					"GGG",
					"CCC",
					"CMC",
					'G', "sheetPlastic",
					'C', chest,
					'M', machineBaseItem,
					} );
		}

		registerMachine(LiquiCrafter, new Object[]
				{
				"GGG",
				"BWB",
				"FMF",
				'G', "sheetPlastic",
				'B', bucket,
				'W', crafting_table,
				'F', item_frame,
				'M', machineBaseItem,
				} );

		registerMachine(LavaFabricator, new Object[]
				{
				"GGG",
				"OBO",
				"CMC",
				'G', "sheetPlastic",
				'O', obsidian,
				'B', blaze_rod,
				'C', magma_cream,
				'M', machineBaseItem,
				} );

		registerMachine(SteamBoiler, new Object[]
				{
				"GGG",
				"OTO",
				"OBO",
				'G', "sheetPlastic",
				'T', bucket,
				'O', nether_brick,
				'B', SludgeBoiler,
				'M', machineBaseItem,
				} );

		registerMachine(AutoJukebox, new Object[]
				{
				"GGG",
				" J ",
				" M ",
				'G', "sheetPlastic",
				'J', jukebox,
				'M', machineBaseItem,
				} );

		registerMachine(Unifier, new Object[]
				{
				"GGG",
				"CBC",
				" M ",
				'G', "sheetPlastic",
				'B', book,
				'C', comparator,
				'M', machineBaseItem,
				} );

		registerMachine(AutoSpawner, new Object[]
				{
				"GGG",
				"ECE",
				"NMS",
				'G', "sheetPlastic",
				'E', "gemEmerald",
				'C', magma_cream,
				'N', Items.nether_wart,
				'S', sugar,
				'M', machineBaseItem,
				} );

		registerMachine(BioReactor, new Object[]
				{
				"GGG",
				"UEU",
				"SMS",
				'G', "sheetPlastic",
				'U', sugar,
				'E', fermented_spider_eye,
				'S', slime_ball,
				'M', machineBaseItem,
				} );

		registerMachine(BioFuelGenerator, new Object[]
				{
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', furnace,
				'P', piston,
				'R', blaze_rod,
				'M', machineBaseItem,
				} );

		registerMachine(AutoDisenchanter, new Object[]
				{
				"GGG",
				"RDR",
				"BMB",
				'G', "sheetPlastic",
				'B', book,
				'D', diamond,
				'R', nether_brick,
				'M', machineBaseItem,
				} );

		registerMachine(Slaughterhouse, new Object[]
				{
				"GGG",
				"SSS",
				"XMX",
				'G', "sheetPlastic",
				'S', golden_sword,
				'X', golden_axe,
				'M', machineBaseItem,
				} );

		registerMachine(MeatPacker, new Object[]
				{
				"GGG",
				"BFB",
				"BMB",
				'G', "sheetPlastic",
				'B', brick_block,
				'F', flint_and_steel,
				'M', machineBaseItem,
				} );

		registerMachine(EnchantmentRouter, new Object[]
				{
				"GGG",
				"RBR",
				" M ",
				'G', "sheetPlastic",
				'B', book,
				'R', repeater,
				'M', machineBaseItem,
				} );

		registerMachine(LaserDrill, new Object[]
				{
				"GGG",
				"LLL",
				"DMD",
				'G', "sheetPlastic",
				'L', glowstone,
				'D', "gemDiamond",
				'M', machineBaseItem,
				} );

		registerMachine(LaserDrillPrecharger, new Object[]
				{
				"GGG",
				"LSL",
				"DMD",
				'G', "sheetPlastic",
				'L', glowstone,
				'D', "gemDiamond",
				'S', pinkSlimeballItem,
				'M', machineBaseItem,
				} );

		registerMachine(AutoAnvil, new Object[]
				{
				"GGG",
				"AAA",
				" M ",
				'G', "sheetPlastic",
				'A', anvil,
				'M', machineBaseItem,
				} );

		registerMachine(BlockSmasher, new Object[]
				{
				"GGG",
				"HHH",
				"BMB",
				'G', "sheetPlastic",
				'H', factoryHammerItem,
				'B', book,
				'M', machineBaseItem,
				} );

		registerMachine(RedNote, new Object[]
				{
				"GGG",
				"CNC",
				" M ",
				'G', "sheetPlastic",
				'C', "cableRedNet",
				'N', noteblock,
				'M', machineBaseItem,
				} );

		registerMachine(AutoBrewer, new Object[]
				{
				"GGG",
				"CBC",
				"RMR",
				'G', "sheetPlastic",
				'C', chest,
				'B', Items.brewing_stand,
				'R', repeater,
				'M', machineBaseItem,
				} );

		registerMachine(FruitPicker, new Object[]
				{
				"GGG",
				"SXS",
				"SMS",
				'G', "sheetPlastic",
				'X', golden_axe,
				'S', shears,
				'M', machineBaseItem,
				} );

		registerMachine(BlockPlacer, new Object[]
				{
				"GGG",
				"DDD",
				" M ",
				'G', "sheetPlastic",
				'D', dispenser,
				'M', machineBaseItem,
				} );

		registerMachine(MobCounter, new Object[]
				{
				"GGG",
				"RCR",
				"SMS",
				'G', "sheetPlastic",
				'R', repeater,
				'C', comparator,
				'S', spyglassItem,
				'M', machineBaseItem,
				} );

		registerMachine(SteamTurbine, new Object[]
				{
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', furnace,
				'P', piston,
				'R', netherbrick,
				'M', machineBaseItem,
				} );

		registerMachine(ChunkLoader, new Object[]
				{
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', nether_star,
				'P', DeepStorageUnit,
				'R', "blockRedstone",
				'M', machineBaseItem,
				} );
		if (MFRConfig.enableCheapCL.getBoolean(false))
		{
			registerMachine(ChunkLoader, new Object[]
					{
					"GGG",
					"PFP",
					"RMR",
					'G', "sheetPlastic",
					'F', "blockGold",
					'P', ender_eye,
					'R', "blockRedstone",
					'M', machineBaseItem,
					} );
		}

		registerMachine(Fountain, new Object[]
				{
				"GBG",
				"GBG",
				"UMU",
				'G', "sheetPlastic",
				'B', iron_bars,
				'U', bucket,
				'M', machineBaseItem,
				} );

		registerMachine(MobRouter, new Object[]
				{
				"GGG",
				"BRB",
				"PCP",
				'G', "sheetPlastic",
				'B', iron_bars,
				'R', ItemRouter,
				'P', "dyeOrange",
				'C', Chronotyper,
				} );
	}

	protected void registerMachine(Machine machine, Object... recipe)
	{
		registerMachine(machine, 1, recipe);
	}

	protected void registerMachine(Machine machine, int amount, Object... recipe)
	{
		if (machine.getIsRecipeEnabled())
		{
			ItemStack item = machine.getItemStack();
			item.stackSize = amount;
			for (int i = recipe.length; i --> 0; )
				if (recipe[i] instanceof Machine)
					recipe[i] = ((Machine)recipe[i]).getItemStack();
			addRecipe(ShapedRecipe(item, recipe));
		}
	}

	protected void registerMachineUpgrades()
	{
		if(_registeredMachineUpgrades)
		{
			return;
		}
		_registeredMachineUpgrades = true;
		
		String[] materials = {"gemLapis", "ingotTin", "ingotIron", "ingotCopper", "ingotBronze",
				"ingotSilver", "ingotGold", "gemQuartz", "gemDiamond", "ingotPlatinum", "gemEmerald",
				"cobblestone"};

		for (int i = 0, e = materials.length; i < e; ++i)
		{
			addRecipe(ShapedRecipe(stack(upgradeItem, 1, i), new Object[]
					{
				"III",
				"PPP",
				"RGR",
				'I', materials[i],
				'P', "dustPlastic",
				'R', "dustRedstone",
				'G', "nuggetGold",
					} ));
		}

		for(int i = 0; i < 16; i++)
		{
			addRecipe(ShapedRecipe(stack(laserFocusItem, 1, i), new Object[]
					{
				"ENE",
				"NGN",
				"ENE",
				'E', "gemEmerald",
				'N', "nuggetGold",
				'G', stack(factoryGlassPaneBlock, 1, i)
					} ));
		}
	}

	protected void registerMachineTinkers()
	{
		if(_registeredMachineTinkers)
		{
			return;
		}
		_registeredMachineTinkers = true;

		addRecipe(new ShapelessMachineTinker(Machine.ItemCollector, "Emits comparator signal",
				stack(gold_nugget)) {
			@Override
			protected boolean isMachineTinkerable(ItemStack machine)
			{
				return !machine.hasTagCompound() || !machine.getTagCompound().hasKey("hasTinkerStuff");
			}

			@Override
			protected ItemStack getTinkeredMachine(ItemStack machine)
			{
				machine = machine.copy();
				NBTTagCompound tag = machine.getTagCompound();
				if (tag == null) machine.setTagCompound(tag = new NBTTagCompound());
				tag.setBoolean("hasTinkerStuff", true);
				return machine;
			}
		});
	}

	protected void registerConveyors()
	{
		if(_registeredConveyors)
		{
			return;
		}
		_registeredConveyors = true;

		addRecipe(ShapedRecipe(stack(conveyorBlock, 16, 16), new Object[]
				{
			"UUU",
			"RIR",
			'U', "itemRubber",
			'R', "dustRedstone",
			'I', "ingotIron",
				} ));

		for (int i = 0; i < 16; i++)
		{
			addRecipe(ShapelessRecipe(stack(conveyorBlock, 1, i),
					stack2(conveyorBlock, 1),
					"dyeCeramic" + DYES[15 - i]));
		}
	}

	protected void registerDecorative()
	{
		if(_registeredDecorative)
		{
			return;
		}
		_registeredDecorative = true;

		addSurroundRecipe(stack(factoryRoadBlock, 16), "sheetPlastic", stack(stonebrick, 1, 0));
		addRotatedGearRecipe(stack(factoryRoadBlock, 4, 1), stack(factoryRoadBlock, 1, 0), stack(redstone_lamp));
		addTwoWayConversionRecipe(stack(factoryRoadBlock, 1, 4), stack(factoryRoadBlock, 1, 1));

		{ // GLASS
			String pane = "paneGlass", glass = "blockGlass";
			for (int i = 0; i < 16; i++)
			{
				ItemStack ceramicDye = stack(ceramicDyeItem, 1, i);
				ItemStack glassStack = stack(factoryGlassBlock, 1, i);
				ItemStack paneStack = stack(factoryGlassPaneBlock, 1, i);
				String dye = DYES[15 - i];
				String dye2 = "dyeCeramic" + dye;
				String dye3 = "dye" + dye;
				addRecipe(ShapelessRecipe(cloneStack(ceramicDye, 4), stack(clay_ball), dye3));
				addRecipe(ShapelessRecipe(cloneStack(ceramicDye, 8), stack(clay_ball), stack(clay_ball), dye3, dye3));
				addRecipe(ShapelessRecipe(cloneStack(glassStack, 1), dye2, glass));
				addRecipe(ShapelessRecipe(cloneStack(glassStack, 3), dye2, glass, glass, glass));
				addRecipe(ShapelessRecipe(cloneStack(glassStack, 6), dye2, dye2, glass, glass, glass, glass, glass, glass));
				addRecipe(ShapelessRecipe(cloneStack(paneStack, 1), dye2, pane));
				addRecipe(ShapelessRecipe(cloneStack(paneStack, 3), dye2, pane, pane, pane));
				addRecipe(ShapelessRecipe(cloneStack(paneStack, 8), dye2, pane, pane, pane, pane, pane, pane, pane, pane));

				addFenceRecipe(cloneStack(paneStack, 16), cloneStack(glassStack, 1));
			}
		}

		addRotatedGearRecipe(stack(factoryDecorativeBrickBlock, 8, 0), stack2(ice), stack2(brick_block));
		addRotatedGearRecipe(stack(factoryDecorativeBrickBlock, 8, 1), stack2(glowstone), stack2(brick_block));
		addRotatedGearRecipe(stack(factoryDecorativeBrickBlock, 8, 2), "blockLapis", stack2(brick_block));
		addRotatedGearRecipe(stack(factoryDecorativeBrickBlock, 8, 3), stack2(obsidian), stack2(brick_block));
		addRotatedGearRecipe(stack(factoryDecorativeBrickBlock, 8, 4), stack(stone_slab, 1, 0), stack2(brick_block));
		addRotatedGearRecipe(stack(factoryDecorativeBrickBlock, 8, 5), stack2(snow), stack2(brick_block));

		addRotatedGearRecipe(stack(factoryDecorativeBrickBlock, 8, 6), stack2(ice), stack(stonebrick));
		addRotatedGearRecipe(stack(factoryDecorativeBrickBlock, 8, 7), stack2(glowstone), stack(stonebrick));
		addRotatedGearRecipe(stack(factoryDecorativeBrickBlock, 8, 8), "blockLapis", stack(stonebrick));
		addRotatedGearRecipe(stack(factoryDecorativeBrickBlock, 8, 9), stack2(obsidian), stack(stonebrick));
		addRotatedGearRecipe(stack(factoryDecorativeBrickBlock, 8, 10), stack(stone_slab, 1, 0), stack(stonebrick));
		addRotatedGearRecipe(stack(factoryDecorativeBrickBlock, 8, 11), stack2(snow), stack(stonebrick));
		// 12 & 13 are special blocks
		addRotatedGearRecipe(stack(factoryDecorativeBrickBlock, 8, 14), stack(brick_block, 1, 0), stack(stonebrick));

		addSmallStorageRecipe(stack(factoryDecorativeBrickBlock, 4, 0), stack(factoryDecorativeBrickBlock, 1, 6));
		addSmallStorageRecipe(stack(factoryDecorativeBrickBlock, 4, 1), stack(factoryDecorativeBrickBlock, 1, 7));
		addSmallStorageRecipe(stack(factoryDecorativeBrickBlock, 4, 2), stack(factoryDecorativeBrickBlock, 1, 8));
		addSmallStorageRecipe(stack(factoryDecorativeBrickBlock, 4, 3), stack(factoryDecorativeBrickBlock, 1, 9));
		addSmallStorageRecipe(stack(factoryDecorativeBrickBlock, 4, 4), stack(factoryDecorativeBrickBlock, 1, 10));
		addSmallStorageRecipe(stack(factoryDecorativeBrickBlock, 4, 5), stack(factoryDecorativeBrickBlock, 1, 11));
		addSmallStorageRecipe(stack(brick_block, 2, 0), stack(factoryDecorativeBrickBlock, 1, 14));

		/**
		 * Smooth:
		 **/
		addSurroundRecipe(stack(factoryDecorativeStoneBlock, 8, 0), "dyeBlack", "stone");
		addSurroundRecipe(stack(factoryDecorativeStoneBlock, 8, 1), stack2(sugar), "stone");

		/**
		 * Cobble:
		 **/
		addSurroundRecipe(stack(factoryDecorativeStoneBlock, 8, 2), "dyeBlack", "cobblestone");
		addSurroundRecipe(stack(factoryDecorativeStoneBlock, 8, 3), stack2(sugar), "cobblestone");

		// meta-sensitive optional override in block code?

		/**
		 * Large brick:
		 **/
		addSurroundRecipe(stack(factoryDecorativeStoneBlock, 8, 4), "dyeBlack", stack(stonebrick));
		addSurroundRecipe(stack(factoryDecorativeStoneBlock, 8, 5), stack2(sugar), stack(stonebrick));
		// smooth->large brick
		addSmallStorageRecipe(stack(factoryDecorativeStoneBlock, 4, 4), stack(factoryDecorativeStoneBlock, 1, 0));
		addSmallStorageRecipe(stack(factoryDecorativeStoneBlock, 4, 5), stack(factoryDecorativeStoneBlock, 1, 1));

		// TODO: add cracked large bricks?

		/**
		 * Small brick:
		 **/
		addSurroundRecipe(stack(factoryDecorativeStoneBlock, 8, 6), "dyeBlack", stack2(brick_block));
		addSurroundRecipe(stack(factoryDecorativeStoneBlock, 8, 7), stack2(sugar), stack2(brick_block));
		// large brick->small brick
		addSmallStorageRecipe(stack(factoryDecorativeStoneBlock, 4, 6), stack(factoryDecorativeStoneBlock, 1, 4));
		addSmallStorageRecipe(stack(factoryDecorativeStoneBlock, 4, 7), stack(factoryDecorativeStoneBlock, 1, 5));

		/**
		 * Gravel:
		 **/
		addSurroundRecipe(stack(factoryDecorativeStoneBlock, 8, 8), "dyeBlack", stack2(gravel));
		addSurroundRecipe(stack(factoryDecorativeStoneBlock, 8, 9), stack2(sugar), stack2(gravel));

		/**
		 * Paved:
		 **/
		addSurroundRecipe(stack(factoryDecorativeStoneBlock, 4, 10), "dyeBlack", stack(stone_slab, 1, 0));
		addSurroundRecipe(stack(factoryDecorativeStoneBlock, 4, 11), stack2(sugar), stack(stone_slab, 1, 0));
		// smooth<->paved
		addTwoWayConversionRecipe(stack(factoryDecorativeStoneBlock, 1, 10), stack(factoryDecorativeStoneBlock, 1, 0));
		addTwoWayConversionRecipe(stack(factoryDecorativeStoneBlock, 1, 11), stack(factoryDecorativeStoneBlock, 1, 1));

		// TODO: add white/black sand?

		addStorageRecipe(stack(meatIngotRawItem), "nuggetMeatRaw");
		addStorageRecipe(stack(meatIngotCookedItem), "nuggetMeat");
		addStorageRecipe(stack(factoryDecorativeBrickBlock, 1, 12), "ingotMeatRaw");
		addStorageRecipe(stack(factoryDecorativeBrickBlock, 1, 13), "ingotMeat");

		addReverseStorageRecipe(stack(meatIngotRawItem), "blockMeatRaw");
		addReverseStorageRecipe(stack(meatIngotCookedItem), "blockMeat");
		addReverseStorageRecipe(stack(meatNuggetRawItem), "ingotMeatRaw");
		addReverseStorageRecipe(stack(meatNuggetCookedItem), "ingotMeat");
	}

	protected void registerSyringes()
	{
		if(_registeredSyringes)
		{
			return;
		}
		_registeredSyringes = true;

		addRecipe(ShapedRecipe(stack(xpExtractorItem), new Object[]
				{
			"PLP",
			"PLP",
			"RPR",
			'R', "itemRubber",
			'L', "blockGlass",
			'P', "sheetPlastic",
				} ));

		addRecipe(ShapedRecipe(stack(syringeEmptyItem, 1), new Object[]
				{
			"PRP",
			"P P",
			" I ",
			'P', "sheetPlastic",
			'R', "itemRubber",
			'I', "ingotIron",
				} ));

		addShapelessRecipe(stack(syringeHealthItem), new Object[] { syringeEmptyItem, apple });
		addShapelessRecipe(stack(syringeGrowthItem), new Object[] { syringeEmptyItem, golden_carrot });

		addSurroundRecipe(stack(syringeZombieItem, 1), stack(syringeEmptyItem), stack2(rotten_flesh));

		addRecipe(ShapedRecipe(stack(syringeSlimeItem, 1), new Object[]
				{
			" S ",
			"BLB",
			'B', "slimeball",
			'L', "gemLapis",
			'S', syringeEmptyItem,
				}));

		addShapelessRecipe(stack(syringeCureItem), new Object[] { syringeEmptyItem, golden_apple });
	}

	protected void registerPlastics()
	{
		if(_registeredPlastics)
		{
			return;
		}
		_registeredPlastics = true;

		addSmallStorageRecipe(stack(plasticSheetItem, 4), "dustPlastic");

		addRecipe(ShapedRecipe(stack(factoryHammerItem, 1), new Object[]
				{
			"PPP",
			" S ",
			" S ",
			'P', "sheetPlastic",
			'S', "stickWood",
				} ));

		addRecipe(ShapedRecipe(stack(strawItem), new Object[]
				{
			"PP",
			"P ",
			"P ",
			'P', "sheetPlastic",
				} ));

		addRecipe(ShapedRecipe(stack(rulerItem), new Object[]
				{
			"P",
			"A",
			"P",
			'P', "sheetPlastic",
			'A', paper,
				} ));

		addRecipe(ShapedRecipe(stack(plasticCupItem, 16), new Object[]
				{
			" P ",
			"P P",
			'P', "sheetPlastic",
				} ));
		/*
		addRecipe(ShapedRecipe(stack(plasticCellItem, 12), new Object[]
				{
			" P ",
			"P P",
			" P ",
			'P', "sheetPlastic",
				} ));//*/

		addRecipe(ShapedRecipe(stack(plasticBagItem, 3), new Object[]
				{
			"SPS",
			"P P",
			"PPP",
			'P', "sheetPlastic",
			'S', string
				} ));

		addShapelessRecipe(stack(plasticBagItem), plasticBagItem);

		addRecipe(ShapedRecipe(stack(plasticBootsItem, 1), new Object[]
				{
			"P P",
			"P P",
			'P', "sheetPlastic",
				} ));

		addRecipe(ShapedRecipe(stack(plasticPipeBlock, 8), new Object[]
				{
			"PPP",
			"   ",
			"PPP",
			'P', "sheetPlastic",
				} ));
	}

	protected void registerMiscItems()
	{
		if(_registeredMiscItems)
		{
			return;
		}
		_registeredMiscItems = true;

		addRecipe(ShapedRecipe(stack(fertilizerItem, 16), new Object[]
				{
			"WBW",
			"STS",
			"WBW",
			'W', Items.wheat,
			'B', stack(dye, 1, 15),
			'S', string,
			'T', "stickWood",
				} ));

		addRecipe(ShapedRecipe(stack(spyglassItem), new Object[]
				{
			"GLG",
			"PLP",
			" S ",
			'G', "ingotGold",
			'L', "blockGlass",
			'P', "sheetPlastic",
			'S', "stickWood",
				} ));

		if (MFRConfig.enablePortaSpawner.getBoolean(true))
			addRecipe(ShapedRecipe(stack(portaSpawnerItem), new Object[]
					{
				"GLG",
				"DND",
				"GLG",
				'G', "ingotGold",
				'L', "blockGlass",
				'D', "gemDiamond",
				'N', nether_star
					}));

		addSurroundRecipe(stack(detCordBlock, 12), stack2(tnt), "itemRubber");

		addRecipe(ShapedRecipe(stack(fishingRodItem, 1), new Object[]
				{
			"DD ",
			"DFD",
			"TDD",
			'D', "wireExplosive",
			'F', fishing_rod,
			'T', redstone_torch
				} ));
	}

	protected void registerSafariNets()
	{
		if(_registeredSafariNets)
		{
			return;
		}
		_registeredSafariNets = true;

		addGearRecipe(stack(safariNetItem, 1), stack2(ender_pearl), stack2(ghast_tear));

		addRecipe(ShapedRecipe(stack(safariNetSingleItem, 1), new Object[]
				{
			"SLS",
			" B ",
			"S S",
			'S', string,
			'L', leather,
			'B', "slimeball",
				}));

		addGearRecipe(stack(safariNetJailerItem, 1), stack2(iron_bars), stack(safariNetSingleItem));

		if (MFRConfig.enableNetLauncher.getBoolean(true))
			addRecipe(ShapedRecipe(stack(safariNetLauncherItem, 1), new Object[]
					{
				"PGP",
				"LGL",
				"IRI",
				'P', "sheetPlastic",
				'L', glowstone_dust,
				'G', gunpowder,
				'I', "ingotIron",
				'R', "dustRedstone",
					} ));
	}

	protected void registerSmelting()
	{
		if (_registeredSmelting) return;
		_registeredSmelting = true;

		for (ItemStack s : OreDictionary.getOres("itemRubber"))
			addSmelting(stack(rawPlasticItem), s, 0.3F);
		
		addSmelting(stack(rawPlasticItem, 2), plasticBagItem);
		addSmelting(stack(rawPlasticItem, 4), strawItem);

		addSmelting(stack(meatIngotCookedItem), meatIngotRawItem, 0.5F);
		addSmelting(stack(meatNuggetCookedItem), meatNuggetRawItem, 0.3F);
		addWeakSmelting(stack(sugarCharcoalItem), sugar);
		// cooked meat block -> charcoal
		addWeakSmelting(stack(coal, 3, 1), stack(factoryDecorativeBrickBlock, 1, 13));

		addWeakSmelting(stack(coal, 1, 1), stack(rubberWoodBlock));
		addWeakSmelting(stack(rubberBarItem), stack(rawRubberItem));

		// decorative bricks: cobble->smooth
		addWeakSmelting(stack(factoryDecorativeStoneBlock, 1, 0), stack(factoryDecorativeStoneBlock, 1, 2));
		addWeakSmelting(stack(factoryDecorativeStoneBlock, 1, 1), stack(factoryDecorativeStoneBlock, 1, 3));
	}

	protected void registerVanillaImprovements()
	{
		if(_registeredVanillaImprovements)
		{
			return;
		}
		_registeredVanillaImprovements = true;

		addShapelessRecipe(stack(planks, 3, 3), stack(rubberWoodBlock));

		addShapelessRecipe(stack(piston, 1, 0), stack(sticky_piston, 1, 0), stack(milk_bucket, 1, 0));

		addRecipe(ShapedRecipe(stack(sticky_piston), new Object[]
				{
			"R",
			"P",
			'R', "itemRawRubber",
			'P', piston
				}));

		addSurroundRecipe(stack(blankRecordItem, 1), stack2(paper), "dustPlastic");

		if(MFRConfig.vanillaOverrideIce.getBoolean(true))
		{
			addRecipe(ShapelessRecipe(stack(ice, 1, 1), stack(ice, 1, 0), "dustPlastic"));
		}

		if(MFRConfig.enableMossyCobbleRecipe.getBoolean(true))
		{
			addShapelessRecipe(stack(mossy_cobblestone), new Object[]
					{
				cobblestone,
				water_bucket,
				Items.wheat
					} );
			addShapelessRecipe(stack(stonebrick, 1, 1), new Object[]
					{
				stack(stonebrick, 1, 0),
				water_bucket,
				Items.wheat
					} );
		}

		if(MFRConfig.enableSmoothSlabRecipe.getBoolean(true))
		{
			addRecipe(stack(double_stone_slab, 1, 8), new Object[]
					{
				"VV",
				'V', stack(stone_slab, 1, 0)
					});
		}

		addRecipe(stack(vineScaffoldBlock, 8), new Object[]
				{
			"VV",
			"VV",
			"VV",
			'V', vine,
				} );

		addShapelessRecipe(stack(milkBottleItem), new Object[]
				{
			milk_bucket,
			glass_bottle
				} );

		addSurroundRecipe(stack(dirt, 1, 2), stack(dirt), stack(leaves, 1, 1));

		addRecipe(ShapelessRecipe(stack(fertileSoil), stack(dirt, 1, 2), stack(fertilizerItem), "listAllmilk"));

		addShapelessRecipe(stack(chocolateMilkBucketItem), milk_bucket, bucket, stack(dye, 1, 3));

		addRecipe(ShapedRecipe(stack(torch, 4), new Object[]
				{
			"R",
			"S",
			'R', "itemRawRubber",
			'S', "stickWood",
				} ));

		addRecipe(ShapedRecipe(stack(torch, 1), new Object[]
				{
			"C",
			"S",
			'C', "itemCharcoalSugar",
			'S', "stickWood",
				} ));

		for (ItemStack torchStone : OreDictionary.getOres("torchStone"))
		{
			if (torchStone == null)
				continue;
			torchStone = torchStone.copy();
			torchStone.stackSize = 4;
			addRecipe(ShapedRecipe(torchStone, new Object[]
					{
					"R",
					"S",
					'R', "itemRawRubber",
					'S', "stoneRod",
					} ));
			torchStone = torchStone.copy();
			torchStone.stackSize = 1;

			addRecipe(ShapedRecipe(torchStone, new Object[]
					{
					"C",
					"S",
					'C', "itemCharcoalSugar",
					'S', "stoneRod",
					} ));
		}
	}

	protected void registerRails()
	{
		if(_registeredRails)
		{
			return;
		}
		_registeredRails = true;

		addRecipe(ShapedRecipe(stack(railPickupCargoBlock, 2), new Object[]
				{
			" C ",
			"SDS",
			"SSS",
			'C', chest,
			'S', "sheetPlastic",
			'D', detector_rail
				} ));

		addRecipe(ShapedRecipe(stack(railDropoffCargoBlock, 2), new Object[]
				{
			"SSS",
			"SDS",
			" C ",
			'C', chest,
			'S', "sheetPlastic",
			'D', detector_rail
				} ));

		addRecipe(ShapedRecipe(stack(railPickupPassengerBlock, 3), new Object[]
				{
			" L ",
			"SDS",
			"SSS",
			'L', lapis_block,
			'S', "sheetPlastic",
			'D', detector_rail
				} ));

		addRecipe(ShapedRecipe(stack(railDropoffPassengerBlock, 3), new Object[]
				{
			"SSS",
			"SDS",
			" L ",
			'L', lapis_block,
			'S', "sheetPlastic",
			'D', detector_rail
				} ));
	}

	protected void registerGuns()
	{
		if(_registeredGuns)
		{
			return;
		}
		_registeredGuns = true;

		addRecipe(ShapedRecipe(stack(needlegunItem), new Object[]
				{
			"PGP",
			"PLP",
			"SIS",
			'P', "sheetPlastic",
			'I', "ingotIron",
			'S', magma_cream,
			'L', safariNetLauncherItem,
			'G', spyglassItem
				}));

		addRecipe(ShapedRecipe(stack(rocketLauncherItem), new Object[]
				{
			"PCP",
			"PRP",
			"ILI",
			'P', "sheetPlastic",
			'I', minecart,
			'L', needlegunItem,
			'R', stack(logicCardItem, 1, 1),
			'C', stack(logicCardItem, 1, 2)
				}));

		addRecipe(ShapedRecipe(stack(needlegunAmmoEmptyItem, 4), new Object[]
				{
			"P P",
			"PIP",
			"PPP",
			'P', "sheetPlastic",
			'I', "ingotIron",
				}));

		addRecipe(ShapedRecipe(stack(rocketItem, 2, 0), new Object[]
				{
			"PCP",
			"PTP",
			"IMI",
			'C', stack(logicCardItem, 1, 0),
			'M', needlegunAmmoEmptyItem,
			'P', "sheetPlastic",
			'T', tnt,
			'I', fireworks
				}));

		addRecipe(ShapedRecipe(stack(rocketItem, 2, 1), new Object[]
				{
			"PPP",
			"PTP",
			"IMI",
			'M', needlegunAmmoEmptyItem,
			'P', "sheetPlastic",
			'T', tnt,
			'I', fireworks
				}));

		addRecipe(ShapedRecipe(stack(needlegunAmmoStandardItem), new Object[]
				{
			"AAA",
			"AAA",
			"GMG",
			'A', arrow,
			'M', needlegunAmmoEmptyItem,
			'G', gunpowder
				}));

		addRecipe(ShapedRecipe(stack(needlegunAmmoAnvilItem), new Object[]
				{
			"SAS",
			"STS",
			"SMS",
			'S', string,
			'A', stack(anvil, 1, 0),
			'T', tnt,
			'M', needlegunAmmoEmptyItem,
				}));

		addShapelessRecipe(stack(needlegunAmmoFireItem),
				needlegunAmmoStandardItem, flint_and_steel);

		addShapelessRecipe(stack(needlegunAmmoLavaItem),
				needlegunAmmoStandardItem, plasticCupItem,
				lava_bucket);

		addShapelessRecipe(stack(needlegunAmmoSludgeItem),
				needlegunAmmoStandardItem, plasticCupItem,
				sludgeBucketItem);

		addShapelessRecipe(stack(needlegunAmmoSewageItem),
				needlegunAmmoStandardItem, plasticCupItem,
				sewageBucketItem);
	}

	protected void registerRedNet()
	{
		if(_registeredRedNet)
		{
			return;
		}
		_registeredRedNet = true;

		addRecipe(ShapedRecipe(stack(rednetCableBlock, 8), new Object[]
				{
			"PPP",
			"RRR",
			"PPP",
			'R', "dustRedstone",
			'P', "sheetPlastic",
				} ));
		
		{
			ItemStack pipe = stack(plasticPipeBlock);
			addRecipe(ShapelessRecipe(stack(rednetCableBlock, 5), pipe, pipe, pipe, pipe, pipe, "dustRedstone", "dustRedstone"));
		}

		addRecipe(ShapelessRecipe(stack(rednetCableBlock, 1, 2), new Object[]
				{
			"nuggetGold",
			"nuggetGold",
			"nuggetGold",
			"dustRedstone",
			"dustRedstone",
			stack(rednetCableBlock),
				} ));

		addRecipe(ShapelessRecipe(stack(rednetCableBlock, 6, 2), new Object[]
				{
			"ingotGold",
			"ingotGold",
			"blockRedstone",
			stack(rednetCableBlock),
			stack(rednetCableBlock),
			stack(rednetCableBlock),
			stack(rednetCableBlock),
			stack(rednetCableBlock),
			stack(rednetCableBlock),
				} ));

		addRecipe(ShapedRecipe(stack(machineItem, 1, 1), new Object[]
				{
			"PRP",
			"RGR",
			"PIP",
			'R', "dustRedstone",
			'P', "sheetPlastic",
			'G', "blockGlass",
			'I', "ingotIron",
				} ));

		addRecipe(ShapedRecipe(stack(rednetLogicBlock), new Object[]
				{
			"RDR",
			"LGL",
			"PHP",
			'H', stack(machineItem, 1, 1),
			'P', "sheetPlastic",
			'G', "ingotGold",
			'L', "gemLapis",
			'D', "gemDiamond",
			'R', "dustRedstone",
				} ));

		addRecipe(ShapedRecipe(stack(logicCardItem, 1, 0), new Object[]
				{
			"RPR",
			"PGP",
			"RPR",
			'P', "sheetPlastic",
			'G', "ingotGold",
			'R', "dustRedstone",
				} ));

		addRecipe(ShapedRecipe(stack(logicCardItem, 1, 1), new Object[]
				{
			"GPG",
			"PCP",
			"RGR",
			'C', stack(logicCardItem, 1, 0),
			'P', "sheetPlastic",
			'G', "ingotGold",
			'R', "dustRedstone",
				} ));

		addRecipe(ShapedRecipe(stack(logicCardItem, 1, 2), new Object[]
				{
			"DPD",
			"RCR",
			"GDG",
			'C', stack(logicCardItem, 1, 1),
			'P', "sheetPlastic",
			'G', "ingotGold",
			'D', "gemDiamond",
			'R', "dustRedstone",
				} ));

		addRecipe(ShapedRecipe(stack(rednetMeterItem, 1, 0), new Object[]
				{
			" G",
			"PR",
			"PP",
			'P', "sheetPlastic",
			'G', "nuggetGold",
			'R', "dustRedstone",
				} ));

		addRecipe(ShapedRecipe(stack(rednetMeterItem, 1, 1), new Object[]
				{
			"RGR",
			"IMI",
			"PPP",
			'P', "sheetPlastic",
			'G', "ingotGold",
			'I', "ingotIron",
			'R', "dustRedstone",
			'M', stack(rednetMeterItem, 1, 0)
				} ));

		addRecipe(ShapedRecipe(stack(rednetMemoryCardItem, 1, 0), new Object[]
				{
			"GGG",
			"PRP",
			"PPP",
			'P', "sheetPlastic",
			'G', "nuggetGold",
			'R', "dustRedstone",
				} ));

		addRecipe(ShapedRecipe(stack(rednetPanelBlock, 1, 0), new Object[]
				{
			"PCP",
			"PBP",
			"KPK",
			'P', "sheetPlastic",
			'C', rednetCableBlock,
			'B', bookshelf,
			'K', "dyeBlack"
				} ));

		addShapelessRecipe(stack(rednetMemoryCardItem, 1, 0), stack(rednetMemoryCardItem, 1, 0));
	}

	private final void registerRedNetManual()
	{
		if(_registeredRedNetManual)
		{
			return;
		}
		_registeredRedNetManual = true;

		addRecipe(ShapelessRecipe(ItemBlockRedNetLogic.manual, plasticSheetItem, "dustRedstone", book));
	}
}
