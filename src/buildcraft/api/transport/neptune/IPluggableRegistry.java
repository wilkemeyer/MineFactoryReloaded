package buildcraft.api.transport.neptune;

import net.minecraft.util.ResourceLocation;

public interface IPluggableRegistry {
    void registerPluggable(PluggableDefinition definition);

    PluggableDefinition getDefinition(ResourceLocation identifier);
}
