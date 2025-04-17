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

package top.continew.license.manager;

import de.schlichtherle.license.*;
import de.schlichtherle.xml.GenericCertificate;

import java.util.Date;

/**
 * 自定义服务端证书管理类(生成证书)
 *
 * @Desc:
 * @Author loach
 * @ClassName top.continew.license.manager.ServerLicenseManager
 * @Date 2025-03-22 14:31
 */
public class ServerLicenseManager extends LicenseManager {

    public ServerLicenseManager(LicenseParam param) {
        super(param);
    }

    /**
     * 证书生成参数验证
     *
     * @param content
     * @throws LicenseContentException
     */
    protected synchronized void validateCreate(final LicenseContent content) throws LicenseContentException {
        Date now = new Date();
        Date notBefore = content.getNotBefore();
        Date notAfter = content.getNotAfter();
        if (notBefore != null && now.before(notBefore)) {
            throw new LicenseContentException("证书尚未生效，无法生成");
        }
        if (notAfter != null && now.after(notAfter)) {
            throw new LicenseContentException("证书已过期，无法生成");
        }

        if (notBefore != null && notAfter != null && notBefore.after(notAfter)) {
            throw new LicenseContentException("证书生效时间晚于失效时间，无法生成");
        }
    }

    /**
     * 重写生成证书的方法，增加生成参数验证
     */
    @Override
    protected synchronized byte[] create(LicenseContent content, LicenseNotary notary) throws Exception {
        initialize(content);
        validateCreate(content);
        final GenericCertificate genericCertificate = notary.sign(content);
        return getPrivacyGuard().cert2key(genericCertificate);
    }
}
