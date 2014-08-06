package powercrystals.minefactoryreloaded.tile.machine;

import cofh.lib.util.position.Area;
import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.FishingHooks;
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
	protected byte _speed, _luck;
	protected int _workNeeded = workBase;
	protected float _next = Float.NaN;
	protected Random _rand = null;

	public TileEntityFisher()
	{
		super(Machine.Fisher);
		createHAM(this, 1);
		setManageSolids(true);
	}

	@Override
	public void setWorldObj(World world)
	{
		super.setWorldObj(world);
		if (_rand == null) {
			_rand = new Random(world.getSeed() ^ world.rand.nextLong());
			_next = _rand.nextFloat();
		}
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
				if (!isValidBlock(bp.x, bp.y, bp.z))
				{
					_isJammed = true;
					setIdleTicks(getIdleTicksMax());
					return false;
				}
				else if (isValidBlock(bp.x, bp.y - 1, bp.z))
				{
					++extraBlocks;
				}
				if ((bp.x != xCoord) | bp.z != zCoord)
				{
					if (isValidBlock(bp.x - (xCoord - bp.x), bp.y, bp.z - (zCoord - bp.z)))
					{
						++extraBlocks;
					}
				}
				else if (isValidBlock(bp.x, bp.y - 2, bp.z))
				{
					++extraBlocks;
				}
			}
			_workNeeded = workBase - extraBlocks * 50;
			_isJammed = false;
		}

		setWorkDone(getWorkDone() + 1);

		if (getWorkDone() > getWorkMax())
		{
			doDrop(FishingHooks.getRandomFishable(_rand, _next, _luck, _speed));
			_next = _rand.nextFloat();
			setWorkDone(0);
		}
		return true;
	}

	protected boolean isValidBlock(int x, int y, int z)
	{
		int meta = worldObj.getBlockMetadata(x, y, z);
		if (meta != 0) return false;
		Block block = worldObj.getBlock(x, y, z);
		return block.isAssociatedBlock(Blocks.water) || block.isAssociatedBlock(Blocks.flowing_water);
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
		if (tag.hasKey("seed"))
			_rand = new Random(tag.getLong("seed"));
		if (tag.hasKey("next"))
			_next = tag.getFloat("next");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setInteger("workNeeded", _workNeeded);
		tag.setBoolean("jam", _isJammed);
		tag.setFloat("next", _next);
		try {
			Field f = Random.class.getDeclaredField("seed");
			f.setAccessible(true);
			tag.setLong("seed", ((AtomicLong)f.get(_rand)).get());
		} catch (Throwable _) {}
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
