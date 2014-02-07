package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.Set;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.gui.client.GuiChunkLoader;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerChunkLoader;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityChunkLoader extends TileEntityFactoryPowered
{
	private static void bypassLimit(Ticket tick)
	{
		try
		{
			Field f = Ticket.class.getDeclaredField("maxDepth");
			f.setAccessible(true);
			f.setInt(tick, Short.MAX_VALUE);
		} catch(Throwable _) {}
	}
	
	protected short _radius;
	protected boolean activated;
	protected Ticket _ticket;
	
	public TileEntityChunkLoader()
	{
		super(Machine.ChunkLoader);
		_radius = 0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiChunkLoader(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerChunkLoader(this, inventoryPlayer);
	}
	
	public void setRadius(short r)
	{
		int maxR = 49;
		if (_ticket != null)
			maxR = Math.min((int)Math.sqrt(_ticket.getChunkListDepth() / Math.PI), maxR);
		if (r < 0 | r > maxR)
			return;
		_radius = r;
		onInventoryChanged();
	}

	@Override
	protected boolean activateMachine()
	{
		activated = true;
		return true;
	}
	
	@Override
	public void updateEntity()
	{
		activated = false;
		super.updateEntity();
		if (worldObj.isRemote)
			return;
		
		if (!activated)
		{
			if (_ticket != null)
			{
				Set<ChunkCoordIntPair> chunks = _ticket.getChunkList();
				for (ChunkCoordIntPair c : chunks)
					ForgeChunkManager.unforceChunk(_ticket, c);
			}
		}
		else if (!isActive())
		{
			if (_ticket == null)
			{
				_ticket = ForgeChunkManager.
						requestPlayerTicket(MineFactoryReloadedCore.instance(),
								_owner, worldObj, Type.NORMAL);
				_ticket.getModData().setInteger("X", xCoord);
				_ticket.getModData().setInteger("Y", yCoord);
				_ticket.getModData().setInteger("Z", zCoord);
			}
			forceChunks();
		}
		
		super.setIsActive(activated);
	}
	
	protected void forceChunks()
	{
		if (MFRConfig.enableChunkLimitBypassing.getBoolean(false))
			bypassLimit(_ticket);
		Set<ChunkCoordIntPair> chunks = _ticket.getChunkList();
		int x = xCoord >> 4;
		int z = zCoord >> 4;
		int r = _radius * _radius;
		for (int xO = -_radius; xO <= _radius; ++xO)
		{
			int xS = xO * xO;
			for (int zO = -_radius; zO <= _radius; ++zO)
				if (xS + zO * zO <= r)
				{
					ChunkCoordIntPair p = new ChunkCoordIntPair(x + xO, z + zO);
					if (!chunks.contains(p))
						ForgeChunkManager.forceChunk(_ticket, p);
				}
		}
	}
	
	@Override
	protected void onFactoryInventoryChanged()
	{
		if (isInvalid())
			return;
		int r = _radius + 1;
		double a = (r*r*32-17+r*r*r);
		for (int i = r / 10; i --> 0; )
			a *= r / 6d;
		setActivationEnergy((int)(a*10));
	}

	public void receiveTicket(Ticket ticket)
	{
		if (_ticket == null)
			_ticket = ticket;
		else
			ForgeChunkManager.releaseTicket(ticket);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		tag.setShort("radius", _radius);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		_radius = tag.getShort("radius");
	}

	@Override
	public int getSizeInventory()
	{
		return 0;
	}

	@Override
	public int getWorkMax()
	{
		return 1;
	}

	@Override
	public int getIdleTicksMax()
	{
		return 1;
	}
	
	public short getRadius()
	{
		return _radius;
	}
	
	@Override public void setIsActive(boolean a) {}
}
