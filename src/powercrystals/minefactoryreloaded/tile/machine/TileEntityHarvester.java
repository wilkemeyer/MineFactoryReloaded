package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.core.Area;
import powercrystals.minefactoryreloaded.core.HarvestMode;
import powercrystals.minefactoryreloaded.core.IHarvestManager;
import powercrystals.minefactoryreloaded.core.TreeHarvestManager;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiHarvester;
import powercrystals.minefactoryreloaded.gui.container.ContainerHarvester;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class TileEntityHarvester extends TileEntityFactoryPowered {

	private static boolean skip = false;
	private static Map<String, Boolean> DEFAULT_SETTINGS;
	static {

		HashMap<String, Boolean> _settings = new HashMap<String, Boolean>();
		_settings.put("silkTouch", false);
		_settings.put("harvestSmallMushrooms", false);
		_settings.put("playSounds", MFRConfig.playSounds.getBoolean(true));
		_settings.put("isHarvestingTree", false);
		DEFAULT_SETTINGS = java.util.Collections.unmodifiableMap(_settings);
	}

	private Map<String, Boolean> _settings;
	private Map<String, Boolean> _immutableSettings;

	private Random _rand;

	private IHarvestManager _treeManager;
	private BlockPos _lastTree;

	public TileEntityHarvester() {

		super(Machine.Harvester);
		createHAM(this, 1);
		setManageSolids(true);

		_settings = new HashMap<String, Boolean>();
		_settings.putAll(DEFAULT_SETTINGS);
		_immutableSettings = java.util.Collections.unmodifiableMap(_settings);

		_rand = new Random();
		setCanRotate(true);

		skip = MFRConfig.harvesterSkip.getBoolean(false);
	}

	@Override
	public void onChunkUnload() {

		super.onChunkUnload();
		if (_treeManager != null)
			_treeManager.free();
		_lastTree = null;
	}

	@Override
	public void validate() {

		super.validate();
		if (!worldObj.isRemote) {
			createHAM(this, 1);
			onFactoryInventoryChanged();
			if (_treeManager != null && _areaManager.getHarvestArea().contains(_treeManager.getOrigin())) {
				_treeManager.setWorld(worldObj);
			} else {
				_treeManager = new TreeHarvestManager(worldObj,
						new Area(pos, 0, 0, 0),
						HarvestMode.FruitTree, _immutableSettings);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiHarvester(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerHarvester getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerHarvester(this, inventoryPlayer);
	}

	public Map<String, Boolean> getSettings() {

		return _settings;
	}

	public Map<String, Boolean> getImmutableSettings() {

		return _immutableSettings;
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

		return 5 + getExtraIdleTime(10);
	}

	protected int getExtraIdleTime(int additionalDelay) {

		return (_tanks[0].getFluidAmount() * additionalDelay / _tanks[0].getCapacity());
	}

	@Override
	public boolean activateMachine() {

		BlockPos target = getNextHarvest();

		if (target == null) {
			setIdleTicks(getIdleTicksMax());
			return false;
		}

		IBlockState state = worldObj.getBlockState(target);
		Block harvestedBlock = state.getBlock();

		IFactoryHarvestable harvestable = MFRRegistry.getHarvestables().get(harvestedBlock);

		List<ItemStack> drops = harvestable.getDrops(worldObj, _rand, _immutableSettings, target);

		harvestable.preHarvest(worldObj, target);

		if (drops instanceof ArrayList) {
			ForgeEventFactory.fireBlockHarvesting(drops, worldObj, target, state, 0,
				1f, _settings.get("silkTouch"), null);
		}

		if (harvestable.breakBlock()) {
			if (!worldObj.setBlockState(target, Blocks.AIR.getDefaultState(), 2))
				return false;
			if (_settings.get("playSounds") == Boolean.TRUE) {
				worldObj.playEvent(null, 2001, target, Block.getStateId(state));
			}
		}

		setIdleTicks(getExtraIdleTime(10));

		doDrop(drops);
		_tanks[0].fill(FluidRegistry.getFluidStack("sludge", 10), true);

		harvestable.postHarvest(worldObj, target);

		return true;
	}

	private BlockPos getNextHarvest() {

		if (!_treeManager.getIsDone())
			return getNextTreeSegment(_lastTree, false);
		BlockPos bp = _areaManager.getNextBlock();
		_lastTree = null;
		if (skip) {
			int extra = getExtraIdleTime(10);
			if (extra > 0 && extra > _rand.nextInt(15))
				return null;
		}
		if (!worldObj.isBlockLoaded(bp)) {
			return null;
		}

		Block search = worldObj.getBlockState(bp).getBlock();

		if (!MFRRegistry.getHarvestables().containsKey(search)) {
			_lastTree = null;
			return null;
		}

		_settings.put("isHarvestingTree", false);

		IFactoryHarvestable harvestable = MFRRegistry.getHarvestables().get(search);
		HarvestType type = harvestable.getHarvestType();
		if (type == HarvestType.Gourd || harvestable.canBeHarvested(worldObj, _immutableSettings, bp)) {
			switch (type) {
			case Gourd:
				return getNextAdjacent(bp, harvestable);
			case Column:
			case LeaveBottom:
				return getNextVertical(bp, type == HarvestType.Column ? 0 : 1, harvestable);
			case Tree:
			case TreeFlipped:
			case TreeLeaf:
				return getNextTreeSegment(bp, type == HarvestType.TreeFlipped);
			case TreeFruit:
			case Normal:
				return bp;
			}
		}
		return null;
	}

	private BlockPos getNextAdjacent(BlockPos pos, IFactoryHarvestable harvestable) {

		for (EnumFacing side : EnumFacing.HORIZONTALS) {
			BlockPos offsetPos = pos.offset(side);
			if (worldObj.isBlockLoaded(offsetPos) && harvestable.canBeHarvested(worldObj, _immutableSettings, offsetPos))
				return offsetPos;
		}
		return null;
	}

	private BlockPos getNextVertical(BlockPos pos, int startOffset, IFactoryHarvestable harvestable) {

		int highestBlockOffset = -1;
		int maxBlockOffset = MFRConfig.verticalHarvestSearchMaxVertical.getInt();

		Block plant = harvestable.getPlant();
		for (int currentYoffset = startOffset; currentYoffset < maxBlockOffset; ++currentYoffset) {
			BlockPos offsetPos = pos.offset(EnumFacing.UP, currentYoffset);
			Block block = worldObj.getBlockState(offsetPos).getBlock();
			if (!block.equals(plant) ||
					!harvestable.canBeHarvested(worldObj, _immutableSettings, offsetPos))
				break;

			highestBlockOffset = currentYoffset;
		}

		if (highestBlockOffset >= 0)
			return pos.offset(EnumFacing.UP, highestBlockOffset);

		return null;
	}

	private BlockPos getNextTreeSegment(BlockPos pos, boolean treeFlipped) {

		Block block;
		_settings.put("isHarvestingTree", true);

		if (!pos.equals(_lastTree) || _treeManager.getIsDone()) {
			int lowerBound = 0;
			int upperBound = MFRConfig.treeSearchMaxVertical.getInt();
			if (treeFlipped) {
				lowerBound = upperBound;
				upperBound = 0;
			}

			_lastTree = new BlockPos(pos);

			Area a = new Area(_lastTree, MFRConfig.treeSearchMaxHorizontal.getInt(), lowerBound, upperBound);

			_treeManager.reset(worldObj, a,
				treeFlipped ? HarvestMode.HarvestTreeInverted : HarvestMode.HarvestTree,
				_immutableSettings);
		}

		Map<Block, IFactoryHarvestable> harvestables = MFRRegistry.getHarvestables();
		while (!_treeManager.getIsDone()) {
			BlockPos bp = _treeManager.getNextBlock();
			_treeManager.moveNext();
			if (!worldObj.isBlockLoaded(bp)) {
				return null;
			}
			block = worldObj.getBlockState(bp).getBlock();

			if (harvestables.containsKey(block)) {
				IFactoryHarvestable obj = harvestables.get(block);
				HarvestType t = obj.getHarvestType();
				if (t == HarvestType.Tree | t == HarvestType.TreeFlipped |
						t == HarvestType.TreeLeaf | t == HarvestType.TreeFruit)
					if (obj.canBeHarvested(worldObj, _immutableSettings, bp))
						return bp;
			}
		}
		return null;
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(4 * BUCKET_VOLUME) };
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		NBTTagCompound list = new NBTTagCompound();
		for (Entry<String, Boolean> setting : _settings.entrySet()) {
			String key = setting.getKey();
			if ("playSounds" == key || "isHarvestingTree" == key)
				continue;
			list.setBoolean(key, setting.getValue() == Boolean.TRUE);
		}
		tag.setTag("harvesterSettings", list);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		NBTTagCompound list = (NBTTagCompound) tag.getTag("harvesterSettings");
		if (list != null) {
			for (String s : _settings.keySet()) {
				if ("playSounds".equals(s))
					continue;
				boolean b = list.getBoolean(s);
				_settings.put(s.intern(), b);
			}
		}
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		NBTTagCompound list = new NBTTagCompound();
		for (Entry<String, Boolean> setting : _settings.entrySet()) {
			String key = setting.getKey();
			if ("playSounds" == key | "isHarvestingTree" == key
					|| DEFAULT_SETTINGS.get(key) == setting.getValue())
				continue;
			list.setBoolean(key, setting.getValue() == Boolean.TRUE);
		}
		if (!list.hasNoTags())
			tag.setTag("harvesterSettings", list);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		tag = super.writeToNBT(tag);

		_treeManager.writeToNBT(tag);
		tag.setInteger("bpos", _areaManager.getPosition());

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		NBTTagCompound list = (NBTTagCompound) tag.getTag("harvesterSettings");
		if (list != null) {
			for (String s : _settings.keySet()) {
				if ("playSounds".equals(s))
					continue;
				boolean b = list.getBoolean(s);
				_settings.put(s.intern(), b);
			}
		}
		if (_treeManager != null)
			_treeManager.free();
		_treeManager = new TreeHarvestManager(tag, _immutableSettings);
		if (!_treeManager.getIsDone())
			_lastTree = _treeManager.getOrigin();
		_areaManager.getHarvestArea();
		_areaManager.setPosition(tag.getInteger("bpos"));
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

	@Override
	public int getStartInventorySide(EnumFacing side) {

		return 0;
	}

	@Override
	public int getSizeInventorySide(EnumFacing side) {

		return 0;
	}

	@Override
	public int getUpgradeSlot() {

		return 0;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, EnumFacing side) {

		return slot == 0 && isUsableAugment(itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing side) {

		return false;
	}

	@Override
	public boolean allowBucketDrain(EnumFacing facing, ItemStack stack) {

		return true;
	}

	@Override
	protected boolean canFillTank(EnumFacing facing, int index) {

		return false;
	}

	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		return 0;
	}

}
