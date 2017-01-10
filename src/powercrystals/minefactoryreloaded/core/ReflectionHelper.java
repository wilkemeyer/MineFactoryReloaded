package powercrystals.minefactoryreloaded.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionHelper {

	public static <T, E> void setFinalValue(Class <? super T > classToAccess, T instance, E value, String... fieldNames) {

		try
		{
			Field field = net.minecraftforge.fml.relauncher.ReflectionHelper.findField(classToAccess, fieldNames);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			
			field.set(instance, value);
		}
		catch (Exception e)
		{
			throw new net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException(fieldNames, e);
		}
	}
}
