package com.shane.android.common.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import android.util.Log;

import com.shane.java.utils.MemberUtils;
import com.shane.java.utils.ObjectReference;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

public class ReflectionUtils {
    /**
     * @hide
     */
    public static final ClassLoader BOOTCLASSLOADER = ClassLoader.getSystemClassLoader();
    private static final String TAG = "ReflectionUtils";
    private static final HashMap<String, Field> fieldCache = new HashMap<String, Field>();
    private static final HashMap<String, Method> methodCache = new HashMap<String, Method>();
    private static final HashMap<String, Constructor<?>> constructorCache = new HashMap<String, Constructor<?>>();

    /**
     * Look up a class with the specified class loader (or the boot class loader
     * if <code>classLoader</code> is <code>null</code>).
     * <p>
     * Class names can be specified in different formats:
     * <ul>
     * <li>java.lang.Integer
     * <li>int
     * <li>int[]
     * <li>[I
     * <li>java.lang.String[]
     * <li>[Ljava.lang.String;
     * <li>android.app.ActivityThread.ResourcesKey
     * <li>android.app.ActivityThread$ResourcesKey
     * <li>android.app.ActivityThread$ResourcesKey[]
     * </ul>
     * <p>
     * If class is not found, ClassNotFoundException is thrown
     *
     * @hide
     */
    public static Class<?> findClass(String className, ClassLoader classLoader)
            throws ClassNotFoundException {
        if (classLoader == null)
            classLoader = BOOTCLASSLOADER;
        return ClassUtils.getClass(classLoader, className, false);
    }

