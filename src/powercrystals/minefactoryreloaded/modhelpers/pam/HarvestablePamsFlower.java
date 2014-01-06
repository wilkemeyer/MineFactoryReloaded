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
    @Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
        List<ItemStack> outStack=new List<ItemStack>();
        TileEntity te=world.getBlockTileEntity(x, y, z);
        if(te!=null)
        {
        try
        {
        int cropID=(Integer)(getCrop.invoke(te,new Object[]{}));
      
        if(cropID == 0)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 0));

            outStack.add(new ItemStack(PamWeeeFlowers.whiteflowerseedItem, seedDrops, 0));

        }
        if(cropID == 1)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 1));

            outStack.add(new ItemStack(PamWeeeFlowers.orangeflowerseedItem, seedDrops, 0));

        }
        if(cropID == 2)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 2));

            outStack.add(new ItemStack(PamWeeeFlowers.magentaflowerseedItem, seedDrops, 0));

        }
        if(cropID == 3)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 3));

            outStack.add(new ItemStack(PamWeeeFlowers.lightblueflowerseedItem, seedDrops, 0));

        }
        if(cropID == 4)
        {
            outStack.add(new ItemStack(Block.plantYellow, seedDrops, 0));

            outStack.add(new ItemStack(PamWeeeFlowers.yellowflowerseedItem, seedDrops, 0));

        }
        if(cropID == 5)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 4));

            outStack.add(new ItemStack(PamWeeeFlowers.limeflowerseedItem, seedDrops, 0));

        }
        if(cropID == 6)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 5));

            outStack.add(new ItemStack(PamWeeeFlowers.pinkflowerseedItem, seedDrops, 0));

        }
        if(cropID == 7)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 6));

            outStack.add(new ItemStack(PamWeeeFlowers.darkgreyflowerseedItem, seedDrops, 0));

        }
        if(cropID == 8)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 7));

            outStack.add(new ItemStack(PamWeeeFlowers.lightgreyflowerseedItem, seedDrops, 0));

        }
        if(cropID == 9)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 8));

            outStack.add(new ItemStack(PamWeeeFlowers.cyanflowerseedItem, seedDrops, 0));

        }
        if(cropID == 10)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 9));

            outStack.add(new ItemStack(PamWeeeFlowers.purpleflowerseedItem, seedDrops, 0));

        }
        if(cropID == 11)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 10));

            outStack.add(new ItemStack(PamWeeeFlowers.blueflowerseedItem, seedDrops, 0));

        }
        if(cropID == 12)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 11));

            outStack.add(new ItemStack(PamWeeeFlowers.brownflowerseedItem, seedDrops, 0));

        }
        if(cropID == 13)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 12));

            outStack.add(new ItemStack(PamWeeeFlowers.greenflowerseedItem, seedDrops, 0));

        }
        if(cropID == 14)
        {
            outStack.add(new ItemStack(Block.plantRed, seedDrops, 0));

            outStack.add(new ItemStack(PamWeeeFlowers.redflowerseedItem, seedDrops, 0));

        }
        if(cropID == 15)
        {
            outStack.add(new ItemStack(Pam.FlowerID, cropDrops, 13));

            outStack.add(new ItemStack(PamWeeeFlowers.blackflowerseedItem, seedDrops, 0));

        }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        }
        return outStack;
	}
}