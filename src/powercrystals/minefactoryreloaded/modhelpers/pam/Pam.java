package powercrystals.minefactoryreloaded.modhelpers.pam;

import java.lang.reflect.Method;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableStandard;

import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod( modid = "MineFactoryReloaded|CompatPams", name = "MFR Compat: Pam's Mods", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:pamharvestcraft" )
@NetworkMod( clientSideRequired = false, serverSideRequired = false )
public class Pam
{
        // stuff I need to get from pams mods that I only want to do once
        public static Method pamTEGetCropId;
        public static Method pamTEGetGrowthStage;
        public static Method pamTESetCropId;
        public static Method pamTESetGrowthStage;
        public static Method pamTEFlowerGetCropId;
        public static Method pamTEFlowerGetGrowthStage;
        public static Method pamTEFlowerSetCropId;
        public static Method pamTEFlowerSetGrowthStage;
        public static Method pamBlockFertilize;
        public static Method pamBlockFlowerFertilize;

        public static Method pamBlockSaplingFertilize;

        public static boolean pamSeedFromCrop;
        public static int flowerId;
        static int flowerSeeds[] = new int[16];
        static Item [] pamSeeds;
        static Item [] pamCropItems;
        private enum Category
        { BUSH( "bushes" ), CROP( "crops.normal" ), CROP_PERENNIAL( "crops.regrow" ), MISC( "misc" );


        private String packageName;
        Category( String packageName )
        {
                this.packageName = packageName;
        }

        public String getPackageName()
        {
                return packageName;
        }
        }

        @EventHandler
        public static void load( FMLInitializationEvent e )
        {
                Class<?>[] noOps = new Class<?> []
                {};
                Class<?>[] SingleIntOps = new Class<?> []
                {int.class};
                Class<?>[] fertilizeOpts = new Class<?> []
                {World.class, int.class, int.class, int.class};
                if ( !Loader.isModLoaded( "pamharvestcraft" ) )
                {
                        FMLLog.warning( "Pam's HC base missing - MFR Pam HC Compat not loading" );
                }
                else
                {
                        try
                        {
                                Class<?> mod = Class.forName( "assets.pamharvestcraft.PamHarvestCraft" );
                                MFRRegistry.registerSludgeDrop(25, new ItemStack( ( Item ) mod.getField( "saltItem" ).get( null ) ) );
                                int blockIdCrop = ( ( Block ) ( mod.getField( "pamCrop" ) ).get( null ) ).blockID;
                                pamSeeds=(Item [])( mod.getField( "PamSeeds" ) ).get( null );
                                pamCropItems=(Item [])( mod.getField( "PamCropItems" ) ).get( null );
                                Class<?> pamTE = Class.forName( "assets.pamharvestcraft.TileEntityPamCrop" );
                                pamTEGetCropId = pamTE.getDeclaredMethod( "getCropID", noOps );
                                pamTEGetGrowthStage = pamTE.getDeclaredMethod( "getGrowthStage", noOps );
                                pamTESetCropId = pamTE.getDeclaredMethod( "setCropID", SingleIntOps );
                                pamTESetGrowthStage = pamTE.getDeclaredMethod( "setGrowthStage", SingleIntOps );
                                Class<?> PamBlock = Class.forName( "assets.pamharvestcraft.BlockPamCrop" );
                                pamBlockFertilize = PamBlock.getDeclaredMethod( "fertilize", fertilizeOpts );
                                Class<?> harvestConfig = Class.forName( "assets.pamharvestcraft.HarvestConfigurationHandler" );
                                pamSeedFromCrop = ( Boolean ) ( harvestConfig.getField( "seedsdropfromcrop" ).get( null ) );
                                MFRRegistry.registerHarvestable( new HarvestablePams( blockIdCrop ) );
                                MFRRegistry.registerFertilizable( new PamFertilizable( blockIdCrop ) );
                                Class<?> PamSapling = Class.forName( "assets.pamharvestcraft.BlockPamSapling" );
                                pamBlockSaplingFertilize=PamSapling.getDeclaredMethod("generateTree", new Class<?> []
                                {World.class,int.class,int.class,int.class,Random.class,int.class});
                                Block [] saplings=(Block []) mod.getField("PamOakSaplings").get(null);
                                int id;
                                for ( Block block : saplings )
                                {
                                    id=block.blockID;
                                    MFRRegistry.registerFertilizable( new PamFertilizableSapling( id ) );
                                    MFRRegistry.registerPlantable(new PlantableStandard(id,id));
                                }
                                saplings=(Block []) mod.getField("PamJungleSaplings").get(null);
                                for ( Block block : saplings )
                                {
                                    id=block.blockID;
                                    MFRRegistry.registerFertilizable( new PamFertilizableSapling( id ) );
                                    MFRRegistry.registerPlantable(new PlantableStandard(id,id));
                                }
                        }
                        catch ( Exception x )
                        {
                                x.printStackTrace();
                        }
                        registerCrop( "Blackberry", true );
                        registerCrop( "Blueberry",  true );
                        registerCrop( "Grape", true );
                        registerCrop( "Kiwi", true );
                        registerCrop( "Raspberry",  true );
                        registerCrop( "Spiceleaf",  true );
                        registerCrop( "Strawberry", true );
                        registerCrop( "Sunflower",  true );
                        registerCrop( "Seaweed", true );

                        // Crops
                        registerCrop( "Artichoke",  false );
                        registerCrop( "Asparagus", false );
                        registerCrop( "Bambooshoot",  false );
                        registerCrop( "Barley",  false );
                        registerCrop( "Bean", false );
                        registerCrop( "Beet",  false );
                        registerCrop( "Bellpepper",  false );
                        registerCrop( "Broccoli", false );
                        registerCrop( "Brusselsprout",  false );
                        registerCrop( "Cabbage", false );
                        registerCrop( "Cantaloupe", false );
                        registerCrop( "Cauliflower", false );
                        registerCrop( "Celery", false );
                        registerCrop( "Chilipepper",false );
                        registerCrop( "Coffee", false );
                        registerCrop( "Corn", false );
                        registerCrop( "Cucumber", false );
                        registerCrop( "Eggplant", false );
                        registerCrop( "Garlic", false );
                        registerCrop( "Ginger",  false );
                        registerCrop( "Leek", false );
                        registerCrop( "Lettuce", false );
                        registerCrop( "Mustard", false );
                        registerCrop( "Oats", false );
                        registerCrop( "Okra", false );
                        registerCrop( "Onion",  false );
                        registerCrop( "Parsnip", false );
                        registerCrop( "Peanut", false );
                        registerCrop( "Peas",  false );
                        registerCrop( "Pineapple", false );
                        registerCrop( "Rhubarb", false );
                        registerCrop( "Radish", false );
                        registerCrop( "Rutabaga", false );
                        registerCrop( "Rye",  false );
                        registerCrop( "Scallion", false );
                        registerCrop( "Soybean", false );
                        registerCrop( "Sweetpotato", false );
                        registerCrop( "Tea", false );
                        registerCrop( "Tomato", false );
                        registerCrop( "Turnip", false );
                        registerCrop( "Wintersquash", false );
                        registerCrop( "Zucchini", false );

                        // misc types
                        registerCrop( "Candleberry", true);
                        registerCrop( "Cotton", true );

                        registerCrop( "Rice",  false );
                        registerCrop( "Cranberry",  true );
                        registerCrop( "Whitemushroom",true );
                        registerCrop( "Cactusfruit", true );

                        // fruits
                        registerFruit( "Apple" );
                        registerFruit( "Avocado" );
                        registerFruit( "Banana" );
                        registerFruit( "Cherry" );
                        registerFruit( "Coconut" );
                        registerFruit( "Dragonfruit" );
                        registerFruit( "Lemon" );
                        registerFruit( "Lime" );
                        registerFruit( "Mango" );
                        registerFruit( "Nutmeg" );
                        registerFruit( "Olive" );
                        registerFruit( "Orange" );
                        registerFruit( "Papaya" );
                        registerFruit( "Peach" );
                        registerFruit( "Pear" );
                        registerFruit( "Peppercorn" );
                        registerFruit( "Plum" );
                        registerFruit( "Pomegranate" );
                        registerFruit( "Starfruit" );
                        registerFruit( "Vanillabean" );
                        registerFruit( "Walnut" );


                        // special case for candle and cinnamon
                        // registerCandle();
                        registerCinnamon();



                }


        private String packageName;
        Category( String packageName )
        {
                this.packageName = packageName;
        }


        public String getPackageName()
        {
                return packageName;
        }
        }


                if ( !Loader.isModLoaded( "pamweeeflowers" ) )
                {
                        FMLLog.warning( "Pam's Weee! Flowers missing - MFR Pam Weee! Flowers Compat not loading" );
                }
                else
                {
                        String[] flowers =
                        { "White", "Orange", "Magenta", "LightBlue", "Yellow", "Lime", "Pink", "LightGrey", "DarkGrey", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black" };

                        try
                        {
                                Class<?> pamFlowerTE = Class.forName( "assets.pamweeeflowers.TileEntityPamFlowerCrop" );
                                pamTEFlowerGetCropId = pamFlowerTE.getDeclaredMethod( "getCropID", noOps );
                                pamTEFlowerGetGrowthStage = pamFlowerTE.getDeclaredMethod( "getGrowthStage", noOps );
                                pamTEFlowerSetCropId = pamFlowerTE.getDeclaredMethod( "setCropID", SingleIntOps );
                                pamTEFlowerSetGrowthStage = pamFlowerTE.getDeclaredMethod( "setGrowthStage", SingleIntOps );
                                Class<?> pamBlockFlower = Class.forName( "assets.pamweeeflowers.BlockPamFlowerCrop" );
                                pamBlockFlowerFertilize = pamBlockFlower.getDeclaredMethod( "fertilize", fertilizeOpts );
                                Class<?> mod = Class.forName( "assets.pamweeeflowers.PamWeeeFlowers" );
                                int blockId = ( ( Block ) mod.getField( "pamflowerCrop" ).get( null ) ).blockID;
                                flowerId = ( ( Block ) mod.getField( "pamFlower" ).get( null ) ).blockID;
                                MFRRegistry.registerHarvestable( new HarvestablePamsFlower( blockId ) );
                                MFRRegistry.registerFertilizable( new PamFertilizableFlower( blockId ) );
                                for ( String flower : flowers )
                                {
                                        Item seed = ( Item ) mod.getField( flower.toLowerCase() + "flowerseedItem" ).get( null );
                                        int seedId = seed.itemID;

                                        int cropId = seed.getClass().getField( "cropID" ).getInt( seed );
                                        flowerSeeds[cropId] = seedId;
                                        MFRRegistry.registerPlantable( new PlantablePamFlower( blockId, seedId, cropId ) );

                                }
                        }
                        catch ( ClassNotFoundException x )
                        {
                                FMLLog.warning( "Unable to load Pam support for Weee! Flowers even though Weee! FLowers was present" );
                        }
                        catch ( Exception x )
                        {
                                x.printStackTrace();
                        }
                }
        }

        private static void registerCrop(String cropName, boolean hasWild )
        {
                try
                {
                        Class<?> mod;
                        int blockIdCrop;
                        int blockIdWild;
                        int seedId;
                        final String cropNameLC;
                        int cropId;
                        cropNameLC = cropName.toLowerCase();
                        mod = Class.forName( "assets.pamharvestcraft.PamHarvestCraft" );
                        Item seed = ( ( Item ) mod.getField( String.format( "%sseedItem", cropNameLC ) ).get( null ) );
                        seedId = seed.itemID;
                        blockIdCrop = ( ( Block ) ( mod.getField( "pamCrop" ) ).get( null ) ).blockID;
                        cropId = seed.getClass().getField( "cropID" ).getInt( seed );
                        MFRRegistry.registerPlantable( new PlantablePamCrop( blockIdCrop, seedId, cropId ) );
                        FMLLog.info( "TESTING block id:%d crop id:%d", blockIdCrop, cropId );
                        if ( hasWild )
                        {
                                blockIdWild = ( ( Block ) mod.getField( String.format( "pam%sWild", cropNameLC ) ).get( null ) ).blockID;
                                MFRRegistry.registerHarvestable( new HarvestableStandard( blockIdWild, HarvestType.Normal ) );
                        }


                }
                catch ( ClassNotFoundException x )
                {
                        FMLLog.warning( "Unable to load Pam support for %s", cropName );
                }
                catch ( Exception x )
                {
                        x.printStackTrace();
                }
        }

        private static void registerFruit( String name )
        {
                try
                {
                        Block fruit = ( Block ) Class.forName( "assets.pamharvestcraft.PamHarvestCraft" ).getField( "pam" + name ).get( null );
                        MFRRegistry.registerFruit( new PamFruit( fruit.blockID ) );
                }
                catch ( ClassNotFoundException x )
                {
                        FMLLog.warning( "Unable to load Pam support for %s trees", name );
                }
                catch ( Exception x )
                {
                        x.printStackTrace();
                }
        }


        @EventHandler
        public static void load( FMLInitializationEvent e )
        {
                Class<?>[] noOps = new Class<?> []
                {};
                Class<?>[] SingleIntOps = new Class<?> []
                {int.class};
                Class<?>[] fertilizeOpts = new Class<?> []
                {World.class, int.class, int.class, int.class};
                if ( !Loader.isModLoaded( "pamharvestcraft" ) )
                {
                        FMLLog.warning( "Pam's HC base missing - MFR Pam HC Compat not loading" );
                }
                else
                {
                        try
                        {
                                Class<?> mod = Class.forName( "assets.pamharvestcraft.PamHarvestCraft" );
                                MFRRegistry.registerSludgeDrop(25, new ItemStack( ( Item ) mod.getField( "saltItem" ).get( null ) ) );
                                int blockIdCrop = ( ( Block ) ( mod.getField( "pamCrop" ) ).get( null ) ).blockID;
                                pamSeeds=(Item [])( mod.getField( "PamSeeds" ) ).get( null );
                                pamCropItems=(Item [])( mod.getField( "PamCropItems" ) ).get( null );
                                Class<?> pamTE = Class.forName( "assets.pamharvestcraft.TileEntityPamCrop" );
                                pamTEGetCropId = pamTE.getDeclaredMethod( "getCropID", noOps );
                                pamTEGetGrowthStage = pamTE.getDeclaredMethod( "getGrowthStage", noOps );
                                pamTESetCropId = pamTE.getDeclaredMethod( "setCropID", SingleIntOps );
                                pamTESetGrowthStage = pamTE.getDeclaredMethod( "setGrowthStage", SingleIntOps );
                                Class<?> PamBlock = Class.forName( "assets.pamharvestcraft.BlockPamCrop" );
                                pamBlockFertilize = PamBlock.getDeclaredMethod( "fertilize", fertilizeOpts );
                                Class<?> harvestConfig = Class.forName( "assets.pamharvestcraft.HarvestConfigurationHandler" );
                                pamSeedFromCrop = ( Boolean ) ( harvestConfig.getField( "seedsdropfromcrop" ).get( null ) );
                                MFRRegistry.registerHarvestable( new HarvestablePams( blockIdCrop ) );
                                MFRRegistry.registerFertilizable( new PamFertilizable( blockIdCrop ) );
                        }
                        catch ( Exception x )
                        {
                                x.printStackTrace();
                        }
                        registerCrop( "Blackberry", true );
                        registerCrop( "Blueberry",  true );
                        registerCrop( "Grape", true );
                        registerCrop( "Kiwi", true );
                        registerCrop( "Raspberry",  true );
                        registerCrop( "Spiceleaf",  true );
                        registerCrop( "Strawberry", true );
                        registerCrop( "Sunflower",  true );
                        registerCrop( "Seaweed", true );

                        // Crops
                        registerCrop( "Artichoke",  false );
                        registerCrop( "Asparagus", false );
                        registerCrop( "Bambooshoot",  false );
                        registerCrop( "Barley",  false );
                        registerCrop( "Bean", false );
                        registerCrop( "Beet",  false );
                        registerCrop( "Bellpepper",  false );
                        registerCrop( "Broccoli", false );
                        registerCrop( "Brusselsprout",  false );
                        registerCrop( "Cabbage", false );
                        registerCrop( "Cantaloupe", false );
                        registerCrop( "Cauliflower", false );
                        registerCrop( "Celery", false );
                        registerCrop( "Chilipepper",false );
                        registerCrop( "Coffee", false );
                        registerCrop( "Corn", false );
                        registerCrop( "Cucumber", false );
                        registerCrop( "Eggplant", false );
                        registerCrop( "Garlic", false );
                        registerCrop( "Ginger",  false );
                        registerCrop( "Leek", false );
                        registerCrop( "Lettuce", false );
                        registerCrop( "Mustard", false );
                        registerCrop( "Oats", false );
                        registerCrop( "Okra", false );
                        registerCrop( "Onion",  false );
                        registerCrop( "Parsnip", false );
                        registerCrop( "Peanut", false );
                        registerCrop( "Peas",  false );
                        registerCrop( "Pineapple", false );
                        registerCrop( "Rhubarb", false );
                        registerCrop( "Radish", false );
                        registerCrop( "Rutabaga", false );
                        registerCrop( "Rye",  false );
                        registerCrop( "Scallion", false );
                        registerCrop( "Soybean", false );
                        registerCrop( "Sweetpotato", false );
                        registerCrop( "Tea", false );
                        registerCrop( "Tomato", false );
                        registerCrop( "Turnip", false );
                        registerCrop( "Wintersquash", false );
                        registerCrop( "Zucchini", false );

                        // misc types
                        registerCrop( "Candleberry", true);
                        registerCrop( "Cotton", true );

                        registerCrop( "Rice",  false );
                        registerCrop( "Cranberry",  true );
                        registerCrop( "Whitemushroom",true );
                        registerCrop( "Cactusfruit", true );

                        // fruits
                        registerFruit( "Apple" );
                        registerFruit( "Avocado" );
                        registerFruit( "Banana" );
                        registerFruit( "Cherry" );
                        registerFruit( "Coconut" );
                        registerFruit( "Dragonfruit" );
                        registerFruit( "Lemon" );
                        registerFruit( "Lime" );
                        registerFruit( "Mango" );
                        registerFruit( "Nutmeg" );
                        registerFruit( "Olive" );
                        registerFruit( "Orange" );
                        registerFruit( "Papaya" );
                        registerFruit( "Peach" );
                        registerFruit( "Pear" );
                        registerFruit( "Peppercorn" );
                        registerFruit( "Plum" );
                        registerFruit( "Pomegranate" );
                        registerFruit( "Starfruit" );
                        registerFruit( "Vanillabean" );
                        registerFruit( "Walnut" );


                        // special case for candle and cinnamon
                        // registerCandle();
                        registerCinnamon();


                }


                if ( !Loader.isModLoaded( "pamweeeflowers" ) )
                {
                        FMLLog.warning( "Pam's Weee! Flowers missing - MFR Pam Weee! Flowers Compat not loading" );
                }
                else
                {
                        String[] flowers =
                        { "White", "Orange", "Magenta", "LightBlue", "Yellow", "Lime", "Pink", "LightGrey", "DarkGrey", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black" };

                        try
                        {
                                Class<?> pamFlowerTE = Class.forName( "assets.pamweeeflowers.TileEntityPamFlowerCrop" );
                                pamTEFlowerGetCropId = pamFlowerTE.getDeclaredMethod( "getCropID", noOps );
                                pamTEFlowerGetGrowthStage = pamFlowerTE.getDeclaredMethod( "getGrowthStage", noOps );
                                pamTEFlowerSetCropId = pamFlowerTE.getDeclaredMethod( "setCropID", SingleIntOps );
                                pamTEFlowerSetGrowthStage = pamFlowerTE.getDeclaredMethod( "setGrowthStage", SingleIntOps );
                                Class<?> pamBlockFlower = Class.forName( "assets.pamweeeflowers.BlockPamFlowerCrop" );
                                pamBlockFlowerFertilize = pamBlockFlower.getDeclaredMethod( "fertilize", fertilizeOpts );
                                Class<?> mod = Class.forName( "assets.pamweeeflowers.PamWeeeFlowers" );
                                int blockId = ( ( Block ) mod.getField( "pamflowerCrop" ).get( null ) ).blockID;
                                flowerId = ( ( Block ) mod.getField( "pamFlower" ).get( null ) ).blockID;
                                MFRRegistry.registerHarvestable( new HarvestablePamsFlower( blockId ) );
                                MFRRegistry.registerFertilizable( new PamFertilizableFlower( blockId ) );
                                for ( String flower : flowers )
                                {
                                        Item seed = ( Item ) mod.getField( flower.toLowerCase() + "flowerseedItem" ).get( null );
                                        int seedId = seed.itemID;

                                        int cropId = seed.getClass().getField( "cropID" ).getInt( seed );
                                        flowerSeeds[cropId] = seedId;
                                        MFRRegistry.registerPlantable( new PlantablePamFlower( blockId, seedId, cropId ) );

                                }
                        }
                        catch ( ClassNotFoundException x )
                        {
                                FMLLog.warning( "Unable to load Pam support for Weee! Flowers even though Weee! FLowers was present" );
                        }
                        catch ( Exception x )
                        {
                                x.printStackTrace();
                        }
                }
        }

        private static void registerCrop(String cropName, boolean hasWild )
        {
                try
                {
                        Class<?> mod;
                        int blockIdCrop;
                        int blockIdWild;
                        int seedId;
                        final String cropNameLC;
                        int cropId;
                        cropNameLC = cropName.toLowerCase();
                        mod = Class.forName( "assets.pamharvestcraft.PamHarvestCraft" );
                        Item seed = ( ( Item ) mod.getField( String.format( "%sseedItem", cropNameLC ) ).get( null ) );
                        seedId = seed.itemID;
                        blockIdCrop = ( ( Block ) ( mod.getField( "pamCrop" ) ).get( null ) ).blockID;
                        cropId = seed.getClass().getField( "cropID" ).getInt( seed );
                        MFRRegistry.registerPlantable( new PlantablePamCrop( blockIdCrop, seedId, cropId ) );
                        FMLLog.info( "TESTING block id:%d crop id:%d", blockIdCrop, cropId );
                        if ( hasWild )
                        {
                                blockIdWild = ( ( Block ) mod.getField( String.format( "pam%sWild", cropNameLC ) ).get( null ) ).blockID;
                                MFRRegistry.registerHarvestable( new HarvestableStandard( blockIdWild, HarvestType.Normal ) );
                        }


                }
                catch ( ClassNotFoundException x )
                {
                        FMLLog.warning( "Unable to load Pam support for %s", cropName );
                }
                catch ( Exception x )
                {
                        x.printStackTrace();
                }
        }

        private static void registerFruit( String name )
        {
                try
                {
                        Block fruit = ( Block ) Class.forName( "assets.pamharvestcraft.PamHarvestCraft" ).getField( "pam" + name ).get( null );
                        MFRRegistry.registerFruit( new PamFruit( fruit.blockID ) );
                }
                catch ( ClassNotFoundException x )
                {
                        FMLLog.warning( "Unable to load Pam support for %s trees", name );
                }
                catch ( Exception x )
                {
                        x.printStackTrace();
                }
        }


        private static void registerCinnamon()
        {
                try
                {
                        Block fruit = ( Block ) Class.forName( "assets.pamharvestcraft.PamHarvestCraft" ).getField( "pamCinnamon" ).get( null );
                        Item cinnamon = ( Item ) Class.forName( "assets.pamharvestcraft.PamHarvestCraft" ).getField( "cinnamonItem" ).get( null );
                        MFRRegistry.registerFruit( new PamFruitCinnamon( fruit.blockID, cinnamon.itemID ) );
                        MFRRegistry.registerFruitLogBlockId( fruit.blockID );
                }
                catch ( Exception x )
                {
                        x.printStackTrace();
                }
        }
}
