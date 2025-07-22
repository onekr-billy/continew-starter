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

package top.continew.starter.extension.tenant.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.continew.starter.core.constant.OrderedConstants;
import top.continew.starter.extension.tenant.annotation.ConditionalOnEnabledTenant;
import top.continew.starter.extension.tenant.config.TenantProvider;
import top.continew.starter.extension.tenant.interceptor.TenantInterceptor;

/**
 * 租户 Web MVC 自动配置
 *
 * @author Charles7c
 * @since 2.7.0
 */
@AutoConfiguration
@ConditionalOnEnabledTenant
@ConditionalOnWebApplication
@EnableConfigurationProperties(TenantProperties.class)
public class TenantWebMvcAutoConfiguration implements WebMvcConfigurer {

    private final TenantProperties tenantProperties;
    private final TenantProvider tenantProvider;

    public TenantWebMvcAutoConfiguration(TenantProperties tenantProperties, TenantProvider tenantProvider) {
        this.tenantProperties = tenantProperties;
        this.tenantProvider = tenantProvider;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantInterceptor(tenantProperties, tenantProvider))
            .order(OrderedConstants.Interceptor.TENANT_INTERCEPTOR);
    }
}
