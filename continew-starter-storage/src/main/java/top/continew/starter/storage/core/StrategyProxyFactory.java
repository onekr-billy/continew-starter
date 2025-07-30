/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.starter.storage.core;

import top.continew.starter.storage.strategy.StorageStrategy;
import top.continew.starter.storage.strategy.StorageStrategyOverride;
import top.continew.starter.storage.strategy.impl.AbstractStorageStrategyOverride;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 策略代理工厂
 *
 * @author echo
 * @since 2.14.0
 */
public class StrategyProxyFactory {

    private final Map<Class<?>, List<StorageStrategyOverride<?>>> overrides = new ConcurrentHashMap<>();

    /**
     * 注册重写
     */
    public <T extends StorageStrategy> void registerOverride(StorageStrategyOverride<T> override) {
        overrides.computeIfAbsent(override.getTargetType(), k -> new CopyOnWriteArrayList<>()).add(override);
    }

    /**
     * 创建代理
     */
    @SuppressWarnings("unchecked")
    public <T extends StorageStrategy> T createProxy(T target) {
        List<StorageStrategyOverride<?>> targetOverrides = overrides.get(target.getClass());
        if (targetOverrides == null || targetOverrides.isEmpty()) {
            return target;
        }

        // 为每个重写对象设置原始目标
        for (StorageStrategyOverride<?> override : targetOverrides) {
            if (override instanceof AbstractStorageStrategyOverride) {
                ((AbstractStorageStrategyOverride<T>)override).setOriginalTarget(target);
            }
        }

        return (T)Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass()
            .getInterfaces(), new StrategyInvocationHandler<>(target, targetOverrides));
    }

    /**
     * 改进的调用处理器
     */
    private static class StrategyInvocationHandler<T extends StorageStrategy> implements InvocationHandler {
        private final T target;
        private final List<StorageStrategyOverride<?>> overrides;
        private final Map<String, Method> overrideMethodCache = new ConcurrentHashMap<>();

        public StrategyInvocationHandler(T target, List<StorageStrategyOverride<?>> overrides) {
            this.target = target;
            this.overrides = overrides;
            cacheOverrideMethods();
        }

        /**
         * 缓存重写方法
         */
        private void cacheOverrideMethods() {
            for (StorageStrategyOverride<?> override : overrides) {
                Class<?> overrideClass = override.getClass();

                // 获取目标策略类的所有方法
                Class<?> targetClass = override.getTargetType();
                Method[] targetMethods = getAllMethods(targetClass);

                for (Method targetMethod : targetMethods) {
                    try {
                        // 查找重写类中是否有相同签名的方法
                        Method overrideMethod = overrideClass.getMethod(targetMethod.getName(), targetMethod
                            .getParameterTypes());

                        // 检查方法是否真的被重写了（不是从接口继承的默认方法）
                        if (isMethodOverridden(overrideMethod, overrideClass)) {
                            overrideMethodCache.put(targetMethod
                                .getName() + getMethodSignature(targetMethod), overrideMethod);
                        }
                    } catch (NoSuchMethodException e) {
                        // 重写类中没有这个方法，忽略
                    }
                }
            }
        }

        /**
         * 获取类及其所有接口的方法
         */
        private Method[] getAllMethods(Class<?> clazz) {

            // 添加类本身的方法
            Set<Method> methods = new HashSet<>(Arrays.asList(clazz.getMethods()));

            // 添加所有接口的方法
            for (Class<?> iface : clazz.getInterfaces()) {
                methods.addAll(Arrays.asList(iface.getMethods()));
            }

            return methods.toArray(new Method[0]);
        }

        /**
         * 检查方法是否真的被重写了
         */
        private boolean isMethodOverridden(Method method, Class<?> overrideClass) {
            // 如果方法声明在重写类中（而不是父类或接口），则认为是重写的
            return method.getDeclaringClass().equals(overrideClass) || (!method.getDeclaringClass()
                .isInterface() && !method.getDeclaringClass().equals(AbstractStorageStrategyOverride.class) && !method
                    .getDeclaringClass()
                    .equals(Object.class));
        }

        /**
         * 获取方法签名
         */
        private String getMethodSignature(Method method) {
            StringBuilder sb = new StringBuilder("(");
            for (Class<?> paramType : method.getParameterTypes()) {
                sb.append(paramType.getName()).append(",");
            }
            if (sb.length() > 1) {
                sb.setLength(sb.length() - 1);
            }
            sb.append(")");
            return sb.toString();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodKey = method.getName() + getMethodSignature(method);
            Method overrideMethod = overrideMethodCache.get(methodKey);

            if (overrideMethod != null) {
                // 找到重写方法，调用重写逻辑
                for (StorageStrategyOverride<?> override : overrides) {
                    if (overrideMethod.getDeclaringClass().equals(override.getClass())) {
                        return overrideMethod.invoke(override, args);
                    }
                }
            }

            // 没有重写，调用原方法
            return method.invoke(target, args);
        }
    }
}