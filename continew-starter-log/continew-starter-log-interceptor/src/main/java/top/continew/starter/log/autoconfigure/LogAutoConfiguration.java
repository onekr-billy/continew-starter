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

package top.continew.starter.log.autoconfigure;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import top.continew.starter.log.annotation.ConditionalOnEnabledLog;
import top.continew.starter.log.dao.DefaultLogDao;
import top.continew.starter.log.dao.LogDao;
import top.continew.starter.log.handler.InterceptorLogHandler;
import top.continew.starter.log.handler.LogHandler;
import top.continew.starter.log.model.LogProperties;

/**
 * 日志自动配置
 *
 * @author Charles7c
 * @since 1.1.0
 */
@AutoConfiguration
@ConditionalOnEnabledLog
@EnableConfigurationProperties(LogProperties.class)
@Import({LogWebConfiguration.class})
public class LogAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(LogAutoConfiguration.class);

    /**
     * 日志处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public LogHandler logHandler() {
        return new InterceptorLogHandler();
    }

    /**
     * 日志持久层
     */
    @Bean
    @ConditionalOnMissingBean
    public LogDao logDao() {
        return new DefaultLogDao();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[ContiNew Starter] - Auto Configuration 'Log-Interceptor' completed initialization.");
    }
}
