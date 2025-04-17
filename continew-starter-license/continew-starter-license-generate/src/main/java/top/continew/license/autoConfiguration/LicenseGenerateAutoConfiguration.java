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

package top.continew.license.autoConfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.continew.license.service.LicenseCreateService;

/**
 * @Desc:
 * @Author loach
 * @ClassName top.continew.license.AutoConfiguration.LicenseGenerateAutoConfiguration
 * @Date 2025-03-23 10:57
 */
@Configuration
public class LicenseGenerateAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LicenseCreateService licenseCreateService() {
        return LicenseCreateService.getInstance();
    }
}
