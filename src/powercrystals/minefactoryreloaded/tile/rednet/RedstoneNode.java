package powercrystals.minefactoryreloaded.tile.rednet;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class RedstoneNode {

	BlockPos pos;
	EnumFacing facing;

	public RedstoneNode(BlockPos pos, EnumFacing facing) {

		this.pos = pos;
		this.facing = facing;
	}

	public BlockPos getPos() {

		return pos;
	}

	public EnumFacing getFacing() {

		return facing;
	}

	@Override
	public boolean equals(Object obj) {

		if(super.equals(obj))
			return true;

		if(obj instanceof RedstoneNode) {
			RedstoneNode other = (RedstoneNode) obj;

			return pos.equals(other.pos) && facing.equals(other.facing);
		}

		return false;
	}
}
