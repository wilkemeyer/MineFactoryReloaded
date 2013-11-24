package powercrystals.minefactoryreloaded.item;

import cofh.api.block.IDismantleable;
import com.google.common.collect.Multimap;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.minefactoryreloaded.api.IToolHammer;
import powercrystals.minefactoryreloaded.setup.Machine;

public class ItemFactoryHammer extends ItemFactory implements IToolHammer, IToolWrench
{
	public ItemFactoryHammer(int i)
	{
		super(i);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		if (block != null)
		{
			if (player.isSneaking() && block instanceof IDismantleable &&
					((IDismantleable)block).canDismantle(player, world, x, y, z))
			{
				if (!world.isRemote)
					((IDismantleable)block).dismantleBlock(player, world, x, y, z, false);
				player.swingItem();
				return !world.isRemote;
			}
			else if (block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) 
			{
				player.swingItem();
				return !world.isRemote;
			}
		}
		return false;
	}
	
	@Override
	public boolean canWrench(EntityPlayer player, int x, int y, int z)
	{
		return true;
	}
	
	@Override
	public void wrenchUsed(EntityPlayer player, int x, int y, int z)
	{
	}
	
	@Override
	public boolean shouldPassSneakingClickToBlock(World world, int x, int y, int z)
	{
		return true;
	}

    @Override
	public boolean canHarvestBlock(Block par1Block)
    {
    	return par1Block != null && 
    			par1Block.blockMaterial == Material.cake |
    			par1Block.blockMaterial == Material.iron |
    			par1Block.blockMaterial == Material.rock |
    			par1Block.blockMaterial == Material.wood |
    			par1Block.blockMaterial == Material.anvil |
    			par1Block.blockMaterial == Material.glass |
    			par1Block.blockMaterial == Material.piston |
    			par1Block.blockMaterial == Material.plants |
    			par1Block.blockMaterial == Material.pumpkin |
    			par1Block.blockMaterial == Machine.MATERIAL |
    			par1Block.blockMaterial == Material.circuits;
    }

    @Override
	public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block)
    {
    	if (par2Block == null)
    		return 0;
    	if (par2Block.blockMaterial == Material.pumpkin |
    			par2Block.blockMaterial == Material.cake)
    		return 15f;
        return canHarvestBlock(par2Block) ? 1.35f : 0.15f;
    }
	
    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
    	Block block = Block.blocksList[player.worldObj.getBlockId(x, y, z)];
    	return block == null || block.getBlockHardness(player.worldObj, x, y, z) > 2.9f;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Multimap getItemAttributeModifiers()
    {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", 1, 0));
        return multimap;
    }
    
	@Override
    public boolean isFull3D()
    {
        return true;
    }
}
