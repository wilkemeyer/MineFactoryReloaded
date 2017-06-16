package buildcraft.api.recipes;

import com.google.common.collect.ImmutableSet;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants;

import java.io.IOException;
import java.util.Iterator;

/** Provides an immutable assembly recipe */
public final class AssemblyRecipe {
    public final long requiredMicroJoules;
    public final ImmutableSet<ItemStack> requiredStacks;
    public final ItemStack output;

    public AssemblyRecipe(long requiredMicroJoules, ImmutableSet<ItemStack> requiredStacks, ItemStack output) {
        this.requiredMicroJoules = requiredMicroJoules;
        this.requiredStacks = requiredStacks;
        this.output = output;
    }

    public AssemblyRecipe(NBTTagCompound nbt) {
        requiredMicroJoules = nbt.getLong("required_micro_joules");
        NBTTagList requiredStacksTag = nbt.getTagList("required_stacks", Constants.NBT.TAG_COMPOUND);
        ItemStack[] requiredStacksArray = new ItemStack[requiredStacksTag.tagCount()];
        for(int i = 0; i < requiredStacksArray.length; i++) {
            requiredStacksArray[i] = ItemStack.loadItemStackFromNBT(requiredStacksTag.getCompoundTagAt(i));
        }
        requiredStacks = ImmutableSet.copyOf(requiredStacksArray);
        output = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("output"));
    }

    public AssemblyRecipe(PacketBuffer buffer) throws IOException {
        requiredMicroJoules = buffer.readLong();
        ItemStack[] requiredStacksArray = new ItemStack[buffer.readInt()];
        for(int i = 0; i < requiredStacksArray.length; i++) {
            requiredStacksArray[i] = buffer.readItemStackFromBuffer();
        }
        requiredStacks = ImmutableSet.copyOf(requiredStacksArray);
        output = buffer.readItemStackFromBuffer();
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("required_micro_joules", requiredMicroJoules);
        NBTTagList requiredStacksTag = new NBTTagList();
        for(ItemStack requiredStack : requiredStacks) {
            requiredStacksTag.appendTag(requiredStack.serializeNBT());
        }
        nbt.setTag("required_stacks", requiredStacksTag);
        nbt.setTag("output", output.serializeNBT());
        return nbt;
    }

    public void writeToBuffer(PacketBuffer buffer) {
        buffer.writeLong(requiredMicroJoules);
        buffer.writeInt(requiredStacks.size());
        requiredStacks.forEach(buffer::writeItemStackToBuffer);
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

        AssemblyRecipe that = (AssemblyRecipe) o;

        if(requiredMicroJoules != that.requiredMicroJoules) {
            return false;
        }
        if(requiredStacks != null && that.requiredStacks != null) {
            Iterator<ItemStack> iterator1 = requiredStacks.iterator();
            Iterator<ItemStack> iterator2 = that.requiredStacks.iterator();
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
            return requiredStacks == null && that.requiredStacks == null && output != null ? ItemStack.areItemStacksEqual(output, that.output) : that.output == null;
        }
    }

    @Override
    public int hashCode() {
        int result = (int) (requiredMicroJoules ^ (requiredMicroJoules >>> 32));
        result = 31 * result + (requiredStacks != null ? requiredStacks.hashCode() : 0);
        result = 31 * result + (output != null ? output.hashCode() : 0);
        return result;
    }
}
