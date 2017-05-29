/*
Copyright Soramitsu Co., Ltd. 2016 All Rights Reserved.
http://soramitsu.co.jp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package io.soramitsu.irohaandroid.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is able to private method on any class.
 */
public class PrivateMethodAccessor {

    /**
     * invoke to "private static" method.
     *
     * @param clazz      target class
     * @param methodName target method name
     * @param paramTypes target method parameter types
     * @param args       parameter when invoke
     * @return result of invoke
     * @throws NoSuchMethodException     not found target method in target class.
     * @throws InvocationTargetException throw exception by target method.
     * @throws IllegalAccessException    If target method object implements Java language access control<br>
     *                                   and can not access the basic method.
     */
    public static Object invoke(Class<?> clazz, String methodName, Class<?>[] paramTypes, Object... args)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method method = clazz.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(null, args);
    }

    /**
     * invoke to "private" method.
     *
     * @param obj        target class instance
     * @param methodName target method name
     * @param paramTypes target method parameter types
     * @param args       parameter when invoke
     * @return result of invoke
     * @throws NoSuchMethodException     not found target method in target class.
     * @throws InvocationTargetException throw exception by target method.
     * @throws IllegalAccessException    If target method object implements Java language access control<br>
     *                                   and can not access the basic method.
     */
    public Object invoke(Object obj, String methodName, Class<?>[] paramTypes, Object[] args)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Class<?> clazz = obj.getClass();
        final Method method = clazz.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(obj, args);
    }
}
