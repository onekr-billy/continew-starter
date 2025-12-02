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

import java.net.Proxy;
import java.util.Map;

/**
 * JustAuth HTTP 配置属性
 *
 * @author <a href="https://gitee.com/justauth/justauth-spring-boot-starter">yangkai.shen</a>
 * @author Charles7c
 * @since 2.15.0
 */
public class JustAuthHttpProperties {

    /**
     * 超时时间（单位：毫秒）
     */
    private int timeout;

    /**
     * 代理配置
     */
    private Map<String, JustAuthProxyConfig> proxy;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Map<String, JustAuthProxyConfig> getProxy() {
        return proxy;
    }

    public void setProxy(Map<String, JustAuthProxyConfig> proxy) {
        this.proxy = proxy;
    }

    /**
     * 代理配置
     */
    public static class JustAuthProxyConfig {

        /**
         * 代理类型
         */
        private String type = Proxy.Type.HTTP.name();

        /**
         * 代理主机名
         */
        private String hostname;

        /**
         * 代理端口号
         */
        private int port;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}
