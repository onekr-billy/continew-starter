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

package top.continew.license.Initializing;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.continew.license.bean.LicenseInstallerBean;

/**
 * 启动校验 License
 *
 * @Desc:
 * @Author loach
 * @ClassName top.continew.license.Initializing.LicenseStarterAutoConfiguration
 * @Date 2025-04-11 15:40
 */
@Configuration
@EnableConfigurationProperties(LicenseStarterInitializingBean.class)
public class LicenseStarterInitializingBean implements InitializingBean {

    @Autowired
    private LicenseInstallerBean licenseInstallerBean;

    @Override
    public void afterPropertiesSet() throws Exception {

        //安装证书，即校验客户机器参数是否符合证书要求，符合则安装成功，不符合则报错无法启动。
        licenseInstallerBean.installLicense();

    }
}
