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

package top.continew.starter.web.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriUtils;
import top.continew.starter.core.constant.StringConstants;
import top.continew.starter.json.jackson.util.JSONUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Servlet 工具类
 *
 * @author Charles7c
 * @since 1.0.0
 */
public class ServletUtils extends JakartaServletUtil {

    private ServletUtils() {
    }

    /**
     * 获取请求属性
     *
     * @return {@link ServletRequestAttributes }
     */
    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes) attributes;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取浏览器及其版本信息
     *
     * @param request 请求对象
     * @return 浏览器及其版本信息
     */
    public static String getBrowser(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        return getBrowser(request.getHeader("User-Agent"));
    }

    /**
     * 获取浏览器及其版本信息
     *
     * @param userAgentString User-Agent 字符串
     * @return 浏览器及其版本信息
     */
    public static String getBrowser(String userAgentString) {
        UserAgent userAgent = UserAgentUtil.parse(userAgentString);
        return userAgent.getBrowser().getName() + StringConstants.SPACE + userAgent.getVersion();
    }

    /**
     * 获取操作系统
     *
     * @param request 请求对象
     * @return 操作系统
     */
    public static String getOs(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        return getOs(request.getHeader("User-Agent"));
    }

    /**
     * 获取操作系统
     *
     * @param userAgentString User-Agent 字符串
     * @return 操作系统
     */
    public static String getOs(String userAgentString) {
        UserAgent userAgent = UserAgentUtil.parse(userAgentString);
        return userAgent.getOs().getName();
    }

    /**
     * 获取 http request
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }

    /**
     * 获取请求方法
     *
     * @return {@link String }
     */
    public static String getReqMethod() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getMethod() : null;
    }

    /**
     * 获取session
     *
     * @return HttpSession
     */
    public static HttpSession getSession() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getSession() : null;
    }

    /**
     * 获取 请求 字符串参数
     *
     * @param name 参数名
     * @return {@link String }
     */
    public static String getReqParameter(String name) {
        HttpServletRequest request = getRequest();
        return request != null ? request.getParameter(name) : null;
    }

    /**
     * 获取请求 Ip
     *
     * @return {@link String }
     */
    public static String getReqIp() {
        HttpServletRequest request = getRequest();
        return request != null ? getClientIP(request) : null;
    }

    /**
     * 获取请求头信息
     *
     * @return {@link Map }<{@link String }, {@link String }>
     */
    public static Map<String, String> getReqHeaders() {
        HttpServletRequest request = getRequest();
        return request != null ? getHeaderMap(request) : Collections.emptyMap();
    }

    /**
     * 获取请求url 包含 query 参数
     * <p>http://localhost:8000/system/user?page=1&size=10</p>
     *
     * @return {@link URI }
     */
    public static URI getReqUrl() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        String queryString = request.getQueryString();
        if (CharSequenceUtil.isBlank(queryString)) {
            return URI.create(request.getRequestURL().toString());
        }
        try {
            StringBuilder urlBuilder = appendQueryString(queryString);
            return new URI(urlBuilder.toString());
        } catch (URISyntaxException e) {
            String encoded = UriUtils.encodeQuery(queryString, StandardCharsets.UTF_8);
            StringBuilder urlBuilder = appendQueryString(encoded);
            return URI.create(urlBuilder.toString());
        }
    }

    /**
     * 获取请求路径
     *
     * @return {@link URI }
     */
    public static String getReqPath() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getRequestURI() : null;
    }

    /**
     * 获取请求 body 参数
     *
     * @return {@link String }
     */
    public static String getReqBody() {
        HttpServletRequest request = getRequest();
        if (request instanceof RepeatReadRequestWrapper wrapper && !wrapper.isMultipartContent(request)) {
            String body = JakartaServletUtil.getBody(request);
            return JSONUtil.isTypeJSON(body) ? body : null;
        }
        return null;
    }

    /**
     * 获取请求参数
     *
     * @return {@link Map }<{@link String }, {@link Object }>
     */
    public static Map<String, Object> getReqParam() {
        String body = getReqBody();
        return CharSequenceUtil.isNotBlank(body) && JSONUtil.isTypeJSON(body)
                ? JSONUtil.toBean(body, Map.class)
                : Collections.unmodifiableMap(JakartaServletUtil.getParamMap(Objects.requireNonNull(getRequest())));
    }

    /**
     * 获取访问日志请求参数
     *
     * @return {@link Object }
     */
    public static Object getAccessLogReqParam() {
        String body = getReqBody();
        if (CharSequenceUtil.isNotBlank(body) && JSONUtil.isTypeJSON(body)) {
            try {
                JsonNode jsonNode = JSONUtil.getObjectMapper().readTree(body);
                if (jsonNode.isArray()) {
                    return JSONUtil.toBean(body, List.class);
                } else {
                    return JSONUtil.toBean(body, Map.class);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return Collections.unmodifiableMap(JakartaServletUtil.getParamMap(Objects.requireNonNull(getRequest())));
    }

    /**
     * 获取 http response
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getResponse();
    }

    /**
     * 获取响应状态
     *
     * @return int
     */
    public static int getRespStatus() {
        HttpServletResponse response = getResponse();
        return response != null ? response.getStatus() : -1;
    }

    /**
     * 获取响应所有的头（header）信息
     *
     * @return header值
     */
    public static Map<String, String> getRespHeaders() {
        HttpServletResponse response = getResponse();
        if (response == null) {
            return Collections.emptyMap();
        }
        final Collection<String> headerNames = response.getHeaderNames();
        final Map<String, String> headerMap = MapUtil.newHashMap(headerNames.size(), true);
        for (String name : headerNames) {
            headerMap.put(name, response.getHeader(name));
        }
        return headerMap;
    }

    /**
     * 获取响应 body 参数
     *
     * @return {@link String }
     */
    public static String getRespBody() {
        HttpServletResponse response = getResponse();
        if (response instanceof RepeatReadResponseWrapper wrapper && !wrapper.isStreamingResponse()) {
            String body = wrapper.getResponseContent();
            return JSONUtil.isTypeJSON(body) ? body : null;
        }
        return null;
    }

    public static Map<String, Object> getRespParam() {
        String body = getRespBody();
        return CharSequenceUtil.isNotBlank(body) && JSONUtil.isTypeJSON(body) ? JSONUtil.toBean(body, Map.class) : null;
    }

    /**
     * 追加查询字符串
     *
     * @param queryString 查询字符串
     * @return {@link StringBuilder }
     */
    private static StringBuilder appendQueryString(String queryString) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return new StringBuilder();
        }
        return new StringBuilder().append(request.getRequestURL())
                .append(StringConstants.QUESTION_MARK)
                .append(queryString);
    }
}
