package powercrystals.minefactoryreloaded.modhelpers.forgemultiparts;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.NetworkMod;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

@Mod(modid = "MineFactoryReloaded|CompatForgeMicroblock", name = "MFR Compat: ForgeMicroblock", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class FMP
{
	@Mod.EventHandler
	public void Init(FMLInitializationEvent evt)
	{
		if (!Loader.isModLoaded("ForgeMicroblock"))
			return;
		try
		{
			addSubtypes(Item.itemsList[MineFactoryReloadedCore.factoryDecorativeBrickBlock.blockID]);
			addSubtypes(Item.itemsList[MineFactoryReloadedCore.factoryDecorativeStoneBlock.blockID]);
			addSubtypes(Item.itemsList[MineFactoryReloadedCore.factoryGlassBlock.blockID]);
			addSubtypes(Item.itemsList[MineFactoryReloadedCore.factoryRoadBlock.blockID]);
			if (MFRConfig.vanillaOverrideIce.getBoolean(true))
				sendComm(new ItemStack(Block.ice, 1, 1));
			sendComm(new ItemStack(MineFactoryReloadedCore.rubberWoodBlock, 1, 0));
		}
		catch (Throwable _)
		{
			ModContainer This = FMLCommonHandler.instance().findContainerFor(this);
			FMLLog.log(This.getModId(), Level.SEVERE, "There was a problem loading %s.", This.getName());
			_.printStackTrace();
		}
	}
	
	private void addSubtypes(Item item)
	{
		List<ItemStack> items = new LinkedList<ItemStack>();
		item.getSubItems(item.itemID, null, items);
		for (int i = items.size(); i --> 0; )
			sendComm(items.get(i));
	}
	
	private void sendComm(ItemStack data)
	{
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", data);
	}
}
