package powercrystals.minefactoryreloaded.item;

import cofh.api.block.IDismantleable;
import cofh.asm.relauncher.Implementable;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.eventhandler.Event.Result;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import powercrystals.minefactoryreloaded.api.IToolHammer;
import powercrystals.minefactoryreloaded.setup.Machine;

@Implementable("buildcraft.api.tools.IToolWrench")
public class ItemFactoryHammer extends ItemFactory implements IToolHammer
{
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		Block block = world.getBlock(x, y, z);
		if (block != null)
		{
			PlayerInteractEvent e = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK,
					x, y, z, side, world);
			if(MinecraftForge.EVENT_BUS.post(e) || e.getResult() == Result.DENY
					|| e.useBlock == Result.DENY || e.useItem == Result.DENY)
			{
				return false;
			}
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

	//@Override
	public boolean canWrench(EntityPlayer player, int x, int y, int z)
	{
		return true;
	}

	//@Override
	public void wrenchUsed(EntityPlayer player, int x, int y, int z)
	{
	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
	{
		return true;
	}

	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack)
	{
		if (block == null)
			return false;
		Material mat = block.getMaterial();
		return 	mat == Material.cake |
				mat == Material.iron |
				mat == Material.rock |
				mat == Material.wood |
				mat == Material.gourd |
				mat == Material.anvil |
				mat == Material.glass |
				mat == Material.piston |
				mat == Material.plants |
				mat == Machine.MATERIAL |
				mat == Material.circuits;
	}

	@Override
	public float func_150893_a(ItemStack stack, Block block)
	{
		if (block == null)
			return 0;
		Material mat = block.getMaterial();
		if (mat == Material.gourd |
				mat == Material.cake)
			return 15f;
		return canHarvestBlock(block, stack) ? 1.35f : 0.15f;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player)
	{
		Block block = player.worldObj.getBlock(x, y, z);
		if (block == null || block.getBlockHardness(player.worldObj, x, y, z) > 2.9f)
		{
			Random rnd = player.getRNG();
			player.playSound("random.break", 0.8F + rnd.nextFloat() * 0.4F, 0.4F);

			for (int i = 0, e = 10 + rnd.nextInt(5); i < e; ++i)
			{
				Vec3 vec3 = Vec3.createVectorHelper((rnd.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
				vec3.rotateAroundX(-player.rotationPitch * (float)Math.PI / 180.0F);
				vec3.rotateAroundY(-player.rotationYaw * (float)Math.PI / 180.0F);
				Vec3 vec31 = Vec3.createVectorHelper((rnd.nextFloat() - 0.5D) * 0.3D, rnd.nextFloat(), 0.6D);
				vec31.rotateAroundX(-player.rotationPitch * (float)Math.PI / 180.0F);
				vec31.rotateAroundY(-player.rotationYaw * (float)Math.PI / 180.0F);
				vec31 = vec31.addVector(player.posX, player.posY + player.getEyeHeight(), player.posZ);
				player.worldObj.spawnParticle("tilecrack_51_0", vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord);
			}
			return true;
		}
		return false;
	}

	@Override
	public Multimap getAttributeModifiers(ItemStack stack)
	{
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(stack);
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
				new AttributeModifier(field_111210_e, "Weapon modifier", 1, 0));
		return multimap;
	}

	@Override
	public boolean isFull3D()
	{
		return true;
	}
}
