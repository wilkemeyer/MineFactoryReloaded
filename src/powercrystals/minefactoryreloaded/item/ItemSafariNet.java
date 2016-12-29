package powercrystals.minefactoryreloaded.item;

import net.minecraft.block.BlockFence;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.IMobEggHandler;
import powercrystals.minefactoryreloaded.api.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.ISafariNetHandler;
import powercrystals.minefactoryreloaded.api.RandomMob;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.village.VillageTradeHandler;

public class ItemSafariNet extends ItemFactory {

	private final boolean multiuse;
	private final int type;

	public ItemSafariNet(int type) {

		this(type, false);
	}

	public ItemSafariNet(int type, boolean multiuse) {

		this.multiuse = multiuse;
		this.type = type;
		setMaxStackSize(multiuse ? 12 : 1);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {

		if (!isSingleUse(stack) || !isEmpty(stack))
			return 1;
		return maxStackSize;
	}

	@Override
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips) {

		super.addInfo(stack, player, infoList, advancedTooltips);

		int type = ((ItemSafariNet) stack.getItem()).type;
		if (1 == (type & 1)) {
			infoList.add(I18n.translateToLocal("tip.info.mfr.safarinet.persistent"));
		}

		if (2 == (type & 2)) {
			infoList.add(I18n.translateToLocal("tip.info.mfr.safarinet.nametag"));
		}

		if (stack.getTagCompound() == null) {
			return;
		}

		if (stack.getTagCompound().getBoolean("hide")) {
			infoList.add(I18n.translateToLocal("tip.info.mfr.safarinet.mystery"));
		} else {
			infoList.add(MFRUtil.localize("entity.", stack.getTagCompound().getString("id")));
			// See Entity.getEntityName()
			Class<?> c = EntityList.NAME_TO_CLASS.get(stack.getTagCompound().getString("id"));
			if (c == null) {
				return;
			}
			for (ISafariNetHandler handler : MFRRegistry.getSafariNetHandlers()) {
				if (handler.validFor().isAssignableFrom(c)) {
					handler.addInformation(stack, player, infoList, advancedTooltips);
				}
			}
		}
	}

/*
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {

		if (isEmpty(stack)) return _iconEmpty;
		if (pass == 0)
			return _iconBack;
		else if (pass == 1)
			return _iconMid;
		else if (pass == 2) return _iconFront;
		return null;
	}

	@Override
	public void registerIcons(IIconRegister ir) {

		_iconEmpty = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".empty");
		_iconBack = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".back");
		_iconMid = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".mid");
		_iconFront = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".front");

		itemIcon = _iconEmpty;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {

		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {

		return 3;
	}

	private Random colorRand = new Random();

	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {

		if (stack.getItemDamage() == 0 && (stack.getTagCompound() == null)) {
			return 16777215;
		}
		if (stack.getTagCompound() != null && stack.getTagCompound().getBoolean("hide")) {
			World world = Minecraft.getMinecraft().theWorld;
			colorRand.setSeed(world.getSeed() ^ (world.getTotalWorldTime() / (7 * 20)) * pass);
			if (pass == 2)
				return colorRand.nextInt();
			else if (pass == 1)
				return colorRand.nextInt();
			else
				return 16777215;
		}
		EntityEggInfo egg = getEgg(stack);

		if (egg == null) {
			return 16777215;
		} else if (pass == 2) {
			return egg.primaryColor;
		} else if (pass == 1) {
			return egg.secondaryColor;
		} else {
			return 16777215;
		}
	}
*/

	private EntityEggInfo getEgg(ItemStack safariStack) {

		if (safariStack.getTagCompound() == null) {
			return null;
		}

		for (IMobEggHandler handler : MFRRegistry.getModMobEggHandlers()) {
			EntityEggInfo egg = handler.getEgg(safariStack);
			if (egg != null) {
				return egg;
			}
		}

		return null;
	}

