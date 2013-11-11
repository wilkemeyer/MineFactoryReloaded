package powercrystals.minefactoryreloaded.setup.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class ThermalExpansion extends Vanilla
{
	@Override
	protected void registerMachines()
	{
		if(!Loader.isModLoaded("ThermalExpansion"))
		{
			return;
		}
		try
		{
			ItemStack conduitLiquid = GameRegistry.findItemStack("ThermalExpansion", "conduitFluidOpaque", 1);
			ItemStack tankBasic = GameRegistry.findItemStack("ThermalExpansion", "tankBasic", 1);
			ItemStack lamp = GameRegistry.findItemStack("ThermalExpansion", "lamp", 1);
			ItemStack hardenedGlass = GameRegistry.findItemStack("ThermalExpansion", "hardenedGlass", 1);
			ItemStack machineFrame = GameRegistry.findItemStack("ThermalExpansion", "machineFrame", 1);
			ItemStack pneumaticServo = GameRegistry.findItemStack("ThermalExpansion", "pneumaticServo", 1);
			ItemStack powerCoilElectrum = GameRegistry.findItemStack("ThermalExpansion", "powerCoilElectrum", 1);
			ItemStack powerCoilGold = GameRegistry.findItemStack("ThermalExpansion", "powerCoilGold", 1);
			ItemStack powerCoilSilver = GameRegistry.findItemStack("ThermalExpansion", "powerCoilSilver", 1);
			ItemStack tesseractFrameFull = GameRegistry.findItemStack("ThermalExpansion", "tesseractFrameFull", 1);
			ItemStack strongBox = GameRegistry.findItemStack("ThermalExpansion", "strongboxReinforced", 1);
			ItemStack strongBoxBasic = GameRegistry.findItemStack("ThermalExpansion", "strongboxBasic", 1);
			
			registerMachine(Machine.Planter, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.flowerPot,
					'S', Block.pistonBase,
					'F', machineFrame,
					'O', "ingotCopper",
					'C', powerCoilGold,
						} );
			
			registerMachine(Machine.Fisher, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.fishingRod,
					'S', Item.bucketEmpty,
					'F', machineFrame,
					'O', "ingotIron",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Harvester, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.axeIron,
					'S', Item.shears,
					'F', machineFrame,
					'O', "ingotGold",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Rancher, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', conduitLiquid,
					'S', Item.shears,
					'F', machineFrame,
					'O', "ingotTin",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Fertilizer, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.glassBottle,
					'S', Item.leather,
					'F', machineFrame,
					'O', "ingotSilver",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Vet, new Object[] {
					"PTP",
					"TFT",
					"OCO",
					'P', "sheetPlastic",
					'T', MineFactoryReloadedCore.syringeEmptyItem,
					'F', machineFrame,
					'O', "ingotCopper",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.ItemCollector, 8, new Object[] {
					"P P",
					" F ",
					"PCP",
					'P', "sheetPlastic",
					'F', machineFrame,
					'C', Block.chest
						} );
			
			registerMachine(Machine.BlockBreaker, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', "gearInvar",
					'S', Item.pickaxeIron,
					'F', machineFrame,
					'O', "ingotIron",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.WeatherCollector, new Object[] {
					"PTP",
					" F ",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.bucketEmpty,
					'F', machineFrame,
					'O', "ingotTin",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.SludgeBoiler, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.bucketEmpty,
					'S', Block.furnaceIdle,
					'F', machineFrame,
					'O', "ingotIron",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Sewer, 4, new Object[] {
					"PTP",
					"SFS",
					"SQS",
					'P', "sheetPlastic",
					'T', Item.bucketEmpty,
					'S', Item.brick,
					'F', machineFrame,
					'Q', pneumaticServo,
						} );
			
			registerMachine(Machine.Composter, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.furnaceIdle,
					'S', Block.pistonBase,
					'F', machineFrame,
					'O', Item.brick,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Breeder, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.appleGold,
					'S', Item.goldenCarrot,
					'F', machineFrame,
					'O', new ItemStack(Item.dyePowder, 1, 5),
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Grinder, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.swordGold,
					'S', "gearTin",
					'F', machineFrame,
					'O', Item.book,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.AutoEnchanter, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.obsidian,
					'S', Item.book,
					'F', machineFrame,
					'O', Item.diamond,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Chronotyper, new Object[] {
					"PTP",
					"TFT",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.emerald,
					'F', machineFrame,
					'O', new ItemStack(Item.dyePowder, 1, 5),
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Ejector, 8, new Object[] {							
					"PTP",
					" F ",
					"OOO",
					'P', "sheetPlastic",
					'T', pneumaticServo,
					'F', machineFrame,
					'O', Item.redstone
						} );
			
			registerMachine(Machine.ItemRouter, 8, new Object[] {					
					"PTP",
					"SFS",
					"PSP",
					'P', "sheetPlastic",
					'T', Block.chest,
					'S', Item.redstoneRepeater,
					'F', machineFrame
						} );
			
			registerMachine(Machine.LiquidRouter, 8, new Object[] {					
					"PTP",
					"SFS",
					"PSP",
					'P', "sheetPlastic",
					'T', conduitLiquid,
					'S', Item.redstoneRepeater,
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
					'T', Block.workbench,
					'S', tankBasic,
					'F', machineFrame,
					'O', Item.book,
					'C', pneumaticServo
						} );
			
			registerMachine(Machine.LavaFabricator, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.obsidian,
					'S', Item.magmaCream,
					'F', machineFrame,
					'O', Item.blazeRod,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.OilFabricator, new Object[] {
					"PTP",
					"OFO",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.tnt,
					'F', machineFrame,
					'O', Block.obsidian,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.AutoJukebox, new Object[] {
					"PJP",
					" F ",
					" P ",
					'P', "sheetPlastic",
					'J', Block.jukebox,
					'F', machineFrame
						} );
			
			registerMachine(Machine.Unifier, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', "ingotCopper",
					'S', "ingotSilver",
					'F', machineFrame,
					'O', Item.comparator,
					'C', Item.book
						} );
			
			registerMachine(Machine.AutoSpawner, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.netherStalkSeeds,
					'S', Item.magmaCream,
					'F', machineFrame,
					'O', Item.emerald,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.BioReactor, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.fermentedSpiderEye,
					'S', Item.slimeBall,
					'F', machineFrame,
					'O', Item.brick,
					'C', Item.sugar
						} );
			
			registerMachine(Machine.BioFuelGenerator, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.furnaceIdle,
					'S', Block.pistonBase,
					'F', machineFrame,
					'O', Item.blazeRod,
					'C', powerCoilSilver
						} );
			
			registerMachine(Machine.AutoDisenchanter, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.netherBrick,
					'S', Item.book,
					'F', machineFrame,
					'O', Item.diamond,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Slaughterhouse, new Object[] {
					"GIG",
					"SFS",
					"XCX",
					'G', "sheetPlastic",
					'S', Item.swordGold,
					'X', Item.axeGold,
					'I', "gearInvar",
					'F', machineFrame,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.MeatPacker, new Object[] {
					"GSG",
					"BFB",
					"BCB",
					'G', "sheetPlastic",
					'B', Block.brick,
					'S', Item.flintAndSteel,
					'F', machineFrame,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.EnchantmentRouter, new Object[] {					
					"PBP",
					"SFS",
					"PSP",
					'P', "sheetPlastic",
					'B', Item.book,
					'S', Item.redstoneRepeater,
					'F', machineFrame
						} );
			
			registerMachine(Machine.LaserDrill, new Object[] {
					"GFG",
					"CFC",
					"DHD",
					'G', "sheetPlastic",
					'D', Item.diamond,
					'H', hardenedGlass,
					'F', lamp,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.LaserDrillPrecharger, new Object[] {
					"GSG",
					"HFH",
					"DCD",
					'G', "sheetPlastic",
					'D', Item.diamond,
					'S', MineFactoryReloadedCore.pinkSlimeballItem,
					'H', hardenedGlass,
					'F', lamp,
					'C', powerCoilElectrum
						} );
			
			registerMachine(Machine.AutoAnvil, new Object[] {
					"GAG",
					"AFA",
					" C ",
					'G', "sheetPlastic",
					'A', Block.anvil,
					'F', machineFrame,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.BlockSmasher, new Object[] {
					"GPG",
					"HFH",
					"BCB",
					'G', "sheetPlastic",
					'P', Block.pistonBase,
					'H', MineFactoryReloadedCore.factoryHammerItem,
					'B', Item.book,
					'F', machineFrame,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.RedNote, new Object[] {
					"GNG",
					"CFC",
					'G', "sheetPlastic",
					'C', MineFactoryReloadedCore.rednetCableBlock,
					'N', Block.music,
					'F', machineFrame
						} );
			
			registerMachine(Machine.AutoBrewer, new Object[] {
					"GBG",
					"CFC",
					"RCR",
					'G', "sheetPlastic",
					'C', conduitLiquid,
					'B', Item.brewingStand,
					'R', Item.redstoneRepeater,
					'F', machineFrame,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.FruitPicker, new Object[] {
					"GXG",
					"SFS",
					"SCS",
					'G', "sheetPlastic",
					'X', Item.axeGold,
					'S', Item.shears,
					'F', machineFrame,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.BlockPlacer, new Object[]
						{
					"GSG",
					"DDD",
					" M ",
					'G', "sheetPlastic",
					'D', Block.dispenser,
					'S', pneumaticServo,
					'M', machineFrame,
						} );
			
			registerMachine(Machine.MobCounter, new Object[]
						{
					"GGG",
					"RCR",
					"SMS",
					'G', "sheetPlastic",
					'R', Item.redstoneRepeater,
					'C', Item.comparator,
					'S', MineFactoryReloadedCore.spyglassItem,
					'M', machineFrame,
						} );
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
}//*/
