package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.lib.util.helpers.FluidHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.RanchedItem;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityRancher extends TileEntityFactoryPowered {

	public TileEntityRancher() {

		super(Machine.Rancher);
		setManageSolids(true);
		createEntityHAM(this);
		setCanRotate(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerFactoryPowered(this, inventoryPlayer);
	}

	@Override
	protected boolean shouldPumpLiquid() {

		return true;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 400;
	}

	@Override
	public boolean activateMachine() {

		boolean didDrop = false;

		List<?> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, _areaManager.getHarvestArea().toAxisAlignedBB());

		for (Object o : entities) {
			EntityLivingBase e = (EntityLivingBase) o;
			if (MFRRegistry.getRanchables().containsKey(e.getClass())) {
				IFactoryRanchable r = MFRRegistry.getRanchables().get(e.getClass());
				List<RanchedItem> drops = r.ranch(worldObj, e, this);
				if (drops != null) {
					for (RanchedItem s : drops) {
						if (s.hasFluid()) {
							// whitelist fluids? multiple tanks?
							fillTank((FluidStack) s.getResult());
							didDrop = true;
							continue;
						}

						doDrop((ItemStack) s.getResult());
						didDrop = true;
					}
					if (didDrop) {
						setIdleTicks(20);
						return true;
					}
				}
			}
		}

		setIdleTicks(getIdleTicksMax());
		return false;
	}

	private int fillTank(FluidStack resource) {

		if (resource != null)
			for (FluidTankCore tank : getTanks())
				if (FluidHelper.isFluidEqualOrNull(tank.getFluid(), resource))
					return tank.fill(resource, true);
		return 0;
	}

	@Override
	public int getSizeInventory() {

		return 9;
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(4 * BUCKET_VOLUME) };
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new FactoryBucketableFluidHandler() {

				@Override
				public boolean allowBucketDrain(ItemStack stack) {

					return true;
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					return 0;
				}
			});
		}

		return super.getCapability(capability, facing);
	}
}
