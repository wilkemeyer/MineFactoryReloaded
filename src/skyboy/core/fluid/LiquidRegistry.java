package skyboy.core.fluid;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidRegistry.FluidRegisterEvent;
import net.minecraftforge.fluids.FluidStack;

import skyboy.core.container.CarbonContainer;

final public class LiquidRegistry {
	private static LiquidRegistry instance;
	private static final Map<String, Fluid> nameMap = new HashMap<String, Fluid>();
	private static final ArrayList<String> names = new ArrayList<String>();
	private static final Map<Fluid, String> liquidMap = new HashMap<Fluid, String>();
	private static final Map<String, Integer> idMap = new HashMap<String, Integer>();
	private static final List<ILiquidRegistryCallback> callbacks = new ArrayList<ILiquidRegistryCallback>();
	private static int id = 1;
	private static Map<String, Boolean> seenNames = new HashMap<String, Boolean>();
	private static File config;
	private static ModContainer parent;

	public LiquidRegistry(File base, ModContainer _parent) throws IOException {
		if (instance != null) {
			throw new IllegalArgumentException();
		}
		instance = this;
		parent = _parent;
		loadLiquidConfig(new File(base.getAbsolutePath() + "/liquids.cfg"));

		for (Map.Entry<String, Fluid> e : FluidRegistry.getRegisteredFluids().entrySet()) {
			Fluid liquid = e.getValue();
			String name = e.getKey();
			preInitRegister(name, liquid);
			CarbonContainer.registerAsContainer(liquid);
		}
	}

	private void loadLiquidConfig(File configFile) throws IOException {
		config = configFile;
		if (!configFile.exists()) configFile.createNewFile();
		BufferedReader f = new BufferedReader(new FileReader(configFile));

		String ids = "";
		for (String line; (line = f.readLine()) != null; ) {
			line = line.trim();
			String t = line.substring(0,3);
			if (t.equalsIgnoreCase("end"))
				id = Integer.parseInt(line.substring(4));
			else if (t.equalsIgnoreCase("lis"))
				ids = line.substring(5);
		}
		f.close();

		if (id < 0) {
			int temp = ids.indexOf(44), c = temp != -1 ? -1 : 0;
			while (temp != -1) {++c; temp = ids.indexOf(44, temp);}
			id = c;
		}
		
		names.ensureCapacity(id);
		for (int i = id; i-- != 0; ) names.add("");
		String[] list = ids.split(",");
		for (int i = list.length; i-- != 0; ) {
			String item = list[i];
			int colon = item.lastIndexOf(58);
			if (colon != -1) {
				int t = Integer.parseInt(item.substring(colon+1));
				item = item.substring(0,colon).trim();
				String existing = names.get(t);
				if (existing.isEmpty() && t < id && t > 0) {
					idMap.put(item, t);
					names.set(t, item);
				} else {
					if (t >= id || t <= 0)
						;//FMLLog.log(parent.getName(), Level.WARNING, "ID %s for %s outside range of 0-%s; skipping %s. This may break existing buckets.", t, item, id, item);
					else
						;//FMLLog.log(parent.getName(), Level.SEVERE, "Existing liquid [%s] using ID %s; skipping %s. This may break existing buckets.", existing, t, item);
				}
			}
		}
	}

	public void saveLiquidConfig() throws IOException {
		BufferedWriter f = new BufferedWriter(new FileWriter(config, false));
		String t;

		f.write("end:", 0, 4);
		t = Integer.toString(id, 10);
		f.write(t, 0, t.length());
		f.newLine();

		f.write("list:", 0, 5);
		for (Map.Entry<String, Integer> e : idMap.entrySet()) {
			t = e.getKey();
			f.write(t, 0, t.length());
			f.write(58);
			t = Integer.toString(e.getValue(), 10);
			f.write(t, 0, t.length());
			f.write(44);
		}
		f.newLine();
		f.close();
	}

	@SubscribeEvent
	public void registeredLiquid(FluidRegisterEvent e) throws Throwable {
		String name = e.fluidName;
		Fluid stack = FluidRegistry.getFluid(name);
		preInitRegister(name, stack);
		if (!seenNames.containsKey(name)) {
			CarbonContainer.registerAsContainer(stack);
			LiquidEvent evt = new LiquidEvent(e, idMap.get(name));
			for (ILiquidRegistryCallback o : callbacks)
				try {
					o.registeredLiquid(evt);
				} catch (Throwable _) {}
			seenNames.put(name, true);
		}
	}

	public void preInitRegister(String name, Fluid stack) throws IOException {
		Fluid liquid = stack;
		if (!liquidMap.containsKey(liquid)) {
			liquidMap.put(liquid, name);
			nameMap.put(name, stack);
		} else ;//FMLLog.log(parent.getName(), Level.FINEST, "%s already has a LiquidStack associated", name);
		if (!names.contains(name)) {
			idMap.put(name, id);
			names.add(id, name);
			++id;
			if (Loader.instance().getModState(parent).ordinal() >= ModState.AVAILABLE.ordinal()) {
				//FMLLog.log(parent.getName(), Level.FINE, "Liquid %s registered after LoadComplete", name);
				saveLiquidConfig();
			}
		}
	}

	public static String getName(FluidStack liquid) {
		return liquidMap.get(liquid.getFluid());
	}

	public static String getName(int ID) {
		return ID < id ? names.get(ID) : null;
	}

	public static FluidStack getLiquid(String name) {
		FluidStack q = FluidRegistry.getFluidStack(name, FluidContainerRegistry.BUCKET_VOLUME);
		return q != null ? q.copy() : null;
	}

	public static FluidStack getLiquid(int id) {
		return getLiquid(getName(id));
	}

	public static FluidStack getLiquid(String name, int amount) {
		FluidStack q = getLiquid(name);
		if (q != null) q.amount = amount;
		return q;
	}

	public static FluidStack getLiquid(int id, int amount) {
		return getLiquid(getName(id), amount);
	}

	public static int getID(FluidStack liquid) {
		return getID(getName(liquid));
	}

	public static int getID(String name) {
		return idMap.get(name);
	}

	public static String[] getNames() {
		int i = id - 1;
		String[] ret = new String[i];
		while (i-- != 0)
			ret[i] = names.get(i);
		return ret;
	}

	public static boolean liquidExists(String name) {
		return nameMap.get(name) != null;
	}

	public static boolean liquidExists(int id) {
		return liquidExists(getName(id));
	}

	public static int getRegisteredLiquidCount() {
		return id - 1;
	}

	public static void registerCallback(ILiquidRegistryCallback o) {
		if (o != null)
			callbacks.add(o);
	}

	public class LiquidEvent extends FluidRegisterEvent {
		public final int ID;
		public LiquidEvent(FluidRegisterEvent evt, int id) {
			super(evt.fluidName, evt.fluidID);
			ID = id;
		}
	}
	public interface ILiquidRegistryCallback {
		void registeredLiquid(LiquidEvent e);
	}
}