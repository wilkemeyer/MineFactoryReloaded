package powercrystals.minefactoryreloaded.modhelpers.mystcraft;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.CustomProperty;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;

import net.minecraft.nbt.NBTTagCompound;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

@Mod(modid = "MineFactoryReloaded|CompatMystcraft", name = "MFR Compat: Mystcraft", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:Mystcraft",
customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class Mystcraft
{

	@EventHandler
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void load(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("Mystcraft"))
		{
			return;
		}
		try
		{
			blackListFluid("mobessence");
			blackListFluid("biofuel");
			Class entityLinkbook = Class.forName("com.xcompwiz.mystcraft.entity.EntityLinkbook");
			MFRRegistry.registerAutoSpawnerBlacklistClass(entityLinkbook);
		}
		catch (Throwable $) {
			ModContainer This = FMLCommonHandler.instance().findContainerFor(this);
			LogManager.getLogger(This.getModId()).log(Level.ERROR, "There was a problem loading " + This.getName(), $);
		}
	}

	public static void blackListFluid(String FluidName){
		NBTTagCompound NBTMsg = new NBTTagCompound();
		NBTTagCompound fluidMsg = new NBTTagCompound();
		fluidMsg.setFloat("rarity", 0.0F);
		fluidMsg.setFloat("grammarweight", 0.0F);
		fluidMsg.setFloat("instabilityPerBlock", 1000F);
		fluidMsg.setString("fluidname", FluidName);
		NBTMsg.setTag("fluidsymbol", fluidMsg);
		FMLInterModComms.sendMessage("Mystcraft", "fluidsymbol", NBTMsg);
	}
}
