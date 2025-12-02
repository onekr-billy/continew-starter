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
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.request.AuthRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * JustAuth 扩展配置属性
 *
 * @author <a href="https://gitee.com/justauth/justauth-spring-boot-starter">yangkai.shen</a>
 * @author Charles7c
 * @since 2.15.0
 */
public class JustAuthExtendProperties {

    /**
     * 枚举类全路径
     */
    private Class<? extends AuthSource> enumClass;

    /**
     * 扩展请求配置
     */
    private Map<String, ExtendRequestConfig> config = new HashMap<>();

    public Class<? extends AuthSource> getEnumClass() {
        return enumClass;
    }

    public void setEnumClass(Class<? extends AuthSource> enumClass) {
        this.enumClass = enumClass;
    }

    public Map<String, ExtendRequestConfig> getConfig() {
        return config;
    }

    public void setConfig(Map<String, ExtendRequestConfig> config) {
        this.config = config;
    }

    /**
     * 扩展请求配置
     */
    public static class ExtendRequestConfig extends AuthConfig {

        /**
         * 平台对应的 AuthRequest 实现类
         */
        private Class<? extends AuthRequest> requestClass;

        public Class<? extends AuthRequest> getRequestClass() {
            return requestClass;
        }

        public void setRequestClass(Class<? extends AuthRequest> requestClass) {
            this.requestClass = requestClass;
        }
    }
}
