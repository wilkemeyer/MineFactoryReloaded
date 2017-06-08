package powercrystals.minefactoryreloaded.tile.machine;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.ReplacementBlock;
import powercrystals.minefactoryreloaded.core.Area;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiUpgradeable;
import powercrystals.minefactoryreloaded.gui.container.ContainerUpgradeable;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class TileEntityFruitPicker extends TileEntityFactoryPowered {

	private Random _rand;

	private List<BlockPos> _treeBlocks;
	private int _currentBlock;

	public TileEntityFruitPicker() {

		super(Machine.FruitPicker);
		createHAM(this, 1);
		_rand = new Random();
		setManageSolids(true);
		setCanRotate(true);
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

	@Override
	public ContainerUpgradeable getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerUpgradeable(this, inventoryPlayer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiUpgradeable(getContainer(inventoryPlayer), this);
	}

	@Override
	protected boolean activateMachine() {

		BlockPos targetCoords = getNextTree();

		if (targetCoords == null) {
			setIdleTicks(getIdleTicksMax());
			return false;
		}

		IBlockState targetState = worldObj.getBlockState(targetCoords);
		Block harvestedBlock = targetState.getBlock();

		IFactoryFruit harvestable = MFRRegistry.getFruits().get(harvestedBlock);

		List<ItemStack> drops = harvestable.getDrops(worldObj, _rand,
			targetCoords);

		ReplacementBlock replacement = harvestable.getReplacementBlock(worldObj,
			targetCoords);

		harvestable.prePick(worldObj, targetCoords);

		if (replacement == null) {
			if (!worldObj.setBlockToAir(targetCoords))
				return false;
			if (MFRConfig.playSounds.getBoolean(true)) {
				worldObj.playEvent(null, 2001, targetCoords, Block.getStateId(targetState));
			}
		} else {
			if (!replacement.replaceBlock(worldObj, targetCoords, null))
				return false;
		}

		doDrop(drops);

		// TODO: sludge?

		harvestable.postPick(worldObj, targetCoords);

		return true;
	}

	private BlockPos getNextTree() {

		BlockPos bp = _areaManager.getNextBlock();
		if (!worldObj.isBlockLoaded(bp)) {
			return null;
		}

		Block search = worldObj.getBlockState(bp).getBlock();

		if (!MFRRegistry.getFruitLogBlocks().contains(search)) {
			IFactoryFruit f = MFRRegistry.getFruits().get(search);
			return f != null && f.canBePicked(worldObj, bp) ? bp : null;
		}

		BlockPos temp = getNextTreeSegment(bp);
		if (temp != null)
			_areaManager.rewindBlock();

		return temp;
	}

	private BlockPos getNextTreeSegment(BlockPos pos) {

		Block block;

		if (_treeBlocks == null || _currentBlock >= _treeBlocks.size()) {
			int lowerBound = 0;
			int upperBound = MFRConfig.fruitTreeSearchMaxVertical.getInt();

			_currentBlock = 0;
			_treeBlocks = (new Area(pos, MFRConfig.fruitTreeSearchMaxHorizontal.getInt(), lowerBound, upperBound)).getPositionsBottomFirst();
		}

		Map<Block, IFactoryFruit> fruits = MFRRegistry.getFruits();
		while (_currentBlock < _treeBlocks.size()) {
			BlockPos bp = _treeBlocks.get(_currentBlock);
			block = worldObj.getBlockState(bp).getBlock();
			IFactoryFruit fruit = fruits.containsKey(block) ? fruits.get(block) : null;

			if (fruit != null && fruit.canBePicked(worldObj, bp))
				return bp;

			_currentBlock++;;
		}
		return null;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 5;
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
	public int getUpgradeSlot() {

		return 0;
	}

	@Override
	public EnumFacing getDropDirection() {

		return getDirectionFacing().getOpposite();
	}
}
