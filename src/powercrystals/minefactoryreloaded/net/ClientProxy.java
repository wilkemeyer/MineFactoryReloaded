package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

public class ClientProxy extends CommonProxy implements IPlayerTracker
{
	@Override
	public void init()
	{
		super.init();
		MineFactoryReloadedClient.init();
		GameRegistry.registerPlayerTracker(this);
	}
	
	@Override
	public void movePlayerToCoordinates(EntityLivingBase e, double x, double y, double z)
	{
		e.setPositionAndUpdate(x, y, z);
	}
	
	@Override
	@ForgeSubscribe
	public void onPostTextureStitch(TextureStitchEvent.Post e)
	{
		setIcons("milk", MineFactoryReloadedCore.milkLiquid);
		setIcons("sludge", MineFactoryReloadedCore.sludgeLiquid);
		setIcons("sewage", MineFactoryReloadedCore.sewageLiquid);
		setIcons("mobessence", MineFactoryReloadedCore.essenceLiquid);
		setIcons("biofuel", MineFactoryReloadedCore.biofuelLiquid);
		setIcons("meat", MineFactoryReloadedCore.meatLiquid);
		setIcons("pinkslime", MineFactoryReloadedCore.pinkSlimeLiquid);
		setIcons("chocolatemilk", MineFactoryReloadedCore.chocolateMilkLiquid);
		setIcons("mushroomsoup", MineFactoryReloadedCore.mushroomSoupLiquid);
	}
	
	private void setIcons(String name, Block block)
	{
		Fluid fluid = FluidRegistry.getFluid(name);
		if (fluid.getBlockID() == block.blockID)
		{
			fluid.setIcons(block.getIcon(1, 0), block.getIcon(2, 0));
		}
	}

	@Override
	public void onPlayerLogin(EntityPlayer player) {
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		MineFactoryReloadedClient._areaTileEntities.clear();
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
	}
}
