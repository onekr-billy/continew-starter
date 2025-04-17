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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.schlichtherle.license.*;
import de.schlichtherle.xml.GenericCertificate;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.continew.license.bean.LicenseInstallerBean;
import top.continew.license.autoconfigure.LicenseVerifyProperties;
import top.continew.license.dto.ConfigParam;
import top.continew.license.dto.LicenseExtraModel;
import top.continew.license.keyStoreParam.CustomKeyStoreParam;
import top.continew.license.utils.ServerInfoUtils;

import java.io.*;
import java.util.prefs.Preferences;

/**
 * 客户端证书管理类(证书验证)
 *
 * @author loach
 * @since 1.2.0
 */
@Component
public class CustomLicenseManager extends LicenseManager {

    private static final Logger log = LoggerFactory.getLogger(CustomLicenseManager.class);

    private static volatile CustomLicenseManager INSTANCE;
    private LicenseExtraModel extraModel;

    private LicenseVerifyProperties properties;

    private CustomLicenseManager(LicenseVerifyProperties properties) {
        this.properties = properties;
        // 初始化服务信息
        initServerExtraModel();
        // 解压证书和配置文件等
        extractZip();
        // 获取配置文件
        ConfigParam configParam = getConfigParam();
        // 安装证书
        Preferences preferences = Preferences.userNodeForPackage(LicenseInstallerBean.class);
        CipherParam cipherParam = new DefaultCipherParam(configParam.getStorePass());
        KeyStoreParam publicKeyStoreParam = new CustomKeyStoreParam(LicenseInstallerBean.class, properties
            .getStorePath() + File.separator + "clientLicense/publicCerts.keystore", configParam
                .getPublicAlias(), configParam.getStorePass(), null);
        LicenseParam licenseParam = new DefaultLicenseParam(configParam
            .getSubject(), preferences, publicKeyStoreParam, cipherParam);

        super.setLicenseParam(licenseParam);
    }

    public static CustomLicenseManager getInstance(LicenseVerifyProperties properties) {
        if (INSTANCE == null) {
            synchronized (CustomLicenseManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CustomLicenseManager(properties);
                }
            }
        }
        return INSTANCE;
    }

    private void initServerExtraModel() {
        this.extraModel = ServerInfoUtils.getServerInfos();
    }

    /**
     * 解压压缩包
     *
     * @throws ZipException
     */
    private void extractZip() {
        ZipFile config = new ZipFile(properties.getStorePath() + File.separator + "clientLicense.zip");
        File licenseDir = new File(properties.getStorePath() + File.separator + "clientLicense");
        if (!licenseDir.exists()) {
            licenseDir.mkdir();
        }
        try {
            config.extractAll(licenseDir.getAbsolutePath());
        } catch (ZipException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取压缩文件中配置的基础参数
     *
     * @return
     * @throws Exception
     */
    private ConfigParam getConfigParam() {
        FileInputStream config = null;
        BufferedReader reader = null;
        try {
            config = new FileInputStream(properties
                .getStorePath() + File.separator + "clientLicense/clientConfig.json");
            reader = new BufferedReader(new InputStreamReader(config, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
            ObjectMapper mapper = new ObjectMapper();
            ConfigParam configParam = mapper.readValue(sb.toString(), ConfigParam.class);
            return configParam;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (config != null) {
                try {
                    config.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    /**
     * 重写验证证书方法，添加自定义参数验证
     */
    @Override
    protected synchronized void validate(LicenseContent content) throws LicenseContentException {
        // 系统验证基本参数：生效时间、失效时间、公钥别名、公钥密码
        super.validate(content);
        // 验证自定义参数
        Object o = content.getExtra();
        if (o != null && extraModel != null && o instanceof LicenseExtraModel) {
            LicenseExtraModel contentExtraModel = (LicenseExtraModel)o;
            if (!contentExtraModel.getCpuSerial().equals(extraModel.getCpuSerial())) {
                throw new LicenseContentException("CPU核数不匹配");
            }
            if (!contentExtraModel.getMainBoardSerial().equals(extraModel.getMainBoardSerial())) {
                throw new LicenseContentException("主板序列号不匹配");
            }
            if (!contentExtraModel.getIpAddress().equals(extraModel.getIpAddress())) {
                throw new LicenseContentException("IP地址不匹配");
            }
            if (!contentExtraModel.getMacAddress().equals(extraModel.getMacAddress())) {
                throw new LicenseContentException("MAC地址不匹配");
            }
        } else {
            throw new LicenseContentException("证书无效");
        }
    }

    /**
     * 重写证书安装方法，主要是更改调用本类的验证方法
     */
    @Override
    protected synchronized LicenseContent install(final byte[] key, LicenseNotary notary) throws Exception {

        final GenericCertificate certificate = getPrivacyGuard().key2cert(key);
        notary.verify(certificate);
        final LicenseContent content = (LicenseContent)certificate.getContent();
        this.validate(content);
        setLicenseKey(key);
        setCertificate(certificate);

        return content;
    }

    /**
     * 重写验证证书合法的方法，主要是更改调用本类的验证方法
     */
    @Override
    protected synchronized LicenseContent verify(LicenseNotary notary) throws Exception {
        GenericCertificate certificate = getCertificate();
        if (null != certificate)
            return (LicenseContent)certificate.getContent();

        // Load license key from preferences,
        final byte[] key = getLicenseKey();
        if (null == key)
            throw new NoLicenseInstalledException(getLicenseParam().getSubject());
        certificate = getPrivacyGuard().key2cert(key);
        notary.verify(certificate);
        final LicenseContent content = (LicenseContent)certificate.getContent();
        this.validate(content);
        setCertificate(certificate);

        return content;
    }
}
