package it.unibo.controller.shared.utils;

import java.lang.reflect.Field;

/**
 * Reflection helpers for reading and writing private fields.
 */
public class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Reads the current value of a private field via reflection.
     *
     * @param instance the object to read from
     * @param fieldName the exact name of the declared field
     * @return the field's current value
     */
    public static Object getField(Object instance, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return accessibleField(instance, fieldName).get(instance);
    }

    /**
     * Overwrites a private field via reflection.
     *
     * @param instance the object to modify
     * @param fieldName the exact name of the declared field
     * @param value the new value; must be assignment-compatible with the field's type
     */
    public static void setField(Object instance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        accessibleField(instance, fieldName).set(instance, value);
    }

    private static Field accessibleField(Object instance, String fieldName) throws NoSuchFieldException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
}
