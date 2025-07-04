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

package top.continew.starter.license.model;

import java.util.Date;

/**
 * 为用户生成证书需要的具体参数
 *
 * @author loach
 * @since 2.12.0
 */
public class LicenseCreatorParamVO {

    /**
     * 有效期截至时间
     */
    private Date expireTime;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 公钥钥库密码库，必须包含数字和字母
     */
    private String storePass;

    /**
     * 私钥密码,必须包含数字和字母
     */
    private String keyPass;

    /**
     * 描述信息
     */
    private String description;

    /**
     * license 保存位置
     */
    private String licenseSavePath;

    /**
     * 额外的服务器硬件校验信息
     */
    private LicenseExtraModel licenseExtraModel;

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStorePass() {
        return storePass;
    }

    public void setStorePass(String storePass) {
        this.storePass = storePass;
    }

    public String getKeyPass() {
        return keyPass;
    }

    public void setKeyPass(String keyPass) {
        this.keyPass = keyPass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLicenseSavePath() {
        return licenseSavePath;
    }

    public void setLicenseSavePath(String licenseSavePath) {
        this.licenseSavePath = licenseSavePath;
    }

    public LicenseExtraModel getLicenseExtraModel() {
        return licenseExtraModel;
    }

    public void setLicenseExtraModel(LicenseExtraModel licenseExtraModel) {
        this.licenseExtraModel = licenseExtraModel;
    }
}
