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

import me.zhyd.oauth.config.AuthConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import top.continew.starter.core.constant.PropertiesConstants;

import java.util.Map;

/**
 * JustAuth 配置属性
 *
 * @author <a href="https://gitee.com/justauth/justauth-spring-boot-starter">yangkai.shen</a>
 * @author Charles7c
 * @since 2.15.0
 */
@ConfigurationProperties(PropertiesConstants.AUTH_JUSTAUTH)
public class JustAuthProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 第三方平台配置
     */
    private Map<String, AuthConfig> type;

    /**
     * 自定义配置
     */
    @NestedConfigurationProperty
    private JustAuthExtendProperties extend;

    /**
     * 缓存配置
     */
    @NestedConfigurationProperty
    private JustAuthCacheProperties cache;

    /**
     * HTTP 配置
     */
    @NestedConfigurationProperty
    private JustAuthHttpProperties http;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, AuthConfig> getType() {
        return type;
    }

    public void setType(Map<String, AuthConfig> type) {
        this.type = type;
    }

    public JustAuthExtendProperties getExtend() {
        return extend;
    }

    public void setExtend(JustAuthExtendProperties extend) {
        this.extend = extend;
    }

    public JustAuthCacheProperties getCache() {
        return cache;
    }

    public void setCache(JustAuthCacheProperties cache) {
        this.cache = cache;
    }

    public JustAuthHttpProperties getHttp() {
        return http;
    }

    public void setHttp(JustAuthHttpProperties http) {
        this.http = http;
    }
}
