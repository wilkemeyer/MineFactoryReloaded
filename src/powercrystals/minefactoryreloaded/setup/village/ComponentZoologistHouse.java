package powercrystals.minefactoryreloaded.setup.village;

import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.CHEST_GEN;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.ComponentVillage;
import net.minecraft.world.gen.structure.ComponentVillageStartPiece;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieceWeight;
import net.minecraftforge.common.ChestGenHooks;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class ComponentZoologistHouse extends ComponentVillage
{
	private int brickId, brickMeta;
	private int paneId, paneMeta;
	private int benchMeta;
	private int lightMeta;
	private boolean hasMadeChest;
	
	public ComponentZoologistHouse() {}
	
	public ComponentZoologistHouse(ComponentVillageStartPiece startPiece, int componentType, Random rand, StructureBoundingBox sbb, int coordBaseMode)
	{
		super(startPiece, componentType);
		this.coordBaseMode = coordBaseMode;
		boundingBox = sbb;
		
		brickId = MineFactoryReloadedCore.factoryDecorativeBrickBlock.blockID;
		brickMeta = rand.nextInt(16);
		if (brickMeta > 0) ++brickMeta; // 1 is glowstone small bricks
		if (brickMeta > 5) ++brickMeta; // 6 is glowstone large bricks
		if (brickMeta == 11) // 11 is PRC housing
		{
			brickId = Block.brick.blockID; // because large red bricks are also an option
			brickMeta = 0; // no need for weird meta, and makes lights correct size
		}
		else if (brickMeta > 11) // 12 & 13 are meat
			brickMeta += 2;
		
		if (brickMeta > 15) // covers the 4 skipped blocks in factoryDecorativeBrickBlock
		{
			brickId = MineFactoryReloadedCore.factoryDecorativeStoneBlock.blockID;
			brickMeta -= 12; // shift into proper range (16 - 12 = 4: first stone brick block)
			lightMeta = brickMeta < 6 ? 6 : 1;
		}
		else
			lightMeta = brickMeta < 6 ? 1 : 6;
		
		paneId = Block.thinGlass.blockID;
		paneMeta = 0;
		if (getBiomeSpecificBlock(paneId, paneMeta) == paneId)
		{
			paneId = MineFactoryReloadedCore.factoryGlassPaneBlock.blockID;
			paneMeta = rand.nextInt(16);
		}
		
		benchMeta = rand.nextInt(10) == 0 ? 11 : 0;
	}

    @Override // write to NBT
	protected void func_143012_a(NBTTagCompound tag)
    {
    	super.func_143012_a(tag);
        tag.setBoolean("Chest", this.hasMadeChest);
    	tag.setIntArray("blocks", new int[]{brickId, brickMeta, paneId, paneMeta});
    }

    @Override // read from NBT
	protected void func_143011_b(NBTTagCompound tag)
    {
    	super.func_143011_b(tag);
        this.hasMadeChest = tag.getBoolean("Chest");
    	int[] blocks = tag.getIntArray("blocks");
    	if (blocks == null || blocks.length != 4)
    	{
    		return;
    	}
		brickId = blocks[0];
		brickMeta = blocks[1];
		paneId = blocks[2];
		paneMeta = blocks[3];
    }
	
	@SuppressWarnings("rawtypes")
	public static ComponentZoologistHouse buildComponent(StructureVillagePieceWeight villagePiece,
			ComponentVillageStartPiece startPiece, List pieces, Random random, int p1, int p2,
			int p3, int p4, int p5)
	{
		StructureBoundingBox sbb = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3,
				0, 0, 0, 9, 9, 6, p4);
		return (!canVillageGoDeeper(sbb)) || (StructureComponent.findIntersecting(pieces, sbb) != null)
				? null : new ComponentZoologistHouse(startPiece, p5, random, sbb, p4);
	}
	
	@Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb)
	{
		if (field_143015_k < 0)
		{
			field_143015_k = getAverageGroundLevel(world, sbb);
			
			if (field_143015_k < 0)
			{
				return true;
			}
			
			boundingBox.offset(0, field_143015_k - boundingBox.maxY + 9 - 1, 0);
		}
		int mfrBrickId = MineFactoryReloadedCore.factoryDecorativeBrickBlock.blockID;
		int mfrLogId = MineFactoryReloadedCore.rubberWoodBlock.blockID, logMeta = 12;
		
		fillWithBlocks(world, sbb, 1, 1, 1, 7, 5, 4, 0, 0, false);
		fillWithMetadataBlocks(world, sbb, 0, 0, 0, 8, 0, 5, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 0, 5, 0, 8, 5, 5, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 0, 6, 1, 8, 6, 4, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 0, 7, 2, 8, 7, 3, brickId, brickMeta, brickId, brickMeta, false);
		int i = getMetadataWithOffset(Block.stairsWoodOak.blockID, 3);
		int j = getMetadataWithOffset(Block.stairsWoodOak.blockID, 2);
		int k;
		int l;
		
		for (k = -1; k <= 2; ++k)
		{
			for (l = 0; l <= 8; ++l)
			{
				placeBlockAtCurrentPosition(world, Block.stairsWoodOak.blockID, i, l, 6 + k, k, sbb);
				placeBlockAtCurrentPosition(world, Block.stairsWoodOak.blockID, j, l, 6 + k, 5 - k, sbb);
			}
		}
		
		fillWithMetadataBlocks(world, sbb, 0, 1, 0, 0, 1, 5, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 1, 1, 5, 8, 1, 5, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 8, 1, 0, 8, 1, 4, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 2, 1, 0, 7, 1, 0, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 0, 2, 0, 0, 4, 0, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 0, 2, 5, 0, 4, 5, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 8, 2, 5, 8, 4, 5, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 8, 2, 0, 8, 4, 0, brickId, brickMeta, brickId, brickMeta, false);
		fillWithBlocks(world, sbb, 0, 2, 1, 0, 4, 4, Block.planks.blockID, Block.planks.blockID, false);
		fillWithBlocks(world, sbb, 1, 2, 5, 7, 4, 5, Block.planks.blockID, Block.planks.blockID, false);
		fillWithBlocks(world, sbb, 8, 2, 1, 8, 4, 4, Block.planks.blockID, Block.planks.blockID, false);
		fillWithBlocks(world, sbb, 1, 2, 0, 7, 4, 0, Block.planks.blockID, Block.planks.blockID, false);
		//{ Windows
		//{ Large front window
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 3, 2, 0, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 4, 2, 0, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 5, 2, 0, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 6, 2, 0, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 7, 2, 0, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 3, 3, 0, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 4, 3, 0, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 5, 3, 0, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 6, 3, 0, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 7, 3, 0, sbb);
		//}
		//{ Window near door
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 0, 2, 1, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 0, 2, 2, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 0, 2, 3, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 0, 2, 4, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 0, 3, 1, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 0, 3, 2, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 0, 3, 3, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 0, 3, 4, sbb);
		//}
		//{ Window opposite above window
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 8, 2, 1, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 8, 2, 2, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 8, 2, 3, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 8, 2, 4, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 8, 3, 1, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 8, 3, 2, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 8, 3, 3, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 8, 3, 4, sbb);
		//}
		//{ Two back windows
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 1, 2, 5, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 2, 2, 5, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 3, 2, 5, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 4, 2, 5, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 5, 2, 5, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 6, 2, 5, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 7, 2, 5, sbb);
		//}
		//}
		fillWithBlocks(world, sbb, 1, 4, 1, 7, 4, 1, Block.planks.blockID, Block.planks.blockID, false);
		fillWithBlocks(world, sbb, 1, 4, 4, 7, 4, 4, Block.planks.blockID, Block.planks.blockID, false);
		fillWithBlocks(world, sbb, 1, 3, 4, 7, 3, 4, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
		//{ Wrap around bench
		k = getMetadataWithOffset(Block.stairsWoodOak.blockID, 0);
		placeBlockAtCurrentPosition(world, Block.stairsWoodOak.blockID, k, 7, 1, 4, sbb);
		placeBlockAtCurrentPosition(world, Block.stairsWoodOak.blockID, k, 7, 1, 3, sbb);
		k = getMetadataWithOffset(Block.stairsWoodOak.blockID, 3);
		placeBlockAtCurrentPosition(world, Block.stairsWoodOak.blockID, k, 6, 1, 4, sbb);
		placeBlockAtCurrentPosition(world, Block.stairsWoodOak.blockID, k, 5, 1, 4, sbb);
		placeBlockAtCurrentPosition(world, Block.stairsWoodOak.blockID, k, 4, 1, 4, sbb);
		placeBlockAtCurrentPosition(world, Block.stairsWoodOak.blockID, k, 3, 1, 4, sbb);
		//{ "Tables"
		placeBlockAtCurrentPosition(world, Block.fence.blockID, 0, 6, 1, 3, sbb);
		placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 6, 2, 3, sbb);
		placeBlockAtCurrentPosition(world, Block.fence.blockID, 0, 4, 1, 3, sbb);
		placeBlockAtCurrentPosition(world, Block.pressurePlatePlanks.blockID, 0, 4, 2, 3, sbb);
		//}
		//}
		k = benchMeta; // workbench or PRC
		placeBlockAtCurrentPosition(world, k == 0 ? Block.workbench.blockID : mfrBrickId, k, 7, 1, 1, sbb);
		
		k = lightMeta; // overhead lights (match large/small bricks)
		fillWithMetadataBlocks(world, sbb, 1, 5, 2, 7, 5, 3, mfrBrickId, k, mfrBrickId, k, false);
		//{ Door
		placeBlockAtCurrentPosition(world, 0, 0, 1, 1, 0, sbb);
		placeBlockAtCurrentPosition(world, 0, 0, 1, 2, 0, sbb);
		placeDoorAtCurrentPosition(world, sbb, random, 1, 1, 0, getMetadataWithOffset(Block.doorWood.blockID, 1));
		//}
		
		if (getBlockIdAtCurrentPosition(world, 1, 0, -1, sbb) == 0 &&
				getBlockIdAtCurrentPosition(world, 1, -1, -1, sbb) != 0)
		{
			placeBlockAtCurrentPosition(world, Block.stairsCobblestone.blockID,
					getMetadataWithOffset(Block.stairsCobblestone.blockID, 3), 1, 0, -1, sbb);
		}
		
		for (l = 0; l < 6; ++l)
		{
			for (int i1 = 0; i1 < 9; ++i1)
			{
				clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
				fillCurrentPositionBlocksDownwards(world, brickId, brickMeta, i1, -1, l, sbb);
			}
		}
		if (!this.hasMadeChest)
        {
            i = this.getYWithOffset(1);
            j = this.getXWithOffset(1, 4);
            k = this.getZWithOffset(1, 4);

            if (sbb.isVecInside(j, i, k))
            {
                this.hasMadeChest = true;
                generateStructureChestContents(world, sbb, random, 1, 1, 4,
                		ChestGenHooks.getItems(CHEST_GEN, random), ChestGenHooks.getCount(CHEST_GEN, random));
            }
        }
		
		spawnVillagers(world, sbb, 2, 1, 2, 1);
		return true;
	}
	
	@Override
	protected int getVillagerType(int par1)
	{
		return MFRConfig.zoolologistEntityId.getInt();
	}
}
