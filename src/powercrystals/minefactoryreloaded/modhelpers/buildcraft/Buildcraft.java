package powercrystals.minefactoryreloaded.modhelpers.buildcraft;

import buildcraft.api.fuels.BuildcraftFuelRegistry;

import cofh.asm.relauncher.Strippable;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.CustomProperty;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.setup.MFRThings;

@Mod(modid = "MineFactoryReloaded|CompatBuildCraft",
		name = "MFR Compat: BuildCraft", version = MineFactoryReloadedCore.version,
		dependencies = "after:MineFactoryReloaded;after:BuildCraft|Core",
		customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class Buildcraft {

	private static String name(Block obj) {

		return Block.blockRegistry.getNameForObject(obj);
	}

	@Mod.EventHandler
	@Strippable("mod:BuildCraft|Core")
	public void init(FMLInitializationEvent evt) {

		for (Object[] q : new Object[][] { { 16, name(MFRThings.factoryDecorativeBrickBlock) },
				{ 12, name(MFRThings.factoryDecorativeStoneBlock) } })
			for (int i = (Integer) q[0]; i-- > 0;)
				FMLInterModComms.sendMessage("BuildCraft|Core", "add-facade", q[1] + "@" + i);
		String block = Block.blockRegistry.getNameForObject(MFRThings.factoryRoadBlock);
		FMLInterModComms.sendMessage("BuildCraft|Core", "add-facade", block + "@0");
		FMLInterModComms.sendMessage("BuildCraft|Core", "add-facade", block + "@1");
		FMLInterModComms.sendMessage("BuildCraft|Core", "add-facade", block + "@4");
	}

	@Mod.EventHandler
	@Strippable("api:BuildCraftAPI|fuels")
	private void postInit(FMLPostInitializationEvent evt) {

		try {
			if (BuildcraftFuelRegistry.fuel != null)
				BuildcraftFuelRegistry.fuel.addFuel(FluidRegistry.getFluid("biofuel"), 40, 15000);
		} catch (Throwable $) {
			ModContainer This = FMLCommonHandler.instance().findContainerFor(this);
			LogManager.getLogger(This.getModId()).log(Level.ERROR, "There was a problem loading " + This.getName(), $);
		}
	}

}
