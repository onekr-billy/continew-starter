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

package top.continew.starter.storage.strategy.impl;

import top.continew.starter.storage.strategy.StorageStrategy;
import top.continew.starter.storage.strategy.StorageStrategyOverride;

/**
 * 抽象基类，提供原始目标对象的访问
 *
 * @author echo
 * @since 2.14.0
 */
public abstract class AbstractStorageStrategyOverride<T extends StorageStrategy> implements StorageStrategyOverride<T> {

    protected T originalTarget;

    /**
     * 设置原始目标对象（由代理工厂调用）
     */
    public void setOriginalTarget(T originalTarget) {
        this.originalTarget = originalTarget;
    }

    @Override
    public T getOriginalTarget() {
        return originalTarget;
    }
}