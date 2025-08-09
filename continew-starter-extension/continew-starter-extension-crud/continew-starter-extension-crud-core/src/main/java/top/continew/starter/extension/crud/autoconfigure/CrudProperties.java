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

package top.continew.starter.extension.crud.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import top.continew.starter.core.constant.PropertiesConstants;
import top.continew.starter.extension.crud.model.query.SortQuery;

/**
 * CRUD 配置属性
 *
 * @author Charles7c
 * @since 2.7.2
 */
@ConfigurationProperties(PropertiesConstants.CRUD)
public class CrudProperties {

    /**
     * 树型字典结构映射配置
     *
     * @see top.continew.starter.extension.crud.controller.AbstractCrudController#dictTree(Object, SortQuery)
     * @see top.continew.starter.extension.crud.service.CrudService#tree(Object, SortQuery, boolean)
     */
    @NestedConfigurationProperty
    private CrudTreeDictModelProperties treeDictModel = new CrudTreeDictModelProperties();

    public CrudTreeDictModelProperties getTreeDictModel() {
        return treeDictModel;
    }

    public void setTreeDictModel(CrudTreeDictModelProperties treeDictModel) {
        this.treeDictModel = treeDictModel;
    }
}
