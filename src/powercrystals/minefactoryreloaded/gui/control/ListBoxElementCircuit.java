package powercrystals.minefactoryreloaded.gui.control;

import cofh.gui.element.ElementListBox;
import cofh.gui.element.listbox.IListBoxElement;

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
	public void draw(ElementListBox listBox, int x, int y, int backColor, int textColor)
	{
		String text = listBox.getContainerScreen().fontRendererObj.trimStringToWidth(StatCollector.translateToLocal(_circuit.getUnlocalizedName()), listBox.getContentWidth());
		listBox.getContainerScreen().fontRendererObj.drawStringWithShadow(text, x, y, textColor);
	}
}