	@Override
	public EnumActionResult onItemUse(ItemStack itemstack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side,
			float xOffset, float yOffset, float zOffset) {

		if (world.isRemote) {
			return EnumActionResult.SUCCESS;
		} else if (isEmpty(itemstack)) {
			return EnumActionResult.SUCCESS;
		} else {
			if (player != null && player.capabilities.isCreativeMode)
				itemstack = itemstack.copy();
			return releaseEntity(itemstack, world, pos, side) != null ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
		}
	}

	public static Entity releaseEntity(ItemStack itemstack, World world, BlockPos pos, EnumFacing side) {

		if (world.isRemote) {
			return null;
		}

		Entity spawnedCreature;
		Block block = world.getBlockState(pos).getBlock();
		pos = pos.offset(side);
		double spawnOffsetY = 0.0D;

		if (side == EnumFacing.UP && block instanceof BlockFence) {
			spawnOffsetY = 0.5D;
		}

		if (itemstack.getItemDamage() != 0) {
			spawnedCreature = spawnCreature(world, itemstack.getItemDamage(), pos.getX() + 0.5D, pos.getY() + spawnOffsetY, pos.getZ() + 0.5D);
		} else {
			spawnedCreature = spawnCreature(world, itemstack.getTagCompound(), pos.getX() + 0.5D, pos.getY() + spawnOffsetY, pos.getZ() + 0.5D, side);
		}

		if (spawnedCreature != null) {
			if ((spawnedCreature instanceof EntityLiving)) {
				int type = ((ItemSafariNet) itemstack.getItem()).type;
				if (1 == (type & 1)) {
					((EntityLiving) spawnedCreature).enablePersistence();
				}
				if (itemstack.hasDisplayName()) {
					spawnedCreature.setCustomNameTag(itemstack.getDisplayName());
					if (2 == (type & 2))
						spawnedCreature.setAlwaysRenderNameTag(true);
				}
			}

			if (isSingleUse(itemstack)) {
				itemstack.stackSize--;
			} else if (itemstack.getItemDamage() != 0) {
				itemstack.setItemDamage(0);
			}
			itemstack.setTagCompound(null);
		}

		return spawnedCreature;
	}

	private static Entity spawnCreature(World world, NBTTagCompound mobTag, double x, double y, double z, EnumFacing side) {

		Entity e;
		if (mobTag.getBoolean("hide")) {
			List<RandomMob> mobs = new ArrayList<RandomMob>();

			for (IRandomMobProvider p : MFRRegistry.getRandomMobProviders()) {
				mobs.addAll(p.getRandomMobs(world));
			}
			RandomMob mob = WeightedRandom.getRandomItem(world.rand, mobs);
			e = mob.getMob();
			if (e instanceof EntityLiving && mob.shouldInit)
				((EntityLiving) e).onInitialSpawn(world.getDifficultyForLocation(e.getPosition()), null);
		} else {
			NBTTagList pos = mobTag.getTagList("Pos", 6);
			pos.set(0, new NBTTagDouble(x));
			pos.set(1, new NBTTagDouble(y));
			pos.set(2, new NBTTagDouble(z));

			e = EntityList.createEntityFromNBT(mobTag, world);
			if (e != null) {
				e.readFromNBT(mobTag);
			}
		}

		if (e != null) {
			int offsetX = side.getFrontOffsetX();
			int offsetY = side == EnumFacing.DOWN ? -1 : 0;
			int offsetZ = side.getFrontOffsetZ();
			AxisAlignedBB bb = e.getEntityBoundingBox();

			e.setLocationAndAngles(x + (bb.maxX - bb.minX) * 0.5 * offsetX,
				y + (bb.maxY - bb.minY) * 0.5 * offsetY,
				z + (bb.maxZ - bb.minZ) * 0.5 * offsetZ,
				world.rand.nextFloat() * 360.0F, 0.0F);

			world.spawnEntityInWorld(e);
			if (e instanceof EntityLiving) {
				((EntityLiving) e).playLivingSound();
			}

			Entity riddenByEntity = e.isBeingRidden() ? e.getPassengers().get(0) : null;
			while (riddenByEntity != null) {
				riddenByEntity.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);

				world.spawnEntityInWorld(riddenByEntity);
				if (riddenByEntity instanceof EntityLiving) {
					((EntityLiving) riddenByEntity).playLivingSound();
				}

				riddenByEntity = riddenByEntity.isBeingRidden() ? riddenByEntity.getPassengers().get(0) : null;
			}
		}

