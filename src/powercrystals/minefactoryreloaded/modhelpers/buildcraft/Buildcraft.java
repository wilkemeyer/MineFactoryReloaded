package powercrystals.minefactoryreloaded.modhelpers.buildcraft;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import buildcraft.api.fuels.IronEngineFuel;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.IAction;
import buildcraft.api.gates.IActionProvider;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.transport.IPipe;

import java.util.LinkedList;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

@Mod(modid = "MineFactoryReloaded|CompatBuildCraft", name = "MFR Compat: BuildCraft", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:BuildCraft")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class Buildcraft implements IActionProvider, ITriggerProvider
{
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent evt)
	{
		if (!Loader.isModLoaded("BuildCraft"))
			return;
		try
		{
			IronEngineFuel.addFuel("biofuel", 4, 15000);
			
			ActionManager.registerActionProvider(this);
			ActionManager.registerTriggerProvider(this);
		}
		catch (Throwable _)
		{
			ModContainer This = FMLCommonHandler.instance().findContainerFor(this);
			FMLLog.log(This.getModId(), Level.SEVERE, "There was a problem loading %s.", This.getName());
			_.printStackTrace();
		}
	}

	@Override
	public LinkedList<ITrigger> getPipeTriggers(IPipe pipe)
	{
		return null;
	}

	@Override
	public LinkedList<ITrigger> getNeighborTriggers(Block block, TileEntity tile)
	{
		return null;
	}

	@Override
	public LinkedList<IAction> getNeighborActions(Block block, TileEntity tile)
	{
		return null;
	}
}
