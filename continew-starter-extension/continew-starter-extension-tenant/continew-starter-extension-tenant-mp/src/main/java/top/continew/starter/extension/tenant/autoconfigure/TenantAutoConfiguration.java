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

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import top.continew.starter.extension.tenant.annotation.ConditionalOnEnabledTenant;
import top.continew.starter.extension.tenant.aop.TenantIgnoreAspect;
import top.continew.starter.extension.tenant.config.TenantProvider;
import top.continew.starter.extension.tenant.TenantDataSourceHandler;
import top.continew.starter.extension.tenant.handler.datasource.DefaultTenantDataSourceHandler;
import top.continew.starter.extension.tenant.handler.datasource.TenantDataSourceAdvisor;
import top.continew.starter.extension.tenant.handler.datasource.TenantDataSourceInterceptor;
import top.continew.starter.extension.tenant.handler.line.DefaultTenantLineHandler;

import javax.sql.DataSource;

/**
 * 租户自动配置
 *
 * @author Charles7c
 * @since 2.7.0
 */
@AutoConfiguration
@ConditionalOnEnabledTenant
public class TenantAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(TenantAutoConfiguration.class);
    private final TenantProperties tenantProperties;

    public TenantAutoConfiguration(TenantProperties tenantProperties) {
        this.tenantProperties = tenantProperties;
    }

    /**
     * 租户忽略切面
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantIgnoreAspect tenantIgnoreAspect() {
        return new TenantIgnoreAspect();
    }

    /**
     * 租户行级隔离拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantLineHandler tenantLineHandler) {
        return new TenantLineInnerInterceptor(tenantLineHandler);
    }

    /**
     * 租户行级隔离处理器（默认）
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantLineHandler tenantLineHandler() {
        return new DefaultTenantLineHandler(tenantProperties);
    }

    /**
     * 租户数据源级隔离通知
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "com.baomidou.dynamic.datasource.DynamicRoutingDataSource")
    public TenantDataSourceAdvisor tenantDataSourceAdvisor(TenantDataSourceInterceptor tenantDataSourceInterceptor) {
        return new TenantDataSourceAdvisor(tenantDataSourceInterceptor);
    }

    /**
     * 租户数据源级隔离拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "com.baomidou.dynamic.datasource.DynamicRoutingDataSource")
    public TenantDataSourceInterceptor tenantDataSourceInterceptor(TenantDataSourceHandler tenantDataSourceHandler) {
        return new TenantDataSourceInterceptor(tenantDataSourceHandler);
    }

    /**
     * 租户数据源级隔离处理器（默认）
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "com.baomidou.dynamic.datasource.DynamicRoutingDataSource")
    public TenantDataSourceHandler tenantDataSourceHandler(DataSource dataSource) {
        return new DefaultTenantDataSourceHandler(dataSource);
    }

    /**
     * 租户提供者
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantProvider tenantProvider() {
        if (log.isErrorEnabled()) {
            log.error("Consider defining a bean of type '{}' in your configuration.", ResolvableType
                .forClass(TenantProvider.class));
        }
        throw new NoSuchBeanDefinitionException(TenantProvider.class);
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[ContiNew Starter] - Auto Configuration 'Tenant' completed initialization.");
    }
}
