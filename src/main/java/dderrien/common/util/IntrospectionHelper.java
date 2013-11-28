package dderrien.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class IntrospectionHelper {

    public static Class<?> getFirstTypeArgument(Class<?> clazz) {
        return getTypeArgument(clazz, 0);
    }

    public static Class<?> getTypeArgument(Class<?> clazz, int i) {
    	// Get the generic definition from the super class
        if (clazz.getGenericSuperclass() != null && ParameterizedType.class.isAssignableFrom(clazz.getGenericSuperclass().getClass())) {
            Type[] actualTypeArguments = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
            // Return the generic class that match the given index
            if (i < actualTypeArguments.length) {
                return (Class<?>) actualTypeArguments[i];
            }
            // Index too high, return default response
            return null;
        }
        // Go higher in the class hierarchy
        if (clazz.getSuperclass() != null) {
            return getTypeArgument(clazz.getSuperclass(), i);
        }
        // Default response
        return null;
    }

}