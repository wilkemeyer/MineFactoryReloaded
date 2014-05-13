package powercrystals.minefactoryreloaded.modhelpers.ic2;

//import ic2.api.crops.CropCard;
//import ic2.api.crops.Crops;
//import ic2.api.crops.ICropTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;

public class HarvestableIC2Crop implements IFactoryHarvestable
{
	private Block _block;
	
	public HarvestableIC2Crop(Block block)
	{
		_block = block;
	}
	
	@Override
	public Block getPlant()
	{
		return _block;
	}
	
	@Override
	public HarvestType getHarvestType()
	{
		return HarvestType.Normal;
	}
	
	@Override
	public boolean breakBlock()
	{
		return false;
	}
	
	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		return false;
		/*
		TileEntity te = world.getTileEntity(x, y, z);
		if(te == null || !(te instanceof ICropTile))
		{
			return false;
		}
		ICropTile tec = (ICropTile)te;
		CropCard crop;
		try
		{
			int ID = tec.getID();
			if (ID < 0)
				return false;
			// previous error here was PC not emy
			crop = Crops.instance.getCropList()[ID];
			if(!crop.canBeHarvested(tec) || crop.canGrow(tec))
			{
				return false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return true;//*/
	}
	
	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		return new ArrayList<ItemStack>();
		/*
		List<ItemStack> drops = new ArrayList<ItemStack>();
		
		ICropTile tec = (ICropTile)world.getTileEntity(x, y, z);
		CropCard crop;
		try
		{
			crop = Crops.instance.getCropList()[tec.getID()];
			
			float chance = crop.dropGainChance();
			for (int i = 0; i < tec.getGain(); i++)
			{
				chance *= 1.03F;
			}
			
			chance -= rand.nextFloat();
			int numDrops = 0;
			while (chance > 0.0F)
			{
				numDrops++;
				chance -= rand.nextFloat();
			}
			ItemStack[] cropDrops = new ItemStack[numDrops];
			for (int i = 0; i < numDrops; i++)
			{
				cropDrops[i] = crop.getGain(tec);
				if((cropDrops[i] != null) && (rand.nextInt(100) <= tec.getGain()))
				{
					cropDrops[i].stackSize += 1;
				}
			}
			
			tec.setSize(crop.getSizeAfterHarvest(tec));
			tec.updateState();
			
			for(ItemStack s : cropDrops)
			{
				drops.add(s);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return drops;//*/
	}
	
	@Override
	public void preHarvest(World world, int x, int y, int z)
	{
	}
	
	@Override
	public void postHarvest(World world, int x, int y, int z)
	{
	}
}
