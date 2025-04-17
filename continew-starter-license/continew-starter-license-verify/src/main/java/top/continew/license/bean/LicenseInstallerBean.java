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

package top.continew.license.bean;

import de.schlichtherle.license.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.continew.license.config.LicenseVerifyProperties;
import top.continew.license.exception.VerifyException;
import top.continew.license.manager.CustomLicenseManager;

import java.io.*;

/**
 * 证书安装业务类
 *
 * @Desc:
 * @Author loach
 * @ClassName top.continew.license.bean.LicenseInstallerBean
 * @Date 2025-04-15 15:05
 */
public class LicenseInstallerBean {

    private static final Logger log = LoggerFactory.getLogger(LicenseInstallerBean.class);

    private String licensePath;

    private LicenseManager licenseManager;

    private LicenseVerifyProperties properties;

    public LicenseInstallerBean(LicenseVerifyProperties properties) {
        this.properties = properties;

        if (properties == null || properties.getSavePath() == null) {
            String os = System.getProperty("os.name");
            if (os.toLowerCase().contains("windows")) {
                this.licensePath = "D:/license/";
            }
            this.licensePath = "/data/license/";
        } else {
            this.licensePath = properties.getSavePath();

        }
    }

    //安装证书
    public void installLicense() throws Exception {

        try {

            licenseManager = CustomLicenseManager.getInstance(properties);
            licenseManager.uninstall();
            LicenseContent licenseContent = licenseManager
                .install(new File(getLicensePath() + "clientLicense/license.lic"));
            log.info("证书认证通过，安装成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new VerifyException("证书认证失败:" + e + " " + e.getMessage());
        }

    }

    //卸载证书
    public void uninstallLicense() throws Exception {
        if (licenseManager != null) {
            licenseManager.uninstall();
            //Log.info("证书已卸载");
        }
    }

    //即时验证证书合法性
    public void verify() throws Exception {
        if (licenseManager != null) {
            licenseManager.verify();
            //Log.info("证书认证通过");
        }
        throw new VerifyException("证书认证失败:licenseManager is null");
    }

    /**
     * 获取license文件位置
     *
     * @return
     */
    private String getLicensePath() {
        return licensePath;
    }

}
