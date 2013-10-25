package powercrystals.minefactoryreloaded.modhelpers.ae;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "MineFactoryReloaded|CompatAppliedEnergistics", name = "MFR Compat: Applied Energistics", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:AppliedEnergistics")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class AppliedEnergistics
{
	@EventHandler
	public static void load(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("AppliedEnergistics"))
		{
			FMLLog.warning("Applied Energistics missing - MFR Applied Energistics Compat not loading");
			return;
		}
		FMLInterModComms.sendMessage("AppliedEnergistics", "movabletile",
				"powercrystals.minefactoryreloaded.tile.base.TileEntityFactory");
		FMLInterModComms.sendMessage("AppliedEnergistics", "movabletile",
				"powercrystals.minefactoryreloaded.tile.conveyor.TileEntityConveyor");
	}
}