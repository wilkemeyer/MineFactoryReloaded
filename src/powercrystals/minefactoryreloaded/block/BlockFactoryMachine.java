package powercrystals.minefactoryreloaded.block;

import cofh.lib.util.position.IRotateableTile;

import java.util.ArrayList;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityBase;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrill;

public class BlockFactoryMachine extends BlockFactory implements IRedNetOmniNode {

	public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);

	private int _mfrMachineBlockIndex;

	public BlockFactoryMachine(int index) {

		super(1.5F);
		setUnlocalizedName("mfr.machine." + index);
		_mfrMachineBlockIndex = index;
		providesPower = true;
		setHarvestLevel("wrench", 0);
	}

	public int getBlockIndex() {

		return _mfrMachineBlockIndex;
	}

/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		Machine.LoadTextures(_mfrMachineBlockIndex, ir);
	}

	@Override
	public IIcon getIcon(IBlockAccess iblockaccess, BlockPos pos, EnumFacing side) {

		int md = iblockaccess.getBlockMetadata(x, y, z);
		boolean isActive = false;
		TileEntity te = iblockaccess.getTileEntity(x, y, z);
		if (te instanceof TileEntityFactory) {
			side = ((TileEntityFactory) te).getRotatedSide(side);
			isActive = ((TileEntityFactory) te).isActive();
		}
		return Machine.getMachineFromIndex(_mfrMachineBlockIndex, md).getIcon(side, isActive);
	}

	private static int[] itemRotation = { 0, 1, 3, 2, 5, 4 };

	@Override
	public IIcon getIcon(EnumFacing side, int meta) {

		side = itemRotation[side];
		return Machine.getMachineFromIndex(_mfrMachineBlockIndex, meta).getIcon(side, false);
	}
