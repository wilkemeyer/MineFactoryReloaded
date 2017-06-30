package powercrystals.minefactoryreloaded.setup;

import com.google.common.base.Strings;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
//import net.minecraftforge.fml.common.registry.LanguageRegistry;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.StringTranslate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.helpers.Loader;
import org.apache.logging.log4j.spi.AbstractLogger;

import java.io.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public abstract class BaseMod {

	protected File _configFolder;
	protected final String _modid;
	protected final Logger _log;

	protected BaseMod(Logger log) {

		String name = getModId();
		_modid = name.toLowerCase(Locale.US);
		_log = log;
		init();
	}

	protected BaseMod() {

		String name = getModId();
		_modid = name.toLowerCase(Locale.US);
		_log = LogManager.getLogger(name);
		init();
	}

	public abstract String getModId();

	private void init() {

		ModContainer container = net.minecraftforge.fml.common.Loader.instance().activeModContainer();
		if (container.getSource().isDirectory()) {
			FMLCommonHandler.instance().registerCrashCallable(new CrashCallable("Loaded from a directory"));
		} else {
			try {
				JarFile jar = new JarFile(container.getSource());
				ZipEntry file = jar.getEntry("vers.prop");
				if (file != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(file)));
					String data = reader.readLine();
					FMLCommonHandler.instance().registerCrashCallable(new CrashCallable(data));
				} else {
					FMLCommonHandler.instance().registerCrashCallable(new CrashCallable("Lacking version information."));
				}
				jar.close();
			} catch (IOException e) {
				FMLCommonHandler.instance().registerCrashCallable(new CrashCallable("Error reading version information." + e.getMessage()));
			}
		}
	}

	@NetworkCheckHandler
	public final boolean networkCheck(Map<String, String> remoteVersions, Side side) throws InvalidVersionSpecificationException {

		if (!requiresRemoteFrom(side)) {
			return true;
		}
		Mod mod = getClass().getAnnotation(Mod.class);
		String _modid = mod.modid();
		if (!remoteVersions.containsKey(_modid)) {
			return false;
		}
		String remotes = mod.acceptableRemoteVersions();
		if (!"*".equals(remotes)) {

			String remote = remoteVersions.get(_modid);
			if (Strings.isNullOrEmpty(remotes)) {
				//return getModVersion().equalsIgnoreCase(remote);
			}
			//return ModRange.createFromVersionSpec(_modid, remotes).containsVersion(new ModVersion(_modid, remote));
		}
		return true;
	}

	protected boolean requiresRemoteFrom(Side side) {

		return true;
	}

	protected String getConfigBaseFolder() {

		String base = getClass().getPackage().getName();
		int i = base.indexOf('.');
		if (i >= 0) {
			return base.substring(0, i);
		}
		return "";
	}

	protected void setConfigFolderBase(File folder) {

		_configFolder = new File(folder, getConfigBaseFolder() + "/" + _modid + "/");
	}

	protected File getConfig(String name) {

		return new File(_configFolder, name + ".cfg");
	}

	protected File getClientConfig() {

		return getConfig("client");
	}

	protected File getCommonConfig() {

		return getConfig("common");
	}

	protected String getAssetDir() {

		return _modid;
	}

	public Logger getLogger() {

		return _log;
	}

	private class CrashCallable implements ICrashCallable {

		private final String data;

		private CrashCallable(String data) {

			this.data = data;
		}

		@Override
		public String call() throws Exception {

			return data;
		}

		@Override
		public String getLabel() {

			return getModId();
		}

	}

}
