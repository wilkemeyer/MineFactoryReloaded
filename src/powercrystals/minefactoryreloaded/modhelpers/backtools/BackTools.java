package powercrystals.minefactoryreloaded.modhelpers.backtools;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

import java.lang.reflect.Method;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.item.ItemFactoryHammer;
import powercrystals.minefactoryreloaded.item.ItemSafariNetLauncher;
import powercrystals.minefactoryreloaded.item.ItemSpyglass;

@Mod(modid = "MineFactoryReloaded|CompatBackTools", name = "MFR Compat: BackTools", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:mod_BackTools")
public class BackTools
{
	private static final String lastUpdated = "for Back Tools 1.5.1v2, current release as of May 20 2013";
	
	@SuppressWarnings({"rawtypes","unchecked"})
	@EventHandler
	public static void load(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("mod_BackTools"))
		{
			return;
		}
		try
		{
			Class modBackTools = Class.forName("mod_BackTools");
			/*
			 *  addBackItem(Class itemClass, int orientation, boolean flipped)
			 *  orientation is 0-3, and rotates counterclockwise by 90 deg * orientation
			 *  flipped true for vertical flipping of the texture  
			 */
			Method addBackItem = modBackTools.getMethod("addBackItem", Class.class, int.class, boolean.class);
			if(addBackItem != null)
			{
				addBackItem.invoke(modBackTools, ItemSafariNetLauncher.class, 2, true);
				addBackItem.invoke(modBackTools, ItemSpyglass.class, 1, true);
				addBackItem.invoke(modBackTools, ItemFactoryHammer.class, 1, true);
			}
		}
		catch (Exception x)
		{
			System.out.println("Last updated for " + lastUpdated);
			x.printStackTrace();
		}
	}
}
