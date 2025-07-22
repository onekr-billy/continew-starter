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

package top.continew.starter.security.password.autoconfigure;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.continew.starter.core.constant.PropertiesConstants;
import top.continew.starter.core.util.GeneralPropertySourceFactory;
import top.continew.starter.core.util.validation.CheckUtils;
import top.continew.starter.security.password.enums.PasswordEncoderAlgorithm;
import top.continew.starter.security.password.util.PasswordEncoderUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 密码编码器自动配置
 *
 * <p>
 * 密码配置类，默认编解码器使用的是 BCryptPasswordEncoder <br />
 * 编码后的密码是遵循一定规则的 {idForEncode}encodePassword，前缀 {} 包含了编码的方式再拼接上该方式编码后的密码串。<br />
 * 可以添加自定义的编解码，也可以修改默认的编解码器，只需修改默认的 encodingId。<br />
 * 优点：如果有一天我们对密码编码规则进行替换或者轮转，现有的用户不会受到影响，只要修改 DelegatingPasswordEncoder 的 idForEncode 即可。
 * </p>
 *
 * @author Jasmine
 * @author Charles7c
 * @since 1.3.0
 */
@AutoConfiguration
@EnableConfigurationProperties(PasswordEncoderProperties.class)
@PropertySource(value = "classpath:default-password.yml", factory = GeneralPropertySourceFactory.class)
@ConditionalOnProperty(prefix = PropertiesConstants.SECURITY_PASSWORD, name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
public class PasswordEncoderAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(PasswordEncoderAutoConfiguration.class);

    /**
     * 密码编码器
     *
     * @see DelegatingPasswordEncoder
     * @see PasswordEncoderFactories
     */
    @Bean
    public PasswordEncoder passwordEncoder(PasswordEncoderProperties properties) {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(PasswordEncoderAlgorithm.BCRYPT.name().toLowerCase(), PasswordEncoderUtil
            .getEncoder(PasswordEncoderAlgorithm.BCRYPT));
        encoders.put(PasswordEncoderAlgorithm.SCRYPT.name().toLowerCase(), PasswordEncoderUtil
            .getEncoder(PasswordEncoderAlgorithm.SCRYPT));
        encoders.put(PasswordEncoderAlgorithm.PBKDF2.name().toLowerCase(), PasswordEncoderUtil
            .getEncoder(PasswordEncoderAlgorithm.PBKDF2));
        encoders.put(PasswordEncoderAlgorithm.ARGON2.name().toLowerCase(), PasswordEncoderUtil
            .getEncoder(PasswordEncoderAlgorithm.ARGON2));
        PasswordEncoderAlgorithm algorithm = properties.getAlgorithm();
        CheckUtils.throwIf(PasswordEncoderUtil.getEncoder(algorithm) == null, "不支持的加密算法: {}", algorithm);
        return new DelegatingPasswordEncoder(algorithm.name().toLowerCase(), encoders);
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[ContiNew Starter] - Auto Configuration 'Security-PasswordEncoder' completed initialization.");
    }
}
