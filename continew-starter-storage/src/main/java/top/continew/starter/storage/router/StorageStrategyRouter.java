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

package top.continew.starter.storage.router;

import top.continew.starter.storage.exception.StorageException;
import top.continew.starter.storage.model.resp.StrategyStatus;
import top.continew.starter.storage.strategy.StorageStrategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储策略路由器
 *
 * @author echo
 * @since 2.14.0
 */
public class StorageStrategyRouter {

    /**
     * 配置策略
     */
    private final Map<String, StorageStrategy> configStrategies = new ConcurrentHashMap<>();

    /**
     * 动态策略
     */
    private final Map<String, StorageStrategy> dynamicStrategies = new ConcurrentHashMap<>();

    public StorageStrategyRouter(List<StorageStrategyRegistrar> registrars) {
        List<StorageStrategy> strategies = new ArrayList<>();
        for (StorageStrategyRegistrar registrar : registrars) {
            registrar.register(strategies);
        }

        // 配置文件加载的策略
        for (StorageStrategy strategy : strategies) {
            configStrategies.put(strategy.getPlatform(), strategy);
        }
    }

    /**
     * 存储选择
     *
     * @param platform 代码
     * @return {@link StorageStrategy }
     */
    public StorageStrategy route(String platform) {
        // 动态注册的策略优先级更高
        StorageStrategy strategy = dynamicStrategies.get(platform);
        if (strategy == null) {
            strategy = configStrategies.get(platform);
        }

        if (strategy == null) {
            throw new StorageException("不支持存储编码: " + platform);
        }
        return strategy;
    }

    /**
     * 动态注册策略
     *
     * @param strategy 存储策略
     * @throws StorageException 如果同一 platform 的动态策略已存在
     */
    public void registerDynamic(StorageStrategy strategy) {
        String platform = strategy.getPlatform();
        if (dynamicStrategies.containsKey(platform)) {
            throw new StorageException("动态策略 platform 已存在: " + platform);
        }
        // 如果配置文件中存在相同 platform，动态注册会覆盖（但不修改配置策略）
        dynamicStrategies.put(platform, strategy);
    }

    /**
     * 卸载动态策略
     *
     * @param platform 策略代码
     * @return 是否成功卸载
     */
    public boolean unloadDynamic(String platform) {
        StorageStrategy strategy = dynamicStrategies.remove(platform);
        if (strategy != null) {
            try {
                strategy.cleanup();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取所有可用代码
     */
    public Set<String> getAllPlatform() {
        Set<String> allPlatform = new HashSet<>(configStrategies.keySet());
        allPlatform.addAll(dynamicStrategies.keySet());
        return allPlatform;
    }

    /**
     * 检查是否为动态注册的策略
     */
    public boolean isDynamic(String platform) {
        return dynamicStrategies.containsKey(platform);
    }

    /**
     * 检查是否为配置文件策略
     */
    public boolean isFromConfig(String platform) {
        return configStrategies.containsKey(platform);
    }

    /**
     * 获取简化的策略信息（当前生效的）
     */
    public Map<String, String> getActiveStrategyInfo() {
        Map<String, String> info = new HashMap<>();

        // 先添加配置文件策略
        configStrategies.keySet().forEach(platform -> info.put(platform, "CONFIG"));

        // 动态策略会覆盖同名的配置策略
        dynamicStrategies.keySet().forEach(platform -> info.put(platform, "DYNAMIC"));

        return info;
    }

    /**
     * 获取完整的策略状态
     */
    public Map<String, StrategyStatus> getFullStrategyStatus() {
        Map<String, StrategyStatus> status = new HashMap<>();

        // 所有唯一的 platform
        Set<String> appPlatform = new HashSet<>();
        appPlatform.addAll(configStrategies.keySet());
        appPlatform.addAll(dynamicStrategies.keySet());

        for (String platform : appPlatform) {
            boolean hasConfig = configStrategies.containsKey(platform);
            boolean hasDynamic = dynamicStrategies.containsKey(platform);

            StrategyStatus strategyStatus = new StrategyStatus(platform, hasConfig, hasDynamic, hasDynamic
                ? "DYNAMIC"
                : "CONFIG", hasDynamic && hasConfig ? "配置策略被覆盖" : "正常");

            status.put(platform, strategyStatus);
        }

        return status;
    }
}