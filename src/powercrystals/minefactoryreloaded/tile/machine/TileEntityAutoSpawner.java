package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.IMobSpawnHandler;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiAutoSpawner;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoSpawner;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.MFRFluids;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import javax.annotation.Nullable;

public class TileEntityAutoSpawner extends TileEntityFactoryPowered {

	protected static final int _spawnRange = 4;

	protected boolean _spawnExact = false;
	protected int _spawnCost = 0;
	protected Entity _spawn = null;
	protected ItemStack _lastSpawnStack = null;

	public TileEntityAutoSpawner() {

		super(Machine.AutoSpawner);
		setManageSolids(true);
		createHAM(this, _spawnRange, 0, 2, false);
		_areaManager.setOverrideDirection(EnumFacing.UP);
		_tanks[0].setLock(MFRFluids.getFluid("mob_essence"));
	}

	public boolean getSpawnExact() {

		return _spawnExact;
	}

	public void setSpawnExact(boolean spawnExact) {

		_spawn = null;
		_spawnExact = spawnExact;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiAutoSpawner(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerAutoSpawner getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerAutoSpawner(this, inventoryPlayer);
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

	protected int getSpawnCost() {

		return _spawnExact ? MFRConfig.autospawnerCostExact.getInt() : MFRConfig.autospawnerCostStandard.getInt();
	}

	protected int getSpawnCost(Entity e, String id) {

		int r = MFRRegistry.getBaseSpawnCost(id);

		if (e instanceof EntityLiving) {
			EntityLiving el = (EntityLiving) e;

			int t = Math.abs(el.experienceValue) + 1;
			r += t + t / 3;

			for (int j = 0; j < el.inventoryArmor.length; ++j) {
				if (el.inventoryArmor[j] != null && el.inventoryArmorDropChances[j] <= 1.0F) {
					r += 1 + 4;
				}
			}

			for (int k = 0; k < el.inventoryHands.length; ++k) {
				if (el.inventoryHands[k] != null && el.inventoryHandsDropChances[k] <= 1.0F) {
					r += 1 + 4;
				}
			}

			r = Math.max(r, 4);
		}

		return (int) ((Math.max(r - 1, 0) + 1) * 66.66666667f * getSpawnCost()) / 10;
	}

	@Override
	protected boolean activateMachine() {

		ItemStack item = getStackInSlot(0);
		if (item == null || !canInsertItem(0, item, null)) {
			setWorkDone(0);
			setIdleTicks(getIdleTicksMax());
			return false;
		}
		NBTTagCompound itemTag = item.getTagCompound();

		if (_spawn == null) {
			String entityID = itemTag.getString("id");
			boolean isBlackListed = MFRRegistry.getAutoSpawnerBlacklist().contains(entityID);
			blackList:
			if (!isBlackListed) {
				Class<?> e = (Class<?>) EntityList.NAME_TO_CLASS.get(entityID);
				if (e == null) {
					isBlackListed = true;
					break blackList;
				}
				for (Class<?> t : MFRRegistry.getAutoSpawnerClassBlacklist()) {
					if (t.isAssignableFrom(e)) {
						isBlackListed = true;
						break blackList;
					}
				}
			}
			if (isBlackListed) {
				setWorkDone(0);
				return false;
			}

			Entity spawnedEntity = _spawn = EntityList.createEntityByName(entityID, worldObj);

			if (!(spawnedEntity instanceof EntityLivingBase)) {
				_spawn = null;
				return false;
			}

			EntityLivingBase spawnedLiving = (EntityLivingBase) spawnedEntity;

			if (_spawnExact) {
				NBTTagCompound tag = itemTag.copy();
				spawnedLiving.readEntityFromNBT(tag);
				for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
					if (spawnedLiving instanceof EntityLiving)
						((EntityLiving) spawnedLiving).setDropChance(slot, Float.NEGATIVE_INFINITY);
				}
			}

			IMobSpawnHandler handler = MFRRegistry.getSpawnHandlers().get(spawnedLiving.getClass());

			if (!_spawnExact) {
				if (spawnedLiving instanceof EntityLiving)
					((EntityLiving) spawnedLiving).onInitialSpawn(worldObj.getDifficultyForLocation(pos), null);
				if (handler != null)
					handler.onMobSpawn(spawnedLiving);
			} else {
				if (handler != null)
					handler.onMobExactSpawn(spawnedLiving);
			}

			_spawnCost = getSpawnCost(_spawn, entityID);
		}

		if (getWorkDone() < getWorkMax()) {
			if (drain(10, false, _tanks[0]) == 10) {
				drain(10, true, _tanks[0]);
				setWorkDone(getWorkDone() + 1);
				return true;
			} else {
				return false;
			}
		} else {
			Entity spawnedEntity = _spawn;
			_spawn = null;

			if (!(spawnedEntity instanceof EntityLivingBase)) {
				return false;
			}

			EntityLivingBase spawnedLiving = (EntityLivingBase) spawnedEntity;

			double x = pos.getX() + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * _spawnRange;
			double y = pos.getY() + worldObj.rand.nextInt(3) - 1;
			double z = pos.getZ() + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * _spawnRange;

			spawnedLiving.setLocationAndAngles(x, y, z, worldObj.rand.nextFloat() * 360.0F, 0.0F);

			if (!worldObj.checkNoEntityCollision(spawnedLiving.getEntityBoundingBox()) ||
					!worldObj.getCollisionBoxes(spawnedLiving, spawnedLiving.getEntityBoundingBox()).isEmpty() ||
					(worldObj.containsAnyLiquid(spawnedLiving.getEntityBoundingBox()) != (spawnedLiving instanceof EntityWaterMob))) {
				// TODO: mob valid spawn location logic shifted to mob, but includes random shit too. need to review the logic
				setIdleTicks(10);
				return false;
			}

			worldObj.spawnEntityInWorld(spawnedLiving);
			worldObj.playEvent(2004, pos, 0);

			if (spawnedLiving instanceof EntityLiving) {
				((EntityLiving) spawnedLiving).spawnExplosionParticle();
				((EntityLiving) spawnedLiving).setCanPickUpLoot(false);
			}
			setWorkDone(0);
			return true;
		}
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		if (!internalChange && !UtilInventory.stacksEqual(_lastSpawnStack, _inventory[0])) {
			setWorkDone(0);
			setIdleTicks(getIdleTicksMax());
		}
		_lastSpawnStack = _inventory[0];
	}

	@Override
	public void setWorkDone(int w) {

		if (w == 0) {
			_spawn = null;
			_spawnCost = 0;
		}
		super.setWorkDone(w);
	}

	@SideOnly(Side.CLIENT)
	public void setWorkMax(int a) {

		_spawnCost = a;
	}

	@Override
	public int getWorkMax() {

		return _spawnCost;
	}

	@Override
	public int getIdleTicksMax() {

		return 7 * 20;
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(BUCKET_VOLUME * 4) };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, EnumFacing side) {

		return ItemSafariNet.isSafariNet(itemstack) &&
				!ItemSafariNet.isSingleUse(itemstack) &&
				!ItemSafariNet.isEmpty(itemstack);
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setBoolean("spawnExact", _spawnExact);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		setSpawnExact(tag.getBoolean("spawnExact"));
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		if (_spawnExact)
			tag.setBoolean("spawnExact", true);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		_spawnExact = tag.getBoolean("spawnExact");
	}

	@Override
	public boolean allowBucketFill(EnumFacing facing, ItemStack stack) {

		return true;
	}

	@Override
	protected boolean canDrainTank(EnumFacing facing, int index) {

		return false;
	}

	@Nullable
	@Override
	public FluidStack drain(EnumFacing facing, FluidStack resource, boolean doDrain) {

		return null;
	}

	@Nullable
	@Override
	public FluidStack drain(EnumFacing facing, int maxDrain, boolean doDrain) {

		return null;
	}

}
