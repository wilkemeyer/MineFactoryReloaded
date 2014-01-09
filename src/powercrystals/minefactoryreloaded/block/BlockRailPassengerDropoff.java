package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class BlockRailPassengerDropoff extends BlockRailBase
{
	public BlockRailPassengerDropoff(int blockId)
	{
		super(blockId, true);
		setUnlocalizedName("mfr.rail.passenger.dropoff");
		setHardness(0.5F);
		setStepSound(Block.soundMetalFootstep);
		setCreativeTab(MFRCreativeTab.tab);
	}
	
	@Override
	public void onMinecartPass(World world, EntityMinecart minecart, int x, int y, int z)
	{
		if(world.isRemote)
		{
			return;
		}
		if(minecart.riddenByEntity == null || !(minecart.riddenByEntity instanceof EntityPlayer))
		{
			return;
		}
		
		Entity player = minecart.riddenByEntity;
		AxisAlignedBB dropCoords = findSpaceForPlayer(player, x, y, z, world);
		if (dropCoords == null)
			return;
		
		player.mountEntity(null);
		MineFactoryReloadedCore.proxy.movePlayerToCoordinates((EntityPlayer)player,
				dropCoords.minX + (dropCoords.maxX - dropCoords.minX) / 2,
				dropCoords.minY,
				dropCoords.minZ + (dropCoords.maxZ - dropCoords.minZ) / 2);
	}
	
	private AxisAlignedBB findSpaceForPlayer(Entity entity, int x, int y, int z, World world)
	{
        AxisAlignedBB bb = entity.boundingBox.getOffsetBoundingBox((Math.floor(entity.posX) - entity.posX) / 2,
        		(Math.floor(entity.posY) - entity.posY) / 2, (Math.floor(entity.posZ) - entity.posZ) / 2);
		bb.offset((int)bb.minX - bb.minX, (int)bb.minY - bb.minY, (int)bb.minZ - bb.minZ);
		bb.offset(x - bb.minX, 0, z - bb.minZ);
		int searchX = MFRConfig.passengerRailSearchMaxHorizontal.getInt();
		int searchY = MFRConfig.passengerRailSearchMaxVertical.getInt();

        bb.offset(0.25, -searchY + 0.01, 0.25);
		for(int offsetY = -searchY; offsetY < searchY; offsetY++)
		{
			bb.offset(-searchX, 0, 0);
			for(int offsetX = -searchX; offsetX <= searchX; offsetX++)
			{
				bb.offset(0, 0, -searchX);
				for(int offsetZ = -searchX; offsetZ <= searchX; offsetZ++)
				{
					int targetX = MathHelper.floor_double(bb.minX + (bb.maxX - bb.minX) / 2);
					int targetY = MathHelper.floor_double(bb.minY);
					int targetZ = MathHelper.floor_double(bb.minZ + (bb.maxZ - bb.minZ) / 2);

					if(world.getCollidingBlockBounds(bb).isEmpty() &&
							!isBadBlockToStandIn(world, targetX, targetY, targetZ) &&
							!isBadBlockToStandOn(world, targetX, targetY - 1, targetZ))
					{
						return bb;
					}
					bb.offset(0, 0, 1);
				}
				bb.offset(1, 0, -searchX - 1);
			}
			bb.offset(-searchX - 1, 1, 0);
		}
		
		return null;
	}
	
	private boolean isBadBlockToStandOn(World world, int x, int y, int z)
	{
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		if (block == null || block.isAirBlock(world, x, y, z) ||
				isBadBlockToStandIn(world, x, y, z) ||
				!block.isBlockSolidOnSide(world, x, y, z, ForgeDirection.UP))
		{
			return true;
		}
		return false;
	}
	
	private boolean isBadBlockToStandIn(World world, int x, int y, int z)
	{
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		if (block != null && (block.blockMaterial.isLiquid() ||
				block instanceof BlockRailBase))
		{
			return true;
		}
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		blockIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}

    @Override
	protected void func_94358_a(World par1World, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        boolean flag = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4);
        boolean flag1 = false;

        if (flag & (par5 & 8) == 0)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, par6 | 8, 3);
            flag1 = true;
        }
        else if (!flag & (par5 & 8) != 0)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, par6, 3);
            flag1 = true;
        }

        if (flag1)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);

            if (par6 == 2 || par6 == 3 || par6 == 4 || par6 == 5)
            {
                par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
            }
        }
    }
}
