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

package top.continew.starter.security.crypto.autoconfigure;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import top.continew.starter.core.constant.OrderedConstants;
import top.continew.starter.core.constant.PropertiesConstants;
import top.continew.starter.core.util.GeneralPropertySourceFactory;
import top.continew.starter.security.crypto.filter.ApiCryptoFilter;

/**
 * API加/解密自动配置
 *
 * @author lishuyan
 * @since 2.14.0
 */
@AutoConfiguration
@EnableConfigurationProperties(ApiCryptoProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.SECURITY_API_CRYPTO, name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
@PropertySource(value = "classpath:default-api-crypto.yml", factory = GeneralPropertySourceFactory.class)
public class ApiCryptoAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApiCryptoAutoConfiguration.class);

    /**
     * API加/解密过滤器
     *
     * @param properties 配置
     * @return 过滤器
     */
    @Bean
    public FilterRegistrationBean<ApiCryptoFilter> apiCryptoFilterRegistration(ApiCryptoProperties properties) {
        FilterRegistrationBean<ApiCryptoFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new ApiCryptoFilter(properties));
        registration.addUrlPatterns("/*");
        registration.setName("apiCryptoFilter");
        registration.setOrder(OrderedConstants.Filter.API_CRYPTO_FILTER);
        return registration;
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[ContiNew Starter] - Auto Configuration 'Security-API-Crypto' completed initialization.");
    }
}
