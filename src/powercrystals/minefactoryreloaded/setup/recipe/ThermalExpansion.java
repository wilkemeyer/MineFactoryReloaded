package powercrystals.minefactoryreloaded.setup.recipe;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.MissingModsException;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;

import java.util.Collections;
import java.util.logging.Level;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;

public class ThermalExpansion extends Vanilla
{
	ItemStack conduitLiquid;
	ItemStack tankBasic;
	ItemStack tankHardened;
	ItemStack lamp;
	ItemStack machineFrame;
	ItemStack pneumaticServo;
	ItemStack powerCoilElectrum;
	ItemStack powerCoilGold;
	ItemStack powerCoilSilver;
	ItemStack tesseractFrameFull;
	ItemStack strongBox;
	ItemStack strongBoxBasic;
	ItemStack multimeter;
	ItemStack invarAxe;
	ItemStack invarSword;
	ItemStack invarPickaxe;
	ItemStack dynamoSteam;
	ItemStack tesseract;
	ItemStack tesseractFrameEmpty;
	ItemStack cellResonant;
	ItemStack cellRedstone;
	
	@Override
	protected void gatherItems()
	{
		if(!Loader.isModLoaded("ThermalExpansion"))
		{
			FMLLog.severe(MineFactoryReloadedCore.modId, Level.SEVERE,
					"ThermalExpansion is required for ThermalExpansion recipes to be enabled.");
			throw new MissingModsException(Collections.
					singleton((ArtifactVersion)new DefaultArtifactVersion("ThermalExpansion", "(3.0.0.2,]")));
		}
		conduitLiquid = GameRegistry.findItemStack("ThermalExpansion", "conduitFluidOpaque", 1);
		tankBasic = GameRegistry.findItemStack("ThermalExpansion", "tankBasic", 1);
		tankHardened = GameRegistry.findItemStack("ThermalExpansion", "tankHardened", 1);
		lamp = GameRegistry.findItemStack("ThermalExpansion", "lamp", 1);
		machineFrame = GameRegistry.findItemStack("ThermalExpansion", "machineFrame", 1);
		pneumaticServo = GameRegistry.findItemStack("ThermalExpansion", "pneumaticServo", 1);
		powerCoilElectrum = GameRegistry.findItemStack("ThermalExpansion", "powerCoilElectrum", 1);
		powerCoilGold = GameRegistry.findItemStack("ThermalExpansion", "powerCoilGold", 1);
		powerCoilSilver = GameRegistry.findItemStack("ThermalExpansion", "powerCoilSilver", 1);
		tesseractFrameFull = GameRegistry.findItemStack("ThermalExpansion", "tesseractFrameFull", 1);
		strongBox = GameRegistry.findItemStack("ThermalExpansion", "strongboxReinforced", 1);
		strongBoxBasic = GameRegistry.findItemStack("ThermalExpansion", "strongboxBasic", 1);
		multimeter = GameRegistry.findItemStack("ThermalExpansion", "multimeter", 1);
		invarAxe = GameRegistry.findItemStack("ThermalExpansion", "toolInvarAxe", 1);
		invarSword = GameRegistry.findItemStack("ThermalExpansion", "toolInvarSword", 1);
		invarPickaxe = GameRegistry.findItemStack("ThermalExpansion", "toolInvarPickaxe", 1);
		dynamoSteam = GameRegistry.findItemStack("ThermalExpansion", "dynamoSteam", 1);
		tesseract = new ItemStack(GameRegistry.findBlock("ThermalExpansion", "Tesseract"));
		tesseractFrameEmpty = GameRegistry.findItemStack("ThermalExpansion", "tesseractFrameEmpty", 1);
		cellResonant = GameRegistry.findItemStack("ThermalExpansion", "cellResonant", 1);
		cellRedstone = GameRegistry.findItemStack("ThermalExpansion", "cellReinforced", 1);
	}
	
	@Override
	protected void registerMachines()
	{
		registerMachine(Machine.Planter, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.flower_pot,
				'S', Blocks.piston,
				'F', machineFrame,
				'O', "thermalexpansion:machineCopper",
				'C', powerCoilGold,
					} );
		
