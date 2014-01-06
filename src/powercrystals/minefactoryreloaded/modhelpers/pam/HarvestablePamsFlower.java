package powercrystals.minefactoryreloaded.modhelpers.pam;

import java.util.List;
import java.util.Map;
import java.util.Random;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.tileentity.TileEntity;
public class HarvestablePamsFlower extends HarvestablePams
{
	public HarvestablePamsFlower(int sourceId) throws ClassNotFoundException
    {
        super(sourceId);
        getGrowthStage=Pam.PamTEFlowerGetGrowthStage;       
        getCrop=Pam.PamTEFlowerGetCropId;
    }
  
	
	
}