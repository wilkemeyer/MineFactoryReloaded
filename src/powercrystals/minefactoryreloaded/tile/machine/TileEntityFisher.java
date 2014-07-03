package powercrystals.minefactoryreloaded.tile.machine;

import cofh.util.position.Area;
import cofh.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFisher;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityFisher extends TileEntityFactoryPowered
{
	public static final int workBase = 1800;

	protected boolean _isJammed = true;
	protected int _workNeeded = workBase;
	
	public TileEntityFisher()
	{
		super(Machine.Fisher);
		createHAM(this, 1);
		setManageSolids(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerFisher getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerFisher(this, inventoryPlayer);
	}
	
	@Override
	public ForgeDirection getDirectionFacing()
	{
		return ForgeDirection.DOWN;
	}
	
	@Override
	public boolean activateMachine()
	{
		if (_isJammed || worldObj.getWorldTime() % 137 == 0)
		{
			Area fishingHole = _areaManager.getHarvestArea();
			int extraBlocks = 0;
			for (BlockPosition bp: fishingHole.getPositionsBottomFirst())
			{
				if (!worldObj.getBlock(bp.x, bp.y, bp.z).isAssociatedBlock(Blocks.water))
				{
					_isJammed = true;
					setIdleTicks(getIdleTicksMax());
					return false;
				}
				else if (worldObj.getBlock(bp.x, bp.y - 1, bp.z).isAssociatedBlock(Blocks.water))
				{
					++extraBlocks;
				}
				if (bp.x != xCoord || bp.z != zCoord)
				{
					if (worldObj.getBlock(bp.x - (xCoord - bp.x), bp.y, bp.z - (zCoord - bp.z)).isAssociatedBlock(Blocks.water))
					{
						++extraBlocks;
					}
				}
			}
			_workNeeded = workBase - extraBlocks * 52;
			_isJammed = false;
		}
		
		setWorkDone(getWorkDone() + 1);
		
		if (getWorkDone() > getWorkMax())
		{ // TODO: forge fishing API
			doDrop(new ItemStack(Items.fish));
			setWorkDone(0);
		}
		return true;
	}
	
	@Override
	public ForgeDirection getDropDirection()
	{
		return ForgeDirection.UP;
	}
	
	@Override
	public int getWorkMax()
	{
		return _workNeeded;
	}
	
	@SideOnly(Side.CLIENT)
	public void setWorkMax(int work)
	{
		_workNeeded = work;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		_workNeeded = tag.getInteger("workNeeded");
		_isJammed = tag.getBoolean("jam");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setInteger("workNeeded", _workNeeded);
		tag.setBoolean("jam", _isJammed);
	}
	
	@Override
	public int getIdleTicksMax()
	{
		return 200;
	}
	
	@Override
	public int getSizeInventory()
	{
		return 0;
	}
}
