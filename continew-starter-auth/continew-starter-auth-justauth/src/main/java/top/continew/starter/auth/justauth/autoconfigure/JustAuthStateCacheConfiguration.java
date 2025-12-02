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

package top.continew.starter.auth.justauth.autoconfigure;

import me.zhyd.oauth.cache.AuthDefaultStateCache;
import me.zhyd.oauth.cache.AuthStateCache;
import org.redisson.client.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import top.continew.starter.auth.justauth.state.RedisAuthStateCache;
import top.continew.starter.core.constant.PropertiesConstants;

/**
 * JustAuth 缓存配置
 *
 * @author <a href="https://gitee.com/justauth/justauth-spring-boot-starter">yangkai.shen</a>
 * @author Charles7c
 * @since 2.15.0
 */
abstract class JustAuthStateCacheConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JustAuthStateCacheConfiguration.class);

    /**
     * Redis 缓存
     */
    @ConditionalOnClass(RedisClient.class)
    @ConditionalOnMissingBean(AuthStateCache.class)
    @ConditionalOnProperty(prefix = PropertiesConstants.AUTH_JUSTAUTH, name = "cache.type", havingValue = "redis")
    static class Redis {
        static {
            log.debug("[ContiNew Starter] - Auto Configuration 'JustAuth-AuthStateCache-Redis' completed initialization.");
        }

        @Bean
        public AuthStateCache authStateCache(JustAuthProperties properties) {
            return new RedisAuthStateCache(properties.getCache());
        }
    }

    /**
     * 默认缓存
     */
    @ConditionalOnMissingBean(AuthStateCache.class)
    @ConditionalOnProperty(prefix = PropertiesConstants.AUTH_JUSTAUTH, name = "cache.type", havingValue = "default", matchIfMissing = true)
    static class Default {
        static {
            log.debug("[ContiNew Starter] - Auto Configuration 'JustAuth-AuthStateCache-Default' completed initialization.");
        }

        @Bean
        public AuthStateCache authStateCache() {
            return AuthDefaultStateCache.INSTANCE;
        }
    }

    /**
     * 自定义缓存
     */
    @ConditionalOnProperty(prefix = PropertiesConstants.AUTH_JUSTAUTH, name = "cache.type", havingValue = "custom")
    static class Custom {
        static {
            log.debug("[ContiNew Starter] - Auto Configuration 'JustAuth-AuthStateCache-Custom' completed initialization.");
        }

        @Bean
        @ConditionalOnMissingBean(AuthStateCache.class)
        public AuthStateCache authStateCache() {
            if (log.isErrorEnabled()) {
                log.error("Consider defining a bean of type '{}' in your configuration.", ResolvableType
                    .forClass(AuthStateCache.class));
            }
            throw new NoSuchBeanDefinitionException(AuthStateCache.class);
        }
    }
}