    /**
     * Look up a class with the specified class loader (or the boot class loader
     * if <code>classLoader</code> is <code>null</code>).
     * <p>
     * Class names can be specified in different formats:
     * <ul>
     * <li>java.lang.Integer
     * <li>int
     * <li>int[]
     * <li>[I
     * <li>java.lang.String[]
     * <li>[Ljava.lang.String;
     * <li>android.app.ActivityThread.ResourcesKey
     * <li>android.app.ActivityThread$ResourcesKey
     * <li>android.app.ActivityThread$ResourcesKey[]
     * </ul>
     * <p>
     * return null if class is not found
     */
    public static Class<?> tryFindClass(String className, ClassLoader classLoader) {
        try {
            return findClass(className, classLoader);
        } catch (ClassNotFoundException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    /**
     * Look up a field in a class and set it to accessible. The result is
     * cached. If the field was not found, NoSuchFieldException is thrown.
     *
     * @hide
     */
    public static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        StringBuilder sb = new StringBuilder(clazz.getName());
        sb.append('#');
        sb.append(fieldName);
        String fullFieldName = sb.toString();

        synchronized (fieldCache) {
            if (fieldCache.containsKey(fullFieldName)) {
                Field field = fieldCache.get(fullFieldName);
                if (field == null)
                    throw new NoSuchFieldException(fullFieldName);
                return field;
            }
        }

        try {
            Field field = findFieldRecursiveImpl(clazz, fieldName);
            field.setAccessible(true);
            synchronized (fieldCache) {
                fieldCache.put(fullFieldName, field);
            }
            return field;
        } catch (NoSuchFieldException e) {
            synchronized (fieldCache) {
                fieldCache.put(fullFieldName, null);
            }
            throw e;
        }
    }

    /**
     * Look up a field in a class and set it to accessible. The result is
     * cached. If the field was not found, return null
     *
     * @hide
     */
    public static Field tryFindField(Class<?> clazz, String fieldName) {
        try {
            return findField(clazz, fieldName);
        } catch (NoSuchFieldException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    private static Field findFieldRecursiveImpl(Class<?> clazz, String fieldName)
            throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            while (true) {
                clazz = clazz.getSuperclass();
                if (clazz == null || clazz.equals(Object.class))
                    break;

                try {
                    return clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ignored) {
                }
            }
            throw e;
        }
    }

    /**
     * Look up a method in a class and set it to accessible. The result is
     * cached. If the method was not found, a {@link NoSuchMethodException} will be
     * thrown.
     * <p>
     * The parameter types may either be specified as <code>Class</code> or
     * <code>String</code> objects. In the latter case, the class is looked up
     * using {@link #findClass} with the same class loader as the method's
     * class.
     *
     * @hide
     */
    public static Method findMethodExact(Class<?> clazz, String methodName,
            Object... parameterTypes) throws ClassNotFoundException, NoSuchMethodException {
        Class<?>[] parameterClasses = null;
        for (int i = parameterTypes.length - 1; i >= 0; i--) {
            Object type = parameterTypes[i];
            if (type == null)
                throw new NullPointerException("parameter type must not be null");

            if (parameterClasses == null)
                parameterClasses = new Class<?>[i + 1];

            if (type instanceof Class)
                parameterClasses[i] = (Class<?>) type;
            else if (type instanceof String)
                parameterClasses[i] = findClass((String) type, clazz.getClassLoader());
            else
                throw new IllegalArgumentException(
                        "parameter type must either be specified as Class or String", null);
        }

        // if there are no arguments for the method
        if (parameterClasses == null)
            parameterClasses = new Class<?>[0];

        return findMethodExact(clazz, methodName, parameterClasses);
    }

    /**
     * Look up a method in a class and set it to accessible. The result is
     * cached. If the method was not found, return null.
     * <p>
     * The parameter types may either be specified as <code>Class</code> or
     * <code>String</code> objects. In the latter case, the class is looked up
     * using {@link #findClass} with the same class loader as the method's
     * class.
     *
     * @hide
     */
    public static Method tryFindMethodExact(Class<?> clazz, String methodName,
            Object... parameterTypes) {
        try {
            return findMethodExact(clazz, methodName, parameterTypes);
        }  catch (ClassNotFoundException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (NoSuchMethodException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    /**
     * Look up a method in a class and set it to accessible. The result is
     * cached. If the method was not found, a {@link NoSuchMethodException} will be
     * thrown.
     *
     * @hide
     */
    public static Method findMethodExact(Class<?> clazz, String methodName,
            Class<?>... parameterTypes) throws NoSuchMethodException {
        StringBuilder sb = new StringBuilder(clazz.getName());
        sb.append('#');
        sb.append(methodName);
        sb.append(getParametersString(parameterTypes));
        sb.append("#exact");
        String fullMethodName = sb.toString();

        synchronized (methodCache) {
            if (methodCache.containsKey(fullMethodName)) {
                Method method = methodCache.get(fullMethodName);
                if (method == null)
                    throw new NoSuchMethodException(fullMethodName);
                return method;
            }
        }

        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            synchronized (methodCache) {
                methodCache.put(fullMethodName, method);
            }
            return method;
        } catch (NoSuchMethodException e) {
            synchronized (methodCache) {
                methodCache.put(fullMethodName, null);
            }
            throw e;
        }
    }

    /**
     * Look up a method in a class and set it to accessible. The result is
     * cached. If the method was not found, return null.
     *
     * @hide
     */
    public static Method tryFindMethodExact(Class<?> clazz, String methodName,
            Class<?>... parameterTypes) {
        try {
            return findMethodExact(clazz, methodName, parameterTypes);
        } catch (NoSuchMethodException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    /**
     * Look up a method in a class and set it to accessible. The result is
     * cached. This does not only look for exact matches, but for the closest
     * match. If the method was not found, a {@link NoSuchMethodException} will be
     * thrown.
     *
     * @see MethodUtils#getMatchingAccessibleMethod
     *
     * @hide
     */
    public static Method findMethodBestMatch(Class<?> clazz, String methodName,
            Class<?>... parameterTypes) throws NoSuchMethodException {
        StringBuilder sb = new StringBuilder(clazz.getName());
        sb.append('#');
        sb.append(methodName);
        sb.append(getParametersString(parameterTypes));
        sb.append("#bestmatch");
        String fullMethodName = sb.toString();

        synchronized (methodCache) {
            if (methodCache.containsKey(fullMethodName)) {
                Method method = methodCache.get(fullMethodName);
                if (method == null)
                    throw new NoSuchMethodException(fullMethodName);
                return method;
            }
        }

        try {
            Method method = findMethodExact(clazz, methodName, parameterTypes);
            synchronized (methodCache) {
                methodCache.put(fullMethodName, method);
            }
            return method;
        } catch (NoSuchMethodException ignored) {
        }

        Method bestMatch = null;
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            // compare name and parameters
            if (method.getName().equals(methodName)
                    && ClassUtils.isAssignable(parameterTypes, method.getParameterTypes(), true)) {
                // get accessible version of method
                if (bestMatch == null || MemberUtils.compareParameterTypes(
                        method.getParameterTypes(),
                        bestMatch.getParameterTypes(),
                        parameterTypes) < 0) {
                    bestMatch = method;
                }
            }
        }

        if (bestMatch != null) {
            bestMatch.setAccessible(true);
            synchronized (methodCache) {
                methodCache.put(fullMethodName, bestMatch);
            }
            return bestMatch;
        } else {
            NoSuchMethodException e = new NoSuchMethodException(fullMethodName);
            synchronized (methodCache) {
                methodCache.put(fullMethodName, null);
            }
            throw e;
        }
    }

    /**
     * Look up a method in a class and set it to accessible. The result is
     * cached. This does not only look for exact matches, but for the closest
     * match. If the method was not found, return null
     *
     * @see MethodUtils#getMatchingAccessibleMethod
     *
     * @hide
     */
    public static Method tryFindMethodBestMatch(Class<?> clazz, String methodName,
            Class<?>... parameterTypes) {
        try {
            return findMethodBestMatch(clazz, methodName, parameterTypes);
        } catch (NoSuchMethodException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    /**
     * Look up a method in a class and set it to accessible. Parameter types are
     * determined from the <code>args</code> for the method call. The result is
     * cached. This does not only look for exact matches, but for the closest
     * match. If the method was not found, a {@link NoSuchMethodException} will be
     * thrown.
     *
     * @hide
     */
    public static Method findMethodBestMatch(Class<?> clazz, String methodName, Object... args) throws NoSuchMethodException {
        return findMethodBestMatch(clazz, methodName, getParameterTypes(args));
    }

    /**
     * Look up a method in a class and set it to accessible. Parameter types are
     * determined from the <code>args</code> for the method call. The result is
     * cached. This does not only look for exact matches, but for the closest
     * match. If the method was not found, return null
     *
     * @hide
     */
    public static Method tryFindMethodBestMatch(Class<?> clazz, String methodName, Object... args) {
        try {
            return findMethodBestMatch(clazz, methodName, args);
        } catch (NoSuchMethodException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    /**
     * Return an array with the classes of the given objects
     *
     * @hide
     */
    public static Class<?>[] getParameterTypes(Object... args) {
        Class<?>[] clazzes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            clazzes[i] = (args[i] != null) ? args[i].getClass() : null;
        }
        return clazzes;
    }

    /**
     * Return an array with the classes of the given objects
     *
     * @hide
     */
    public static Class<?>[] getClassesAsArray(Class<?>... clazzes) {
        return clazzes;
    }

    private static String getParametersString(Class<?>... clazzes) {
        StringBuilder sb = new StringBuilder("(");
        boolean first = true;
        for (Class<?> clazz : clazzes) {
            if (first)
                first = false;
            else
                sb.append(",");

            if (clazz != null)
                sb.append(clazz.getCanonicalName());
            else
                sb.append("null");
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * find one constructor.
     * if not found, NoSuchMethodException is thrown.
     *
     * @hide
     */
    public static Constructor<?> findConstructorExact(Class<?> clazz, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        StringBuilder sb = new StringBuilder(clazz.getName());
        sb.append(getParametersString(parameterTypes));
        sb.append("#exact");
        String fullConstructorName = sb.toString();

        synchronized (constructorCache) {
            if (constructorCache.containsKey(fullConstructorName)) {
                Constructor<?> constructor = constructorCache.get(fullConstructorName);
                if (constructor == null)
                    throw new NoSuchMethodException(fullConstructorName);
                return constructor;
            }
        }

        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            synchronized (constructorCache) {
                constructorCache.put(fullConstructorName, constructor);
            }
            return constructor;
        } catch (NoSuchMethodException e) {
            synchronized (constructorCache) {
                constructorCache.put(fullConstructorName, null);
            }
            throw e;
        }
    }

    /**
     * find one constructor.
     * if not found, return null
     *
     * @hide
     */
    public static Constructor<?> tryFindConstructorExact(Class<?> clazz, Class<?>... parameterTypes) {
        try {
            return findConstructorExact(clazz, parameterTypes);
        } catch (NoSuchMethodException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    /**
     * find one constructor.
     * If not found, NoSuchMethodException is thrown
     *
     * @hide
     */
    public static Constructor<?> findConstructorBestMatch(Class<?> clazz,
            Class<?>... parameterTypes) throws NoSuchMethodException {
        StringBuilder sb = new StringBuilder(clazz.getName());
        sb.append(getParametersString(parameterTypes));
        sb.append("#bestmatch");
        String fullConstructorName = sb.toString();

        synchronized (constructorCache) {
            if (constructorCache.containsKey(fullConstructorName)) {
                Constructor<?> constructor = constructorCache.get(fullConstructorName);
                if (constructor == null)
                    throw new NoSuchMethodException(fullConstructorName);
                return constructor;
            }
        }

        try {
            Constructor<?> constructor = findConstructorExact(clazz, parameterTypes);
            synchronized (constructorCache) {
                constructorCache.put(fullConstructorName, constructor);
            }
            return constructor;
        } catch (NoSuchMethodException ignored) {
        }

        Constructor<?> bestMatch = null;
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            // compare name and parameters
            if (ClassUtils.isAssignable(parameterTypes, constructor.getParameterTypes(), true)) {
                // get accessible version of method
                if (bestMatch == null || MemberUtils.compareParameterTypes(
                        constructor.getParameterTypes(),
                        bestMatch.getParameterTypes(),
                        parameterTypes) < 0) {
                    bestMatch = constructor;
                }
            }
        }

        if (bestMatch != null) {
            bestMatch.setAccessible(true);
            synchronized (constructorCache) {
                constructorCache.put(fullConstructorName, bestMatch);
            }
            return bestMatch;
        } else {
            NoSuchMethodException e = new NoSuchMethodException(fullConstructorName);
            synchronized (constructorCache) {
                constructorCache.put(fullConstructorName, null);
            }
            throw e;
        }
    }

    /**
     * find one constructor.
     * If not found, return null
     *
     * @hide
     */
    public static Constructor<?> tryFindConstructorBestMatch(Class<?> clazz,
            Class<?>... parameterTypes) {
        try {
            return findConstructorBestMatch(clazz, parameterTypes);
        } catch (NoSuchMethodException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    /**
     * find one constructor.
     * If not found, NoSuchMethodException is thrown.
     *
     * @hide
     */
    public static Constructor<?> findConstructorBestMatch(Class<?> clazz, Object... args) throws NoSuchMethodException {
        return findConstructorBestMatch(clazz, getParameterTypes(args));
    }

    /**
     * find one constructor.
     * If not found, return null
     *
     * @hide
     */
    public static Constructor<?> tryFindConstructorBestMatch(Class<?> clazz, Object... args) {
        try {
            return findConstructorBestMatch(clazz, args);
        } catch (NoSuchMethodException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    /**
     * set the field of object.
     *
     * @hide
     */
    public static void setObjectField(Object obj, String fieldName, Object value)
            throws NoSuchFieldException,  IllegalAccessException,  IllegalArgumentException {
        findField(obj.getClass(), fieldName).set(obj, value);
    }

    /**
     * set the field of object.
     * If failed to set, do nothing.
     *
     * @hide
     */
    public static void trySetObjectField(Object obj, String fieldName, Object value) {
        try {
            setObjectField(obj, fieldName, value);
        } catch (NoSuchFieldException ex) {
            Log.w(TAG, "", ex);
        } catch (IllegalAccessException ex) {
            Log.w(TAG, "", ex);
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "", ex);
        }
    }

    /**
     * get the field of object
     * @param obj, the object
     * @param fieldName, the name of field
     * @param fieldClazz, the expected type of field
     *
     * @hide
     */
    public static <T> T getObjectField(Object obj, String fieldName, Class<T> fieldClazz)
            throws NoSuchFieldException,  IllegalAccessException,  IllegalArgumentException{
        final Object value = findField(obj.getClass(), fieldName).get(obj);
        return (T)checkFieldValue(value, fieldClazz);
    }

    /**
     * Get the value of one field.
     * @param obj, the object
     * @param fieldName, the name of field
     * @param fieldClazz, the expected type of field
     * If error happens, return null
     * If got value, return one ObjectReference, can use ObjectReference.get to access the field value.
     *
     * @hide
     */
    public static <T> ObjectReference<T> tryGetObjectField(Object obj, String fieldName, Class<T> fieldClazz)  {
        try {
            return new ObjectReference<T>(getObjectField(obj, fieldName, fieldClazz));
        } catch (NoSuchFieldException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (IllegalAccessException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    /**
     * For inner classes, return the "this" reference of the surrounding class
     * @param surroundingClazz, the type of the surrounding class
     *
     * @hide
     */
    public static <T> T getSurroundingThis(Object obj, Class<T> surroundingClazz)
            throws NoSuchFieldException,  IllegalAccessException,  IllegalArgumentException {
        return getObjectField(obj, "this$0", surroundingClazz);
    }

    /** For inner classes, return the "this" reference of the surrounding class
     * @param surroundingClazz, the type of the surrounding class
     * If error happens, return null
     * If got value, return one ObjectReference, can use ObjectReference.get
     * to access the field value.
     *
     * @hide
    */
    public static <T> ObjectReference<T> tryGetSurroundingThis(Object obj, Class<T> surroundingClazz)  {
        try {
            return new ObjectReference<T>(getSurroundingThis(obj, surroundingClazz));
        } catch (NoSuchFieldException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (IllegalAccessException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    /**
     * set the static field of class
     * @hide
     */
    public static void setStaticObjectField(Class<?> clazz, String fieldName, Object value)
            throws NoSuchFieldException,  IllegalAccessException,  IllegalArgumentException {
        final Field field = findField(clazz, fieldName);
        try {
            field.set(null, value);
        } catch (NullPointerException ex) {
            // if field is not static field, NullPointerException will be thrown.
            // in order to deal with one situation: one field is changed from static to instance,
            // here we convert it to be IllegalArgumentException
            Log.w(TAG, "", ex);
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * set the static field of class.
     * If error happens, do nothing.
     *
     * @hide
     */
    public static void trySetStaticObjectField(Class<?> clazz, String fieldName, Object value) {
        try {
            setStaticObjectField(clazz, fieldName, value);
        } catch (NoSuchFieldException ex) {
            Log.w(TAG, "", ex);
        } catch (IllegalAccessException ex) {
            Log.w(TAG, "", ex);
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "", ex);
        }
    }

    /**
     * get the value of static field.
     * @param fieldClazz, the type of field
     *
     * @hide
     */
    public static <T> T getStaticObjectField(Class<?> clazz, String fieldName, Class<T> fieldClazz)
            throws NoSuchFieldException,  IllegalAccessException,  IllegalArgumentException {
        final Field field = findField(clazz, fieldName);
        try {
            final Object value = field.get(null);
            return (T)checkFieldValue(value, fieldClazz);
        } catch (NullPointerException ex) {
            Log.w(TAG, "", ex);
            // if field is not static field, NullPointerException will be thrown.
            // in order to deal with one situation: one field is changed from static to instance,
            // here we convert it to be IllegalArgumentException
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * get the value of static field.
     * @param fieldClazz, the type of field
     * If error happens, return null
     * If got value, return one ObjectReference, can use ObjectReference.get
     * to access the real return value.
     *
     * @hide
     */
    public static <T> ObjectReference<T> tryGetStaticObjectField(Class<?> clazz, String fieldName, Class<T> fieldClazz)  {
        try {
            return new ObjectReference<T>(getStaticObjectField(clazz, fieldName, fieldClazz));
        } catch (NoSuchFieldException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (IllegalAccessException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    private static Object checkFieldValue(Object value, Class<?> fieldClazz)
            throws IllegalArgumentException {
        if (fieldClazz == Void.class) {
            throw new IllegalArgumentException("fieldClazz");
        }
        if (value == null) {
            return null;
        }
        if (fieldClazz == null) {
            return value;
        }
        if (ClassUtils.isAssignable(value.getClass(), fieldClazz, true)) {
            return value;
        }
        // The value of field is not expected type, treat it as exception
        throw new IllegalArgumentException("fieldClazz");
    }

    /**
     * Call instance or static method <code>methodName</code> for object
     * <code>obj</code> with the arguments <code>args</code>. The types for the
     * arguments will be determined automaticall from <code>args</code>
     * @param returnValueClazz, the type of returned value
     *
     * @hide
     */
    public static <T> T callMethod(Object obj, String methodName, Class<T> returnValueClazz, Object... args)
            throws NoSuchMethodException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
        final Object returnedValue = findMethodBestMatch(obj.getClass(), methodName, args).invoke(obj, args);
        return (T)checkMethodReturnValue(returnedValue, returnValueClazz);
    }

    /**
     * Call instance or static method <code>methodName</code> for object
     * <code>obj</code> with the arguments <code>args</code>. The types for the
     * arguments will be determined automaticall from <code>args</code>
     * <p>
     * @param returnValueClazz, the type of returned value
     * If error happens, return null
     * If got value, return one ObjectReference, can use ObjectReference.get
     * to access the real return value.
     *
     * @hide
     */
    public static <T> ObjectReference<T> tryCallMethod(Object obj, String methodName, Class<T> returnValueClazz, Object... args) {
        try {
            return new ObjectReference<T>(callMethod(obj, methodName, returnValueClazz, args));
        } catch (NoSuchMethodException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (IllegalAccessException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (InvocationTargetException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    /**
     * Call static method <code>methodName</code> for class <code>clazz</code>
     * with the arguments <code>args</code>. The types for the arguments will be
     * determined automaticall from <code>args</code>
     * @param returnValueClazz, the type of returned value
     *
     * @hide
     */
    public static <T> T callStaticMethod(Class<?> clazz, String methodName, Class<T> returnValueClazz, Object... args)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
                InvocationTargetException  {
        final Method method = findMethodBestMatch(clazz, methodName, args);
        try {
            final Object returnedValue = method.invoke(null, args);
            return (T)checkMethodReturnValue(returnedValue, returnValueClazz);
        } catch (NullPointerException ex) {
            Log.w(TAG, "", ex);
            // If method is not static method, NullPointerException will be thrown.
            // in order to deal with one situation: one method is changed from static to instance,
            // here we convert it to be IllegalArgumentException
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Call static method <code>methodName</code> for class <code>clazz</code>
     * with the arguments <code>args</code>. The types for the arguments will be
     * determined automaticall from <code>args</code>
     * <p>
     * @param returnValueClazz, the type of returned value
     * If error happens, return null
     * If got value, return one ObjectReference, can use ObjectReference.get
     * to access the real return value.
     */
    public static <T> ObjectReference<T> tryCallStaticMethod(Class<?> clazz,
            String methodName, Class<T> returnValueClazz, Object... args) {
        try {
            return new ObjectReference<T>(callStaticMethod(clazz, methodName, returnValueClazz, args));
        } catch (NoSuchMethodException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (IllegalAccessException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (InvocationTargetException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }

    private static Object checkMethodReturnValue(Object returnedValue, Class<?> returnValueClazz)
            throws IllegalArgumentException {
        if (returnedValue == null) {
            return null;
        }
        if (returnValueClazz == null) {
            return returnedValue;
        }
        if (returnValueClazz == Void.class) {
            return null;
        }
        if (ClassUtils.isAssignable(returnedValue.getClass(), returnValueClazz, true)) {
            return returnedValue;
        }
        // The returned value is not expected type, treat it as exception
        throw new IllegalArgumentException("returnValueClazz");
    }

    /**
     * new instance of one class.
     *
     * @hide
     */
    public static Object newInstance(Class<?> clazz, Object... args)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
        return findConstructorBestMatch(clazz, args).newInstance(args);
    }

    /**
     * new instance of one class. If failed, return null
     *
     * @hide
     */
    public static Object tryNewInstance(Class<?> clazz, Object... args) {
        try {
            return newInstance(clazz, args);
        } catch (NoSuchMethodException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (InstantiationException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (IllegalAccessException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "", ex);
            return null;
        } catch (InvocationTargetException ex) {
            Log.w(TAG, "", ex);
            return null;
        }
    }
}
