package powercrystals.minefactoryreloaded.tile.machine;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiBlockSmasher;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerBlockSmasher;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;
import powercrystals.minefactoryreloaded.world.SmashingWorld;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityBlockSmasher extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	private LiquidTank _tank;
	
	private int _fortune = 0;
	
	private ItemStack _lastInput;
	private List<ItemStack> _lastOutput;
	
	private SmashingWorld _smashingWorld;
	private boolean _shouldWork = true;
	
	public TileEntityBlockSmasher()
	{
		super(Machine.BlockSmasher);
		_tank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME * 4);
	}
	
	@Override
	public void setWorldObj(World world)
	{
		super.setWorldObj(world);
		_smashingWorld = new SmashingWorld(this.worldObj);
	}
	
	@Override
	public int getSizeInventory()
	{
		return 2;
	}
	
	@Override
	public String getGuiBackground()
	{
		return "blocksmasher.png";
	}
	
	@Override
	public ContainerBlockSmasher getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerBlockSmasher(this, inventoryPlayer);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiBlockSmasher(getContainer(inventoryPlayer), this);
	}
	
	@Override
	protected boolean activateMachine()
	{
		if(_shouldWork && _inventory[0] == null)
		{
			setWorkDone(0);
			return false;
		}
		if(_lastInput == null || !UtilInventory.stacksEqual(_lastInput, _inventory[0]))
		{
			_lastInput = _inventory[0].copy(); // protect against amorphous itemstacks
			_lastOutput = getOutput(_lastInput);
		}
		if(_lastOutput == null)
		{
			setWorkDone(0);
			return false;
		}
		if(_shouldWork & _fortune > 0 && (_tank.getLiquid() == null || _tank.getLiquid().amount < _fortune))
		{
			return false;
		}
		ItemStack outSlot = _inventory[1];
		ItemStack output = getEqualStack(outSlot, _lastOutput);
		// TODO: ^ inefficient
		if(output == null)
		{
			if (_shouldWork)
				setWorkDone(0);
			return false;
		}
		if(outSlot != null && outSlot.getMaxStackSize() - outSlot.stackSize < output.stackSize)
		{
			return false;
		}
		
		if(getWorkDone() >= getWorkMax())
		{
			if (_shouldWork)
			{
				_inventory[0].stackSize--;
				if(_inventory[0].stackSize == 0)
				{
					_inventory[0] = null;
				}
			}
			_shouldWork = false;
			if(_inventory[1] == null)
			{
				_inventory[1] = output.copy();
			}
			else
			{
				_inventory[1].stackSize += output.stackSize;
			}
			_lastOutput.remove(output);
			if (_lastOutput.size() == 0)
			{
				setWorkDone(0);
				_shouldWork = true;
				_lastInput = null;
				_lastOutput = null;
			}
		}
		else
		{
			setWorkDone(getWorkDone() + 1);
			_tank.drain(_fortune, true);
		}
		return true;
	}
	
	private static ItemStack getEqualStack(ItemStack a, List<ItemStack> b)
	{
		if (a != null & b != null && a.stackSize > 0 && b.size() > 0)
			for (ItemStack i : b)
				if (UtilInventory.stacksEqual(a, i)) return i;
		return a == null && b.size() > 0 ? b.get(0) : null;
	}
	
	@SuppressWarnings("unchecked")
	private List<ItemStack> getOutput(ItemStack input)
	{
		if(!(input.getItem() instanceof ItemBlock))
		{
			return null;
		}
		int blockId = ((ItemBlock)input.getItem()).getBlockID();
		Block b = Block.blocksList[blockId];
		if(b == null)
		{
			return null;
		}
		
		@SuppressWarnings("rawtypes")
		ArrayList drops = _smashingWorld.smashBlock(input, b, blockId, input.getItemDamage(), _fortune);
		if (drops != null && drops.size() > 0)
		{
			return drops;
		}
		return null;
	}
	
	public int getFortune()
	{
		return _fortune;
	}
	
	public void setFortune(int fortune)
	{
		if(fortune >= 0 && fortune <= 3)
		{
			if(_fortune < fortune)
			{
				setWorkDone(0);
			}
			_fortune = fortune;
		}
	}
	
	@Override
	public int getEnergyStoredMax()
	{
		return 16000;
	}
	
	@Override
	public int getWorkMax()
	{
		return 60;
	}
	
	@Override
	public int getIdleTicksMax()
	{
		return 1;
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideordinal)
	{
		if(slot == 0) return true;
		return false;
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int sideordinal)
	{
		if(slot == 1) return true;
		return false;
	}
	
	@Override
	public boolean allowBucketFill()
	{
		return true;
	}
	
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		if(resource == null || (resource.itemID != LiquidDictionary.getCanonicalLiquid("mobEssence").itemID))
		{
			return 0;
		}
		
		return _tank.fill(resource, doFill);
	}
	
	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		return fill(ForgeDirection.UNKNOWN, resource, doFill);
	}
	
	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return null;
	}
	
	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		return null;
	}
	
	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction)
	{
		return new ILiquidTank[] { _tank };
	}
	
	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
	{
		if(type != null && type.itemID == LiquidDictionary.getCanonicalLiquid("mobEssence").itemID)
		{
			return _tank;
		}
		return null;
	}
	
	@Override
	public ILiquidTank getTank()
	{
		return _tank;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setInteger("fortune", _fortune);
		tag.setBoolean("shouldWork", _shouldWork);
		tag.setTag("stack", _lastInput != null ? _lastInput.writeToNBT(new NBTTagCompound()) : null);
		
		if (_lastOutput != null)
		{
			NBTTagList nbttaglist = new NBTTagList();
			for (ItemStack item : _lastOutput)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				item.writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
			tag.setTag("SmashedItems", nbttaglist);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		_fortune = tag.getInteger("fortune");
		_shouldWork = tag.hasKey("shouldWork") ? tag.getBoolean("shouldWork") : true;
		if (tag.hasKey("stack"))
			_lastInput = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("stack"));

		if (tag.hasKey("SmashedItems"))
		{
			List<ItemStack> drops = new ArrayList<ItemStack>();
			NBTTagList nbttaglist = tag.getTagList("SmashedItems");
			for (int i = nbttaglist.tagCount(); i --> 0; )
			{
				NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
				ItemStack item = ItemStack.loadItemStackFromNBT(nbttagcompound1);
				if (item != null && item.stackSize > 0)
				{
					drops.add(item);
				}
			}
			if (drops.size() != 0)
			{
				_lastOutput = drops;
			}
		}
	}
}
