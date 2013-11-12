package powercrystals.minefactoryreloaded.gui.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import powercrystals.minefactoryreloaded.tile.base.TileEntityLiquidGenerator;

public class ContainerLiquidGenerator extends ContainerFactoryInventory
{
	public ContainerLiquidGenerator(TileEntityLiquidGenerator tileentity, InventoryPlayer inv)
	{
		super(tileentity, inv);
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for(int i = 0; i < crafters.size(); i++)
		{
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 100, ((TileEntityLiquidGenerator)_te).getBuffer());
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);
		if(var == 100) ((TileEntityLiquidGenerator)_te).setBuffer(value);
	}
}
