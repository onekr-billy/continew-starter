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

package top.continew.starter.json.jackson.enums;

/**
 * 大数值序列化模式
 *
 * @author Jasmine
 * @author Charles7c
 * @since 2.12.1
 */
public enum BigNumberSerializeMode {

    /**
     * 超过 JS 范围的数值转为 {@link String} 类型，否则保持原类型
     * <p>
     * JS：Number.MIN_SAFE_INTEGER：-9007199254740991L <br />
     * JS：Number.MAX_SAFE_INTEGER：9007199254740991L
     * </p>
     */
    FLEXIBLE,

    /**
     * 统一转为 {@link String} 类型
     */
    TO_STRING,

    /**
     * 不操作（不建议）
     * <p>
     * 注意：超过 JS 范围的数值会损失精度，例如：8014753905961037835 会被转为 8014753905961038000
     * </p>
     */
    NO_OPERATE,
}
