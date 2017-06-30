package powercrystals.minefactoryreloaded.tile.machine;

import net.minecraft.item.ItemDoor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityBreeder extends TileEntityFactoryPowered {

	public TileEntityBreeder() {

		super(Machine.Breeder);
		createEntityHAM(this);
		setManageSolids(true);
		setCanRotate(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerFactoryPowered(this, inventoryPlayer);
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 200;
	}

	@Override
	protected boolean activateMachine() {

		List<EntityAnimal> entities = worldObj.getEntitiesWithinAABB(EntityAnimal.class,
				_areaManager.getHarvestArea().toAxisAlignedBB());

		if (entities.size() > MFRConfig.breederShutdownThreshold.getInt()) {
			setIdleTicks(getIdleTicksMax());
			return false;
		}
		ArrayList<Integer> doors = new ArrayList<Integer>();

		for (int i = getSizeInventory(); i-- > 0; ) {
			ItemStack item = _inventory[i];
			if (item != null) {
				if (item.getItem() instanceof ItemDoor) {
					doors.add(i);
				}
				if (entities.size() == 0)
					continue;
				Iterator<EntityAnimal> iter = entities.iterator();
				while (iter.hasNext()) {
					EntityAnimal a = iter.next();

					if (!a.isInLove() && a.getGrowingAge() == 0) {
						if (a.isBreedingItem(_inventory[i])) {
							a.setInLove(null);
							decrStackSize(i, 1);
							iter.remove();
							return true;
						}
					} else
						iter.remove();
				}
			}
		}

		if (doors.size() > 0) {
			List<EntityVillager> villagers = worldObj.getEntitiesWithinAABB(EntityVillager.class,
					_areaManager.getHarvestArea().toAxisAlignedBB());

			if (villagers.size() > MFRConfig.breederShutdownThreshold.getInt()) {
				setIdleTicks(getIdleTicksMax());
				return false;
			}
			if (villagers.size() != 0)
				for (int i : doors) {
					ItemStack item = _inventory[i];
					if (item != null) {
						if (villagers.size() == 0)
							break;
						Iterator<EntityVillager> iter = villagers.iterator();
						while (iter.hasNext()) {
							EntityVillager v = iter.next();
							if (v.getGrowingAge() == 0 && !v.isMating()) {
								for (Object o : v.tasks.taskEntries) {
									if (o instanceof EntityAIVillagerMate) {
										((EntityAIVillagerMate) o).startExecuting();
										decrStackSize(i, 1);
										iter.remove();
										return true;
									}
								}
							} else
								iter.remove();
						}
					}
				}
		}
		setIdleTicks(getIdleTicksMax());
		return false;
	}

	@Override
	public int getSizeInventory() {

		return 9;
	}

}
