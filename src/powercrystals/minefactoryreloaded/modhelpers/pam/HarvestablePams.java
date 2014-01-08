package powercrystals.minefactoryreloaded.modhelpers.pam;

import java.util.List;import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.tileentity.TileEntity;
public class HarvestablePams implements IFactoryHarvestable
{
        protected int _sourceId;
        protected Method getCrop;
        protected Method getGrowthStage;
        protected final Object[] dummyArgs = new Object[]
        {};        
        public HarvestablePams( int sourceId ) throws ClassNotFoundException
        {
            _sourceId = sourceId;
            getCrop = Pam.pamTEGetCropId;
            getGrowthStage = Pam.pamTEGetGrowthStage;
        }
        @Override
        public int getPlantId()
        {
            return _sourceId;
        }
        @Override
        public HarvestType getHarvestType()
        {
            return HarvestType.Normal;
        }        @Override
        public boolean breakBlock()
        {
            return false;        }
        @Override
        public boolean canBeHarvested( World world, Map<String, Boolean> harvesterSettings, int x, int y, int z )        {            TileEntity te = world.getBlockTileEntity( x, y, z );
            try
            {
                if ( te != null && ( Integer ) ( getGrowthStage.invoke( te, dummyArgs ) ) >= 2 )
                {
                    return true;
                }
            }
            catch ( Exception ex )
            {
                ex.printStackTrace();
            }            return false;
        }
        @Override
        public List<ItemStack> getDrops( World world, Random rand, Map<String, Boolean> harvesterSettings, int x, int y, int z )
        {
            List<ItemStack> outStack = new ArrayList<ItemStack>();
            TileEntity te = world.getBlockTileEntity( x, y, z );
            if ( te != null )
            {
                try
                {
                    int cropID = ( Integer ) ( getCrop.invoke( te, new Object[]
                    {} ) );                    if(cropID <=28)                    {
                        int cropDrops = rand.nextInt(3) + 2;
                        int seedDrops = rand.nextInt(2) + 1;
                        if ( Pam.pamSeedFromCrop )
                        {
                            outStack.add( new ItemStack( Pam.pamCropItems[cropID], 1, 0 ) );
                            outStack.add( new ItemStack( Pam.pamSeeds[cropID], seedDrops, 0 ) );
                        }
                        else
                        {
                            outStack.add( new ItemStack( Pam.pamCropItems[cropID], cropDrops, 0 ) );
                        }                    }                    else                        outStack.add(new ItemStack(Pam.pamCropItems[cropID], 1,0));
                }
                catch ( Exception ex )
                {
                        ex.printStackTrace();
                }
            }            return outStack;
        }
        @Override        public void preHarvest( World world, int x, int y, int z )        {        }
        @Override
        public void postHarvest( World world, int x, int y, int z )
        {            TileEntity te = world.getBlockTileEntity( x, y, z );            try            {                int cropId = ( Integer ) ( getCrop.invoke( te, dummyArgs ) );                if(cropId>28)                {                    Pam.pamTESetGrowthStage.invoke(te,1);                    world.markBlockForUpdate(x,y,z);                }                else                {                    world.removeBlockTileEntity(x,y,z);                    world.setBlockToAir(x,y,z);                }            }            catch ( Exception ex )            {                ex.printStackTrace();            }
        }
}