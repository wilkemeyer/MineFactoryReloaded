package powercrystals.minefactoryreloaded.tile.base;

import java.util.ArrayList;
import java.util.List;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import powercrystals.core.util.Util;
import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.setup.Machine;
import universalelectricity.core.block.IElectrical;
import universalelectricity.core.electricity.ElectricityPack;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PerditionCalculator;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.transport.IPipeTile.PipeType;

/*
 * There are three pieces of information tracked - energy, work, and idle ticks.
 * 
 * Energy is stored and used when the machine activates. The energy stored must be >= energyActivation for the activateMachine() method to be called.
 * If activateMachine() returns true, energy will be drained.
 * 
 * Work is built up and then when at 100% something happens. This is tracked/used entirely by the derived class. If not used (f.ex. harvester), return max 1.
 * 
 * Idle ticks cause an artificial delay before activateMachine() is called again. Max should be the highest value the machine will use, to draw the
 * progress bar correctly.
 */

public abstract class TileEntityFactoryPowered extends TileEntityFactoryInventory implements IPowerReceptor, IEnergySink, IElectrical
{	
	public static final int energyPerEU = 4;
	public static final int energyPerMJ = 10;
	public static final int wPerEnergy = 7;
	
	private int _energyStored;
	protected int _energyActivation;
	
	protected int _energyRequiredThisTick = 0;
	
	private int _workDone;
	
	private int _idleTicks;
	
	protected List<ItemStack> failedDrops = null;
	private List<ItemStack> missedDrops = new ArrayList<ItemStack>();
	
	protected int _failedDropTicksMax = 20;
	private int _failedDropTicks = 0;
	
	// buildcraft-related fields
	
	private PowerHandler _powerProvider;
	
	// IC2-related fields
	
	private boolean _isAddedToIC2EnergyNet;
	private boolean _addToNetOnNextTick;
	
	// UE-related fields
	
	private int _ueBuffer;
	
	// constructors
	
	protected TileEntityFactoryPowered(Machine machine)
	{
		this(machine, machine.getActivationEnergyMJ());
	}
	
	protected TileEntityFactoryPowered(Machine machine, int activationCostMJ)
	{
		super(machine);
		_energyActivation = activationCostMJ * energyPerMJ;
		_powerProvider = new PowerHandler(this, PowerHandler.Type.MACHINE);
		configurePowerProvider();
		setIsActive(false);
	}
	
	// local methods
	
	protected void configurePowerProvider()
	{
		int activation = getMaxEnergyPerTick() / energyPerMJ;
		int maxReceived = Math.min(activation * 20, 1000);
		_powerProvider.configure(activation < 10 ? 1 : 10, maxReceived, 1, 1000);
		_powerProvider.setPerdition(MFRPerdition.DEFAULT);
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		_energyStored = Math.min(_energyStored, getEnergyStoredMax());
		
		if(worldObj.isRemote)
		{
			return;
		}
		
		if(_addToNetOnNextTick)
		{
			if(!worldObj.isRemote)
			{
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			}
			_addToNetOnNextTick = false;
			_isAddedToIC2EnergyNet = true;
		}
		
		int energyRequired = Math.min(getEnergyStoredMax() - getEnergyStored(), getMaxEnergyPerTick());
		
		if (energyRequired > 0)
		{
			PowerHandler pp = _powerProvider; 
			bcpower: if(pp != null)
			{
				int mjRequired = energyRequired / energyPerMJ;
				if (mjRequired <= 0) break bcpower;
				
				pp.update();
				
				if(pp.useEnergy(0, mjRequired, false) > 0)
				{
					int mjGained = (int)(pp.useEnergy(0, mjRequired, true) * energyPerMJ);
					_energyStored += mjGained;
					energyRequired -= mjGained;
				}
			}
		}
		
		_energyRequiredThisTick = energyRequired;
		
		setIsActive(_energyStored >= _energyActivation * 2);
		
		if (failedDrops != null)
		{
			if (_failedDropTicks < _failedDropTicksMax)
			{
				_failedDropTicks++;
				return;
			}
			_failedDropTicks = 0;
			if (!doDrop(failedDrops))
			{
				setIdleTicks(getIdleTicksMax());
				return;
			}
			failedDrops = null;
		}
		
		if(Util.isRedstonePowered(this))
		{
			setIdleTicks(getIdleTicksMax());
		}
		else if(_idleTicks > 0)
		{
			_idleTicks--;
		}
		else if(_energyStored >= _energyActivation)
		{
			if(activateMachine())
			{
				_energyStored -= _energyActivation;
			}
		}
	}

