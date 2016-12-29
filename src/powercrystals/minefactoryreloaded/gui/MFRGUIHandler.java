package powercrystals.minefactoryreloaded.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.network.IGuiHandler;
import powercrystals.minefactoryreloaded.gui.client.GUIBag;
import powercrystals.minefactoryreloaded.gui.client.GuiNeedlegun;
import powercrystals.minefactoryreloaded.gui.client.GuiRedNetLogic;
import powercrystals.minefactoryreloaded.gui.container.ContainerBag;
import powercrystals.minefactoryreloaded.gui.container.ContainerNeedlegun;
import powercrystals.minefactoryreloaded.gui.container.ContainerRedNetLogic;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

public class MFRGUIHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 0)
		{
			TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if(te instanceof TileEntityFactory)
			{
				return ((TileEntityFactory)te).getContainer(player.inventory);
			}
			else if(te instanceof TileEntityRedNetLogic)
			{
				return new ContainerRedNetLogic((TileEntityRedNetLogic)te);
			}
		}
		else if(ID == 1)
		{
			if(player.getActiveItemStack() != null &&
					player.getActiveItemStack().getItem() == MFRThings.needlegunItem)
			{
				return new ContainerNeedlegun(new NeedlegunContainerWrapper(player.getActiveItemStack()), player.inventory);
			}
		}
		else if(ID == 2)
		{
			if(player.getActiveItemStack() != null &&
					player.getActiveItemStack().getItem() == MFRThings.plasticBagItem)
			{
				return new ContainerBag(player.getActiveItemStack(), player.inventory);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 0)
		{
			TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if(te instanceof TileEntityFactory)
			{
				return ((TileEntityFactory)te).getGui(player.inventory);
			}
			else if(te instanceof TileEntityRedNetLogic)
			{
				return new GuiRedNetLogic(new ContainerRedNetLogic((TileEntityRedNetLogic)te), (TileEntityRedNetLogic)te);
			}
		}
		else if(ID == 1)
		{
			if(player.getActiveItemStack() != null &&
					player.getActiveItemStack().getItem() == MFRThings.needlegunItem)
			{
				return new GuiNeedlegun(new ContainerNeedlegun(new NeedlegunContainerWrapper(player.getActiveItemStack()), player.inventory), player.getActiveItemStack());
			}
		}
		else if(ID == 2)
		{
			if(player.getActiveItemStack() != null &&
					player.getActiveItemStack().getItem() == MFRThings.plasticBagItem)
			{
				return new GUIBag(new ContainerBag(player.getActiveItemStack(), player.inventory));
			}
		}
		return null;
	}
}
