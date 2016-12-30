package buildcraft.api.transport.neptune;

import net.minecraft.client.renderer.VertexBuffer;

public interface IPluggableDynamicRenderer {
    void render(double x, double y, double z, float partialTicks, VertexBuffer vb);
}
