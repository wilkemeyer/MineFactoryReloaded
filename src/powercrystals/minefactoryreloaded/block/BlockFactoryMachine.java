package powercrystals.minefactoryreloaded.block;

import cofh.api.block.IDismantleable;
import cofh.util.position.IRotateableTile;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidContainerItem;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.IEntityCollidable;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrill;

public class BlockFactoryMachine extends BlockContainer
implements IRedNetOmniNode, IDismantleable
{
	private int _mfrMachineBlockIndex;

	public BlockFactoryMachine(int index)
	{
		super(Machine.MATERIAL);
		setHardness(0.5F);
		setStepSound(soundTypeMetal);
		setCreativeTab(MFRCreativeTab.tab);
		setBlockName("mfr.machine." + index);
		_mfrMachineBlockIndex = index;
	}

	public int getBlockIndex()
	{
		return _mfrMachineBlockIndex;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		Machine.LoadTextures(_mfrMachineBlockIndex, ir);
	}

	@Override
	public IIcon getIcon(IBlockAccess iblockaccess, int x, int y, int z, int side)
	{
		int md = iblockaccess.getBlockMetadata(x, y, z);
		boolean isActive = false;
		TileEntity te = iblockaccess.getTileEntity(x, y, z);
		if(te instanceof TileEntityFactory)
		{
			side = ((TileEntityFactory)te).getRotatedSide(side);
			isActive = ((TileEntityFactory)te).isActive();
		}
		return Machine.getMachineFromIndex(_mfrMachineBlockIndex, md).getIcon(side, isActive);
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		if(side > 1)
		{
			side += 2;
			if(side > 5)
			{
				side -= 4;
			}
		}
		return Machine.getMachineFromIndex(_mfrMachineBlockIndex, meta).getIcon(side, false);
	}

	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z)
	{
		if(world.getTileEntity(x, y, z) instanceof TileEntityLaserDrill)
		{
			return 0;
		}
		return super.getLightOpacity(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IEntityCollidable)
		{
			float shrinkAmount = 0.125F;
			return AxisAlignedBB.getBoundingBox(x + shrinkAmount, y + shrinkAmount, z + shrinkAmount,
					x + 1 - shrinkAmount, y + 1 - shrinkAmount, z + 1 - shrinkAmount);
		}
		else
		{
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (world.isRemote)
			return;

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IEntityCollidable)
			((IEntityCollidable)te).onEntityCollided(entity);

		super.onEntityCollidedWithBlock(world, x, y, z, entity);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
	{
		TileEntity te = world.getTileEntity(x, y, z);

		if(te instanceof TileEntityFactory)
		{
			((TileEntityFactory)te).onNeighborTileChange(tileX, tileY, tileZ);
		}
	}

	private void dropContents(TileEntity te)
	{
		if (te instanceof IInventory)
		{
			World world = te.getWorldObj();
			IInventory inventory = ((IInventory)te);
			TileEntityFactoryInventory factoryInv = null;
			if (te instanceof TileEntityFactoryInventory)
				factoryInv = (TileEntityFactoryInventory)te;

			inv: for (int i = inventory.getSizeInventory(); i --> 0 ; )
			{
				if (factoryInv != null)
					if (!factoryInv.shouldDropSlotWhenBroken(i))
						continue;

				ItemStack itemstack = inventory.getStackInSlot(i);
				if (itemstack == null)
					continue;
				inventory.setInventorySlotContents(i, null);

				do
				{
					if (itemstack.stackSize <= 0)
						continue inv;

					float xOffset = world.rand.nextFloat() * 0.8F + 0.1F;
					float yOffset = world.rand.nextFloat() * 0.8F + 0.1F;
					float zOffset = world.rand.nextFloat() * 0.8F + 0.1F;

					int amountToDrop = Math.min(world.rand.nextInt(21) + 10, itemstack.stackSize);

					EntityItem entityitem = new EntityItem(world,
							te.xCoord + xOffset, te.yCoord + yOffset, te.zCoord + zOffset,
							itemstack.splitStack(amountToDrop));

					float motionMultiplier = 0.05F;
					entityitem.motionX = (float)world.rand.nextGaussian() * motionMultiplier;
					entityitem.motionY = (float)world.rand.nextGaussian() * motionMultiplier + 0.2F;
					entityitem.motionZ = (float)world.rand.nextGaussian() * motionMultiplier;

					world.spawnEntityInWorld(entityitem);
				} while(true);
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block blockId, int meta)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null)
		{
			dropContents(te);

			if (te instanceof TileEntityFactoryInventory)
				((TileEntityFactoryInventory)te).onBlockBroken();
		}
		super.breakBlock(world, x, y, z, blockId, meta);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta)
	{
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player)
	{ // HACK: called before block is destroyed by the player prior to the player getting the drops. destroy block here.
		if (!player.capabilities.isCreativeMode)
		{
			world.func_147480_a(x, y, z, true);
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata,
			int fortune)
			{
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		ItemStack machine = new ItemStack(getItemDropped(metadata, world.rand, fortune), 1,
				damageDropped(metadata));

		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null)
		{
			NBTTagCompound tag = new NBTTagCompound();
			if (te instanceof TileEntityFactoryInventory)
				((TileEntityFactoryInventory)te).writeItemNBT(tag);
			if (!tag.hasNoTags())
				machine.setTagCompound(tag);

			drops.add(machine);
		}
		return drops;
			}

	@Override
	public ItemStack dismantleBlock(EntityPlayer player, World world, int x, int y, int z,
			boolean returnBlock)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityFactory)
		{
			ItemStack machine = new ItemStack(getItemDropped(world.getBlockMetadata(x, y, z),
					world.rand, 0), 1, damageDropped(world.getBlockMetadata(x, y, z)));

			dropContents(te);
			if(te instanceof TileEntityFactoryInventory)
				((TileEntityFactoryInventory)te).onDisassembled();

			NBTTagCompound tag = new NBTTagCompound();
			te.writeToNBT(tag);
			if (te instanceof IInventory && ((IInventory)te).hasCustomInventoryName())
			{
				NBTTagCompound name = new NBTTagCompound();
				name.setString("Name", ((IInventory)te).getInventoryName());
				tag.setTag("display", name);
			}
			machine.setTagCompound(tag);

			world.setBlockToAir(x, y, z);
			if (!returnBlock)
				dropBlockAsItem(world, x, y, z, machine);
			return machine;
		}
		return null;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z)
	{
		return world.getTileEntity(x, y, z) instanceof TileEntityFactory;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity,
			ItemStack stack)
	{
		if(entity == null)
		{
			return;
		}
		TileEntity te = world.getTileEntity(x, y, z);
		if(stack.getTagCompound() != null)
		{
			stack.getTagCompound().setInteger("x", x);
			stack.getTagCompound().setInteger("y", y);
			stack.getTagCompound().setInteger("z", z);
			te.readFromNBT(stack.getTagCompound());
		}

		if(te instanceof TileEntityFactory)
		{
			if (((TileEntityFactory)te).canRotate())
				switch (MathHelper.floor_double((entity.rotationYaw * 4F) / 360F + 0.5D) & 3)
				{
				case 0:
					((TileEntityFactory)te).rotateDirectlyTo(3);
					break;
				case 1:
					((TileEntityFactory)te).rotateDirectlyTo(4);
					break;
				case 2:
					((TileEntityFactory)te).rotateDirectlyTo(2);
					break;
				case 3:
					((TileEntityFactory)te).rotateDirectlyTo(5);
					break;
				}

			if (te instanceof TileEntityFactoryInventory)
			{
				if (stack.hasDisplayName())
				{
					((TileEntityFactoryInventory)te).setInvName(stack.getDisplayName());
				}
			}

			if (entity instanceof ICommandSender && entity.addedToChunk)
				((TileEntityFactory)te).setOwner(((ICommandSender)entity).getCommandSenderName());
			else
				((TileEntityFactory)te).setOwner(null);
		}
	}

	@Override
	public int damageDropped(int i)
	{
		return i;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return Machine.getMachineFromIndex(_mfrMachineBlockIndex, meta).getNewTileEntity();
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis)
	{
		if (world.isRemote)
		{
			return false;
		}
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof IRotateableTile)
		{
			IRotateableTile tile = ((IRotateableTile)te);
			if (tile.canRotate(axis))
			{
				tile.rotate(axis);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityFactoryInventory)
			return ((TileEntityFactoryInventory)te).getComparatorOutput(side);
		return 0;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer,
			int side, float xOffset, float yOffset, float zOffset)
	{
		PlayerInteractEvent e = new PlayerInteractEvent(entityplayer, Action.RIGHT_CLICK_BLOCK,
				x, y, z, side);
		if(MinecraftForge.EVENT_BUS.post(e) || e.getResult() == Result.DENY || e.useBlock == Result.DENY)
		{
			return false;
		}

		TileEntity te = world.getTileEntity(x, y, z);
		if(te == null)
		{
			return false;
		}
		ItemStack ci = entityplayer.inventory.getCurrentItem();
		if(te instanceof ITankContainerBucketable)
		{
			boolean isFluidContainer = ci != null && ci.getItem() instanceof IFluidContainerItem;
			if((isFluidContainer || FluidContainerRegistry.isEmptyContainer(ci)) &&
					((ITankContainerBucketable)te).allowBucketDrain(ci))
			{
				if(MFRLiquidMover.manuallyDrainTank((ITankContainerBucketable)te, entityplayer))
				{
					return true;
				}
			}
			if((isFluidContainer || FluidContainerRegistry.isFilledContainer(ci)) &&
					((ITankContainerBucketable)te).allowBucketFill(ci))
			{
				if(MFRLiquidMover.manuallyFillTank((ITankContainerBucketable)te, entityplayer))
				{
					return true;
				}
			}
		}

		if(te instanceof TileEntityFactory &&
				((TileEntityFactory)te).getContainer(entityplayer.inventory) != null)
		{
			if(!world.isRemote)
			{
				entityplayer.openGui(MineFactoryReloadedCore.instance(), 0, world, x, y, z);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}

	@Override
	public boolean isNormalCube()
	{
		return false;
	}

	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityFactory)
		{
			return ((TileEntityFactory)te).getRedNetOutput(ForgeDirection.getOrientation(side));
		}
		return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
	{
		return isProvidingWeakPower(world, x, y, z, side);
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side)
	{
		return RedNetConnectionType.CableSingle;
	}

	@Override
	public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side)
	{
		return null;
	}

	@Override
	public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet)
	{
		return 0;
	}

	@Override
	public void onInputsChanged(World world, int x, int y, int z, ForgeDirection side, int[] inputValues)
	{
	}

	@Override
	public void onInputChanged(World world, int x, int y, int z, ForgeDirection side, int inputValue)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityFactory)
		{
			((TileEntityFactory)te).onRedNetChanged(side, inputValue);
			onNeighborBlockChange(world, x, y, z, MineFactoryReloadedCore.rednetCableBlock);
		}
	}
}
