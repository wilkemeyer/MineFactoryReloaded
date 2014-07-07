package powercrystals.minefactoryreloaded.block;

import cofh.api.block.IDismantleable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.Machine;

public class BlockFactory extends Block implements IRedNetOmniNode, IDismantleable
{
	@SideOnly(Side.CLIENT)
	private IIcon topIcon;
	@SideOnly(Side.CLIENT)
	private IIcon bottomIcon;
	
	public BlockFactory()
	{
		super(Machine.MATERIAL);
		setHardness(0.5F);
		setStepSound(soundTypeMetal);
		setCreativeTab(MFRCreativeTab.tab);
		setBlockName("mfr.machineblock");
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z)
	{
		return true;
	}

	@Override
	public ItemStack dismantleBlock(EntityPlayer player, World world, int x, int y, int z,
			boolean returnBlock)
	{
		ItemStack machine = new ItemStack(getItemDropped(world.getBlockMetadata(x, y, z),
				world.rand, 0), 1, damageDropped(world.getBlockMetadata(x, y, z)));

		world.setBlockToAir(x, y, z);
		if (!returnBlock)
			dropBlockAsItem(world, x, y, z, machine);
		return machine;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		bottomIcon = ir.registerIcon("minefactoryreloaded:machines/tile.mfr.machine.0.bottom");
		blockIcon = ir.registerIcon("minefactoryreloaded:machines/tile.mfr.machine.0.active.side");
		topIcon = ir.registerIcon("minefactoryreloaded:machines/tile.mfr.machine.0.top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		switch (side)
		{
		case 0:
			return bottomIcon;
		case 1:
			return topIcon;
		default:
			return blockIcon;
		}
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
	{
		return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
	{
		return isProvidingWeakPower(world, x, y, z, side);
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
	public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side)
	{
		return RedNetConnectionType.DecorativeSingle;
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
	}
}
