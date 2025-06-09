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

package top.continew.starter.security.crypto.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.type.SimpleTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.continew.starter.security.crypto.annotation.FieldEncrypt;
import top.continew.starter.security.crypto.autoconfigure.CryptoProperties;
import top.continew.starter.security.crypto.encryptor.IEncryptor;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;

/**
 * 字段解密拦截器
 *
 * @author Charles7c
 * @since 1.4.0
 */
@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class MyBatisDecryptInterceptor extends AbstractMyBatisInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(MyBatisDecryptInterceptor.class);
    private CryptoProperties properties;

    public MyBatisDecryptInterceptor(CryptoProperties properties) {
        this.properties = properties;
    }

    public MyBatisDecryptInterceptor() {
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object obj = invocation.proceed();
        if (obj == null) {
            return null;
        }
        // 确保目标是 ResultSetHandler
        if (!(invocation.getTarget() instanceof ResultSetHandler)) {
            return obj;
        }
        // 处理查询结果
        if (obj instanceof List<?> resultList) {
            // 处理列表结果
            this.decryptList(resultList);
        } else {
            // 处理单个对象结果
            this.decryptObject(obj);
        }
        return obj;
    }

    /**
     * 解密列表结果
     *
     * @param resultList 结果列表
     */
    private void decryptList(List<?> resultList) {
        if (CollUtil.isEmpty(resultList)) {
            return;
        }
        for (Object result : resultList) {
            decryptObject(result);
        }
    }

    /**
     * 解密单个对象结果
     *
     * @param result 结果对象
     */
    private void decryptObject(Object result) {
        if (result == null) {
            return;
        }
        // String、Integer、Long 等简单类型对象无需处理
        if (SimpleTypeRegistry.isSimpleType(result.getClass())) {
            return;
        }
        // 获取所有字符串类型、需要解密的、有值字段
        List<Field> fieldList = super.getEncryptFields(result);
        if (fieldList.isEmpty()) {
            return;
        }
        // 解密处理
        for (Field field : fieldList) {
            IEncryptor encryptor = super.getEncryptor(field.getAnnotation(FieldEncrypt.class));
            Object fieldValue = ReflectUtil.getFieldValue(result, field);
            if (fieldValue == null) {
                continue;
            }
            // 优先获取自定义对称加密算法密钥，获取不到时再获取全局配置
            String password = ObjectUtil.defaultIfBlank(field.getAnnotation(FieldEncrypt.class).password(), properties
                .getPassword());
            try {
                String ciphertext = encryptor.decrypt(fieldValue.toString(), password, properties.getPrivateKey());
                ReflectUtil.setFieldValue(result, field, ciphertext);
            } catch (Exception e) {
                // 解密失败时保留原值，避免影响正常业务流程
                log.warn("解密失败，请检查加密配置", e);
            }
        }
    }
}