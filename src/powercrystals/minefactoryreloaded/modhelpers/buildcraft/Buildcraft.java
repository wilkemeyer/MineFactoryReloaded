package powercrystals.minefactoryreloaded.modhelpers.buildcraft;

import buildcraft.api.fuels.IronEngineFuel;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.transport.IPipeTile;

import cofh.asm.relauncher.Strippable;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import org.apache.logging.log4j.Level;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

@Mod(modid = "MineFactoryReloaded|CompatBuildCraft", 
name = "MFR Compat: BuildCraft", version = MineFactoryReloadedCore.version,
dependencies = "after:MineFactoryReloaded;after:BuildCraft|Core")
@Strippable("buildcraft.api.gates.ITriggerProvider")
public class Buildcraft implements ITriggerProvider
{
	private static String name(Block obj)
	{
		return Block.blockRegistry.getNameForObject(obj);
	}
	@Mod.EventHandler
	public void init(FMLInitializationEvent evt)
	{
		if (!Loader.isModLoaded("BuildCraft|Core"))
			return;
		
		for (Object[] q : new Object[][]{ {16, name(MineFactoryReloadedCore.factoryDecorativeBrickBlock)},
									{12, name(MineFactoryReloadedCore.factoryDecorativeStoneBlock)}})
			for(int i = (Integer)q[0]; i --> 0; )
				FMLInterModComms.sendMessage("BuildCraft|Core", "add-facade", q[1] + "@" + i);
		String block = Block.blockRegistry.getNameForObject(MineFactoryReloadedCore.factoryRoadBlock);
		FMLInterModComms.sendMessage("BuildCraft|Core", "add-facade", block + "@0");
		FMLInterModComms.sendMessage("BuildCraft|Core", "add-facade", block + "@1");
		FMLInterModComms.sendMessage("BuildCraft|Core", "add-facade", block + "@4");
	}
	
	@Mod.EventHandler
	@Strippable({"buildcraft.api.gates.ITriggerProvider","buildcraft.api.gates.ITrigger"})
	private void postInit(FMLPostInitializationEvent evt)
	{
		try
		{
			IronEngineFuel.addFuel("biofuel", 4, 15000);
			
			isBackstuffed = new TriggerIsBackstuffed();
			isRunning = new TriggerIsRunning();
			isReversed = new TriggerIsReversed();
			
			ActionManager.registerTriggerProvider(this);
			ActionManager.registerTrigger(isBackstuffed);
			ActionManager.registerTrigger(isRunning);
			ActionManager.registerTrigger(isReversed);
		}
		catch (Throwable _)
		{
			ModContainer This = FMLCommonHandler.instance().findContainerFor(this);
			FMLLog.log(This.getModId(), Level.ERROR, "There was a problem loading %s.", This.getName());
			_.printStackTrace();
		}
	}

	@Override
	@Strippable("buildcraft.api.gates.ITriggerProvider")
	public LinkedList<ITrigger> getPipeTriggers(IPipeTile pipe)
	{
		return null;
	}

	@Strippable("buildcraft.api.gates.ITrigger")
	private static MFRBCTrigger isBackstuffed;
	@Strippable("buildcraft.api.gates.ITrigger")
	private static MFRBCTrigger isRunning;
	@Strippable("buildcraft.api.gates.ITrigger")
	private static MFRBCTrigger isReversed;

	@Override
	@Strippable("buildcraft.api.gates.ITriggerProvider")
	public LinkedList<ITrigger> getNeighborTriggers(Block block, TileEntity tile)
	{
		LinkedList<ITrigger> triggers = new LinkedList<ITrigger>();
		addTrigger(triggers, isBackstuffed, tile);
		addTrigger(triggers, isRunning, tile);
		addTrigger(triggers, isReversed, tile);
		return triggers;
	}

	@Strippable("buildcraft.api.gates.ITriggerProvider")
	private void addTrigger(LinkedList<ITrigger> triggers, MFRBCTrigger t, TileEntity tile)
	{
		if (t.canApplyTo(tile))
			triggers.add(t);
	}
}