	public boolean doDrop(ItemStack drop)
	{
		drop = UtilInventory.dropStack(this, drop, this.getDropDirection());
		if (drop != null && drop.stackSize > 0)
		{
			if (failedDrops == null)
			{
				failedDrops = new ArrayList<ItemStack>();
			}
			failedDrops.add(drop);
		}
		return true;
	}
	
	public boolean doDrop(List<ItemStack> drops)
	{
		if (drops == null || drops.size() <= 0)
		{
			return true;
		}
		List<ItemStack> missed = missedDrops;
		missed.clear();
		for (int i = drops.size(); i --> 0; )
		{
			ItemStack dropStack = drops.get(i);
			dropStack = UtilInventory.dropStack(this, dropStack, this.getDropDirection());
			if (dropStack != null && dropStack.stackSize > 0)
			{
				missed.add(dropStack);
			}
		}
		
		if (missed.size() != 0)
		{
			if (drops != failedDrops)
			{
				if (failedDrops == null)
				{
					failedDrops = new ArrayList<ItemStack>();
				}
				failedDrops.addAll(missed);
			}
			else
			{
				failedDrops.clear();
				failedDrops.addAll(missed);
			}
			return false;
		}
		
		return true;
	}
	
	@Override
	public void validate()
	{
		super.validate();
		if(!_isAddedToIC2EnergyNet)
		{
			_addToNetOnNextTick = true;
		}
	}
	
	@Override
	public void invalidate()
	{
		if(_isAddedToIC2EnergyNet)
		{
			if(!worldObj.isRemote)
			{
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			}
			_isAddedToIC2EnergyNet = false;
		}
		super.invalidate();
	}
	
	protected abstract boolean activateMachine();
	
	@Override
	public void onBlockBroken()
	{
		super.onBlockBroken();
		if(_isAddedToIC2EnergyNet)
		{
			_isAddedToIC2EnergyNet = false;
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
		}
		if (failedDrops != null)
			inv: while (failedDrops.size() > 0) 
		{
			ItemStack itemstack = failedDrops.remove(0);
			if (itemstack == null)
			{
				continue;
			}
			float xOffset = worldObj.rand.nextFloat() * 0.8F + 0.1F;
			float yOffset = worldObj.rand.nextFloat() * 0.8F + 0.1F;
			float zOffset = worldObj.rand.nextFloat() * 0.8F + 0.1F;
			do
			{
				if(itemstack.stackSize <= 0)
				{
					continue inv;
				}
				int amountToDrop = worldObj.rand.nextInt(21) + 10;
				if(amountToDrop > itemstack.stackSize)
				{
					amountToDrop = itemstack.stackSize;
				}
				itemstack.stackSize -= amountToDrop;
				EntityItem entityitem = new EntityItem(worldObj, xCoord + xOffset, yCoord + yOffset, zCoord + zOffset, new ItemStack(itemstack.itemID, amountToDrop, itemstack.getItemDamage()));
				if(itemstack.getTagCompound() != null)
				{
					entityitem.getEntityItem().setTagCompound(itemstack.getTagCompound());
				}
				float motionMultiplier = 0.05F;
				entityitem.motionX = (float)worldObj.rand.nextGaussian() * motionMultiplier;
				entityitem.motionY = (float)worldObj.rand.nextGaussian() * motionMultiplier + 0.2F;
				entityitem.motionZ = (float)worldObj.rand.nextGaussian() * motionMultiplier;
				worldObj.spawnEntityInWorld(entityitem);
			} while(true);
		}
	}
	
	public int getMaxEnergyPerTick()
	{
		return _energyActivation;
	}
	
	public int getEnergyStored()
	{
		return _energyStored;
	}
	
	public abstract int getEnergyStoredMax();
	
	public void setEnergyStored(int energy)
	{
		_energyStored = energy;
	}
	
	public int getWorkDone()
	{
		return _workDone;
	}
	
	public abstract int getWorkMax();
	
	public void setWorkDone(int work)
	{
		_workDone = work;
	}
	
	public int getIdleTicks()
	{
		return _idleTicks;
	}
	
	public abstract int getIdleTicksMax();
	