		return e;
	}

	private static Entity spawnCreature(World world, int mobId, double x, double y, double z) {

		if (!EntityList.ENTITY_EGGS.containsKey(Integer.valueOf(mobId))) {
			return null;
		} else {
			Entity e = EntityList.createEntityByID(mobId, world);

			if (e != null) {
				e.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);
				if (e instanceof EntityLiving)
					((EntityLiving) e).onInitialSpawn(world.getDifficultyForLocation(e.getPosition()), null);
				world.spawnEntityInWorld(e);
				if (e instanceof EntityLiving)
					((EntityLiving) e).playLivingSound();
			}

			return e;
		}
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {

		return captureEntity(itemstack, entity, player);
	}

	public static boolean captureEntity(ItemStack itemstack, EntityLivingBase entity) {

		return captureEntity(itemstack, entity, null);
	}

	public static boolean captureEntity(ItemStack itemstack, EntityLivingBase entity, EntityPlayer player) {

		if (entity.worldObj.isRemote) {
			return false;
		}
		if (!isEmpty(itemstack)) {
			return false;
		} else if (MFRRegistry.getSafariNetBlacklist().contains(entity.getClass())) {
			return false;
		}
		else if (!(entity instanceof EntityPlayer)) {
			boolean flag = player != null && player.capabilities.isCreativeMode;
			NBTTagCompound c = new NBTTagCompound();

			synchronized (entity) {
				entity.writeToNBT(c);

				c.setString("id", EntityList.getEntityString(entity));

				if (entity.isDead)
					return false;

				if (!flag)
					entity.setDead();
				if (flag | entity.isDead) {
					flag = false;
					if (--itemstack.stackSize > 0) {
						flag = true;
						itemstack = itemstack.copy();
					}
					itemstack.stackSize = 1;
					itemstack.setTagCompound(c);
					if (flag && (player == null || !player.inventory.addItemStackToInventory(itemstack)))
						UtilInventory.dropStackInAir(entity.worldObj, entity, itemstack);
					else if (flag) {
						player.openContainer.detectAndSendChanges();
						((EntityPlayerMP) player).updateCraftingInventory(player.openContainer,
							player.openContainer.getInventory());
					}

					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isEmpty(ItemStack s) {

		return !isSafariNet(s) || (s.getItemDamage() == 0 && (s.getTagCompound() == null || (!s.getTagCompound().hasKey("id") && !s.getTagCompound().getBoolean("hide"))));
	}

	public static boolean isSingleUse(ItemStack s) {

		return isSafariNet(s) && !((ItemSafariNet) s.getItem()).multiuse;
	}

	public static boolean isSafariNet(ItemStack s) {

		return s != null && (s.getItem() instanceof ItemSafariNet);
	}

	public static ItemStack makeMysteryNet(ItemStack s) {

		if (isSafariNet(s)) {
			NBTTagCompound c = new NBTTagCompound();
			c.setBoolean("hide", true);
			s.setTagCompound(c);
		}
		return s;
	}

	public static Class<?> getEntityClass(ItemStack s) {

		if (!isSafariNet(s) || isEmpty(s))
			return null;
		if (s.getItemDamage() != 0) {
			int mobId = s.getItemDamage();
			if (!EntityList.ENTITY_EGGS.containsKey(Integer.valueOf(mobId)))
				return null;
			return (Class<?>) EntityList.ID_TO_CLASS.get(mobId);
		} else {
			String mobId = s.getTagCompound().getString("id");
			if (!EntityList.NAME_TO_CLASS.containsKey(mobId))
				return null;
			return (Class<?>) EntityList.NAME_TO_CLASS.get(mobId);
		}
	}

	@Override
	public void getSubItems(Item item, List<ItemStack> subTypes) {

		super.getSubItems(item, subTypes);
		if (item.equals(MFRThings.safariNetSingleItem)) {
			subTypes.add(VillageTradeHandler.getHiddenNetStack());
		}
	}

}
