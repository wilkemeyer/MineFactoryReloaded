package buildcraft.api.transport.neptune;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EnumWirePart {
    EAST_UP_SOUTH(true, true, true),
    EAST_UP_NORTH(true, true, false),
    EAST_DOWN_SOUTH(true, false, true),
    EAST_DOWN_NORTH(true, false, false),
    WEST_UP_SOUTH(false, true, true),
    WEST_UP_NORTH(false, true, false),
    WEST_DOWN_SOUTH(false, false, true),
    WEST_DOWN_NORTH(false, false, false);

    public static final EnumWirePart[] VALUES = values();

    public final AxisDirection x, y, z;

    /** The bounding box for rendering a wire or selecting an already-placed wire. */
    public final AxisAlignedBB boundingBox;

    /** The bounding box that is used when adding pipe wire to a pipe */
    public final AxisAlignedBB boundingBoxPossible;

    public final double[][] poses;
    public final double[][] texes;

    EnumWirePart(boolean x, boolean y, boolean z) {
        this.x = x ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE;
        this.y = y ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE;
        this.z = z ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE;
        double x1 = this.x.getOffset() * (5 / 16.0) + 0.5;
        double y1 = this.y.getOffset() * (5 / 16.0) + 0.5;
        double z1 = this.z.getOffset() * (5 / 16.0) + 0.5;
        double x2 = this.x.getOffset() * (4 / 16.0) + 0.5;
        double y2 = this.y.getOffset() * (4 / 16.0) + 0.5;
        double z2 = this.z.getOffset() * (4 / 16.0) + 0.5;
        this.boundingBox = new AxisAlignedBB(x1, y1, z1, x2, y2, z2);

        Vec3d center = new Vec3d(0.5, 0.5, 0.5);
        Vec3d edge = new Vec3d(x ? 0.75 : 0.25, y ? 0.75 : 0.25, z ? 0.75 : 0.25);
        this.boundingBoxPossible = new AxisAlignedBB(center, edge);

        poses = getPoses(boundingBox);
        texes = getTexes(boundingBox);
    }

    public static double[][] getPoses(AxisAlignedBB bb) {
        return new double[][] {
                {bb.minX, bb.maxY, bb.minZ},
                {bb.maxX, bb.maxY, bb.minZ},
                {bb.maxX, bb.minY, bb.minZ},
                {bb.minX, bb.minY, bb.minZ},
                {bb.minX, bb.minY, bb.maxZ},
                {bb.maxX, bb.minY, bb.maxZ},
                {bb.maxX, bb.maxY, bb.maxZ},
                {bb.minX, bb.maxY, bb.maxZ},
                {bb.minX, bb.minY, bb.minZ},
                {bb.maxX, bb.minY, bb.minZ},
                {bb.maxX, bb.minY, bb.maxZ},
                {bb.minX, bb.minY, bb.maxZ},
                {bb.minX, bb.maxY, bb.maxZ},
                {bb.maxX, bb.maxY, bb.maxZ},
                {bb.maxX, bb.maxY, bb.minZ},
                {bb.minX, bb.maxY, bb.minZ},
                {bb.minX, bb.minY, bb.maxZ},
                {bb.minX, bb.maxY, bb.maxZ},
                {bb.minX, bb.maxY, bb.minZ},
                {bb.minX, bb.minY, bb.minZ},
                {bb.maxX, bb.minY, bb.minZ},
                {bb.maxX, bb.maxY, bb.minZ},
                {bb.maxX, bb.maxY, bb.maxZ},
                {bb.maxX, bb.minY, bb.maxZ}
        };
    }

    public static double[][] getTexes(AxisAlignedBB bb) {
        Vec3d renderingScale = new Vec3d(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).scale(16);
        return new double[][] {
                {0/*                */, 0/*                */},
                {renderingScale.xCoord, 0/*                */},
                {renderingScale.xCoord, renderingScale.yCoord},
                {0/*                */, renderingScale.yCoord},
                {0/*                */, 0/*                */},
                {renderingScale.xCoord, 0/*                */},
                {renderingScale.xCoord, renderingScale.yCoord},
                {0/*                */, renderingScale.yCoord},
                {0/*                */, 0/*                */},
                {renderingScale.xCoord, 0/*                */},
                {renderingScale.xCoord, renderingScale.zCoord},
                {0/*                */, renderingScale.zCoord},
                {0/*                */, 0/*                */},
                {renderingScale.xCoord, 0/*                */},
                {renderingScale.xCoord, renderingScale.zCoord},
                {0/*                */, renderingScale.zCoord},
                {0/*                */, 0/*                */},
                {renderingScale.yCoord, 0/*                */},
                {renderingScale.yCoord, renderingScale.zCoord},
                {0/*                */, renderingScale.zCoord},
                {0/*                */, 0/*                */},
                {renderingScale.yCoord, 0/*                */},
                {renderingScale.yCoord, renderingScale.zCoord},
                {0/*                */, renderingScale.zCoord}
        };
    }

    public AxisDirection getDirection(EnumFacing.Axis axis) {
        switch(axis) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
                return z;
        }
        return null;
    }

    public List<Triple<EnumFacing, BlockPos, EnumWirePart>> getAllPossibleConnections() {
        return Arrays.stream(EnumWirePart.values())
                .map(part -> {
                    EnumFacing.Axis axis = null;
                    if(this == part) {
                        axis = null;
                    } else if(y == part.y && z == part.z) {
                        axis = EnumFacing.Axis.X;
                    } else if(z == part.z && x == part.x) {
                        axis = EnumFacing.Axis.Y;
                    } else if(x == part.x && y == part.y) {
                        axis = EnumFacing.Axis.Z;
                    }
                    return Pair.of(axis, part);
                })
                .filter(axisPart -> axisPart.getLeft() != null)
                .flatMap(axisPart -> Stream.of(
                        Pair.of(
                                (EnumFacing) null,
                                axisPart.getRight()
                        ),
                        Pair.of(
                                EnumFacing.getFacingFromAxis(getDirection(axisPart.getLeft()), axisPart.getLeft()),
                                axisPart.getRight()
                        )
                ))
                .map(sidePart -> Triple.of(
                        sidePart.getLeft(),
                        sidePart.getLeft() == null ? BlockPos.ORIGIN : new BlockPos(sidePart.getLeft().getDirectionVec()),
                        sidePart.getRight()
                ))
                .collect(Collectors.toList());
    }
}
