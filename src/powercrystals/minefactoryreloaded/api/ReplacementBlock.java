package powercrystals.minefactoryreloaded.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ReplacementBlock
{
	public Block block;
	public int meta;
	public NBTTagCompound tileTag;
	
	public boolean replaceBlock(World world, int x, int y, int z)
	{
		if (world.setBlock(x, y, z, block, meta, 3))
		{
			if (tileTag != null && block.hasTileEntity(meta))
			{
				TileEntity tile = world.getTileEntity(x, y, z);
				if (tile != null)
					tile.readFromNBT(tileTag);
			}
			return true;
		}
		return false;
	}
	
	public ReplacementBlock(Item block)
	{
		this(Block.getBlockFromItem(block));
	}
	
	public ReplacementBlock(Item block, int meta)
	{
		this(Block.getBlockFromItem(block), meta);
	}
	
	public ReplacementBlock(Item block, int meta, NBTTagCompound tag)
	{
		this(Block.getBlockFromItem(block), meta, tag);
	}
	
	public ReplacementBlock(Block block)
	{
		this(block, 0);
	}
	
	public ReplacementBlock(Block block, int meta)
	{
		this(block, meta, null);
	}
	
	public ReplacementBlock(Block _block, int _meta, NBTTagCompound tag)
	{
		block = _block;
		meta = _meta;
		tileTag = tag;
	}
}
