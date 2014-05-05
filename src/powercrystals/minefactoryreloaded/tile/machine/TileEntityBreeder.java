package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.InventoryPlayer;

import powercrystals.minefactoryreloaded.core.HarvestAreaManager;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityBreeder extends TileEntityFactoryPowered
{
	
	public TileEntityBreeder()
	{
		super(Machine.Breeder);
		_areaManager = new HarvestAreaManager(this, 2, 2, 1);
		setManageSolids(true);
		setCanRotate(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerFactoryPowered(this, inventoryPlayer);
	}
	
	@Override
	public int getWorkMax()
	{
		return 1;
	}
	
	@Override
	public int getIdleTicksMax()
	{
		return 200;
	}
	
	@Override
	protected boolean activateMachine()
	{
		List<?> entities = worldObj.getEntitiesWithinAABB(EntityAnimal.class, _areaManager.getHarvestArea().toAxisAlignedBB());
		
		if(entities.size() > MFRConfig.breederShutdownThreshold.getInt())
		{
			setIdleTicks(getIdleTicksMax());
			return false;
		}
		
		for (Object o : entities)
		{
			if (o instanceof EntityAnimal)
			{
				EntityAnimal a = ((EntityAnimal)o);
					
				if (!a.isInLove() && a.getGrowingAge() == 0)
				{
					for (int i = getSizeInventory(); i --> 0; )
					{
						if (_inventory[i] != null && a.isBreedingItem(_inventory[i]))
						{
							a.func_146082_f(null);
							decrStackSize(i, 1);
							return true;
						}
					}
				}
			}
		}
		setIdleTicks(getIdleTicksMax());
		return false;
	}
	
	@Override
	public int getSizeInventory()
	{
		return 9;
	}
}
