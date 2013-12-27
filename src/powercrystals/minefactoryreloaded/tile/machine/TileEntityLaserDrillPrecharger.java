package powercrystals.minefactoryreloaded.tile.machine;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.core.position.BlockPosition;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.IFactoryLaserTarget;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLaserDrillPrecharger extends TileEntityFactoryPowered
{
	public TileEntityLaserDrillPrecharger()
	{
		super(Machine.LaserDrillPrecharger);
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
	public int getSizeInventory()
	{
		return 0;
	}
	
	@Override
	protected boolean activateMachine()
	{
		IFactoryLaserTarget drill = getDrill();
		if(drill == null)
		{
			setIdleTicks(getIdleTicksMax());
			resetLaser();
		}
		else
		{
			int energy = getActivationEnergy();
			ForgeDirection facing = getDirectionFacing().getOpposite();
			if (drill.canFormBeamWith(facing))
			{
				stripBlock();
				int excess = drill.addEnergy(facing, energy, true);
				if (excess == 0)
				{
					drill.addEnergy(facing, energy, false);
					return true;
				}
				else
				{
					excess = drill.addEnergy(facing, energy, false);
					drainEnergy(energy - excess);
				}
			}
			else
				if (stripTick > 0)
					--stripTick;
				else
				{
					resetLaser();
					stripTick = 20;
				}
		}
		return false;
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
	
	public boolean shouldDrawBeam()
	{
		IFactoryLaserTarget drill = getDrill();
		return drill != null && drill.canFormBeamWith(getDirectionFacing().getOpposite());
	}
	
	protected IFactoryLaserTarget getDrill()
	{
		BlockPosition bp = new BlockPosition(this);
		bp.orientation = getDirectionFacing();
		bp.moveForwards(1);
		
		int midId = worldObj.getBlockId(bp.x, bp.y, bp.z);
		if (!TileEntityLaserDrill.canReplaceBlock(Block.blocksList[midId], worldObj, bp.x, bp.y, bp.z))
			return null;
		
		bp.moveForwards(1);
		
		TileEntity te = worldObj.getBlockTileEntity(bp.x, bp.y, bp.z);
		if (te instanceof IFactoryLaserTarget)
			return ((IFactoryLaserTarget)te);
		
		return null;
	}
	
	private int stripTick = 0;
	protected void stripBlock()
	{
		if (stripTick > 0)
		{
			--stripTick;
			return;
		}
		stripTick = 20;
		BlockPosition bp = new BlockPosition(this);
		bp.orientation = getDirectionFacing();
		bp.moveForwards(1);
		if (worldObj.getBlockId(bp.x, bp.y, bp.z) != MineFactoryReloadedCore.fakeLaserBlock.blockID)
			worldObj.setBlock(bp.x, bp.y, bp.z, MineFactoryReloadedCore.fakeLaserBlock.blockID, 1, 3);
	}
	
	@Override
	public void onDisassembled()
	{
		super.onDisassembled();
		resetLaser();
	}
	
	protected void resetLaser()
	{
		BlockPosition bp = new BlockPosition(this);
		bp.orientation = getDirectionFacing();
		bp.moveForwards(1);
		if (worldObj.getBlockId(bp.x, bp.y, bp.z) == MineFactoryReloadedCore.fakeLaserBlock.blockID)
			worldObj.setBlockMetadataWithNotify(bp.x, bp.y, bp.z, 0, 0);
		
	}

	@Override
	public int getMaxSafeInput()
	{
		return Integer.MAX_VALUE;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536;
	}
}
