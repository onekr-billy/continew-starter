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

package top.continew.starter.storage.common.enums;

/**
 * 默认存储配置来源
 * 决定默认存储平台配置从哪里加载
 *
 * @author echo
 * @since 2.14.0
 */
public enum DefaultStorageSource {

    /**
     * 从配置文件加载默认存储
     */
    CONFIG,

    /**
     * 从动态配置加载默认存储
     */
    DYNAMIC,

}
