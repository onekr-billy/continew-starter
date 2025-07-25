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

package top.continew.starter.extension.crud.autoconfigure;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import top.continew.starter.extension.crud.annotation.CrudApi;
import top.continew.starter.extension.crud.aop.CrudApiAnnotationAdvisor;
import top.continew.starter.extension.crud.aop.CrudApiAnnotationInterceptor;

/**
 * CRUD API 自动配置
 *
 * @author Charles7c
 * @since 2.7.5
 */
@AutoConfiguration
@EnableConfigurationProperties(CrudProperties.class)
public class CrudApiAutoConfiguration extends DelegatingWebMvcConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CrudApiAutoConfiguration.class);

    /**
     * CRUD 请求映射器处理器映射器（覆盖默认 RequestMappingHandlerMapping）
     */
    @Override
    public RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new CrudRequestMappingHandlerMapping();
    }

    @Bean
    @Primary
    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping(@Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager,
                                                                     @Qualifier("mvcConversionService") FormattingConversionService conversionService,
                                                                     @Qualifier("mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {
        return super.requestMappingHandlerMapping(contentNegotiationManager, conversionService, resourceUrlProvider);
    }

    /**
     * CRUD API 注解通知
     */
    @Bean
    @ConditionalOnMissingBean
    public CrudApiAnnotationAdvisor crudApiAnnotationAdvisor(CrudApiAnnotationInterceptor crudApiAnnotationInterceptor) {
        return new CrudApiAnnotationAdvisor(crudApiAnnotationInterceptor, CrudApi.class);
    }

    /**
     * CRUD API 注解拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public CrudApiAnnotationInterceptor crudApiAnnotationInterceptor() {
        return new CrudApiAnnotationInterceptor();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[ContiNew Starter] - Auto Configuration 'Extension-CRUD API' completed initialization.");
    }
}
