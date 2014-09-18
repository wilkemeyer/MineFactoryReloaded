package powercrystals.minefactoryreloaded.modhelpers.ae;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;

import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.setup.Machine;

@Mod(modid = "MineFactoryReloaded|CompatAppliedEnergistics", name = "MFR Compat: Applied Energistics", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:appliedenergistics2")
public class AppliedEnergistics
{
	@EventHandler
	public static void load(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("appliedenergistics2"))
		{
			return;
		}
		FMLInterModComms.sendMessage( "appliedenergistics2", "whitelist-spatial",
				"powercrystals.minefactoryreloaded.tile.base.TileEntityBase" );

		FMLInterModComms.sendMessage( "appliedenergistics2", "add-p2p-attunement-redstone",
				new ItemStack( MineFactoryReloadedCore.rednetCableBlock, 1, 0 ) );
		FMLInterModComms.sendMessage( "appliedenergistics2", "add-p2p-attunement-redstone",
				new ItemStack( MineFactoryReloadedCore.rednetCableBlock, 1, 1 ) );
		FMLInterModComms.sendMessage( "appliedenergistics2", "add-p2p-attunement-rf-power",
				new ItemStack( MineFactoryReloadedCore.rednetCableBlock, 1, 2 ) );
		FMLInterModComms.sendMessage( "appliedenergistics2", "add-p2p-attunement-rf-power",
				new ItemStack( MineFactoryReloadedCore.rednetCableBlock, 1, 3 ) );
		FMLInterModComms.sendMessage( "appliedenergistics2", "add-p2p-attunement-fluid",
				new ItemStack( MineFactoryReloadedCore.plasticPipeBlock, 1, 0 ) );
		FMLInterModComms.sendMessage( "appliedenergistics2", "add-p2p-attunement-fluid",
				Machine.LiquidRouter.getItemStack() );
		FMLInterModComms.sendMessage( "appliedenergistics2", "add-p2p-attunement-item",
				Machine.ItemRouter.getItemStack() );
		for (int i = 17; i --> 0; )
			FMLInterModComms.sendMessage( "appliedenergistics2", "add-p2p-attunement-item",
					new ItemStack( MineFactoryReloadedCore.conveyorBlock, 1, i ) );
	}
}