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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
import top.continew.starter.security.crypto.enums.PasswordEncoderAlgorithm;
import top.continew.starter.security.crypto.mybatis.MyBatisDecryptInterceptor;
import top.continew.starter.security.crypto.mybatis.MyBatisEncryptInterceptor;
import top.continew.starter.security.crypto.util.EncryptHelper;
import top.continew.starter.security.crypto.util.PasswordEncoderUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 加/解密自动配置
 *
 * @author Charles7c
 * @author lishuyan
 * @since 1.4.0
 */
@AutoConfiguration
@EnableConfigurationProperties(CryptoProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.SECURITY_CRYPTO, name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
@PropertySource(value = "classpath:default-crypto.yml", factory = GeneralPropertySourceFactory.class)
public class CryptoAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CryptoAutoConfiguration.class);
    private final CryptoProperties properties;

    public CryptoAutoConfiguration(CryptoProperties properties) {
        this.properties = properties;
    }

    /**
     * MyBatis 加密拦截器配置
     */
    @Bean
    @ConditionalOnMissingBean
    public MyBatisEncryptInterceptor mybatisEncryptInterceptor() {
        return new MyBatisEncryptInterceptor();
    }

    /**
     * MyBatis 解密拦截器配置
     */
    @Bean
    @ConditionalOnMissingBean(MyBatisDecryptInterceptor.class)
    public MyBatisDecryptInterceptor mybatisDecryptInterceptor() {
        return new MyBatisDecryptInterceptor();
    }

    /**
     * 密码编码器配置
     *
     * @see DelegatingPasswordEncoder
     * @see PasswordEncoderFactories
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = PropertiesConstants.SECURITY_CRYPTO + ".password-encoder", name = PropertiesConstants.ENABLED, havingValue = "true")
    public PasswordEncoder passwordEncoder() {
        PasswordEncoderProperties passwordEncoderProperties = properties.getPasswordEncoder();
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(PasswordEncoderAlgorithm.BCRYPT.name().toLowerCase(), PasswordEncoderUtil
            .getEncoder(PasswordEncoderAlgorithm.BCRYPT));
        encoders.put(PasswordEncoderAlgorithm.SCRYPT.name().toLowerCase(), PasswordEncoderUtil
            .getEncoder(PasswordEncoderAlgorithm.SCRYPT));
        encoders.put(PasswordEncoderAlgorithm.PBKDF2.name().toLowerCase(), PasswordEncoderUtil
            .getEncoder(PasswordEncoderAlgorithm.PBKDF2));
        encoders.put(PasswordEncoderAlgorithm.ARGON2.name().toLowerCase(), PasswordEncoderUtil
            .getEncoder(PasswordEncoderAlgorithm.ARGON2));
        PasswordEncoderAlgorithm algorithm = passwordEncoderProperties.getAlgorithm();
        CheckUtils.throwIf(PasswordEncoderUtil.getEncoder(algorithm) == null, "不支持的加密算法: {}", algorithm);
        return new DelegatingPasswordEncoder(algorithm.name().toLowerCase(), encoders);
    }

    @PostConstruct
    public void postConstruct() {
        EncryptHelper.init(properties);
        log.debug("[ContiNew Starter] - Auto Configuration 'Security-Crypto' completed initialization.");
    }
}