*/

	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {

		if (world.getTileEntity(pos) instanceof TileEntityLaserDrill) {
			return 0;
		}
		return super.getLightOpacity(state, world, pos);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighborPos) {

		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TileEntityFactory) {
			((TileEntityFactory) te).onNeighborTileChange(neighborPos);
		}
	}

	private void dropContents(TileEntity te, ArrayList<ItemStack> list) {

		if (te instanceof IInventory) {
			World world = te.getWorld();
			IInventory inventory = ((IInventory) te);
			TileEntityFactoryInventory factoryInv = null;
			if (te instanceof TileEntityFactoryInventory)
				factoryInv = (TileEntityFactoryInventory) te;

			for (int i = inventory.getSizeInventory(); i-- > 0;) {
				if (factoryInv != null)
					if (!factoryInv.shouldDropSlotWhenBroken(i))
						continue;

				ItemStack itemstack = inventory.getStackInSlot(i);
				if (itemstack == null)
					continue;
				inventory.setInventorySlotContents(i, null);
				if (list != null) {
					list.add(itemstack);
				} else
					dropStack(world, te.getPos(), itemstack);
			}
		}
	}

	private void dropStack(World world, BlockPos pos, ItemStack itemstack) {

		do {
			if (itemstack.stackSize <= 0)
				break;

			float xOffset = world.rand.nextFloat() * 0.8F + 0.1F;
			float yOffset = world.rand.nextFloat() * 0.8F + 0.1F;
			float zOffset = world.rand.nextFloat() * 0.8F + 0.1F;

			int amountToDrop = Math.min(world.rand.nextInt(21) + 10, itemstack.stackSize);

			EntityItem entityitem = new EntityItem(world,
					pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset,
					itemstack.splitStack(amountToDrop));

			float motionMultiplier = 0.05F;
			entityitem.motionX = (float) world.rand.nextGaussian() * motionMultiplier;
			entityitem.motionY = (float) world.rand.nextGaussian() * motionMultiplier + 0.2F;
			entityitem.motionZ = (float) world.rand.nextGaussian() * motionMultiplier;

			world.spawnEntityInWorld(entityitem);
		} while (true);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {

		TileEntity te = getTile(world, pos);
		if (te != null) {
			dropContents(te, null); // TODO: rewrite drop logic

			if (te instanceof TileEntityFactoryInventory)
				((TileEntityFactoryInventory) te).onBlockBroken();
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, BlockPos pos, boolean returnBlock) {

		ArrayList<ItemStack> list = new ArrayList<ItemStack>(1);
		IBlockState state = world.getBlockState(pos);
		ItemStack machine = new ItemStack(getItemDropped(state, world.rand, 0),	1, damageDropped(state));
		list.add(machine);
		TileEntity te = getTile(world, pos);
		if (te instanceof TileEntityBase) {
			dropContents(te, list);

			if (te instanceof TileEntityFactoryInventory)
				((TileEntityFactoryInventory) te).onDisassembled();

			NBTTagCompound tag = new NBTTagCompound();
			((TileEntityBase) te).writeItemNBT(tag);
			if (!tag.hasNoTags())
				machine.setTagCompound(tag);
		}
		world.setBlockToAir(pos);
		if (!returnBlock)
			for (ItemStack stack : list)
				dropStack(world, pos, stack);
		return list;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, BlockPos pos) {

		return getTile(world, pos) instanceof TileEntityFactory;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {

		super.onBlockPlacedBy(world, pos, state, entity, stack);
		if (entity != null) {
			TileEntity te = getTile(world, pos);
			if (te instanceof IRotateableTile)
				if (((IRotateableTile) te).canRotate())
					switch (MathHelper.floor_double((entity.rotationYaw * 4F) / 360F + 0.5D) & 3) {
					case 0:
						((IRotateableTile) te).rotateDirectlyTo(3);
						break;
					case 1:
						((IRotateableTile) te).rotateDirectlyTo(4);
						break;
					case 2:
						((IRotateableTile) te).rotateDirectlyTo(2);
						break;
					case 3:
						((IRotateableTile) te).rotateDirectlyTo(5);
						break;
					}

			if (te instanceof TileEntityFactory) {
				if (entity.addedToChunk)
					((TileEntityFactory) te).setOwner(entity.getName());
				else
					((TileEntityFactory) te).setOwner(null);
			}
		}
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return Machine.getMachineFromIndex(_mfrMachineBlockIndex, getMetaFromState(state)).getNewTileEntity();
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {

		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {

		TileEntity te = getTile(world, pos);
		if (te instanceof TileEntityFactoryInventory)
			return ((TileEntityFactoryInventory) te).getComparatorOutput();
		return 0;
	}

	@Override
	public boolean activated(World world, BlockPos pos, EntityPlayer entityplayer, EnumFacing side, EnumHand hand, ItemStack heldItem) {

		if (super.activated(world, pos, entityplayer, side, hand, heldItem))
			return true;
		TileEntity te = getTile(world, pos);
		if (te == null) {
			return false;
		}

		if (te instanceof TileEntityFactoryInventory) {
			if (((TileEntityFactoryInventory)te).acceptUpgrade(heldItem)) {
				if (entityplayer.capabilities.isCreativeMode) {
					++heldItem.stackSize;
				}
				if (heldItem.stackSize <= 0) {
					EntityEquipmentSlot slot = hand == EnumHand.OFF_HAND ? EntityEquipmentSlot.OFFHAND : EntityEquipmentSlot.MAINHAND;
					entityplayer.setItemStackToSlot(slot, null);
				}
				return true;
			}
		}

		if (te instanceof TileEntityFactory &&
				((TileEntityFactory) te).getContainer(entityplayer.inventory) != null) {
			if (!world.isRemote) {
				entityplayer.openGui(MineFactoryReloadedCore.instance(), 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return true;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFactory) {
			return ((TileEntityFactory) te).getRedNetOutput(side);
		}
		return 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return getWeakPower(state, world, pos, side);
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, BlockPos pos, EnumFacing side) {

		return RedNetConnectionType.DecorativeSingle;
	}

	@Override
	public int[] getOutputValues(World world, BlockPos pos, EnumFacing side) {

		return null;
	}

	@Override
	public void onInputsChanged(World world, BlockPos pos, EnumFacing side, int[] inputValues) {

	}

	@Override
	public int getOutputValue(World world, BlockPos pos, EnumFacing side, int subnet) {

		TileEntity te = getTile(world, pos);
		if (te instanceof TileEntityFactory) {
			return ((TileEntityFactory) te).getRedNetOutput(side);
		}
		return 0;
	}

	@Override
	public void onInputChanged(World world, BlockPos pos, EnumFacing side, int inputValue) {

		TileEntity te = getTile(world, pos);
		if (te instanceof TileEntityFactory) {
			((TileEntityFactory) te).onRedNetChanged(side, inputValue);
			neighborChanged(world.getBlockState(pos), world, pos, MFRThings.rednetCableBlock);
		}
	}

	public enum Type implements IStringSerializable {
		PLANTER(0, 0, "Planter"),
		FISHER(0, 1, "Fisher"),
		HARVESTER(0, 2, "Harvester"),
		RANCHER(0, 3, "Rancher"),
		FERTILIZER(0, 4, "Fertilizer"),
		VET(0, 5, "Vet"),
		ITEMCOLLECTOR(0, 6, "ItemCollector"),
		BLOCKBREAKER(0, 7, "BlockBreaker"),
		WEATHERCOLLECTOR(0, 8, "WeatherCollector"),
		SLUDGEBOILER(0, 9, "SludgeBoiler"),
		SEWER(0, 10, "Sewer"),
		COMPOSTER(0, 11, "Composter"),
		BREEDER(0, 12, "Breeder"),
		GRINDER(0, 13, "Grinder"),
		AUTOENCHANTER(0, 14, "AutoEnchanter"),
		CHRONOTYPER(0, 15, "Chronotyper"),

		EJECTOR(1, 0, "Ejector"),
		ITEMROUTER(1, 1, "ItemRouter"),
		LIQUIDROUTER(1, 2, "LiquidRouter"),
		DEEPSTORAGEUNIT(1, 3, "DeepStorageUnit");

		private final int group;
		private final int meta;
		private final String name;

		Type(int group, int meta, String name) {

			this.group = group;
			this.meta = meta;
			this.name = name;
		}

		public int getGroup() {
			return group;
		}

		public int getMeta() {
			return meta;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
