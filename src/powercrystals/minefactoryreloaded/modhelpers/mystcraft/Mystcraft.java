package powercrystals.minefactoryreloaded.modhelpers.mystcraft;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;

import net.minecraft.nbt.NBTTagCompound;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

@Mod(modid = "MineFactoryReloaded|CompatMystcraft", name = "MFR Compat: Mystcraft", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:Mystcraft")
public class Mystcraft
{
	private static final String lastUpdated = "for Mystcraft-uni-1.5.1-0.10.3.00, current release as of May 25 2013";
	
	@EventHandler
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void load(FMLInitializationEvent e)
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
		catch (Exception x)
		{
			System.out.println("Last updated for " + lastUpdated);
			x.printStackTrace();
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
