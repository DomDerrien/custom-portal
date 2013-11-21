package dderrien.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class IntrospectionHelper {

    public static Class<?> getFirstTypeArgument(Class<?> clazz) {
        return getTypeArgument(clazz, 0);
    }

    public static Class<?> getTypeArgument(Class<?> clazz, int i) {
        if (clazz.getGenericSuperclass() != null && ParameterizedType.class.isAssignableFrom(clazz.getGenericSuperclass().getClass())) {
            Type[] actualTypeArguments = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
            if (actualTypeArguments.length > i) {
                return (Class<?>) actualTypeArguments[i];
            }
            else {
                return null;
            }
        }
        else {
            if (clazz.getSuperclass() != null) {
                return getTypeArgument(clazz.getSuperclass(), i);
            }
            else {
                return null;
            }
        }
    }

}