package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ServerProxy extends CommonProxy
{
	private GridTickHandler gridTickHandler;
	
	@Override
	public void init()
	{
		super.init();
		gridTickHandler = new GridTickHandler();
		TickRegistry.registerScheduledTickHandler(gridTickHandler, Side.SERVER);
	}
}
