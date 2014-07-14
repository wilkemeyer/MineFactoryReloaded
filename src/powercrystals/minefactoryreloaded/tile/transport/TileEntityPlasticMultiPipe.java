package powercrystals.minefactoryreloaded.tile.transport;

import static powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticMultiPipe.Flags.*;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import powercrystals.minefactoryreloaded.tile.base.TileEntityBase;

public class TileEntityPlasticMultiPipe extends TileEntityBase {
	protected static final class Flags {
		protected static final int EXISTS = 1;
		protected static final int CONNECTED_DOWN = 2;
		protected static final int CONNECTED_UP = 4;
		protected static final int CONNECTED_NORTH = 8;
		protected static final int CONNECTED_SOUTH = 16;
		protected static final int CONNECTED_WEST = 32;
		protected static final int CONNECTED_EAST = 64;
		protected static final int HAS_CACHE = 128;
		protected static final int HAS_NETWORK = 256;
		protected static final int IS_EXTERNAL = 512;
		
		protected static final int[][] LOOKUP = {
			{0, 0, CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_WEST, CONNECTED_EAST, CONNECTED_UP},
			{0, 0, CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_WEST, CONNECTED_EAST, CONNECTED_DOWN},
			{CONNECTED_DOWN, CONNECTED_UP, 0, 0, CONNECTED_WEST, CONNECTED_EAST, CONNECTED_SOUTH},
			{CONNECTED_DOWN, CONNECTED_UP, 0, 0, CONNECTED_WEST, CONNECTED_EAST, CONNECTED_NORTH},
			{CONNECTED_DOWN, CONNECTED_UP, CONNECTED_NORTH, CONNECTED_SOUTH, 0, 0, CONNECTED_EAST},
			{CONNECTED_DOWN, CONNECTED_UP, CONNECTED_NORTH, CONNECTED_SOUTH, 0, 0, CONNECTED_WEST},
			{CONNECTED_DOWN, CONNECTED_UP, CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_WEST, CONNECTED_EAST, 0}};
		protected static final int[][] LOOKUP_B = {
			{CONNECTED_DOWN, 0, 0, 0, 0, 0, 0},
			{0, CONNECTED_UP, 0, 0, 0, 0, 0},
			{0, 0, CONNECTED_NORTH, 0, 0, 0, 0},
			{0, 0, 0, CONNECTED_SOUTH, 0, 0, 0},
			{0, 0, 0, 0, CONNECTED_WEST, 0, 0},
			{0, 0, 0, 0, 0, CONNECTED_EAST, 0},
			{0, 0, 0, 0, 0, 0, 0}};
		
		protected static final ForgeDirection getTo(int dir) {
			switch (dir) {
			case CONNECTED_DOWN:
				return ForgeDirection.UP;
			case CONNECTED_UP:
				return ForgeDirection.DOWN;
			case CONNECTED_NORTH:
				return ForgeDirection.SOUTH;
			case CONNECTED_SOUTH:
				return ForgeDirection.NORTH;
			case CONNECTED_WEST:
				return ForgeDirection.EAST;
			case CONNECTED_EAST:
				return ForgeDirection.WEST;
			default:
				return ForgeDirection.UNKNOWN; 
			}
		}
		protected static final ForgeDirection getFrom(int dir) {
			switch (dir) {
			case CONNECTED_DOWN:
				return ForgeDirection.DOWN;
			case CONNECTED_UP:
				return ForgeDirection.UP;
			case CONNECTED_NORTH:
				return ForgeDirection.NORTH;
			case CONNECTED_SOUTH:
				return ForgeDirection.SOUTH;
			case CONNECTED_WEST:
				return ForgeDirection.WEST;
			case CONNECTED_EAST:
				return ForgeDirection.EAST;
			default:
				return ForgeDirection.UNKNOWN; 
			}
		}
	}
	protected int[] faces = {0,0,0, 0,0,0, 0};
	protected FluidNetwork[] networks = {null,null,null, null,null,null, null};
	protected IFluidHandler[] cache = {null,null,null, null,null,null};

	public void addPipeToSide(ForgeDirection side) {
		int bSide = side.ordinal();
		faces[bSide] |= EXISTS;
		FluidNetwork network = networks[bSide];
		int s = -1, c = 0;
		switch (side) {
		case DOWN:
		case UP:
			for (int i = 7; i --> 3; )
				if (network.canMergeGrid(networks[i])) {
					if (s == -1 || networks[i] != networks[s]) ++c;
					s = i;
				}
			break;
		case NORTH:
		case SOUTH:
			for (int i = 7; i --> 0; )
				if ((i != 2) & i != 3)
					if (network.canMergeGrid(networks[i])) {
						if (s == -1 || networks[i] != networks[s]) ++c;
						s = i;
					}
			break;
		case WEST:
		case EAST:
			for (int i = 7; i --> 0; )
				if ((i != 4) & i != 5)
					if (network.canMergeGrid(networks[i]))
					{
						if (s == -1 || networks[i] != networks[s]) ++c;
						s = i;
					}
			break;
		case UNKNOWN:
			for (int i = 6; i --> 0; )
				if (network.canMergeGrid(networks[i])) {
					if (s == -1 || networks[i] != networks[s]) ++c;
					s = i;
				}
			break;
		}
		if (c == 1)
			network.mergeGrid(networks[s]);
		updateConnectionStatus(side);
	}
	
	private void updateConnectionStatus(ForgeDirection side) {
		ForgeDirection f;
		int bSide = side.ordinal(), dir;
		int[] lookup = LOOKUP[bSide];
		FluidNetwork network = networks[bSide];
		faces[bSide] &= EXISTS;
		for (int i = 7; i --> 0; )
			if ((dir = lookup[i]) == 0) {
				if (cache[i] != null)
					if (cache[i] instanceof TileEntityPlasticMultiPipe)
						faces[bSide] |= LOOKUP_B[bSide][i] | HAS_NETWORK | IS_EXTERNAL;
					else if (cache[i].canFill(f = getTo(dir), null) ||
								 cache[i].canDrain(f, null))
						faces[bSide] |= LOOKUP_B[bSide][i] | HAS_CACHE | IS_EXTERNAL;
			} else if (network == networks[i])
				faces[bSide] |= dir | HAS_NETWORK;
			else if (i < 6 && cache[i] != null &&
					(cache[i].canFill(f = getTo(dir), null) ||
					 cache[i].canDrain(f, null)))
				faces[bSide] |= dir | HAS_CACHE;
	}
}
