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

import java.time.Duration;

/**
 * JustAuth 缓存配置属性
 *
 * @author <a href="https://gitee.com/justauth/justauth-spring-boot-starter">yangkai.shen</a>
 * @author Charles7c
 * @since 2.15.0
 */
public class JustAuthCacheProperties {

    /**
     * 缓存类型
     */
    private CacheType type = CacheType.DEFAULT;

    /**
     * 缓存前缀
     * <p>
     * 目前仅 {@link #type CacheType.REDIS} 缓存生效（默认：{@code JUSTAUTH::STATE::}）
     * </p>
     */
    private String prefix = "JUSTAUTH::STATE::";

    /**
     * 超时时长
     * <p>
     * 目前仅 {@link #type CacheType.REDIS} 缓存生效（默认：3分钟）
     * </p>
     */
    private Duration timeout = Duration.ofMinutes(3);

    public CacheType getType() {
        return type;
    }

    public void setType(CacheType type) {
        this.type = type;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    /**
     * 缓存类型枚举
     */
    public enum CacheType {

        /**
         * 使用 JustAuth 内置缓存
         */
        DEFAULT,

        /**
         * 使用 Redis 缓存
         */
        REDIS,

        /**
         * 自定义缓存
         */
        CUSTOM
    }
}
