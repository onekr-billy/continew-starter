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

package top.continew.starter.idempotent.generator;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import top.continew.starter.core.constant.StringConstants;

import java.lang.reflect.Method;

/**
 * 默认幂等名称生成器
 *
 * @author Charles7c
 * @since 2.12.1
 */
public class DefaultIdempotentNameGenerator implements IdempotentNameGenerator {

    @Override
    public String generate(Object target, Method method, Object... args) {
        StringBuilder nameSb = new StringBuilder();
        // 添加类名
        nameSb.append(ClassUtil.getClassName(target, false));
        nameSb.append(StringConstants.COLON);
        // 添加方法名
        nameSb.append(method.getName());
        // 添加参数信息的哈希值（如果有参数）
        if (args != null && args.length > 0) {
            nameSb.append(StringConstants.COLON);
            // 使用JSONUtil序列化参数，然后计算哈希值以确保唯一性
            String argsJson = JSONUtil.toJsonStr(args);
            nameSb.append(DigestUtil.md5Hex(argsJson));
        }
        return nameSb.toString();
    }
}