		registerMachine(Machine.Fisher, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.fishing_rod,
				'S', Items.bucket,
				'F', machineFrame,
				'O', "thermalexpansion:machineIron",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.Harvester, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', invarAxe,
				'S', Items.shears,
				'F', machineFrame,
				'O', "thermalexpansion:machineGold",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.Rancher, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', conduitLiquid,
				'S', Items.shears,
				'F', machineFrame,
				'O', "thermalexpansion:machineTin",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.Fertilizer, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.glass_bottle,
				'S', Items.leather,
				'F', machineFrame,
				'O', "thermalexpansion:machineNickel",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.Vet, new Object[] {
				"PTP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'T', MineFactoryReloadedCore.syringeEmptyItem,
				'F', machineFrame,
				'O', "thermalexpansion:machineCopper",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.ItemCollector, 8, new Object[] {
				"P P",
				" F ",
				"PCP",
				'P', "sheetPlastic",
				'F', machineFrame,
				'C', Blocks.chest
					} );
		
		registerMachine(Machine.BlockBreaker, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', "gearInvar",
				'S', invarPickaxe,
				'F', machineFrame,
				'O', "thermalexpansion:machineIron",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.WeatherCollector, new Object[] {
				"PBP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'B', Blocks.iron_bars,
				'T', Items.bucket,
				'F', machineFrame,
				'O', "thermalexpansion:machineCopper",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.SludgeBoiler, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.bucket,
				'S', Blocks.furnace,
				'F', machineFrame,
				'O', "thermalexpansion:machineIron",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.Sewer, 4, new Object[] {
				"PTP",
				"SFS",
				"SQS",
				'P', "sheetPlastic",
				'T', Items.bucket,
				'S', Items.brick,
				'F', machineFrame,
				'Q', pneumaticServo,
					} );
		
		registerMachine(Machine.Composter, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.furnace,
				'S', Blocks.piston,
				'F', machineFrame,
				'O', Items.brick,
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.Breeder, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.golden_apple,
				'S', Items.golden_carrot,
				'F', machineFrame,
				'O', "dyePurple",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.Grinder, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', invarSword,
				'S', "thermalexpansion:machineTin",
				'F', machineFrame,
				'O', Items.book,
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.AutoEnchanter, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.obsidian,
				'S', Items.book,
				'F', machineFrame,
				'O', "gemDiamond",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.Chronotyper, new Object[] {
				"PTP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'T', "gemEmerald",
				'F', machineFrame,
				'O', "dyePurple",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.Ejector, 8, new Object[] {							
				"PPP",
				" T ",
				"OFO",
				'P', "sheetPlastic",
				'T', pneumaticServo,
				'F', machineFrame,
				'O', "dustRedstone"
					} );
		
		registerMachine(Machine.ItemRouter, 8, new Object[] {					
				"PTP",
				"SFS",
				"PSP",
				'P', "sheetPlastic",
				'T', Blocks.chest,
				'S', multimeter,
				'F', machineFrame
					} );
		
		registerMachine(Machine.LiquidRouter, 8, new Object[] {					
				"PTP",
				"SFS",
				"PSP",
				'P', "sheetPlastic",
				'T', conduitLiquid,
				'S', multimeter,
				'F', machineFrame
					} );
		
		int dsuCount = MFRConfig.craftSingleDSU.getBoolean(false) ? 1 : 4;
		registerMachine(Machine.DeepStorageUnit, dsuCount, new Object[] {					
			"PCP",
			"CFC",
			"PCP",
			'P', "sheetPlastic",
			'C', strongBox,
			'F', tesseractFrameFull
				} );
		
		if(MFRConfig.enableCheapDSU.getBoolean(false))
		{
			registerMachine(Machine.DeepStorageUnit, new Object[] {					
				"PCP",
				"CFC",
				"PCP",
				'P', "sheetPlastic",
				'C', strongBoxBasic,
				'F', machineFrame
					} );
		}
		
		registerMachine(Machine.LiquiCrafter, new Object[] {						
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.crafting_table,
				'S', tankBasic,
				'F', machineFrame,
				'O', Items.book,
				'C', pneumaticServo
					} );
		
		registerMachine(Machine.LavaFabricator, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.obsidian,
				'S', Items.magma_cream,
				'F', machineFrame,
				'O', Items.blaze_rod,
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.SteamBoiler, new Object[] {
				"PPP",
				"TBT",
				"OOO",
				'P', "sheetPlastic",
				'T', tankHardened,
				'B', Machine.SludgeBoiler.getItemStack(),
				'O', Blocks.nether_brick
					} );
		
		registerMachine(Machine.AutoJukebox, new Object[] {
				"PJP",
				"PFP",
				'P', "sheetPlastic",
				'J', Blocks.jukebox,
				'F', machineFrame
					} );
		
		registerMachine(Machine.Unifier, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', multimeter,
				'S', "ingotSilver",
				'F', machineFrame,
				'O', Items.comparator,
				'C', Items.book
					} );
		
		registerMachine(Machine.AutoSpawner, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.nether_wart,
				'S', Items.magma_cream,
				'F', machineFrame,
				'O', "gemEmerald",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.BioReactor, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.fermented_spider_eye,
				'S', "slimeball",
				'F', machineFrame,
				'O', Items.brick,
				'C', Items.sugar
					} );
		
		registerMachine(Machine.BioFuelGenerator, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.furnace,
				'S', Blocks.piston,
				'F', machineFrame,
				'O', Items.blaze_rod,
				'C', powerCoilSilver
					} );
		
