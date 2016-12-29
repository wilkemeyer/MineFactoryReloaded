package powercrystals.minefactoryreloaded.item.gun.ammo;


import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemNeedlegunAmmoAnvil extends ItemNeedlegunAmmoBlock {

	public ItemNeedlegunAmmoAnvil(IBlockState state) {
		super(state);
		setShots(1);
	}

	public ItemNeedlegunAmmoAnvil() {
		this(Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.EAST));
	}

	@Override
	protected void placeBlockAt(World world, BlockPos pos, double distance) {
		if (!world.isRemote) {
	        EntityFallingBlock anvil = new EntityFallingBlock(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
	        		_blockState);
	        anvil.setHurtEntities(true);
	        world.spawnEntityInWorld(anvil);
	        anvil.fallDistance = ((float)distance) + 1f;
	        anvil.fallTime = 3;
	        anvil.onUpdate();
		}
	}

}
