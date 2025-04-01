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

package top.continew.starter.log.model;

import top.continew.starter.log.enums.Include;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

/**
 * 日志信息
 *
 * @author Dave Syer（Spring Boot Actuator）
 * @author Andy Wilkinson（Spring Boot Actuator）
 * @author Phillip Webb（Spring Boot Actuator）
 * @author Charles7c
 * @since 1.1.0
 */
public class LogRecord {

    /**
     * 描述
     */
    private String description;

    /**
     * 模块
     */
    private String module;

    /**
     * 请求信息
     */
    private LogRequest request;

    /**
     * 响应信息
     */
    private LogResponse response;

    /**
     * 耗时
     */
    private Duration timeTaken;

    /**
     * 时间戳
     */
    private final Instant timestamp;

    /**
     * 错误信息
     */
    private String errorMsg;

    public LogRecord(Instant timestamp, LogRequest request, LogResponse response, Duration timeTaken) {
        this.timestamp = timestamp;
        this.request = request;
        this.response = response;
        this.timeTaken = timeTaken;
    }

    /**
     * 开始记录日志
     *
     * @return 日志记录器
     */
    public static Started start() {
        return start(Instant.now());
    }

    /**
     * 开始记录日志
     *
     * @param timestamp 开始时间
     * @return 日志记录器
     */
    public static Started start(Instant timestamp) {
        return new Started(timestamp);
    }

    /**
     * 日志记录器
     */
    public static final class Started {

        private final Instant timestamp;

        private Started(Instant timestamp) {
            this.timestamp = timestamp;
        }

        /**
         * 结束日志记录
         *
         * @param timestamp 结束时间
         * @param includes  包含信息
         * @return 日志记录
         */
        public LogRecord finish(Instant timestamp, Set<Include> includes) {
            LogRequest logRequest = new LogRequest(includes);
            LogResponse logResponse = new LogResponse(includes);
            Duration duration = Duration.between(this.timestamp, timestamp);
            return new LogRecord(this.timestamp, logRequest, logResponse, duration);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public LogRequest getRequest() {
        return request;
    }

    public void setRequest(LogRequest request) {
        this.request = request;
    }

    public LogResponse getResponse() {
        return response;
    }

    public void setResponse(LogResponse response) {
        this.response = response;
    }

    public Duration getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Duration timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
