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

package top.continew.starter.storage.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import top.continew.starter.core.constant.PropertiesConstants;
import top.continew.starter.storage.autoconfigure.properties.S3StorageConfig;
import top.continew.starter.storage.autoconfigure.properties.StorageProperties;
import top.continew.starter.storage.router.StorageStrategyRegistrar;
import top.continew.starter.storage.strategy.StorageStrategy;
import top.continew.starter.storage.strategy.impl.OssStorageStrategy;

import java.util.List;

/**
 * s3存储自动配置
 *
 * @author echo
 * @since 2.14.0
 */
@ConditionalOnProperty(prefix = PropertiesConstants.STORAGE, name = "s3")
public class S3StorageAutoConfiguration implements StorageStrategyRegistrar {

    private final StorageProperties properties;

    public S3StorageAutoConfiguration(StorageProperties properties) {
        this.properties = properties;
    }

    /**
     * 注册配置策略
     *
     * @param strategies 策略列表
     */
    @Override
    @Bean
    public void register(List<StorageStrategy> strategies) {
        for (S3StorageConfig config : properties.getS3()) {
            if (config.isEnabled()) {
                strategies.add(new OssStorageStrategy(config));
            }
        }
    }
}