	public void setIdleTicks(int ticks)
	{
		_idleTicks = ticks;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		tag.setInteger("energyStored", _energyStored);
		tag.setInteger("workDone", _workDone);
		tag.setInteger("ueBuffer", _ueBuffer);
		NBTTagCompound pp = new NBTTagCompound();
		_powerProvider.writeToNBT(pp);
		tag.setCompoundTag("powerProvider", pp);
		
		if (failedDrops != null)
		{
			NBTTagList nbttaglist = new NBTTagList();
			for (ItemStack item : failedDrops)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				item.writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
			tag.setTag("DropItems", nbttaglist);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		_energyStored = Math.min(tag.getInteger("energyStored"), getEnergyStoredMax());
		_workDone = Math.min(tag.getInteger("workDone"), getWorkMax());
		_ueBuffer = tag.getInteger("ueBuffer");
		if (tag.hasKey("powerProvider"))
		{
			_powerProvider.readFromNBT(tag.getCompoundTag("powerProvider"));
		}
		else // TODO: remove legacy code (below) in 2.8, losses from upgrades 2.6 or below acceptable
		{
			_powerProvider.readFromNBT(tag);
			tag.removeTag("latency");
			tag.removeTag("minEnergyReceived");
			tag.removeTag("maxEnergyReceived");
			tag.removeTag("maxStoreEnergy");
			tag.removeTag("minActivationEnergy");
			tag.removeTag("storedEnergy");
		}
		configurePowerProvider();

		if (tag.hasKey("DropItems"))
		{
			List<ItemStack> drops = new ArrayList<ItemStack>();
			NBTTagList nbttaglist = tag.getTagList("DropItems");
			for (int i = nbttaglist.tagCount(); i --> 0; )
			{
				NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
				ItemStack item = ItemStack.loadItemStackFromNBT(nbttagcompound1);
				if (item != null && item.stackSize > 0)
				{
					drops.add(item);
				}
			}
			if (drops.size() != 0)
			{
				failedDrops = drops;
			}
		}
	}
	
	public int getEnergyRequired()
	{
		return Math.min(getEnergyStoredMax() - getEnergyStored(), _energyRequiredThisTick);
	}
	
	// BC methods
	
	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side)
	{
		return _powerProvider.getPowerReceiver();
	}
	
	@Override
	public final void doWork(PowerHandler workProvider)
	{
	}

	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		if (type == PipeType.POWER && this.machine.getActivationEnergyMJ() > 0)
			return ConnectOverride.CONNECT;
		return super.overridePipeConnection(type, with);
	}
	
	// IC2 methods
	
	@Override
	public double demandedEnergyUnits()
	{
		return Math.max(getEnergyRequired() / energyPerEU, 0);
	}
	
	@Override
	public double injectEnergyUnits(ForgeDirection from, double amount)
	{
		double euInjected = Math.max(Math.min(demandedEnergyUnits(), amount), 0);
		double energyInjected = euInjected * energyPerEU;
		_energyStored += energyInjected;
		_energyRequiredThisTick -= energyInjected;
		return amount - euInjected;
	}
	
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
	{
		return true;
	}
	
	@Override
	public int getMaxSafeInput()
	{
		return 128;
	}
	
	// IC2-lf methods
	
	public int demandsEnergy()
	{
		return Math.max(getEnergyRequired() / energyPerEU, 0);
	}

	public int injectEnergy(Direction directionFrom, int amount)
	{
		int euInjected = Math.max(Math.min(demandsEnergy(), amount), 0);
		int energyInjected = euInjected * energyPerEU;
		_energyStored += energyInjected;
		_energyRequiredThisTick -= energyInjected;
		return amount - euInjected;
	}
	
	public boolean acceptsEnergyFrom(TileEntity tile, Direction side)
	{
		return true;
	}
	
	// UE Methods
	
	@Override
	public float getVoltage()
	{
		return 120;
	}
	
	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return true;
	}

	@Override
	public float receiveElectricity(ForgeDirection from, ElectricityPack powerPack, boolean doReceive) {
		int energyRequired = getEnergyRequired();
		int buff = _ueBuffer;
		buff += powerPack.getWatts();
		
		int energyFromUE = Math.min(buff / wPerEnergy, energyRequired);
		energyRequired -= energyFromUE;
		buff -= (energyFromUE * wPerEnergy);
		if (doReceive)
		{
			_energyStored += energyFromUE;
			_ueBuffer = buff;
			_energyRequiredThisTick -= energyFromUE;
		}
		return energyFromUE * wPerEnergy;
	}

	@Override
	public float getRequest(ForgeDirection direction) {
		return Math.max(getEnergyRequired() * wPerEnergy, 0);
	}

	@Override
	public ElectricityPack provideElectricity(ForgeDirection from, ElectricityPack request, boolean doProvide) {
		return null;
	}

	@Override
	public float getProvide(ForgeDirection direction) {
		return 0;
	}
	
	// BC PerditionCalculator compat
	public static class MFRPerdition extends PerditionCalculator
	{
		public static final MFRPerdition DEFAULT = new MFRPerdition();
		@Override
		public float applyPerdition(PowerHandler powerHandler, float current, long ticksPassed)
		{
			return current;
		}
	}
}