		registerMachine(Machine.AutoDisenchanter, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.nether_brick,
				'S', Items.book,
				'F', machineFrame,
				'O', "gemDiamond",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.Slaughterhouse, new Object[] {
				"GIG",
				"SFS",
				"XCX",
				'G', "sheetPlastic",
				'S', invarSword,
				'X', invarAxe,
				'I', "gearInvar",
				'F', machineFrame,
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.MeatPacker, new Object[] {
				"GSG",
				"BFB",
				"BCB",
				'G', "sheetPlastic",
				'B', Blocks.brick_block,
				'S', Items.flint_and_steel,
				'F', machineFrame,
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.EnchantmentRouter, new Object[] {					
				"PBP",
				"SFS",
				"PSP",
				'P', "sheetPlastic",
				'B', Items.book,
				'S', Items.repeater,
				'F', machineFrame
					} );
		
		registerMachine(Machine.LaserDrill, new Object[] {
				"GFG",
				"CFC",
				"DHD",
				'G', "sheetPlastic",
				'D', "gemDiamond",
				'H', "glassHardened",
				'F', lamp,
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.LaserDrillPrecharger, new Object[] {
				"GSG",
				"HFH",
				"CDC",
				'G', "sheetPlastic",
				'D', "gemDiamond",
				'S', MineFactoryReloadedCore.pinkSlimeballItem,
				'H', "glassHardened",
				'F', lamp,
				'C', powerCoilElectrum
					} );
		
		registerMachine(Machine.AutoAnvil, new Object[] {
				"GGG",
				"AFA",
				"OCO",
				'G', "sheetPlastic",
				'A', Blocks.anvil,
				'F', machineFrame,
				'C', powerCoilGold,
				'O', "thermalexpansion:machineIron"
					} );
		
		registerMachine(Machine.BlockSmasher, new Object[] {
				"GPG",
				"HFH",
				"BCB",
				'G', "sheetPlastic",
				'P', Blocks.piston,
				'H', MineFactoryReloadedCore.factoryHammerItem,
				'B', Items.book,
				'F', machineFrame,
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.RedNote, new Object[] {
				"GNG",
				"CFC",
				'G', "sheetPlastic",
				'C', "cableRedNet",
				'N', Blocks.noteblock,
				'F', machineFrame
					} );
		
		registerMachine(Machine.AutoBrewer, new Object[] {
				"GBG",
				"CFC",
				"RPR",
				'G', "sheetPlastic",
				'C', conduitLiquid,
				'B', Items.brewing_stand,
				'R', Items.repeater,
				'F', machineFrame,
				'P', powerCoilGold
					} );
		
		registerMachine(Machine.FruitPicker, new Object[] {
				"GXG",
				"SFS",
				"OCO",
				'G', "sheetPlastic",
				'X', invarAxe,
				'S', Items.shears,
				'F', machineFrame,
				'C', powerCoilGold,
				'O', "thermalexpansion:machineTin"
					} );
		
		registerMachine(Machine.BlockPlacer, new Object[]
					{
				"GDG",
				"DMD",
				"GSG",
				'G', "sheetPlastic",
				'D', Blocks.dispenser,
				'S', powerCoilGold,
				'M', machineFrame,
					} );
		
		registerMachine(Machine.MobCounter, new Object[]
					{
				"GGG",
				"RCR",
				"SMS",
				'G', "sheetPlastic",
				'R', Items.repeater,
				'C', Items.comparator,
				'S', multimeter,
				'M', machineFrame,
					} );
		
		registerMachine(Machine.SteamTurbine, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', dynamoSteam,
				'S', Blocks.piston,
				'F', machineFrame,
				'O', "thermalexpansion:machineSilver",
				'C', powerCoilSilver
					} );
		
		registerMachine(Machine.ChunkLoader, new Object[] {
				"PEP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'T', tesseract,
				'E', cellResonant,
				'F', tesseractFrameEmpty,
				'O', "thermalexpansion:machineElectrum",
				'C', powerCoilElectrum
					} );
		if (MFRConfig.enableCheapCL.getBoolean(false))
		{
			registerMachine(Machine.ChunkLoader, new Object[] {
					"PEP",
					"TFT",
					"OCO",
					'P', "sheetPlastic",
					'T', tesseractFrameEmpty,
					'E', cellRedstone,
					'F', machineFrame,
					'O', "thermalexpansion:machineElectrum",
					'C', powerCoilElectrum
						} );
		}
		
		registerMachine(Machine.Fountain, new Object[] {
				"PBP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'B', Blocks.iron_bars,
				'T', tankBasic,
				'F', machineFrame,
				'O', "thermalexpansion:machineNickel",
				'C', powerCoilGold
					} );
		
		registerMachine(Machine.MobRouter, new Object[] {
				"PBP",
				"CFR",
				"OGO",
				'P', "sheetPlastic",
				'B', Blocks.iron_bars,
				'C', Machine.Chronotyper.getItemStack(),
				'R', Machine.ItemRouter.getItemStack(),
				'F', machineFrame,
				'O', "dyeOrange",
				'G', powerCoilGold
					} );
	}

	@Override
	protected void registerMiscItems()
	{
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
			'G', "ingotInvar",
			'L', "glass",
			'D', "thermalexpansion:machineEnderium",
			'N', Items.nether_star
				} ));
		
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
	
