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

package top.continew.starter.security.crypto.filter;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.continew.starter.security.crypto.annotation.ApiEncrypt;
import top.continew.starter.security.crypto.autoconfigure.ApiCryptoProperties;

import java.io.IOException;

/**
 * API加/解密过滤器
 *
 * @author lishuyan
 * @since 2.14.0
 */
public class ApiCryptoFilter implements Filter {

    /**
     * API加/密配置
     */
    private final ApiCryptoProperties properties;

    public ApiCryptoFilter(ApiCryptoProperties properties) {
        this.properties = properties;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        // 获取API加密注解
        ApiEncrypt apiEncrypt = this.getApiEncryptAnnotation(request);
        // 响应加密标识
        boolean responseEncryptFlag = ObjectUtil.isNotNull(apiEncrypt) && apiEncrypt.response();
        // 密钥标头
        String secretKeyHeader = properties.getSecretKeyHeader();
        ServletRequest requestWrapper = null;
        ServletResponse responseWrapper = null;
        ResponseBodyEncryptWrapper responseBodyEncryptWrapper = null;
        // 是否为 PUT 或者 POST 请求
        if (HttpMethod.PUT.matches(request.getMethod()) || HttpMethod.POST.matches(request.getMethod())) {
            // 获取密钥值
            String secretKeyValue = request.getHeader(secretKeyHeader);
            if (CharSequenceUtil.isNotBlank(secretKeyValue)) {
                // 请求解密
                requestWrapper = new RequestBodyDecryptWrapper(request, properties.getPrivateKey(), secretKeyHeader);
            }
        }
        // 响应加密，响应包装器替换响应体加密包装器
        if (responseEncryptFlag) {
            responseBodyEncryptWrapper = new ResponseBodyEncryptWrapper(response);
            responseWrapper = responseBodyEncryptWrapper;
        }
        // 继续执行
        chain.doFilter(ObjectUtil.defaultIfNull(requestWrapper, request), ObjectUtil
            .defaultIfNull(responseWrapper, response));
        // 响应加密，执行完成后，响应密文
        if (responseEncryptFlag) {
            servletResponse.reset();
            // 获取密文
            String encryptContent = responseBodyEncryptWrapper.getEncryptContent(response, properties
                .getPublicKey(), secretKeyHeader);
            // 写出密文
            servletResponse.getWriter().write(encryptContent);
        }
    }

    /**
     * 获取 ApiEncrypt 注解
     *
     * @param request HTTP请求
     * @return ApiEncrypt注解，如果未找到则返回null
     */
    private ApiEncrypt getApiEncryptAnnotation(HttpServletRequest request) {
        try {
            RequestMappingHandlerMapping handlerMapping = SpringUtil
                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
            HandlerExecutionChain mappingHandler = handlerMapping.getHandler(request);
            // 检查是否存在处理链
            if (ObjectUtil.isNull(mappingHandler)) {
                return null;
            }
            // 获取处理器
            Object handler = mappingHandler.getHandler();
            // 检查是否为HandlerMethod类型
            if (!(handler instanceof HandlerMethod handlerMethod)) {
                return null;
            }
            // 获取方法上的ApiEncrypt注解
            return handlerMethod.getMethodAnnotation(ApiEncrypt.class);
        } catch (Exception e) {
            return null;
        }
    }
}