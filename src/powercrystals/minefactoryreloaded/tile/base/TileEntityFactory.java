package powercrystals.minefactoryreloaded.tile.base;

import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;

import cofh.api.inventory.IInventoryConnection;
import cofh.api.tileentity.IPortableData;
import cofh.asm.relauncher.Strippable;
import cofh.lib.util.position.IRotateableTile;
import com.google.common.base.Strings;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;
import powercrystals.minefactoryreloaded.core.HarvestAreaManager;
import powercrystals.minefactoryreloaded.core.IHarvestAreaContainer;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.setup.Machine;

@Strippable("buildcraft.api.transport.IPipeConnection")
public abstract class TileEntityFactory extends TileEntityBase
																implements IRotateableTile, IInventoryConnection, IPortableData,
																IHarvestAreaContainer, IPipeConnection {

	protected static class FactoryAreaManager extends HarvestAreaManager<TileEntityFactory> {

		public FactoryAreaManager(TileEntityFactory owner, int harvestRadius,
				int harvestAreaUp, int harvestAreaDown, float upgradeModifier, boolean usesBlocks) {

			super(owner, harvestRadius, harvestAreaUp, harvestAreaDown, upgradeModifier, usesBlocks);
		}
	}

	private EnumFacing _forwardDirection;
	private boolean _canRotate = false;

	private boolean _manageFluids = false;
	private boolean _manageSolids = false;

	private boolean _isActive = false, _prevActive;
	protected byte _activeSyncTimeout = 101;
	private long _lastActive = -100;
	private int _lastUpgrade = 0;

	protected int _rednetState;

	protected HarvestAreaManager<TileEntityFactory> _areaManager;
	protected Machine _machine;

	protected String _owner = "";

	protected TileEntityFactory(Machine machine) {

		this._machine = machine;
		_forwardDirection = EnumFacing.NORTH;
	}

	@Override
	public void validate() {

		super.validate();
		onRotate();
		if (worldObj.isRemote && hasHAM()) {
			MineFactoryReloadedClient.addTileToAreaList(this);
		}
	}

	@Override
	public void invalidate() {

		if (worldObj != null && worldObj.isRemote && hasHAM()) {
			MineFactoryReloadedClient.removeTileFromAreaList(this);
		}
		super.invalidate();
	}

	/**
	 * Used to create HarvestAreas for entity-interacting machines.
	 */
	protected static void createEntityHAM(TileEntityFactory owner) {

		createHAM(owner, 2, 2, 1, 1.0f, false);
	}

	/**
	 * Used to create HarvestAreas for block-interacting machines
	 */
	protected static void createHAM(TileEntityFactory owner, int harvestRadius) {

		createHAM(owner, harvestRadius, 0, 0, 1.0f, true);
	}

	protected static void createHAM(TileEntityFactory owner, int harvestRadius, int harvestAreaUp, int harvestAreaDown,
			boolean usesBlocks) {

		createHAM(owner, harvestRadius, harvestAreaUp, harvestAreaDown, 1.0f, usesBlocks);
	}

	protected static void createHAM(TileEntityFactory owner, int harvestRadius, int harvestAreaUp, int harvestAreaDown,
			float upgradeModifier, boolean usesBlocks) {

		owner._areaManager = new FactoryAreaManager(owner, harvestRadius, harvestAreaUp, harvestAreaDown,
				upgradeModifier, usesBlocks);
	}

	@Override
	public boolean hasHAM() {

		return getHAM() != null;
	}

	@Override
	public HarvestAreaManager<TileEntityFactory> getHAM() {

		return _areaManager;
	}

	public World getWorld() {

		return worldObj;
	}

	@Override
	public EnumFacing getDirectionFacing() {

		return _forwardDirection;
	}

	@Override
	public boolean canRotate() {

		return _canRotate;
	}

	@Override
	public boolean canRotate(EnumFacing axis) {

		return _canRotate;
	}

	protected void setCanRotate(boolean canRotate) {

		_canRotate = canRotate;
	}

	@Override
	public void rotate(EnumFacing axis) {

		if (canRotate())
			rotate(false);
	}

	public void rotate(boolean reverse) {

		if (worldObj != null && !worldObj.isRemote) {
			switch ((reverse ? _forwardDirection.getOpposite() : _forwardDirection).ordinal()) {
			case 2://NORTH:
				_forwardDirection = EnumFacing.EAST;
				break;
			case 5://EAST:
				_forwardDirection = EnumFacing.SOUTH;
				break;
			case 3://SOUTH:
				_forwardDirection = EnumFacing.WEST;
				break;
			case 4://WEST:
				_forwardDirection = EnumFacing.NORTH;
				break;
			default:
				_forwardDirection = EnumFacing.NORTH;
			}

			onRotate();
		}
	}

	@Override
	public void rotateDirectlyTo(int rotation) {

		EnumFacing p = _forwardDirection;
		_forwardDirection = EnumFacing.VALUES[rotation];
		if (worldObj != null && p != _forwardDirection) {
			onRotate();
		}
	}

	protected void onRotate() {

		if (!isInvalid() && worldObj.isBlockLoaded(pos)) {
			markForUpdate();
			MFRUtil.notifyNearbyBlocks(worldObj, pos, getBlockType());
			MFRUtil.notifyBlockUpdate(worldObj, pos);
		}
	}

	public EnumFacing getDropDirection() {

		if (canRotate())
			return getDirectionFacing().getOpposite();
		return EnumFacing.UP;
	}

	public EnumFacing[] getDropDirections() {

		return EnumFacing.VALUES;
	}

	public boolean isActive() {

		return _isActive;
	}

	public void setIsActive(boolean isActive) {

		if (_isActive != isActive & worldObj != null &&
				!worldObj.isRemote && _lastActive < worldObj.getTotalWorldTime()) {
			_lastActive = worldObj.getTotalWorldTime() + _activeSyncTimeout;
			_prevActive = _isActive;
			MFRUtil.notifyBlockUpdate(worldObj, pos); //TODO either remove this as unnecessary or do something with the timeout above,
			// perhaps this should have timeout for active==false only?
		}
		_isActive = isActive;
	}

	@Override
	public void update() {

		super.update();

		if (!worldObj.isRemote && _prevActive != _isActive && _lastActive < worldObj.getTotalWorldTime()) {
			_prevActive = _isActive;
			MFRUtil.notifyBlockUpdate(worldObj, pos);
		}
	}

	public void setOwner(String owner) {

		if (owner == null)
			owner = "";
		if (_owner == null || _owner.isEmpty())
			_owner = owner;
	}

	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return null;
	}

	public ContainerFactoryInventory getContainer(InventoryPlayer inventoryPlayer) {

		return null;
	}

	public String getGuiBackground() {

		if (_machine == null)
			return null;
		return _machine.getName().toLowerCase(Locale.US);
	}

	@Override
	public void markDirty() {

		if (worldObj != null && !worldObj.isRemote && hasHAM()) {
			HarvestAreaManager<TileEntityFactory> ham = getHAM();
			int u = ham.getUpgradeLevel();
			if (_lastUpgrade != u)
				Packets.sendToAllPlayersWatching(worldObj, pos, ham.getUpgradePacket());
			_lastUpgrade = u;
		}
		super.markDirty();
	}

	protected NBTTagCompound writePacketData(NBTTagCompound tag) {

		tag.setByte("r", (byte) _forwardDirection.ordinal());
		tag.setBoolean("a", _isActive);

		return super.writePacketData(tag);
	}

	protected void handlePacketData(NBTTagCompound tag) {

		rotateDirectlyTo(tag.getByte("r"));
		_prevActive = _isActive;
		_isActive = tag.getBoolean("a");
		if (_prevActive != _isActive)
			MFRUtil.notifyBlockUpdate(worldObj, pos);
		if (_lastActive < 0 && hasHAM()) {
			Packets.sendToServer(Packets.HAMUpdate, this);
		}
		_lastActive = 5;
	}

	public void markForUpdate() {

		_lastActive = 0;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {

		if (worldObj != null && _lastActive < worldObj.getTotalWorldTime()) {
			return super.getUpdatePacket();
		}
		return null;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {

		switch (pkt.getTileEntityType()) {
		case 0:
			super.onDataPacket(net, pkt);
			break;
		case 255:
			handleUpgradePacket(pkt);
			break;
		}
	}

	protected void handleUpgradePacket(SPacketUpdateTileEntity pkt) {

		if (hasHAM()) {
			getHAM().setUpgradeLevel(pkt.getNbtCompound().getInteger("_upgradeLevel"));
		}
	}

	@Override
	public String getDataType() {

		return _machine.getInternalName() + ".name";
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		super.writeToNBT(tag);
		tag.setInteger("rotation", getDirectionFacing().ordinal());
		if (!Strings.isNullOrEmpty(_owner))
			tag.setString("owner", _owner);
		tag.setBoolean("a", _isActive);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		if (tag.hasKey("rotation"))
			rotateDirectlyTo(tag.getInteger("rotation"));
		if (tag.hasKey("owner"))
			_owner = tag.getString("owner");
		_isActive = tag.getBoolean("a");
	}

	public void onRedNetChanged(EnumFacing side, int value) {

		_rednetState = value;
	}

	public int getRedNetOutput(EnumFacing side) {

		return 0;
	}

	// hoisted IMachine methods

	public void setManageFluids(boolean manageFluids) {

		_manageFluids = manageFluids;
	}

	public boolean manageFluids() {

		return _manageFluids;
	}

	public void setManageSolids(boolean manageSolids) {

		_manageSolids = manageSolids;
	}

	public boolean manageSolids() {

		return _manageSolids;
	}

	@Override
	public ConnectionType canConnectInventory(EnumFacing from) {

		return manageSolids() ? ConnectionType.FORCE : ConnectionType.DENY;
	}

	@Override
	@Strippable("buildcraft.api.transport.IPipeConnection")
	public ConnectOverride overridePipeConnection(PipeType type, EnumFacing with) {

		if (type == PipeType.FLUID)
			return manageFluids() ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
		if (type == PipeType.ITEM)
			return canConnectInventory(with).canConnect ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
		if (type == PipeType.STRUCTURE)
			return ConnectOverride.CONNECT;
		return ConnectOverride.DEFAULT;
	}
}