	@Override
	protected void registerRedNet()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 8), new Object[]
				{
			"PPP",
			"RRR",
			"PPP",
			'R', "dustRedstone",
			'P', "sheetPlastic",
				} ));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 1, 2), new Object[]
				{
			"nuggetElectrum",
			"nuggetElectrum",
			"nuggetElectrum",
			"dustRedstone",
			"dustRedstone",
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
				} ));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 6, 2), new Object[]
				{
			"ingotElectrum",
			"ingotElectrum",
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
			'R', "dustRedstone",
			'P', "sheetPlastic",
			'G', "glass",
			'I', "ingotIron",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetLogicBlock), new Object[]
				{
			"RDR",
			"LGL",
			"PHP",
			'H', new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 11),
			'P', "sheetPlastic",
			'G', "ingotGold",
			'L', "dustLapis",
			'D', "gemDiamond",
			'R', "dustRedstone",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 0), new Object[]
				{
			"RPR",
			"PGP",
			"RPR",
			'P', "sheetPlastic",
			'G', "ingotGold",
			'R', "dustRedstone",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 1), new Object[]
				{
			"GPG",
			"PCP",
			"RGR",
			'C', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 0),
			'P', "sheetPlastic",
			'G', "ingotGold",
			'R', "dustRedstone",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 2), new Object[]
				{
			"DPD",
			"RCR",
			"GDG",
			'C', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 1),
			'P', "sheetPlastic",
			'G', "ingotGold",
			'D', "gemDiamond",
			'R', "dustRedstone",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetMeterItem, 1, 0), new Object[]
				{
			" G",
			"PR",
			"PP",
			'P', "sheetPlastic",
			'G', "nuggetGold",
			'R', "dustRedstone",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetMemoryCardItem, 1, 0), new Object[]
				{
			"GGG",
			"PRP",
			"PPP",
			'P', "sheetPlastic",
			'G', "nuggetGold",
			'R', "dustRedstone",
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
}
