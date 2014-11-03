package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;
import powercrystals.minefactoryreloaded.block.fluid.BlockFactoryFluid;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		super.init();
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
		MineFactoryReloadedClient.init();
	}

	@Override
	public void movePlayerToCoordinates(EntityLivingBase e, double x, double y, double z)
	{
		e.setPositionAndUpdate(x, y, z);
	}

	@Override
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onPostTextureStitch(TextureStitchEvent.Post e)
	{
		setIcons("milk", MFRThings.milkLiquid);
		setIcons("sludge", MFRThings.sludgeLiquid);
		setIcons("sewage", MFRThings.sewageLiquid);
		setIcons("mobessence", MFRThings.essenceLiquid);
		setIcons("biofuel", MFRThings.biofuelLiquid);
		setIcons("meat", MFRThings.meatLiquid);
		setIcons("pinkslime", MFRThings.pinkSlimeLiquid);
		setIcons("chocolatemilk", MFRThings.chocolateMilkLiquid);
		setIcons("mushroomsoup", MFRThings.mushroomSoupLiquid);
		setIcons("steam", MFRThings.steamFluid);
	}

	private void setIcons(String name, BlockFactoryFluid block)
	{
		Fluid fluid = FluidRegistry.getFluid(name);
		if (fluid.getBlock().equals(block))
		{
			fluid.setIcons(block.getIcon(1, 0), block.getIcon(2, 0));
		}
		else
		{
			block.setIcons(fluid.getStillIcon(), fluid.getFlowingIcon());
		}
	}

	@SubscribeEvent
	public void clientLoggedIn(ClientConnectedToServerEvent evt)
	{
		MineFactoryReloadedClient.prcPages.clear();
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent player)
	{
		MineFactoryReloadedClient._areaTileEntities.clear();
	}
}
