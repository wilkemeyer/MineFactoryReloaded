package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import cofh.util.position.Area;
import cofh.util.position.BlockPosition;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.core.HarvestAreaManager;
import powercrystals.minefactoryreloaded.core.HarvestMode;
import powercrystals.minefactoryreloaded.core.IHarvestManager;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.SideOffset;
import powercrystals.minefactoryreloaded.core.TreeHarvestManager;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiHarvester;
import powercrystals.minefactoryreloaded.gui.container.ContainerHarvester;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityHarvester extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	private Map<String, Boolean> _settings;
	private Map<String, Boolean> _immutableSettings;
	
	private Random _rand;
	
	private IHarvestManager _treeManager;
	private BlockPosition _lastTree;
	
	public TileEntityHarvester()
	{
		super(Machine.Harvester);
		_areaManager = new HarvestAreaManager(this, 1, 0, 0);
		setManageSolids(true);
		
		_settings = new HashMap<String, Boolean>();
		_settings.put("silkTouch", false);
		_settings.put("harvestSmallMushrooms", false);
		_settings.put("playSounds", MFRConfig.playSounds.getBoolean(true));
		_immutableSettings = java.util.Collections.unmodifiableMap(_settings);
		
		_rand = new Random();
		setCanRotate(true);
	}
	
	@Override
	public void onChunkUnload()
	{
		super.onChunkUnload();
		if (_treeManager != null)
			_treeManager.free();
		_lastTree = null;
	}
	
	@Override
	public void validate()
	{
		super.validate();
		_areaManager = new HarvestAreaManager(this, 1, 0, 0);
		onFactoryInventoryChanged();
		if (!worldObj.isRemote)
		{
			if (_treeManager != null && _areaManager.getHarvestArea().contains(_treeManager.getOrigin()))
			{
				_treeManager.setWorld(worldObj);
			}
			else
			{
				_treeManager = new TreeHarvestManager(worldObj,
						new Area(new BlockPosition(this),0,0,0),
						HarvestMode.FruitTree, _immutableSettings);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiHarvester(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerHarvester getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerHarvester(this, inventoryPlayer);
	}
	
	public Map<String, Boolean> getSettings()
	{
		return _settings;
	}
	
	public Map<String, Boolean> getImmutableSettings()
	{
		return _immutableSettings;
	}
	
	@Override
	protected boolean shouldPumpLiquid()
	{
		return true;
	}
	
	@Override
	public int getWorkMax()
	{
		return 1;
	}
	
	@Override
	public int getIdleTicksMax()
	{
		return 5;
	}
	
	@Override
	protected void onFactoryInventoryChanged()
	{
		_areaManager.updateUpgradeLevel(_inventory[0]);
	}
	
	@Override
	public boolean activateMachine()
	{
		BlockPosition targetCoords = getNextHarvest();
		
		if (targetCoords == null)
		{
			setIdleTicks(getIdleTicksMax());
			return false;
		}
		_settings.put("playSounds", MFRConfig.playSounds.getBoolean(true));
		
		Block harvestedBlock = worldObj.getBlock(targetCoords.x, targetCoords.y, targetCoords.z);
		int harvestedBlockMetadata = worldObj.getBlockMetadata(targetCoords.x,
															targetCoords.y, targetCoords.z);
		
		IFactoryHarvestable harvestable = MFRRegistry.getHarvestables().
											get(harvestedBlock);
		
		harvestable.preHarvest(worldObj, targetCoords.x, targetCoords.y, targetCoords.z);
		
		List<ItemStack> drops = harvestable.getDrops(worldObj, _rand, _immutableSettings,
													targetCoords.x, targetCoords.y, targetCoords.z);
		
		if(harvestable.breakBlock())
		{
			if (!worldObj.setBlock(targetCoords.x, targetCoords.y, targetCoords.z, Blocks.air, 0, 2))
				return false;
			if(_settings.get("playSounds"))
			{
				worldObj.playAuxSFXAtEntity(null, 2001, targetCoords.x,
						targetCoords.y, targetCoords.z,
						Block.getIdFromBlock(harvestedBlock) + (harvestedBlockMetadata << 12));
			}
		}
		
		doDrop(drops);
		
		harvestable.postHarvest(worldObj, targetCoords.x, targetCoords.y, targetCoords.z);
		
		_tanks[0].fill(FluidRegistry.getFluidStack("sludge", 10), true);
		
		return true;
	}
	
	private BlockPosition getNextHarvest()
	{
		if (!_treeManager.getIsDone())
			return getNextTreeSegment(_lastTree, false);
		BlockPosition bp = _areaManager.getNextBlock();
		_lastTree = null;
		
		Block search = worldObj.getBlock(bp.x, bp.y, bp.z);
		
		if(!MFRRegistry.getHarvestables().containsKey(search))
		{
			_lastTree = null;
			return null;
		}
		
		IFactoryHarvestable harvestable = MFRRegistry.getHarvestables().get(search);
		if(harvestable.canBeHarvested(worldObj, _immutableSettings, bp.x, bp.y, bp.z))
		{
			HarvestType type = harvestable.getHarvestType(); 
			switch (type)
			{
			case Gourd:
				return getNextAdjacent(bp.x, bp.y, bp.z, harvestable);
			case Column:
			case LeaveBottom:
				return getNextVertical(bp.x, bp.y, bp.z, type == HarvestType.Column ? 0 : 1, harvestable);
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
	
	private BlockPosition getNextAdjacent(int x, int y, int z, IFactoryHarvestable harvestable)
	{
		for (SideOffset side : SideOffset.SIDES)
		{
			int X = x + side.offsetX, Y = y +  side.offsetY, Z = z + side.offsetX;
			if (harvestable.canBeHarvested(worldObj, _immutableSettings, X, Y, Z))
				return new BlockPosition(x, y, z);
		}
		return null;
	}
	
	private BlockPosition getNextVertical(int x, int y, int z, int startOffset, IFactoryHarvestable harvestable)
	{
		int highestBlockOffset = -1;
		int maxBlockOffset = MFRConfig.verticalHarvestSearchMaxVertical.getInt();
		
		Block plant = harvestable.getPlant();
		for (int currentYoffset = startOffset; currentYoffset < maxBlockOffset; ++currentYoffset)
		{
			Block block = worldObj.getBlock(x, y + currentYoffset, z);
			if (!block.equals(plant) ||
					!harvestable.canBeHarvested(worldObj, _immutableSettings, x, y + currentYoffset, z))
				break;
			
			highestBlockOffset = currentYoffset;
		}
		
		if (highestBlockOffset < 0)
			return new BlockPosition(x, y + highestBlockOffset, z);
		
		return null;
	}
	
	private BlockPosition getNextTreeSegment(BlockPosition pos, boolean treeFlipped)
	{
		Block block;
		
		if (!pos.equals(_lastTree) || _treeManager.getIsDone())
		{
			int lowerBound = 0;
			int upperBound = MFRConfig.treeSearchMaxVertical.getInt();
			if (treeFlipped)
			{
				lowerBound = upperBound;
				upperBound = 0;
			}
			
			_lastTree = new BlockPosition(pos);
			
			Area a = new Area(_lastTree, MFRConfig.treeSearchMaxHorizontal.getInt(), lowerBound, upperBound);
			
			_treeManager.reset(worldObj, a,
					treeFlipped ? HarvestMode.HarvestTreeInverted : HarvestMode.HarvestTree,
							_immutableSettings);
		}
		
		Map<Block, IFactoryHarvestable> harvestables = MFRRegistry.getHarvestables();
		while (!_treeManager.getIsDone())
		{
			BlockPosition bp = _treeManager.getNextBlock();
			_treeManager.moveNext();
			block = worldObj.getBlock(bp.x, bp.y, bp.z);
			
			if (harvestables.containsKey(block))
			{
				IFactoryHarvestable obj = harvestables.get(block);
				HarvestType t = obj.getHarvestType();
				if (t == HarvestType.Tree | t == HarvestType.TreeFlipped |
						t == HarvestType.TreeLeaf | t == HarvestType.TreeFruit)
					if (obj.canBeHarvested(worldObj, _immutableSettings, bp.x, bp.y, bp.z))
						return bp;
			}
		}
		return null;
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		return 0;
	}
	
	@Override
	public boolean allowBucketDrain()
	{
		return true;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		for (FluidTank _tank : (FluidTank[])getTanks())
			if (_tank.getFluidAmount() > 0)
				return _tank.drain(maxDrain, doDrain);
		return null;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if (resource != null)
			for (FluidTank _tank : (FluidTank[])getTanks())
				if (resource.isFluidEqual(_tank.getFluid()))
					return _tank.drain(resource.amount, doDrain);
		return null;
	}
	
	@Override
	protected FluidTank[] createTanks()
	{
		return new FluidTank[]{new FluidTank(4 * FluidContainerRegistry.BUCKET_VOLUME)};
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		NBTTagCompound list = new NBTTagCompound();
		for(Entry<String, Boolean> setting : _settings.entrySet())
		{
			list.setByte(setting.getKey(), (byte)(setting.getValue() ? 1 : 0));
		}
		tag.setTag("harvesterSettings", list);
		_treeManager.writeToNBT(tag);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		NBTTagCompound list = (NBTTagCompound)tag.getTag("harvesterSettings");
		if (list != null)
		{
			for (String s : _settings.keySet())
			{
				if ("playSounds".equals(s))
					continue;
				byte b = list.getByte(s); 
				if (b == 1)
				{
					_settings.put(s, true);
				}
			}
		}
		if (_treeManager != null)
			_treeManager.free();
		_treeManager = new TreeHarvestManager(tag, _immutableSettings);
		if (!_treeManager.getIsDone())
			_lastTree = _treeManager.getOrigin();
	}
	
	@Override
	public int getSizeInventory()
	{
		return 1;
	}
	
	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		return 0;
	}
	
	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return 0;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return true;
	}
}
