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

package top.continew.starter.storage.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import top.continew.starter.core.constant.PropertiesConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储属性
 *
 * @author echo
 * @since 2.14.0
 */
@ConfigurationProperties(prefix = PropertiesConstants.STORAGE)
public class StorageProperties {

    /**
     * 默认使用的存储平台
     */
    private String defaultPlatform = "local";

    /**
     * 本地存储配置列表
     */
    private List<LocalStorageConfig> local = new ArrayList<>();

    /**
     * S3 存储配置列表
     */
    private List<S3StorageConfig> s3 = new ArrayList<>();

    public String getDefaultPlatform() {
        return defaultPlatform;
    }

    public void setDefaultPlatform(String defaultPlatform) {
        this.defaultPlatform = defaultPlatform;
    }

    public List<LocalStorageConfig> getLocal() {
        return local;
    }

    public void setLocal(List<LocalStorageConfig> local) {
        this.local = local;
    }

    public List<S3StorageConfig> getS3() {
        return s3;
    }

    public void setS3(List<S3StorageConfig> s3) {
        this.s3 = s3;
    }
}
