package powercrystals.minefactoryreloaded.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nullable;

public class BlockRubberLeaves extends BlockLeaves implements IRedNetNoConnection
{
	public static final PropertyEnum<Type> VARIANT = PropertyEnum.create("variant", Type.class, new Predicate<Type>() {
		public boolean apply(@Nullable Type input) {
			return input.getMetadata() < 4;
		}
	});
	static String[] _names = {null, "dry"};

	public BlockRubberLeaves()
	{
		setUnlocalizedName("mfr.rubberwood.leaves");
		setCreativeTab(MFRCreativeTab.tab);
	}

/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		String unlocalizedName = getUnlocalizedName();
		for (int i = _names.length; i --> 0; )
		{
			String name = getName(unlocalizedName, _names[i]);
			_iconOpaque[i] = ir.registerIcon("minefactoryreloaded:" + name + ".opaque");
			_iconTransparent[i] = ir.registerIcon("minefactoryreloaded:" + name + ".transparent");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return getIcon(side, world.getBlockMetadata(x, y, z));
	}

	@Override
	public IIcon getIcon(EnumFacing side, int meta)
	{
		meta %= 4; // bits 3 and 4 are state flags
		// using mod instead of bitand to preserve sign just incase something is passing us a negative value
		if (meta < 0 || meta >= _names.length) {
			return Blocks.bedrock.getIcon(0, 0); // invalid metadata gets something distinct (green bedrock!)
		}

		return isOpaqueCube() ? _iconOpaque[meta] : _iconTransparent[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int par1)
	{
		return par1 == 1 ? 0xFFFFFF : super.getRenderColor(par1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess iba, BlockPos pos)
	{
		int meta = iba.getBlockMetadata(x, y, z) & 3;
		int r = 0;
		int g = 0;
		int b = 0;

		for (int l1 = -1; l1 <= 1; ++l1)
			for (int i2 = -1; i2 <= 1; ++i2)
			{
				int j2 = iba.getBiomeGenForCoords(x + i2, z + l1).getBiomeFoliageColor(x, y, z);
				r += (j2 & 16711680) >> 16;
			g += (j2 & 65280) >> 8;
			b += j2 & 255;
			}

		r = (r / 9 & 255);
		g = (g / 9 & 255);
		b = (b / 9 & 255);
		if (meta == 1)
			return (r / 4 << 16 | g / 4 << 8 | b / 4) + 0xc0c0c0;

		return r << 16 | g << 8 | b;
	}
*/

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return Blocks.LEAVES.isOpaqueCube(state);
	}

	@Override
	public BlockPlanks.EnumType getWoodType(int i) {
		return null;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(MFRThings.rubberSaplingBlock);
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}

	private ThreadLocal<Boolean> updating = new ThreadLocal<Boolean>();

	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
	{
		if (updating.get() != null)
			return;
		super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		return this.getDefaultState().withProperty(VARIANT, this.getVariant(meta)).withProperty(DECAYABLE, (meta & 4) == 0).withProperty(CHECK_DECAY, (meta & 8) > 0);
	}

	private Type getVariant(int meta) {
		return Type.byMetadata((meta & 3) % 4);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {

		int meta = state.getValue(VARIANT).getMetadata();
		if(!state.getValue(DECAYABLE)) {
			meta |= 4;
		}

		if(state.getValue(CHECK_DECAY)) {
			meta |= 8;
		}

		return meta;	
	}

	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		Random rand = world instanceof World ? ((World)world).rand : RANDOM;

		if (state.getValue(DECAYABLE))
			return ret;

		int chance = 20 + 15 * state.getValue(VARIANT).getMetadata();

		if (fortune > 0)
			chance = Math.max(chance - (2 << fortune), 10);

		if (rand.nextInt(chance) == 0)
			ret.add(new ItemStack(getItemDropped(getDefaultState().withProperty(VARIANT, state.getValue(VARIANT)), rand, fortune), 1,
					rand.nextInt(50000) == 0 ? 2 : 0));

		return ret;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if (world.isRemote)
			return;
		if (state.getValue(VARIANT) == Type.NORMAL && !state.getValue(DECAYABLE))
		{
			boolean decay = state.getValue(CHECK_DECAY);
			if (decay)
			{
				updating.set(Boolean.TRUE);
				super.updateTick(world, pos, state, rand);
				updating.set(null);
				if (!world.getBlockState(pos).getBlock().equals(this))
					dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
				return;
			}
			int chance = 15;
			Biome b = world.getBiome(pos);
			if (b != null)
			{
				float temp = b.getTemperature();
				float rain = b.getRainfall(); // getFloatRainfall is client only!?
				boolean t;
				decay |= (t = rain <= 0.05f);
				if (t) chance -= 5;
				decay |= ((rain <= 0.2f) & temp >= 1.2f);
				decay |= (t = temp > 1.8f);
				if (t) chance -= 5;
				if (rain >= 0.4f & temp <= 1.4f)
					chance += 7;
				else if (temp < 0.8f)
					chance += 3;
			}
			if (decay && rand.nextInt(chance) == 0)
			{
				world.setBlockState(pos, state.withProperty(VARIANT, Type.DRY));
				return;
			}
		}
		super.updateTick(world, pos, state, rand);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		if (updating.get() != null)
		{
			boolean decay = false;
			int chance = 15;
			Biome b = world.getBiome(pos);
			if (b != null)
			{
				float temp = b.getTemperature();
				float rain = b.getRainfall(); // getFloatRainfall is client only!?
				boolean t;
				decay |= (t = rain <= 0.05f);
				if (t) chance -= 5;
				decay |= ((rain <= 0.2f) & temp >= 1.2f);
				decay |= (t = temp > 1.8f);
				if (t) chance -= 5;
				if (rain >= 0.4f & temp <= 1.4f)
					chance += 7;
				else if (temp < 0.8f)
					chance += 3;
			}
			if (decay && world.rand.nextInt(chance) == 0)
				world.setBlockState(pos, state.withProperty(VARIANT, Type.DRY));
		}
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		return super.getFireSpreadSpeed(world, pos, face) * ((world.getBlockState(pos).getValue(VARIANT).getMetadata()) * 2 + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		boolean cube = isOpaqueCube(state);
		if (cube && state.getBlock() == this)
			return false;
		return cube ? super.shouldSideBeRendered(state, world, pos, side) : true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(Item blockId, CreativeTabs creativeTab, List subTypes)
	{
		subTypes.add(new ItemStack(blockId, 1, 0));
		subTypes.add(new ItemStack(blockId, 1, 1));
	}

	@Override
	public List<ItemStack> onSheared(ItemStack itemStack, IBlockAccess iBlockAccess, BlockPos blockPos, int i) {
		return null;
	}

	enum Type implements IStringSerializable {
		NORMAL (0, "normal"),
		DRY(1, "dry");

		private int meta;
		private String name;

		private static final Type[] META_LOOKUP = new Type[values().length];
		Type(int meta, String name) {

			this.meta = meta;
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMetadata() {
			return meta;
		}

		public static Type byMetadata(int meta) {
			
			if(meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		static {
			for(int i = 0; i < values().length; ++i) {
				Type variant = values()[i];
				META_LOOKUP[variant.getMetadata()] = variant;
			}

		}
	}
}
