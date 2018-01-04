/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.macaca.android.testing.server.xmlUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

    public static Class getClass(final String name) throws Exception {
        return Class.forName(name);
    }

    public static Object getField(final Class clazz, final String fieldName, final Object object) throws Exception {
        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    public static Object getField(final String className, final String field, final Object object) throws Exception {
        Class a = getClass(className);
        return getField(a, field, object);
    }

    public static void setField(final Class clazz, final String fieldName, final Object object, final Object value) throws Exception {
        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    public static Object invoke(final Method method, final Object object, final Object... parameters) throws Exception {
        return method.invoke(object, parameters);
    }

    public static Method method(final Class clazz, final String methodName, final Class... parameterTypes) throws Exception {
        final Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    public static Method method(final String className, final String method, final Class... parameterTypes) throws Exception {
        return method(getClass(className), method, parameterTypes);
    }
}
