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

package top.continew.starter.security.crypto.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.continew.starter.security.crypto.annotation.FieldEncrypt;
import top.continew.starter.security.crypto.autoconfigure.CryptoProperties;
import top.continew.starter.security.crypto.encryptor.CryptoContext;
import top.continew.starter.security.crypto.encryptor.IEncryptor;
import top.continew.starter.security.crypto.enums.Algorithm;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加密助手
 *
 * @author lishuyan
 * @since 2.13.2
 */
public class EncryptHelper {

    private static final Logger log = LoggerFactory.getLogger(EncryptHelper.class);

    /**
     * 默认加密配置
     */
    private static CryptoProperties defaultProperties;

    /**
     * 加密器缓存
     */
    private static final Map<Integer, IEncryptor> ENCRYPTOR_CACHE = new ConcurrentHashMap<>();

    private EncryptHelper() {
    }

    /**
     * 初始化默认配置
     *
     * @param properties 加密配置
     */
    public static void init(CryptoProperties properties) {
        defaultProperties = properties;
    }

    /**
     * 注册加密执行者到缓存
     * <p>
     * 计算 CryptoContext 对象的hashCode作为缓存中的key，通过hashCode查询缓存中存在则直接返回，不存在则创建并缓存
     * </p>
     *
     * @param encryptContext 加密执行者需要的相关配置参数
     * @return 加密执行者
     */
    public static IEncryptor registerAndGetEncryptor(CryptoContext encryptContext) {
        int key = encryptContext.hashCode();
        return ENCRYPTOR_CACHE.computeIfAbsent(key, k -> encryptContext.getEncryptor().equals(IEncryptor.class)
            ? ReflectUtil.newInstance(encryptContext.getAlgorithm().getEncryptor(), encryptContext)
            : ReflectUtil.newInstance(encryptContext.getEncryptor(), encryptContext));
    }

    /**
     * 获取字段上的 @FieldEncrypt 注解
     *
     * @param obj       对象
     * @param fieldName 字段名称
     * @return 字段上的 @FieldEncrypt 注解
     * @throws NoSuchFieldException /
     */
    public static FieldEncrypt getFieldEncrypt(Object obj, String fieldName) throws NoSuchFieldException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        return field.getAnnotation(FieldEncrypt.class);
    }

    /**
     * 加密方法
     *
     * @param value        待加密字符串
     * @param fieldEncrypt 待加密字段注解
     * @return 加密后的字符串
     */
    public static String encrypt(String value, FieldEncrypt fieldEncrypt) {
        if (CharSequenceUtil.isBlank(value) || fieldEncrypt == null || !defaultProperties.isEnabled()) {
            return value;
        }
        String ciphertext = value;
        try {
            CryptoContext encryptContext = buildEncryptContext(fieldEncrypt);
            IEncryptor encryptor = registerAndGetEncryptor(encryptContext);
            ciphertext = encryptor.encrypt(ciphertext);
        } catch (Exception e) {
            log.warn("加密失败，请检查加密配置，处理加密字段异常：{}", e.getMessage(), e);
        }
        return ciphertext;
    }

    /**
     * 加密方法
     *
     * @param value 待加密字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String value) {
        if (CharSequenceUtil.isBlank(value) || !defaultProperties.isEnabled()) {
            return value;
        }
        String ciphertext = value;
        try {
            CryptoContext encryptContext = buildEncryptContext();
            IEncryptor encryptor = registerAndGetEncryptor(encryptContext);
            ciphertext = encryptor.encrypt(ciphertext);
        } catch (Exception e) {
            log.warn("加密失败，请检查加密配置，处理加密字段异常：{}", e.getMessage(), e);
        }
        return ciphertext;
    }

    /**
     * 解密方法
     *
     * @param value        待解密字符串
     * @param fieldEncrypt 待解密字段注解
     * @return 解密后的字符串
     */
    public static String decrypt(String value, FieldEncrypt fieldEncrypt) {
        if (CharSequenceUtil.isBlank(value) || fieldEncrypt == null || !defaultProperties.isEnabled()) {
            return value;
        }
        String plaintext = value;
        try {
            CryptoContext encryptContext = buildEncryptContext(fieldEncrypt);
            IEncryptor encryptor = registerAndGetEncryptor(encryptContext);
            plaintext = encryptor.decrypt(plaintext);
        } catch (Exception e) {
            log.warn("解密失败，请检查加密配置，处理解密字段异常：{}", e.getMessage(), e);
        }
        return plaintext;
    }

    /**
     * 解密方法
     *
     * @param value 待解密字符串
     * @return 解密后的字符串
     */
    public static String decrypt(String value) {
        if (CharSequenceUtil.isBlank(value) || !defaultProperties.isEnabled()) {
            return value;
        }
        String plaintext = value;
        try {
            CryptoContext encryptContext = buildEncryptContext();
            IEncryptor encryptor = registerAndGetEncryptor(encryptContext);
            plaintext = encryptor.decrypt(plaintext);
        } catch (Exception e) {
            log.warn("解密失败，请检查加密配置，处理解密字段异常：{}", e.getMessage(), e);
        }
        return plaintext;
    }

    /**
     * 构建加密上下文
     *
     * @param fieldEncrypt 字段加密注解
     * @return 加密上下文
     */
    private static CryptoContext buildEncryptContext(FieldEncrypt fieldEncrypt) {
        CryptoContext encryptContext = new CryptoContext();
        encryptContext.setAlgorithm(fieldEncrypt.value() == Algorithm.DEFAULT
            ? defaultProperties.getAlgorithm()
            : fieldEncrypt.value());
        encryptContext.setEncryptor(fieldEncrypt.encryptor().equals(IEncryptor.class)
            ? IEncryptor.class
            : fieldEncrypt.encryptor());
        encryptContext.setPassword(fieldEncrypt.password().isEmpty()
            ? defaultProperties.getPassword()
            : fieldEncrypt.password());
        encryptContext.setPrivateKey(fieldEncrypt.privateKey().isEmpty()
            ? defaultProperties.getPrivateKey()
            : fieldEncrypt.privateKey());
        encryptContext.setPublicKey(fieldEncrypt.publicKey().isEmpty()
            ? defaultProperties.getPublicKey()
            : fieldEncrypt.publicKey());
        return encryptContext;
    }

    /**
     * 构建加密上下文
     *
     * @return 加密上下文
     */
    private static CryptoContext buildEncryptContext() {
        CryptoContext encryptContext = new CryptoContext();
        encryptContext.setAlgorithm(defaultProperties.getAlgorithm());
        encryptContext.setPassword(defaultProperties.getPassword());
        encryptContext.setPrivateKey(defaultProperties.getPrivateKey());
        encryptContext.setPublicKey(defaultProperties.getPublicKey());
        return encryptContext;
    }
}
