package powercrystals.minefactoryreloaded.gui.control;

import cofh.gui.element.ElementListBox;
import cofh.gui.element.listbox.IListBoxElement;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

public class ListBoxElementCircuit implements IListBoxElement
{
	private IRedNetLogicCircuit _circuit;
	
	public ListBoxElementCircuit(IRedNetLogicCircuit circuit)
	{
		_circuit = circuit;
	}
	
	@Override
	public Object getValue()
	{
		return _circuit;
	}
	
	@Override
	public int getHeight()
	{
		return 10;
	}
	
	@Override
	public int getWidth()
	{
		return Minecraft.getMinecraft().fontRenderer.getStringWidth(StatCollector.translateToLocal(_circuit.getUnlocalizedName()));
	}
	
	@Override
	public void draw(ElementListBox listBox, int x, int y, int backColor, int textColor)
	{
		listBox.getContainerScreen().fontRendererObj.drawString(StatCollector.translateToLocal(_circuit.getUnlocalizedName()), x, y, textColor);
	}
}
