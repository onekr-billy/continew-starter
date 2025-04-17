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

package top.continew.license.config;

import de.schlichtherle.license.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.continew.license.bean.LicenseInstallerBean;
import top.continew.license.manager.CustomLicenseManager;

/**
 * @Desc:
 * @Author loach
 * @ClassName top.continew.license.config.LicenseAutoConfiguration
 * @Date 2025-04-15 15:17
 */
@Configuration
public class LicenseAutoConfiguration {

    private String licensePath;

    @Bean
    public LicenseVerifyProperties licenseVerifyProperties() {
        return new LicenseVerifyProperties();
    }

    @Bean
    public LicenseInstallerBean licenseInstallerBean(LicenseVerifyProperties properties) {
        return new LicenseInstallerBean(properties);
    }

    @Bean
    public LicenseManager licenseManager(LicenseVerifyProperties properties) {
        return CustomLicenseManager.getInstance(properties);
    }

}
