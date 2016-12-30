package buildcraft.api.recipes;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants;

import java.io.IOException;
import java.util.Iterator;

public final class IntegrationRecipe {
    public final long requiredMicroJoules;
    public final ItemStack target;
    public final ImmutableList<ItemStack> toIntegrate;
    public final ItemStack output;

    public IntegrationRecipe(long requiredMicroJoules, ItemStack target, ImmutableList<ItemStack> toIntegrate, ItemStack output) {
        this.requiredMicroJoules = requiredMicroJoules;
        this.target = target;
        this.toIntegrate = toIntegrate;
        this.output = output;
    }

    public IntegrationRecipe(NBTTagCompound nbt) {
        requiredMicroJoules = nbt.getLong("required_micro_joules");
        target = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("target"));
        NBTTagList toIntegrateTag = nbt.getTagList("to_integrate", Constants.NBT.TAG_COMPOUND);
        ItemStack[] toIntegrateArray = new ItemStack[toIntegrateTag.tagCount()];
        for(int i = 0; i < toIntegrateArray.length; i++) {
            toIntegrateArray[i] = ItemStack.loadItemStackFromNBT(toIntegrateTag.getCompoundTagAt(i));
        }
        toIntegrate = ImmutableList.copyOf(toIntegrateArray);
        output = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("output"));
    }

    public IntegrationRecipe(PacketBuffer buffer) throws IOException {
        requiredMicroJoules = buffer.readLong();
        target = buffer.readItemStackFromBuffer();
        ItemStack[] toIntegrateArray = new ItemStack[buffer.readInt()];
        for(int i = 0; i < toIntegrateArray.length; i++) {
            toIntegrateArray[i] = buffer.readItemStackFromBuffer();
        }
        toIntegrate = ImmutableList.copyOf(toIntegrateArray);
        output = buffer.readItemStackFromBuffer();
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("required_micro_joules", requiredMicroJoules);
        nbt.setTag("target", target.serializeNBT());
        NBTTagList toIntegrateTag = new NBTTagList();
        for(ItemStack toIntegrateElement : toIntegrate) {
            toIntegrateTag.appendTag(toIntegrateElement.serializeNBT());
        }
        nbt.setTag("to_integrate", toIntegrateTag);
        nbt.setTag("output", output.serializeNBT());
        return nbt;
    }

    public void writeToBuffer(PacketBuffer buffer) {
        buffer.writeLong(requiredMicroJoules);
        buffer.writeItemStackToBuffer(target);
        buffer.writeInt(toIntegrate.size());
        toIntegrate.forEach(buffer::writeItemStackToBuffer);
        buffer.writeItemStackToBuffer(output);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        IntegrationRecipe that = (IntegrationRecipe) o;

        if(requiredMicroJoules != that.requiredMicroJoules) {
            return false;
        }
        if(target != null ? !ItemStack.areItemStacksEqual(target, that.target) : that.target != null) {
            return false;
        }
        if(toIntegrate != null && that.toIntegrate != null) {
            Iterator<ItemStack> iterator1 = toIntegrate.iterator();
            Iterator<ItemStack> iterator2 = that.toIntegrate.iterator();
            while(iterator1.hasNext()) {
                if (!iterator2.hasNext()) {
                    return false;
                }
                ItemStack o1 = iterator1.next();
                ItemStack o2 = iterator2.next();
                if (!ItemStack.areItemStacksEqual(o1, o2)) {
                    return false;
                }
            }
            return !iterator2.hasNext() && output != null ? ItemStack.areItemStacksEqual(output, that.output) : that.output == null;
        } else {
            return toIntegrate == null && that.toIntegrate == null && output != null ? ItemStack.areItemStacksEqual(output, that.output) : that.output == null;
        }
    }

    @Override
    public int hashCode() {
        int result = (int) (requiredMicroJoules ^ (requiredMicroJoules >>> 32));
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (toIntegrate != null ? toIntegrate.hashCode() : 0);
        result = 31 * result + (output != null ? output.hashCode() : 0);
        return result;
    }
}
