package powercrystals.minefactoryreloaded.render.model;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public enum MFRModelLoader implements ICustomModelLoader {
	INSTANCE;

	@Override
	public boolean accepts(ResourceLocation modelLocation) {

		return modelLocation.equals(PlasticCupModel.MODEL_LOCATION)
				|| modelLocation.equals(SyringeModel.MODEL_LOCATION)
				|| modelLocation.equals(FactoryGlassModel.MODEL_LOCATION);
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) {

		if (modelLocation.equals(PlasticCupModel.MODEL_LOCATION))
			return PlasticCupModel.MODEL;
		if (modelLocation.equals(SyringeModel.MODEL_LOCATION))
			return SyringeModel.MODEL;
		if (modelLocation.equals(FactoryGlassModel.MODEL_LOCATION))
			return FactoryGlassModel.MODEL;

		return null;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// no need to clear cache since we create a new model instance
	}
}
