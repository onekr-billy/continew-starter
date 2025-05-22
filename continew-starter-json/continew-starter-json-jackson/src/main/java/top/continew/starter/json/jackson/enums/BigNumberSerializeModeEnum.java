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
 * @since 2.12.0
 */
public enum BigNumberSerializeModeEnum {
    /** 超过js的最大值转字符串类型，否则保持原类型 */
    FLEXIBLE,
    /** 不操作 */
    NO_OPERATE,
    /** 统一转String类型 */
    TO_STRING,;
}
