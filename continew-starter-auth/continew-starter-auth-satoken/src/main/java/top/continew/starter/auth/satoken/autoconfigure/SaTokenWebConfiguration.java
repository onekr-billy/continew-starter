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

package top.continew.starter.auth.satoken.autoconfigure;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.continew.starter.core.constant.OrderedConstants;
import top.continew.starter.core.constant.StringConstants;

/**
 * SaToken Web 层配置（拦截器等）
 *
 * @author Charles7c
 * @since 2.16.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SaTokenWebConfiguration implements WebMvcConfigurer {

    private final SaInterceptor saInterceptor;
    private final SaTokenExtensionProperties properties;

    protected SaTokenWebConfiguration(SaInterceptor saInterceptor, SaTokenExtensionProperties properties) {
        this.saInterceptor = saInterceptor;
        this.properties = properties;
    }

    /**
     * SaToken 拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public SaInterceptor saInterceptor() {
        return new SaInterceptor(handle -> SaRouter.match(StringConstants.PATH_PATTERN)
            .notMatch(properties.getSecurity().getExcludes())
            .check(r -> StpUtil.checkLogin()));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.saInterceptor)
            .addPathPatterns(StringConstants.PATH_PATTERN)
            .order(OrderedConstants.Interceptor.AUTH_INTERCEPTOR);
    }
}
