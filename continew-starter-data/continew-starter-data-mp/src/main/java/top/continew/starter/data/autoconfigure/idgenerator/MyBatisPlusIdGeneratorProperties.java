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

package top.continew.starter.data.autoconfigure.idgenerator;

/**
 * MyBatis Plus ID 生成器配置属性
 *
 * @author Charles7c
 * @since 1.4.0
 */
public class MyBatisPlusIdGeneratorProperties {

    /**
     * 类型
     */
    private IdGeneratorType type = IdGeneratorType.DEFAULT;

    public IdGeneratorType getType() {
        return type;
    }

    public void setType(IdGeneratorType type) {
        this.type = type;
    }

    /**
     * ID 生成器类型枚举
     */
    public enum IdGeneratorType {

        /**
         * 使用雪花算法（使用网卡信息绑定雪花生成器，防止集群雪花 ID 重复）
         */
        DEFAULT,

        /**
         * 使用 CosId
         */
        COSID,

        /**
         * 自定义
         */
        CUSTOM
    }

}
