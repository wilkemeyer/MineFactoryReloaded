package powercrystals.minefactoryreloaded.block.transport;

import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.renderIdPPipe;
import static powercrystals.minefactoryreloaded.block.transport.BlockRedNetCable._subSideMappings;

import codechicken.lib.raytracer.RayTracer;
import cofh.api.block.IBlockInfo;
import cofh.lib.util.helpers.ItemHelper;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.BlockFactory;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.render.block.PlasticPipeRenderer;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe;

public class BlockPlasticPipe extends BlockFactory implements IBlockInfo {

	public BlockPlasticPipe() {

		super(0.8F);
		setUnlocalizedName("mfr.cable.plastic");
		providesPower = true;
	}

	@Override
	public boolean activated(World world, BlockPos pos, EntityPlayer player, EnumFacing side) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityPlasticPipe) {
			TileEntityPlasticPipe cable = (TileEntityPlasticPipe) te;

			harvesters.set(player);
			IBlockState state = world.getBlockState(pos);
			RayTraceResult part = collisionRayTrace(state, world, pos, RayTracer.getStartVec(player), RayTracer.getEndVec(player));
			harvesters.set(null);
			if (part == null)
				return false;

			int subHit = part.subHit;
			if (subHit < 0) {
				MineFactoryReloadedCore.instance().getLogger().error("subHit was " + subHit, new Throwable());
				return false;
			}
			int subSide = _subSideMappings[subHit];

			ItemStack s = player.getActiveItemStack();
			EntityEquipmentSlot activeSlot = player.getActiveHand() == EnumHand.OFF_HAND ? EntityEquipmentSlot.OFFHAND : EntityEquipmentSlot.MAINHAND;

			l2: if (cable.onPartHit(player, EnumFacing.VALUES[subSide], subHit)) {
				if (MFRUtil.isHoldingUsableTool(player, pos)) {
					MFRUtil.usedWrench(player, pos);
				}
			}
			else if (s != null && s.isItemEqual(new ItemStack(Blocks.REDSTONE_TORCH))) {
				int t = cable.getUpgrade();
				if (t != 0) {
					if (t == 1) break l2;
					if (t == 2) 	
						UtilInventory.dropStackInAir(world, pos, new ItemStack(Blocks.REDSTONE_BLOCK));
				}
				if (!world.isRemote) {
					if (!player.capabilities.isCreativeMode) {
						player.setItemStackToSlot(activeSlot, ItemHelper.consumeItem(s));
						
					}
					cable.setUpgrade(1);
					neighborChanged(state, world, pos, Blocks.AIR);
					player.addChatMessage(new TextComponentTranslation(
							"chat.info.mfr.fluid.install.torch"));
				}
				return true;
			}
			else if (s != null && s.isItemEqual(new ItemStack(Blocks.REDSTONE_BLOCK))) {
				int t = cable.getUpgrade();
				if (t != 0) {
					if (t == 2) break l2;
					if (t == 1)
						UtilInventory.dropStackInAir(world, pos, new ItemStack(Blocks.REDSTONE_TORCH));
				}
				if (!world.isRemote) {
					if (!player.capabilities.isCreativeMode)
						player.setItemStackToSlot(activeSlot, ItemHelper.consumeItem(s));
					cable.setUpgrade(2);
					neighborChanged(state, world, pos, Blocks.AIR);
					player.addChatMessage(new TextComponentTranslation(
							"chat.info.mfr.fluid.install.block"));
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		Random rand = world instanceof World ? ((World)world).rand : RANDOM;

		ItemStack machine = new ItemStack(getItemDropped(state, rand, fortune), 1, damageDropped(state));
		drops.add(machine);

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityPlasticPipe) {
			switch (((TileEntityPlasticPipe) te).getUpgrade()) {
			case 1:
				drops.add(new ItemStack(Blocks.REDSTONE_TORCH));
				break;
			case 2:
				drops.add(new ItemStack(Blocks.REDSTONE_BLOCK));
				break;
			}
		}

		return drops;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {

		return false;
	}

/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		blockIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		PlasticPipeRenderer.updateUVT(blockIcon);
	}
*/

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {

		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		return new TileEntityPlasticPipe();
	}
}
