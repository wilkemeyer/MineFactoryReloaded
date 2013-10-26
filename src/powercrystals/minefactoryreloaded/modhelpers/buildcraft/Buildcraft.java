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

@Mod(modid = "MineFactoryReloaded|CompatBuildCraft", name = "MFR Compat: BuildCraft", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:BuildCraft|Core")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class Buildcraft implements IActionProvider, ITriggerProvider
{
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent evt)
	{
		if (!Loader.isModLoaded("BuildCraft|Core"))
			return;
		try
		{
			IronEngineFuel.addFuel("biofuel", 4, 15000);
			
			isBackstuffed = new TriggerIsBackstuffed();
			isRunning = new TriggerIsRunning();
			isReversed = new TriggerIsReversed();
			reverse = new ActionReverse();
			
			ActionManager.registerTriggerProvider(this);
			ActionManager.registerTrigger(isBackstuffed);
			ActionManager.registerTrigger(isRunning);
			ActionManager.registerTrigger(isReversed);
			ActionManager.registerActionProvider(this);
			ActionManager.registerAction(reverse);
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

	private static MFRBCTrigger isBackstuffed;
	private static MFRBCTrigger isRunning;
	private static MFRBCTrigger isReversed;
	public static MFRBCAction reverse;

	@Override
	public LinkedList<ITrigger> getNeighborTriggers(Block block, TileEntity tile)
	{
		LinkedList<ITrigger> triggers = new LinkedList<ITrigger>();
		addTrigger(triggers, isBackstuffed, tile);
		addTrigger(triggers, isRunning, tile);
		addTrigger(triggers, isReversed, tile);
		return triggers;
	}
	
	private void addTrigger(LinkedList<ITrigger> triggers, MFRBCTrigger t, TileEntity tile)
	{
		if (t.canApplyTo(tile))
			triggers.add(t);
	}

	@Override
	public LinkedList<IAction> getNeighborActions(Block block, TileEntity tile)
	{
		LinkedList<IAction> triggers = new LinkedList<IAction>();
		addAction(triggers, reverse, tile);
		return triggers;
	}
	
	private void addAction(LinkedList<IAction> triggers, MFRBCAction t, TileEntity tile)
	{
		if (t.canApplyTo(tile))
			triggers.add(t);
	}
}
