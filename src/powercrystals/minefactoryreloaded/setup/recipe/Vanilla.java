package powercrystals.minefactoryreloaded.setup.recipe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.ItemBlockRedNetLogic;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.setup.recipe.handler.ShapelessMachineTinker;
import cpw.mods.fml.common.registry.GameRegistry;

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
	private static boolean _registeredVanillaImprovements;
	private static boolean _registeredRails;
	private static boolean _registeredGuns;
	private static boolean _registeredRedNet;
	private static boolean _registeredRedNetManual;
	
	public final void registerRecipes()
	{
		gatherItems();
		registerMachines();
		registerMachineUpgrades();
		registerMachineTinkers();
		registerConveyors();
		registerDecorative();
		if (MFRConfig.enableSyringes.getBoolean(true))
				registerSyringes();
		registerPlastics();
		registerMiscItems();
		registerSafariNets();
		registerVanillaImprovements();
		registerRails();
		if (MFRConfig.enableGuns.getBoolean(true))
			registerGuns();
		registerRedNet();
		registerRedNetManual();
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
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBaseItem, 3), new Object[]
				{
			"PPP",
			"SSS",
			'P', "sheetPlastic",
			'S', "stone",
				} ));
		
		// regex: if\s*\((Machine\.\w+)[^\n]+\n[^\n]+\n[^\n]+\n\s+(\{[^}]+\} \))[^\n]+\n[^\n]+
		
		registerMachine(Machine.Planter, new Object[]
					{
				"GGG",
				"CPC",
				" M ",
				'G', "sheetPlastic",
				'P', Blocks.piston,
				'C', Items.flower_pot,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Fisher, new Object[]
					{
				"GGG",
				"RRR",
				"BMB",
				'G', "sheetPlastic",
				'R', Items.fishing_rod,
				'B', Items.bucket,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Harvester, new Object[]
					{
				"GGG",
				"SXS",
				" M ",
				'G', "sheetPlastic",
				'X', Items.golden_axe,
				'S', Items.shears,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Rancher, new Object[]
					{
				"GGG",
				"SBS",
				" M ",
				'G', "sheetPlastic",
				'B', Items.bucket,
				'S', Items.shears,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Fertilizer, new Object[]
					{
				"GGG",
				"LBL",
				" M ",
				'G', "sheetPlastic",
				'L', Items.leather,
				'B', Items.glass_bottle,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Vet, new Object[]
					{
				"GGG",
				"SSS",
				"EME",
				'G', "sheetPlastic",
				'E', Items.spider_eye,
				'S', MineFactoryReloadedCore.syringeEmptyItem,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.ItemCollector, 8, new Object[]
					{
				"GGG",
				" C ",
				" M ",
				'G', "sheetPlastic",
				'C', Blocks.chest,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.BlockBreaker, new Object[]
					{
				"GGG",
				"PHS",
				" M ",
				'G', "sheetPlastic",
				'P', Items.golden_pickaxe,
				'H', MineFactoryReloadedCore.factoryHammerItem,
				'S', Items.golden_shovel,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.WeatherCollector, new Object[]
					{
				"GGG",
				"BBB",
				"UMU",
				'G', "sheetPlastic",
				'B', Blocks.iron_bars,
				'U', Items.bucket,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.SludgeBoiler, new Object[]
					{
				"GGG",
				"FFF",
				" M ",
				'G', "sheetPlastic",
				'F', Blocks.furnace,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Sewer, 4, new Object[]
					{
				"GGG",
				"BUB",
				"BMB",
				'G', "sheetPlastic",
				'B', Items.brick,
				'U', Items.bucket,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Composter, new Object[]
					{
				"GGG",
				"PFP",
				" M ",
				'G', "sheetPlastic",
				'P', Blocks.piston,
				'F', Blocks.furnace,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Breeder, new Object[]
					{
				"GGG",
				"CAC",
				"PMP",
				'G', "sheetPlastic",
				'P', "dyePurple",
				'C', Items.golden_carrot,
				'A', Items.golden_apple,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Grinder, new Object[]
					{
				"GGG",
				"BSP",
				" M ",
				'G', "sheetPlastic",
				'P', Blocks.piston,
				'B', Items.book,
				'S', Items.golden_sword,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.AutoEnchanter, new Object[]
					{
				"GGG",
				"BBB",
				"DMD",
				'G', "sheetPlastic",
				'B', Items.book,
				'D', Items.diamond,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Chronotyper, new Object[]
					{
				"GGG",
				"EEE",
				"PMP",
				'G', "sheetPlastic",
				'E', Items.emerald,
				'P', "dyePurple",
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Ejector, 8, new Object[]
					{
				"GGG",
				" D ",
				"RMR",
				'G', "sheetPlastic",
				'D', Blocks.dropper,
				'R', Items.redstone,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.ItemRouter, 8, new Object[]
					{
				"GGG",
				"RCR",
				" M ",
				'G', "sheetPlastic",
				'C', Blocks.chest,
				'R', Items.repeater,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.LiquidRouter, 8, new Object[]
					{
				"GGG",
				"RBR",
				"BMB",
				'G', "sheetPlastic",
				'B', Items.bucket,
				'R', Items.repeater,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		int dsuCount = MFRConfig.craftSingleDSU.getBoolean(false) ? 1 : 4;
		registerMachine(Machine.DeepStorageUnit, dsuCount, new Object[]
					{
				"GGG",
				"PPP",
				"EME",
				'G', "sheetPlastic",
				'P', Items.ender_pearl,
				'E', Items.ender_eye,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		if(MFRConfig.enableCheapDSU.getBoolean(false))
		{
			registerMachine(Machine.DeepStorageUnit, new Object[]
					{
				"GGG",
				"CCC",
				"CMC",
				'G', "sheetPlastic",
				'C', Blocks.chest,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		}
		
		registerMachine(Machine.LiquiCrafter, new Object[]
					{
				"GGG",
				"BWB",
				"FMF",
				'G', "sheetPlastic",
				'B', Items.bucket,
				'W', Blocks.crafting_table,
				'F', Items.item_frame,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.LavaFabricator, new Object[]
					{
				"GGG",
				"OBO",
				"CMC",
				'G', "sheetPlastic",
				'O', Blocks.obsidian,
				'B', Items.blaze_rod,
				'C', Items.magma_cream,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.OilFabricator, new Object[]
					{
				"GGG",
				"OTO",
				"OMO",
				'G', "sheetPlastic",
				'O', Blocks.obsidian,
				'T', Blocks.tnt,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.AutoJukebox, new Object[]
					{
				"GGG",
				" J ",
				" M ",
				'G', "sheetPlastic",
				'J', Blocks.jukebox,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Unifier, new Object[]
					{
				"GGG",
				"CBC",
				" M ",
				'G', "sheetPlastic",
				'B', Items.book,
				'C', Items.comparator,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.AutoSpawner, new Object[]
					{
				"GGG",
				"ECE",
				"NMS",
				'G', "sheetPlastic",
				'E', Items.emerald,
				'C', Items.magma_cream,
				'N', Items.nether_wart,
				'S', Items.sugar,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.BioReactor, new Object[]
					{
				"GGG",
				"UEU",
				"SMS",
				'G', "sheetPlastic",
				'U', Items.sugar,
				'E', Items.fermented_spider_eye,
				'S', Items.slime_ball,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.BioFuelGenerator, new Object[]
					{
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', Blocks.furnace,
				'P', Blocks.piston,
				'R', Items.blaze_rod,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.AutoDisenchanter, new Object[]
					{
				"GGG",
				"RDR",
				"BMB",
				'G', "sheetPlastic",
				'B', Items.book,
				'D', Items.diamond,
				'R', Blocks.nether_brick,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Slaughterhouse, new Object[]
					{
				"GGG",
				"SSS",
				"XMX",
				'G', "sheetPlastic",
				'S', Items.golden_sword,
				'X', Items.golden_axe,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.MeatPacker, new Object[]
					{
				"GGG",
				"BFB",
				"BMB",
				'G', "sheetPlastic",
				'B', Blocks.brick_block,
				'F', Items.flint_and_steel,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.EnchantmentRouter, new Object[]
					{
				"GGG",
				"RBR",
				" M ",
				'G', "sheetPlastic",
				'B', Items.book,
				'R', Items.repeater,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.LaserDrill, new Object[]
					{
				"GGG",
				"LLL",
				"DMD",
				'G', "sheetPlastic",
				'L', Blocks.glowstone,
				'D', Items.diamond,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.LaserDrillPrecharger, new Object[]
					{
				"GGG",
				"LSL",
				"DMD",
				'G', "sheetPlastic",
				'L', Blocks.glowstone,
				'D', Items.diamond,
				'S', MineFactoryReloadedCore.pinkSlimeballItem,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.AutoAnvil, new Object[]
					{
				"GGG",
				"AAA",
				" M ",
				'G', "sheetPlastic",
				'A', Blocks.anvil,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.BlockSmasher, new Object[]
					{
				"GGG",
				"HHH",
				"BMB",
				'G', "sheetPlastic",
				'H', MineFactoryReloadedCore.factoryHammerItem,
				'B', Items.book,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.RedNote, new Object[]
					{
				"GGG",
				"CNC",
				" M ",
				'G', "sheetPlastic",
				'C', "cableRedNet",
				'N', Blocks.noteblock,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.AutoBrewer, new Object[]
					{
				"GGG",
				"CBC",
				"RMR",
				'G', "sheetPlastic",
				'C', Blocks.chest,
				'B', Items.brewing_stand,
				'R', Items.repeater,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.FruitPicker, new Object[]
					{
				"GGG",
				"SXS",
				"SMS",
				'G', "sheetPlastic",
				'X', Items.golden_axe,
				'S', Items.shears,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.BlockPlacer, new Object[]
					{
				"GGG",
				"DDD",
				" M ",
				'G', "sheetPlastic",
				'D', Blocks.dispenser,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.MobCounter, new Object[]
					{
				"GGG",
				"RCR",
				"SMS",
				'G', "sheetPlastic",
				'R', Items.repeater,
				'C', Items.comparator,
				'S', MineFactoryReloadedCore.spyglassItem,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.SteamTurbine, new Object[]
					{
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', Blocks.furnace,
				'P', Blocks.piston,
				'R', Items.netherbrick,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.ChunkLoader, new Object[]
					{
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', Items.nether_star,
				'P', Machine.DeepStorageUnit.getItemStack(),
				'R', Blocks.redstone_block,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		if (MFRConfig.enableCheapCL.getBoolean(false))
		{
			registerMachine(Machine.ChunkLoader, new Object[]
					{
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', Blocks.gold_block,
				'P', Items.ender_eye,
				'R', Blocks.redstone_block,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		}
		
		registerMachine(Machine.Fountain, new Object[]
					{
				"GBG",
				"GBG",
				"UMU",
				'G', "sheetPlastic",
				'B', Blocks.iron_bars,
				'U', Items.bucket,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.MobRouter, new Object[]
					{
				"GGG",
				"CBR",
				"PMP",
				'G', "sheetPlastic",
				'C', Machine.Chronotyper.getItemStack(),
				'B', Blocks.iron_bars,
				'R', Machine.ItemRouter.getItemStack(),
				'P', "dyeOrange",
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
	}
	
	protected void registerMachine(Machine machine, Object... recipe)
	{
		registerMachine(machine, 1, recipe);
	}
	
	protected void registerMachine(Machine machine, int amount, Object... recipe)
	{
		if(machine.getIsRecipeEnabled())
		{
			ItemStack item = machine.getItemStack();
			item.stackSize = amount;
			GameRegistry.addRecipe(new ShapedOreRecipe(item, recipe));
		}
	}
	
	protected void registerMachineUpgrades()
	{
		if(_registeredMachineUpgrades)
		{
			return;
		}
		_registeredMachineUpgrades = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 0), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', new ItemStack(Items.dye, 1, 4),
			'P', "dustPlastic",
			'R', Items.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 1), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', Items.iron_ingot,
			'P', "dustPlastic",
			'R', Items.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 2), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "ingotTin",
			'P', "dustPlastic",
			'R', Items.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 3), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "ingotCopper",
			'P', "dustPlastic",
			'R', Items.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 4), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "ingotBronze",
			'P', "dustPlastic",
			'R', Items.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 5), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "ingotSilver",
			'P', "dustPlastic",
			'R', Items.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 6), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "ingotGold",
			'P', "dustPlastic",
			'R', Items.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 7), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', Items.quartz,
			'P', "dustPlastic",
			'R', Items.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 8), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', Items.diamond,
			'P', "dustPlastic",
			'R', Items.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 9), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "ingotPlatinum",
			'P', "dustPlastic",
			'R', Items.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 10), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', Items.emerald,
			'P', "dustPlastic",
			'R', Items.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 11), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "cobblestone",
			'P', "dustPlastic",
			'R', Items.redstone,
			'G', "nuggetGold",
				} ));
		
		for(int i = 0; i < 16; i++)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, i), new Object[]
					{
				"ENE",
				"NGN",
				"ENE",
				'E', Items.emerald,
				'N', "nuggetGold",
				'G', new ItemStack(MineFactoryReloadedCore.factoryGlassPaneBlock, 1, i)
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
		
		GameRegistry.addRecipe(new ShapelessMachineTinker(Machine.ItemCollector, "Emits comparator signal",
				new ItemStack(Items.gold_nugget)) {
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
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.conveyorBlock, 16, 16), new Object[]
				{
			"UUU",
			"RIR",
			'U', "itemRubber",
			'R', Items.redstone,
			'I', Items.iron_ingot,
				} ));

		String[] dyes = { "Black", "Red", "Green", "Brown", "Blue", "Purple",
				"Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow",
				"LightBlue", "Magenta", "Orange", "White" }; // order copied from forge
		
		for(int i = 0; i < 16; i++)
		{
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MineFactoryReloadedCore.conveyorBlock, 1, i),
					new ItemStack(MineFactoryReloadedCore.conveyorBlock, 1, 16),
					"dyeCeramic" + dyes[15 - i]));
		}
	}
	
	protected void registerDecorative()
	{
		if(_registeredDecorative)
		{
			return;
		}
		_registeredDecorative = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 16),
				new Object[] {
			"BBB",
			"BPB",
			"BBB",
			'P', "sheetPlastic",
			'B', new ItemStack(Blocks.stonebrick, 1, 0),
				} ));
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 4, 1), new Object[]
				{
			"R R",
			" G ",
			"R R",
			'R', new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 1, 0),
			'G', Blocks.redstone_lamp,
				} );
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 1, 4), new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 1, 1), new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 1, 4));

		String[] dyes = { "Black", "Red", "Green", "Brown", "Blue", "Purple",
				"Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow",
				"LightBlue", "Magenta", "Orange", "White" }; // order copied from forge
		
		ItemStack pane = new ItemStack(Blocks.glass_pane);
		for(int i = 0; i < 16; i++)
		{
			ItemStack dye = new ItemStack(MineFactoryReloadedCore.ceramicDyeItem, 4, i);
			GameRegistry.addRecipe(new ShapelessOreRecipe(dye, new ItemStack(Items.clay_ball), "dye" + dyes[15 - i]));
			dye.stackSize = 1;
			ItemStack glassStack = new ItemStack(MineFactoryReloadedCore.factoryGlassBlock, 1, i);
			ItemStack paneStack = new ItemStack(MineFactoryReloadedCore.factoryGlassPaneBlock, 1, i);
			OreDictionary.registerOre("glass" + dyes[15 - i], glassStack.copy());
			OreDictionary.registerOre("glassPane" + dyes[15 - i], paneStack.copy());
			OreDictionary.registerOre("dyeCeramic" + dyes[15 - i], dye.copy());
			GameRegistry.addRecipe(new ShapelessOreRecipe(glassStack, dye, "glass"));
			glassStack.stackSize = 3;
			GameRegistry.addRecipe(new ShapelessOreRecipe(glassStack, dye, "glass", "glass", "glass"));
			GameRegistry.addShapelessRecipe(paneStack.copy(), dye, pane);
			paneStack.stackSize = 3;
			GameRegistry.addShapelessRecipe(paneStack.copy(), dye, pane, pane, pane);
			paneStack.stackSize = 8;
			GameRegistry.addShapelessRecipe(paneStack.copy(), dye, pane, pane, pane, pane, pane, pane, pane, pane);
			
			GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryGlassPaneBlock, 16, i), new Object[]
					{
				"GGG",
				"GGG",
				'G', new ItemStack(MineFactoryReloadedCore.factoryGlassBlock, 1, i)
					} );
		}
		
		
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 0), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Blocks.brick_block,
			'M', Blocks.ice,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 1), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Blocks.brick_block,
			'M', Blocks.glowstone,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 2), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Blocks.brick_block,
			'M', Blocks.lapis_block,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 3), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Blocks.brick_block,
			'M', Blocks.obsidian,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 4), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Blocks.brick_block,
			'M', new ItemStack(Blocks.stone_slab, 1, 0),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 5), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Blocks.brick_block,
			'M', Blocks.snow,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 6), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Blocks.stonebrick,
			'M', Blocks.glowstone,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 7), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Blocks.stonebrick,
			'M', Blocks.ice,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 8), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Blocks.stonebrick,
			'M', Blocks.lapis_block,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 9), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Blocks.stonebrick,
			'M', Blocks.obsidian,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 10), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Blocks.stonebrick,
			'M', Blocks.snow,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 14), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Blocks.stonebrick,
			'M', new ItemStack(Blocks.stone_slab, 1, 0),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 15), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Blocks.stonebrick,
			'M', new ItemStack(Blocks.brick_block, 1, 0),
				} );
		
		/**
		 * Smooth:
		 **/
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 0), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', "stone",
			'D', new ItemStack(Items.dye, 1, 0),
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 1), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', "stone",
			'D', Items.sugar,
				}));
		
		OreDictionary.registerOre("stone", new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 0));
		OreDictionary.registerOre("stone", new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 1));
		
		// cobble->smooth
		Block stoneID = MineFactoryReloadedCore.factoryDecorativeStoneBlock;
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(stoneID, 1, 2), new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 0), 0.0001F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(stoneID, 1, 3), new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 1), 0.0001F);
		
		/**
		 * Cobble:
		 **/
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 2), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', "cobblestone",
			'D', new ItemStack(Items.dye, 1, 0),
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 3), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', "cobblestone",
			'D', Items.sugar,
				}));
		
		OreDictionary.registerOre("cobblestone", new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 2));
		OreDictionary.registerOre("cobblestone", new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 3));
		
		// meta-sensitive optional override in block code?
		
		/**
		 * Large brick:
		 **/
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 4), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', Blocks.stonebrick,
			'D', new ItemStack(Items.dye, 1, 0),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 5), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', Blocks.stonebrick,
			'D', Items.sugar,
				} );
		
		// smooth->large brick
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 4, 4), new Object[]
				{
			"SS",
			"SS",
			'S', new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 0),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 4, 5), new Object[]
				{
			"SS",
			"SS",
			'S', new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 1),
				} );
		
		/**
		 * Small brick:
		 **/
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 6), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', Blocks.brick_block,
			'D', new ItemStack(Items.dye, 1, 0),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 7), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', Blocks.brick_block,
			'D', Items.sugar,
				} );
		
		// large brick->small brick
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 4, 6), new Object[]
				{
			"SS",
			"SS",
			'S', new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 4),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 4, 7), new Object[]
				{
			"SS",
			"SS",
			'S', new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 5),
				} );
		
		/**
		 * Gravel:
		 **/
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 8), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', Blocks.gravel,
			'D', new ItemStack(Items.dye, 1, 0),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 9), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', Blocks.gravel,
			'D', Items.sugar,
				} );
		
		// FZ grinder?
		
		/**
		 * Paved:
		 **/
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 4, 10), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', new ItemStack(Blocks.stone_slab, 1, 0),
			'D', new ItemStack(Items.dye, 1, 0),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 4, 11), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', new ItemStack(Blocks.stone_slab, 1, 0),
			'D', Items.sugar,
				} );
		
		// smooth->paved
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 10), 
				new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 11), 
				new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 1));
		
		// paved->smooth
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 0), 
				new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 10));
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 1), 
				new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 11));
		
		// TODO: add white/black sand?
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 12), new Object[]
				{
			"MMM",
			"MMM",
			"MMM",
			'M', MineFactoryReloadedCore.meatIngotRawItem,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 13), new Object[]
				{
			"MMM",
			"MMM",
			"MMM",
			'M', MineFactoryReloadedCore.meatIngotCookedItem,
				} );
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.meatIngotRawItem, 9), new Object[]
				{
			new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 12)
				} );
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MineFactoryReloadedCore.meatIngotCookedItem, 9), new Object[]
				{
			new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 13)
				}));
		
		Block brickID = MineFactoryReloadedCore.factoryDecorativeBrickBlock;
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(brickID, 1, 13), new ItemStack(Items.coal, 3, 1), 0.001F);
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.meatIngotRawItem), new Object[]
				{
			"MMM",
			"MMM",
			"MMM",
			'M', MineFactoryReloadedCore.meatNuggetRawItem,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.meatIngotCookedItem), new Object[]
				{
			"MMM",
			"MMM",
			"MMM",
			'M', MineFactoryReloadedCore.meatNuggetCookedItem,
				} );
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.meatNuggetRawItem, 9), new Object[]
				{
			new ItemStack(MineFactoryReloadedCore.meatIngotRawItem)
				} );
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.meatNuggetCookedItem, 9), new Object[]
				{
			new ItemStack(MineFactoryReloadedCore.meatIngotCookedItem)
				} );
	}
	
	protected void registerSyringes()
	{
		if(_registeredSyringes)
		{
			return;
		}
		_registeredSyringes = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.xpExtractorItem), new Object[]
				{
			"PLP",
			"PLP",
			"RPR",
			'R', "itemRubber",
			'L', "glass",
			'P', "sheetPlastic",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.syringeEmptyItem, 1), new Object[]
				{
			"PRP",
			"P P",
			" I ",
			'P', "sheetPlastic",
			'R', "itemRubber",
			'I', Items.iron_ingot,
				} ));
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.syringeHealthItem), new Object[] { MineFactoryReloadedCore.syringeEmptyItem, Items.apple });
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.syringeGrowthItem), new Object[] { MineFactoryReloadedCore.syringeEmptyItem, Items.golden_carrot });
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.syringeZombieItem, 1), new Object[]
				{
			"FFF",
			"FSF",
			"FFF",
			'F', Items.rotten_flesh,
			'S', MineFactoryReloadedCore.syringeEmptyItem,
				} );
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.syringeSlimeItem, 1), new Object[]
				{
			"   ",
			" S ",
			"BLB",
			'B', "slimeball",
			'L', new ItemStack(Items.dye, 1, 4),
			'S', MineFactoryReloadedCore.syringeEmptyItem,
				}));
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.syringeCureItem), new Object[] { MineFactoryReloadedCore.syringeEmptyItem, Items.golden_apple });
	}
	
	protected void registerPlastics()
	{
		if(_registeredPlastics)
		{
			return;
		}
		_registeredPlastics = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.plasticSheetItem, 4), new Object[]
				{
			"##",
			"##",
			'#', "dustPlastic",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryHammerItem, 1), new Object[]
				{
			"PPP",
			" S ",
			" S ",
			'P', "sheetPlastic",
			'S', "stickWood",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.strawItem), new Object[]
				{
			"PP",
			"P ",
			"P ",
			'P', "sheetPlastic",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rulerItem), new Object[]
				{
			"P",
			"A",
			"P",
			'P', "sheetPlastic",
			'A', Items.paper,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.plasticCupItem, 16), new Object[]
				{
			" P ",
			"P P",
			'P', "sheetPlastic",
				} ));
		/*
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.plasticCellItem, 12), new Object[]
				{
			" P ",
			"P P",
			" P ",
			'P', "sheetPlastic",
				} ));//*/
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.bagItem, 3), new Object[]
				{
			"SPS",
			"P P",
			"PPP",
			'P', "sheetPlastic",
			'S', Items.string
				} ));
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.bagItem), MineFactoryReloadedCore.bagItem);
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.plasticBootsItem, 1), new Object[]
				{
			"P P",
			"P P",
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
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.fertilizerItem, 16), new Object[]
				{
			"WBW",
			"STS",
			"WBW",
			'W', Items.wheat,
			'B', new ItemStack(Items.dye, 1, 15),
			'S', Items.string,
			'T', "stickWood",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.spyglassItem), new Object[]
				{
			"GLG",
			"PLP",
			" S ",
			'G', "ingotGold",
			'L', "glass",
			'P', "sheetPlastic",
			'S', "stickWood",
				} ));
		
		if (MFRConfig.enablePortaSpawner.getBoolean(true))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.portaSpawnerItem), new Object[]
				{
			"GLG",
			"DND",
			"GLG",
			'G', "ingotGold",
			'L', "glass",
			'D', Items.diamond,
			'N', Items.nether_star
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.detCordBlock, 12), new Object[]
				{
			"PPP",
			"PTP",
			"PPP",
			'P', "itemRubber",
			'T', Blocks.tnt,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.fishingRodItem, 1), new Object[]
				{
			"DD ",
			"DFD",
			"TDD",
			'D', "wireExplosive",
			'F', Items.fishing_rod,
			'T', Blocks.redstone_torch
				} ));
	}
	
	protected void registerSafariNets()
	{
		if(_registeredSafariNets)
		{
			return;
		}
		_registeredSafariNets = true;
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.safariNetItem, 1), new Object[]
				{
			" E ",
			"EGE",
			" E ",
			'E', Items.ender_pearl,
			'G', Items.ghast_tear,
				} );
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.safariNetSingleItem, 1), new Object[]
				{
			"SLS",
			" B ",
			"S S",
			'S', Items.string,
			'L', Items.leather,
			'B', "slimeball",
				}));
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.safariNetJailerItem, 1), new Object[]
				{
			" I ",
			"ISI",
			" I ",
			'S', MineFactoryReloadedCore.safariNetSingleItem,
			'I', Blocks.iron_bars
				} );
		
		if (MFRConfig.enableNetLauncher.getBoolean(true))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.safariNetLauncherItem, 1), new Object[]
				{
			"PGP",
			"LGL",
			"IRI",
			'P', "sheetPlastic",
			'L', Items.glowstone_dust,
			'G', Items.gunpowder,
			'I', Items.iron_ingot,
			'R', Items.redstone,
				} ));
	}
	
	protected void registerVanillaImprovements()
	{
		if(_registeredVanillaImprovements)
		{
			return;
		}
		_registeredVanillaImprovements = true;
		
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(MineFactoryReloadedCore.rawRubberItem),
				new ItemStack(MineFactoryReloadedCore.rubberBarItem), 0.1F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(MineFactoryReloadedCore.rubberWoodBlock),
				new ItemStack(Items.coal, 1, 1), 0.1F);
		
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.planks, 3, 3),
				new ItemStack(MineFactoryReloadedCore.rubberWoodBlock));
		
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.piston, 1, 0),
				new ItemStack(Blocks.sticky_piston, 1, 0), new ItemStack(Items.milk_bucket, 1, 0));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.sticky_piston), new Object[]
				{
			"R",
			"P",
			'R', "itemRawRubber",
			'P', Blocks.piston
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.blankRecordItem, 1), new Object[]
				{
			"RRR",
			"RPR",
			"RRR",
			'R', "dustPlastic",
			'P', Items.paper,
				} ));
		
		if(MFRConfig.vanillaOverrideIce.getBoolean(true))
		{
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.ice, 1, 1), new ItemStack(Blocks.ice, 1, 0), "dustPlastic"));
		}
		
		if(MFRConfig.enableMossyCobbleRecipe.getBoolean(true))
		{
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.mossy_cobblestone), new Object[]
					{
				Blocks.cobblestone,
				Items.water_bucket,
				Items.wheat
					} );
		}
		
		if(MFRConfig.enableSmoothSlabRecipe.getBoolean(true))
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.double_stone_slab, 1, 8), new Object[]
					{
				"VV",
				'V', new ItemStack(Blocks.stone_slab, 1, 0)
					}));
		}
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.vineScaffoldBlock, 8), new Object[]
				{
			"VV",
			"VV",
			"VV",
			'V', Blocks.vine,
				} );
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.milkBottleItem), new Object[]
				{
			Items.milk_bucket,
			Items.glass_bottle
				} );
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.chocolateMilkBucketItem), Items.milk_bucket, Items.bucket, new ItemStack(Items.dye, 1, 3));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.torch, 4), new Object[]
				{
			"R",
			"S",
			'R', "itemRawRubber",
			'S', "stickWood",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.torch, 1), new Object[]
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
			GameRegistry.addRecipe(new ShapedOreRecipe(torchStone, new Object[]
					{
				"R",
				"S",
				'R', "itemRawRubber",
				'S', "stoneRod",
					} ));
			torchStone = torchStone.copy();
			torchStone.stackSize = 1;
			
			GameRegistry.addRecipe(new ShapedOreRecipe(torchStone, new Object[]
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
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.railPickupCargoBlock, 2), new Object[]
				{
			" C ",
			"SDS",
			"SSS",
			'C', Blocks.chest,
			'S', "sheetPlastic",
			'D', Blocks.detector_rail
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.railDropoffCargoBlock, 2), new Object[]
				{
			"SSS",
			"SDS",
			" C ",
			'C', Blocks.chest,
			'S', "sheetPlastic",
			'D', Blocks.detector_rail
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.railPickupPassengerBlock, 3), new Object[]
				{
			" L ",
			"SDS",
			"SSS",
			'L', Blocks.lapis_block,
			'S', "sheetPlastic",
			'D', Blocks.detector_rail
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.railDropoffPassengerBlock, 3), new Object[]
				{
			"SSS",
			"SDS",
			" L ",
			'L', Blocks.lapis_block,
			'S', "sheetPlastic",
			'D', Blocks.detector_rail
				} ));
	}
	
	protected void registerGuns()
	{
		if(_registeredGuns)
		{
			return;
		}
		_registeredGuns = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunItem), new Object[]
				{
					"PGP",
					"PLP",
					"SIS",
					'P', "sheetPlastic",
					'I', Items.iron_ingot,
					'S', Items.magma_cream,
					'L', MineFactoryReloadedCore.safariNetLauncherItem,
					'G', MineFactoryReloadedCore.spyglassItem
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rocketLauncherItem), new Object[]
				{
					"PCP",
					"PRP",
					"ILI",
					'P', "sheetPlastic",
					'I', Items.minecart,
					'L', MineFactoryReloadedCore.needlegunItem,
					'R', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 1),
					'C', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 2)
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoEmptyItem, 4), new Object[]
				{
					"P P",
					"PIP",
					"PPP",
					'P', "sheetPlastic",
					'I', Items.iron_ingot,
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rocketItem, 2, 0), new Object[]
				{
					"PCP",
					"PTP",
					"IMI",
					'C', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 0),
					'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
					'P', "sheetPlastic",
					'T', Blocks.tnt,
					'I', Items.fireworks
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rocketItem, 2, 1), new Object[]
				{
					"PPP",
					"PTP",
					"IMI",
					'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
					'P', "sheetPlastic",
					'T', Blocks.tnt,
					'I', Items.fireworks
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoStandardItem), new Object[]
				{
					"AAA",
					"AAA",
					"GMG",
					'A', Items.arrow,
					'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
					'G', Items.gunpowder
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoAnvilItem), new Object[]
				{
					"SMS",
					"SAS",
					"STS",
					'A', new ItemStack(Blocks.anvil, 1, 0),
					'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
					'S', Items.string,
					'T', Blocks.tnt
				}));
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoFireItem),
				MineFactoryReloadedCore.needlegunAmmoStandardItem, Items.flint_and_steel);
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoLavaItem),
				MineFactoryReloadedCore.needlegunAmmoStandardItem, MineFactoryReloadedCore.plasticCupItem,
				Items.lava_bucket);
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoSludgeItem),
				MineFactoryReloadedCore.needlegunAmmoStandardItem, MineFactoryReloadedCore.plasticCupItem,
				MineFactoryReloadedCore.sludgeBucketItem);
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoSewageItem),
				MineFactoryReloadedCore.needlegunAmmoStandardItem, MineFactoryReloadedCore.plasticCupItem,
				MineFactoryReloadedCore.sewageBucketItem);
	}
	
	protected void registerRedNet()
	{
		if(_registeredRedNet)
		{
			return;
		}
		_registeredRedNet = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 8), new Object[]
				{
			"PPP",
			"RRR",
			"PPP",
			'R', Items.redstone,
			'P', "sheetPlastic",
				} ));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 1, 2), new Object[]
				{
			"nuggetGold",
			"nuggetGold",
			"nuggetGold",
			Items.redstone,
			Items.redstone,
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
				} ));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 6, 2), new Object[]
				{
			"ingotGold",
			"ingotGold",
			Blocks.redstone_block,
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 11), new Object[]
				{
			"PRP",
			"RGR",
			"PIP",
			'R', Items.redstone,
			'P', "sheetPlastic",
			'G', "glass",
			'I', Items.iron_ingot,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetLogicBlock), new Object[]
				{
			"RDR",
			"LGL",
			"PHP",
			'H', new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 11),
			'P', "sheetPlastic",
			'G', "ingotGold",
			'L', new ItemStack(Items.dye, 1, 4),
			'D', Items.diamond,
			'R', Items.redstone,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 0), new Object[]
				{
			"RPR",
			"PGP",
			"RPR",
			'P', "sheetPlastic",
			'G', "ingotGold",
			'R', Items.redstone,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 1), new Object[]
				{
			"GPG",
			"PCP",
			"RGR",
			'C', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 0),
			'P', "sheetPlastic",
			'G', "ingotGold",
			'R', Items.redstone,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 2), new Object[]
				{
			"DPD",
			"RCR",
			"GDG",
			'C', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 1),
			'P', "sheetPlastic",
			'G', "ingotGold",
			'D', Items.diamond,
			'R', Items.redstone,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetMeterItem, 1, 0), new Object[]
				{
			" G",
			"PR",
			"PP",
			'P', "sheetPlastic",
			'G', "nuggetGold",
			'R', Items.redstone,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetMemoryCardItem, 1, 0), new Object[]
				{
			"GGG",
			"PRP",
			"PPP",
			'P', "sheetPlastic",
			'G', "nuggetGold",
			'R', Items.redstone,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetPanelBlock, 1, 0), new Object[]
				{
			"PCP",
			"PBP",
			"KPK",
			'P', "sheetPlastic",
			'C', MineFactoryReloadedCore.rednetCableBlock,
			'B', Blocks.bookshelf,
			'K', new ItemStack(Items.dye, 1, 0)
				} ));
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.rednetMemoryCardItem, 1, 0), new ItemStack(MineFactoryReloadedCore.rednetMemoryCardItem, 1, 0));
	}
	
	private final void registerRedNetManual()
	{
		if(_registeredRedNetManual)
		{
			return;
		}
		_registeredRedNetManual = true;
		
		GameRegistry.addShapelessRecipe(ItemBlockRedNetLogic.manual, MineFactoryReloadedCore.plasticSheetItem, Items.redstone, Items.book);
	}
}
