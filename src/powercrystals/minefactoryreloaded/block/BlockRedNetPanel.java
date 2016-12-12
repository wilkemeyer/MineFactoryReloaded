package powercrystals.minefactoryreloaded.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetHistorian;

public class BlockRedNetPanel extends BlockFactory implements IRedNetInputNode, ITileEntityProvider
{
	public BlockRedNetPanel()
	{
		super(0.8F);
		setUnlocalizedName("mfr.rednet.panel");
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityFactory)
		{
			if(((TileEntityFactory)te).getDirectionFacing() == EnumFacing.NORTH)
			{
				setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
			}
			else if(((TileEntityFactory)te).getDirectionFacing() == EnumFacing.SOUTH)
			{
				setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
			}
			else if(((TileEntityFactory)te).getDirectionFacing() == EnumFacing.EAST)
			{
				setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}
			else if(((TileEntityFactory)te).getDirectionFacing() == EnumFacing.WEST)
			{
				setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, EntityLivingBase entity, ItemStack stack)
	{
		super.onBlockPlacedBy(world, x, y, z, entity, stack);
		if(entity == null)
		{
			return;
		}

		TileEntity te = getTile(world, x, y, z);
		if(te instanceof TileEntityFactory && ((TileEntityFactory)te).canRotate())
		{
			int facing = MathHelper.floor_double((entity.rotationYaw * 4F) / 360F + 0.5D) & 3;
			if(facing == 0)
			{
				((TileEntityFactory)te).rotateDirectlyTo(3);
			}
			else if(facing == 1)
			{
				((TileEntityFactory)te).rotateDirectlyTo(4);
			}
			else if(facing == 2)
			{
				((TileEntityFactory)te).rotateDirectlyTo(2);
			}
			else if(facing == 3)
			{
				((TileEntityFactory)te).rotateDirectlyTo(5);
			}
		}
	}

	@Override
	public boolean activated(World world, BlockPos pos, EntityPlayer player, EnumFacing side)
	{
		ItemStack s = player.inventory.getCurrentItem();

		TileEntity te = getTile(world, x, y, z);
		if (MFRUtil.isHoldingUsableTool(player, x, y, z) && te instanceof TileEntityFactory && ((TileEntityFactory)te).canRotate())
		{
			((TileEntityFactory)te).rotate(EnumFacing.getOrientation(side));
			world.markBlockForUpdate(x, y, z);
			MFRUtil.usedWrench(player, x, y, z);
			return true;
		}
		else if(te instanceof TileEntityFactory && ((TileEntityFactory)te).getContainer(player.inventory) != null)
		{
			player.openGui(MineFactoryReloadedCore.instance(), 0, world, x, y, z);
			return true;
		}
		else if(te instanceof TileEntityRedNetHistorian && s != null && s.getItem().equals(Items.dye))
		{
			((TileEntityRedNetHistorian)te).setSelectedSubnet(15 - s.getItemDamage());
			world.markBlockForUpdate(x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return MineFactoryReloadedCore.renderIdRedNetPanel;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityRedNetHistorian();
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, BlockPos pos, EnumFacing side)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityFactory)
		{
			return side == ((TileEntityFactory)te).getDirectionFacing() ? RedNetConnectionType.CableAll : RedNetConnectionType.None;
		}
		return RedNetConnectionType.None;
	}

	@Override
	public void onInputsChanged(World world, BlockPos pos, EnumFacing side, int[] inputValues)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityRedNetHistorian)
		{
			((TileEntityRedNetHistorian)te).valuesChanged(inputValues);
		}
	}

	@Override
	public void onInputChanged(World world, BlockPos pos, EnumFacing side, int inputValue)
	{
	}

	@Override
	public void registerBlockIcons(IIconRegister ir)
	{
		blockIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}
}
