package skyboy.core.container;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import powercrystals.minefactoryreloaded.api.IUseHandler;

import skyboy.core.fluid.LiquidRegistry;

public class CarbonContainer extends ItemBucket {
	public static CarbonContainer cell;

	public static void registerAsContainer(Fluid fluid) {
		FluidStack stack = FluidRegistry.getFluidStack(fluid.getName(), FluidContainerRegistry.BUCKET_VOLUME);
		stack = stack.copy(); stack.amount = cell.volume;
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(stack,
				new ItemStack(cell, 1, LiquidRegistry.getID(stack)),
				new ItemStack(cell, 1, 0)));
	}
	
	private final static DefaultUseHandler defaultUseAction = new DefaultUseHandler();

	boolean canPlaceInWorld;
	boolean canBeFilledFromWorld;
	protected Item filledItem, emptyItem;
	protected int volume;
	protected Icon[] icons;
	protected List<IUseHandler> useHandlers;

	public CarbonContainer(int id, String name) {
		this(id, 64, name);
	}

	public CarbonContainer(int id, String name, int volume) {
		this(id, 64, name, volume);
	}

	public CarbonContainer(int id, int stackSize, String name) {
		this(id, stackSize, name, false);
	}

	public CarbonContainer(int id, int stackSize, String name, int volume) {
		this(id, stackSize, name, volume, false);
	}

	public CarbonContainer(int id, int stackSize, String name, boolean canPlace) {
		this(id, stackSize, name, FluidContainerRegistry.BUCKET_VOLUME, canPlace);
	}

	public CarbonContainer(int id, int stackSize, String name, int volume, boolean canPlace) {
		super(id, 0);
		setMaxStackSize(stackSize);
		setHasSubtypes(true);
		setMaxDamage(0);
		setUnlocalizedName(name);
		canBeFilledFromWorld = volume >= FluidContainerRegistry.BUCKET_VOLUME;
		canPlaceInWorld = canPlace && canBeFilledFromWorld;
		this.volume = volume;
		useHandlers = new LinkedList<IUseHandler>();
		useHandlers.add(defaultUseAction);
	}
	
	@Override
	public Item setUnlocalizedName(String name)
	{
		super.setUnlocalizedName(name);
		GameRegistry.registerItem(this, getUnlocalizedName());
		return this;
	}

	public CarbonContainer setFilledItem(Item item) {
		filledItem = item;
		return this;
	}

	public CarbonContainer setEmptyItem(Item item) {
		emptyItem = item;
		return this;
	}

	public boolean addUseHandler(IUseHandler handler) {
		return useHandlers.add(handler);
	}

	public boolean removeUseHandler(IUseHandler handler) {
		return useHandlers.remove(handler);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer entity) {
		for (IUseHandler handler : useHandlers)
			if (handler.canUse(item, entity))
				return handler.onTryUse(item, world, entity);
		return item;
	}

	@Override
	public boolean tryPlaceContainedLiquid(World par1World, int par8, int par9, int par10) {
		return false;
	}

	public ItemStack tryPlaceContainedLiquid(World world, ItemStack bucket, int x, int y, int z, int side) {
		if (world.isRemote) return bucket;
		CarbonContainer item = (CarbonContainer)bucket.getItem();
		int id = bucket.getItemDamage();
		if (id != 0) {
			if (!item.canPlaceInWorld) return bucket;
			FluidStack liquid = LiquidRegistry.getLiquid(id);
			if (world.setBlock(x, y, z, liquid.getFluid().getBlockID(), 0, 3))
				return item.getContainerItemStack(bucket);
			return bucket;
		}
		if (!item.canBeFilledFromWorld) return bucket;
		int blockID = world.getBlockId(x, y, z);
		Block block = Block.blocksList[blockID];
		if (block instanceof IFluidBlock) {
			FluidStack liquid = ((IFluidBlock)block).drain(world, x, y, z, false);
			if (liquid != null && FluidRegistry.isFluidRegistered(liquid.getFluid())) {
				ItemStack r = FluidContainerRegistry.fillFluidContainer(liquid, bucket);
				if (r != null && FluidContainerRegistry.isFilledContainer(r)) {
					((IFluidBlock)block).drain(world, x, y, z, true);
					return r;
				}
			}
		}
		return bucket;
	}

	@Override
	public ItemStack onEaten(ItemStack item, World world, EntityPlayer entity) {
		for (IUseHandler handler : useHandlers)
			if (handler.isUsable(item))
				return handler.onUse(item, entity);
		return item;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack item) {
		for (IUseHandler handler : useHandlers)
			if (handler.isUsable(item))
				return handler.useAction(item);
		return EnumAction.none;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack item) {
		for (IUseHandler handler : useHandlers)
			if (handler.isUsable(item))
				return handler.getMaxUseDuration(item);
		return 0;
	}

	public ItemStack setLiquid(ItemStack bucket, FluidStack liquid, EntityPlayer par3EntityPlayer) {
		CarbonContainer item = (CarbonContainer)bucket.getItem();
		if (liquid == null || liquid.amount < item.volume) return bucket;
		int id = LiquidRegistry.getID(liquid);
		if (--bucket.stackSize <= 0) {
			bucket = new ItemStack(item.filledItem, 1, id);
		} else {
			ItemStack bucket2 = new ItemStack(item.filledItem, 1, id);
			if (!par3EntityPlayer.inventory.addItemStackToInventory(bucket2))
				par3EntityPlayer.dropPlayerItem(bucket2);
		}
		liquid.amount -= item.volume;
		return bucket;
	}

	public static ItemStack setLiquid(ItemStack bucket, FluidStack liquid) {
		if (LiquidRegistry.getName(liquid) != null) {
			bucket.stackSize = 1;
			bucket.setItemDamage(LiquidRegistry.getID(liquid));
		}
		return bucket;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister par1IconRegister) {
		icons = new Icon[2];
		icons[0] = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		icons[1] = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".fill");
	}

	public FluidStack getLiquid(ItemStack bucket) {
		CarbonContainer item = (CarbonContainer)bucket.getItem();
		return LiquidRegistry.getLiquid(bucket.getItemDamage(), item.volume);
	}

	private boolean _prefix = false;

	@Override
	public String getUnlocalizedName(ItemStack item) {
		if (item != null && item.itemID == filledItem.itemID && item.getItemDamage() != 0)
			return getUnlocalizedName() + (_prefix ? ".prefix" : ".suffix");
		return getUnlocalizedName();
	}

	public String getLocalizedName(String str) {
		String name = getUnlocalizedName() + "." + str;
		if (StatCollector.func_94522_b(name))
			return StatCollector.translateToLocal(name);
		return null;
	}

	@Override
	public String getItemDisplayName(ItemStack item) {
		int id = item.getItemDamage();
		if (id != 0) {
			String ret = LiquidRegistry.getName(id), t = getLocalizedName(ret);
			if (t != null && !t.isEmpty())
				return EnumChatFormatting.RESET + t + EnumChatFormatting.RESET;
			Fluid liquid = FluidRegistry.getFluid(ret);
			if (liquid != null)
				ret = liquid.getLocalizedName();
			_prefix = true;
			t = super.getItemDisplayName(item);
			_prefix = false;
			t = t != null ? t.trim() : "";
			ret = (t.isEmpty() ? "" : t + " ") + ret;
			t = super.getItemDisplayName(item);
			t = t != null ? t.trim() : "";
			ret += t.isEmpty() ? " " + getTranslatedBucketName() : " " + t;
			return ret;
		}
		return super.getItemDisplayName(item);
	}

	protected String getTranslatedBucketName() {
		return Item.bucketEmpty.getItemDisplayName(FluidContainerRegistry.EMPTY_BUCKET);
	}

	@Override
	public ItemStack getContainerItemStack(ItemStack itemStack) {
		if (itemStack.getItemDamage() != 0 && hasContainerItem())
			return new ItemStack(getContainerItem(), 1, 0);
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, @SuppressWarnings("rawtypes") List par3List) {
		if (emptyItem.itemID == par1) par3List.add(new ItemStack(par1, 1, 0));
		if (filledItem.itemID == par1)
			for (int i = 0, e = LiquidRegistry.getRegisteredLiquidCount(); i++ < e; )
				par3List.add(new ItemStack(par1, 1, i));
	}

	@Override
	public Icon getIcon(ItemStack stack, int pass) {
		return icons[pass];
	}

	MovingObjectPosition rayTrace(World world, EntityLivingBase entity, boolean adjacent) {
		float f1 = entity.rotationPitch;
		float f2 = entity.rotationYaw;
		double y = entity.posY + entity.getEyeHeight() - entity.yOffset;
		Vec3 vec3 = world.getWorldVec3Pool().getVecFromPool(entity.posX, y, entity.posZ);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = 5.0D;
		if (entity instanceof EntityPlayerMP)
		{
			d3 = ((EntityPlayerMP)entity).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);
		MovingObjectPosition ret = world.rayTraceBlocks_do_do(vec3, vec31, adjacent, !adjacent);
		if (ret != null && adjacent) {
			ForgeDirection side = ForgeDirection.getOrientation(ret.sideHit);
			ret.blockX += side.offsetX;
			ret.blockY += side.offsetY;
			ret.blockZ += side.offsetZ;
		}
		return ret;
	}
}