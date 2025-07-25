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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import top.continew.starter.core.constant.PropertiesConstants;
import top.continew.starter.security.crypto.enums.Algorithm;

/**
 * 加/解密配置属性
 *
 * @author Charles7c
 * @author lishuyan
 * @since 1.4.0
 */
@ConfigurationProperties(PropertiesConstants.SECURITY_CRYPTO)
public class CryptoProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 默认算法
     */
    private Algorithm algorithm = Algorithm.AES;

    /**
     * 对称加密算法密钥
     */
    private String password;

    /**
     * 非对称加密算法公钥
     */
    private String publicKey;

    /**
     * 非对称加密算法私钥
     */
    private String privateKey;

    /**
     * 密码编码器配置
     */
    @NestedConfigurationProperty
    private PasswordEncoderProperties passwordEncoder;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public PasswordEncoderProperties getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoderProperties passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}