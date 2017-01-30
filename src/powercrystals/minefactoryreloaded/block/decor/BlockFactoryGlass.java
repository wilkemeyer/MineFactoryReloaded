package powercrystals.minefactoryreloaded.block.decor;

import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.SpriteSheetManager;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class BlockFactoryGlass extends BlockGlass implements IRedNetDecorative, IWorldBlockTextureProvider
{
	public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);
	public static final SpriteSheetManager.SpriteSheet spriteSheet = SpriteSheetManager.getSheet(8, 8, new ResourceLocation(MineFactoryReloadedCore.textureFolder + "blocks/tile.mfr.stainedglass.png"));

	public static final String[] _names = { "white", "orange", "magenta", "lightblue", "yellow", "lime",
		"pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black" }; //TODO change to EnumDyeColor

	public BlockFactoryGlass()
	{
		super(Material.GLASS, false);
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		setUnlocalizedName("mfr.stainedglass.block");
		setHardness(0.3F);
		setSoundType(SoundType.GLASS);
		setCreativeTab(MFRCreativeTab.tab);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, COLOR);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(COLOR).getMetadata();
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return getMetaFromState(state);
	}

	@Override
	public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return true;
	}

/*	
	@Override
	public int getRenderBlockPass()
	{
		return 1;
	}


	@Override
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color)
	{
		int meta = getBlockMetadata(x, y, z);
		if (meta != colour)
		{
			return world.setBlockMetadataWithNotify(x, y, z, colour, 3);
		}
		return false;
	}*/

/*	@Override
	public int getRenderColor(int meta)
	{
		return MFRUtil.COLORS[Math.min(Math.max(meta, 0), 15)];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		_texture = ir.registerIcon("minefactoryreloaded:tile.mfr.stainedglass");
	}

	@Override
	public IIcon getIcon(EnumFacing side, int meta)
	{
		return new IconOverlay(_texture, 8, 8, meta > 15 ? 6 : 7, 7);
	}

	public IIcon getBlockOverlayTexture()
	{
		return new IconOverlay(_texture, 8, 8, 0, 0);
	}*/

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		boolean r = super.shouldSideBeRendered(state, world, pos, side);
		
		IBlockState neighborState = world.getBlockState(pos.offset(side));
		if (!r) {
			return getMetaFromState(state) != neighborState.getBlock().getMetaFromState(neighborState);
		}
		return r;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {

		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.SOLID;
	}

	//Extract into separate renderer
	@Override
	public TextureAtlasSprite getTexture(EnumFacing side, IBlockState state, BlockRenderLayer layer, IBlockAccess world, BlockPos pos) {

		BlockPos posToCheck;
		boolean[] sides = new boolean[8];
		if (side.getAxis() == EnumFacing.Axis.Y)
		{
			posToCheck = pos.offset(EnumFacing.EAST);
			sides[0] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.SOUTH);
			sides[4] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.WEST);
			sides[1] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.WEST);
			sides[5] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.NORTH);
			sides[3] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.NORTH);
			sides[6] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.EAST);
			sides[2] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.EAST);
			sides[7] = world.getBlockState(posToCheck).getBlock().equals(this);
		}
		else
		{
			EnumFacing right = side.getAxis() == EnumFacing.Axis.Z ? EnumFacing.VALUES[(side.ordinal() + 2) ^ 1] : EnumFacing.VALUES[(side.ordinal() - 2) ^ 1];
			EnumFacing left = side.getAxis() == EnumFacing.Axis.Z ? EnumFacing.VALUES[(side.ordinal() + 2) ^ 1] : EnumFacing.VALUES[(side.ordinal() - 2) ^ 1];

			posToCheck = pos.offset(right);
			sides[0] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = pos.offset(EnumFacing.DOWN);
			sides[4] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = pos.offset(left);
			sides[1] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = pos.offset(left);
			sides[5] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = pos.offset(EnumFacing.UP);
			sides[3] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = pos.offset(EnumFacing.UP);
			sides[6] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = pos.offset(right);
			sides[2] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = pos.offset(right);
			sides[7] = world.getBlockState(posToCheck).getBlock().equals(this);
		}
		return getSpriteFromSheet(8, 8, sides);
	}

	private TextureAtlasSprite getSpriteFromSheet(int subX, int subY, boolean[] sides) {

		int parts = toInt(sides) & 255;
		int index = (parts & 15);
		parts = parts >> 4;
		int w;
		switch (index) {
			case 3: // bottom right connection
				index ^= ((parts & 1) << 4); // bithack: add 16 if connection
				break;
			case 5: // top right connection
				index ^= ((parts & 8) << 1); // bithack: add 16 if connection
				break;
			case 7: // left empty
				w = parts & 9;
				index ^= ((w & (w << 3)) << 1); // bithack: add 16 if both connections
				if ((w == 1) | w == 8) // bottom right, top right
					index = 32 | (w >> 3);
				break;
			case 10: // bottom left connection
				index ^= ((parts & 2) << 3); // bithack: add 16 if connection
				break;
			case 11: // top empty
				w = parts & 3;
				index ^= ((w & (w << 1)) << 3); // bithack: add 16 if both connections
				if ((w == 1) | w == 2) // bottom right, bottom left
					index = 34 | (w >> 1);
				break;
			case 12: // top left connection
				index ^= ((parts & 4) << 2); // bithack: add 16 if connection
				break;
			case 13: // bottom empty
				w = parts & 12;
				index ^= ((w & (w << 1)) << 1); // bithack: add 16 if both connections
				if ((w == 4) | w == 8) // top left, top right
					index = 36 | (w >> 3);
				break;
			case 14: // right empty
				w = parts & 6;
				index ^= ((w & (w << 1)) << 2); // bithack: add 16 if both connections
				if ((w == 2) | w == 4) // bottom left, top left
					index = 38 | (w >> 2);
				break;
			case 15: // all sides
				index = 40 + parts;
			default:
		}
		return spriteSheet.getSprite(index);
	}

	private static int toInt(boolean ...flags) {
		int ret = 0;
		for (int i = flags.length; i --> 0;)
			ret |= (flags[i] ? 1 : 0) << i;
		return ret;
	}

	@Override
	public TextureAtlasSprite getTexture(EnumFacing side, int metadata) {

		return spriteSheet.getSprite(63); //TODO fix this
	}

/*	public IIcon getBlockOverlayTexture(IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		BlockPos bp;
		boolean[] sides = new boolean[8];
		if (side <= 1)
		{
			bp = new BlockPos(x, y, z, EnumFacing.NORTH);
			bp.moveRight(1);
			sides[0] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveBackwards(1);
			sides[4] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveLeft(1);
			sides[1] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveLeft(1);
			sides[5] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveForwards(1);
			sides[3] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveForwards(1);
			sides[6] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveRight(1);
			sides[2] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveRight(1);
			sides[7] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		}
		else
		{
			bp = new BlockPos(x, y, z, EnumFacing.VALID_DIRECTIONS[side]);
			bp.moveRight(1);
			sides[0] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveDown(1);
			sides[4] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveLeft(1);
			sides[1] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveLeft(1);
			sides[5] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveUp(1);
			sides[3] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveUp(1);
			sides[6] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveRight(1);
			sides[2] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
			bp.moveRight(1);
			sides[7] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		}
		return new IconOverlay(_texture, 8, 8, sides);
	}

	@Override
	public int getRenderType()
	{
		return MineFactoryReloadedCore.renderIdFactoryGlass;
	}*/
}
