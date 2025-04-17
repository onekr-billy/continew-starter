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

package top.continew.license.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.schlichtherle.license.*;
import net.lingala.zip4j.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.continew.license.dto.ConfigParam;
import top.continew.license.dto.LicenseCreatorParam;
import top.continew.license.dto.LicenseCreatorParamVO;
import top.continew.license.dto.LicenseExtraModel;
import top.continew.license.exception.LicenseException;
import top.continew.license.keyStoreParam.CustomKeyStoreParam;
import top.continew.license.manager.ServerLicenseManager;
import top.continew.license.util.ExecCmdUtil;
import top.continew.license.util.ServerInfoUtils;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.prefs.Preferences;

/**
 * 证书生成接口 实现类
 *
 * @Desc:
 * @Author loach
 * @ClassName top.continew.license.service.impl.LicenseCreateServiceImpl
 * @Date 2025-03-22 18:36
 */
@Component
public class LicenseCreateService {

    private static final Logger log = LoggerFactory.getLogger(LicenseCreateService.class);

    private static volatile LicenseCreateService instance;

    private LicenseCreateService() {
        // 私有构造
    }

    public static LicenseCreateService getInstance() {
        if (instance == null) {
            synchronized (LicenseCreateService.class) {
                if (instance == null) {
                    instance = new LicenseCreateService();
                }
            }
        }
        return instance;
    }

    private static final X500Principal DEFAULT_HOLDER_ISSUER = new X500Principal("CN=localhost, OU=localhost, O=localhost, L=SH, ST=SH, C=CN");

    /**
     * 获取服务器信息
     *
     * @return
     */
    public LicenseExtraModel getServerInfo() {
        LicenseExtraModel serverInfos = ServerInfoUtils.getServerInfos();
        return serverInfos;
    }

    /**
     * 生成一个证书
     *
     * @param paramVO
     */
    public void generateLicense(LicenseCreatorParamVO paramVO) throws Exception {

        Map<String, Object> map = buildCreator(paramVO);
        LicenseCreatorParam param = (LicenseCreatorParam)map.get("creator");
        ZipFile clientZipFile = (ZipFile)map.get("clientZipFile");
        try {
            LicenseParam licenseParam = initLicenseParam(param);
            LicenseManager licenseManager = new ServerLicenseManager(licenseParam);
            LicenseContent licenseContent = initLcenseContent(param);
            licenseManager.store(licenseContent, new File(param.getLicensePath()));
            log.info("{}证书生成成功", param.getSubject());
            clientZipFile.addFile(param.getLicensePath());
        } catch (Exception e) {
            e.printStackTrace();
            // log.error("{}生成证书失败:", param.getSubject());
            throw new LicenseException("生成证书失败:" + param.getSubject());
        }

    }

    /**
     * 封装证书生成参数
     */
    private Map<String, Object> buildCreator(LicenseCreatorParamVO paramVO) throws Exception {
        String customerName = paramVO.getCustomerName();
        String privateAlias = customerName + "-private-alias";
        String publicAlias = customerName + "-public-alias";
        String relativePath = relativePath(paramVO);
        String currentCustomerDir = relativePath + customerName + uuid() + "/";
        File file = new File(currentCustomerDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String privateKeystore = currentCustomerDir + "privateKeys.keystore";
        String publicKeystore = currentCustomerDir + "publicCerts.keystore";

        LicenseCreatorParam param = new LicenseCreatorParam();
        param.setSubject(customerName);
        param.setPrivateAlias(privateAlias);
        param.setKeyPass(paramVO.getKeyPass());
        param.setStorePass(paramVO.getStorePass());
        param.setLicensePath(currentCustomerDir + "license.lic");
        param.setPrivateKeysStorePath(privateKeystore);
        param.setExpiryTime(paramVO.getExpireTime());
        param.setDescription(paramVO.getDescription());
        param.setLicenseExtraModel(paramVO.getLicenseExtraModel());

        if (checkJavaVersion()) {
            // JDK>=17 生成私钥库

            String exe1 = "keytool -genkeypair -keyalg DSA -keysize 1024 -validity " + getValidity(param
                .getIssuedTime(), paramVO
                    .getExpireTime()) + " -alias " + privateAlias + " -keystore " + privateKeystore + " -storepass " + paramVO
                        .getStorePass() + " -keypass " + paramVO
                            .getKeyPass() + " -dname \"CN=localhost, OU=localhost, O=localhost, L=SH, ST=SH, C=CN\"";
            String exe2 = "keytool -exportcert -alias " + privateAlias + " -keystore " + privateKeystore + " -storepass " + paramVO
                .getStorePass() + " -file \"" + currentCustomerDir + "certfile.cer\"";

            String exe3 = "keytool -noprompt -import -alias " + publicAlias + " -file \"" + currentCustomerDir + "certfile.cer\"" + " -keystore " + publicKeystore + " -storepass " + paramVO
                .getStorePass();

            ExecCmdUtil.exec(exe1);
            ExecCmdUtil.exec(exe2);
            ExecCmdUtil.exec(exe3);

        } else {
            // JDK<17 生成私钥库
            String exe1 = "keytool -genkeypair -keysize 1024 -validity " + getValidity(param.getIssuedTime(), paramVO
                .getExpireTime()) + " -alias " + privateAlias + " -keystore " + privateKeystore + " -storepass " + paramVO
                    .getStorePass() + " -keypass " + paramVO
                        .getKeyPass() + " -dname \"CN=localhost, OU=localhost, " + "O=localhost, L=SH, ST=SH, C=CN\"";
            String exe2 = "keytool -exportcert -alias " + privateAlias + " -keystore " + privateKeystore + " -storepass " + paramVO
                .getStorePass() + " -file \"" + currentCustomerDir + "certfile.cer\"";
            String exe3 = "keytool -noprompt -import -alias " + publicAlias + " -file \"" + currentCustomerDir + "certfile.cer\" -keystore " + publicKeystore + " -storepass " + paramVO
                .getStorePass();
            ExecCmdUtil.exec(exe1);
            ExecCmdUtil.exec(exe2);
            ExecCmdUtil.exec(exe3);
        }

        ZipFile clientZipFile = generateClientConfig(param, currentCustomerDir, publicAlias);
        Map<String, Object> map = new HashMap<>();
        map.put("creator", param);
        map.put("clientZipFile", clientZipFile);
        return map;
    }

    private String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 校验JDK版本
     *
     * @return
     * @throws Exception
     */
    private boolean checkJavaVersion() throws Exception {
        String version = System.getProperty("java.version");

        int currentVersion = 0;
        if (version.startsWith("1.")) {
            currentVersion = Integer.parseInt(version.split("\\.")[1]);
        } else {
            currentVersion = Integer.parseInt(version.split("\\.")[0]);
        }

        if (currentVersion >= 17) {
            return true;
        } else {
            return false;
        }
    }

    private ZipFile generateClientConfig(LicenseCreatorParam param,
                                         String currentCustomerDir,
                                         String publicAlias) throws Exception {

        ZipFile clientLicense = new ZipFile(currentCustomerDir + "clientLicense.zip");
        File config = new File(currentCustomerDir + "clientConfig.json");
        ConfigParam configParam = new ConfigParam();
        configParam.setPublicAlias(publicAlias);
        configParam.setStorePass(param.getStorePass());
        configParam.setSubject(param.getSubject());
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(configParam);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(config);
            out.write(json.getBytes("UTF-8"));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new LicenseException("密钥文件生成失败");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        List<File> files = new ArrayList<>();
        files.add(config);
        files.add(new File(currentCustomerDir + "publicCerts.keystore"));
        clientLicense.addFiles(files);
        return clientLicense;
    }

    //将有效时间转换成天
    private int getValidity(Date issuedTime, Date expireTime) {
        long issued = issuedTime.getTime();
        long expire = expireTime.getTime();
        long differ = expire - issued;
        long remaining = differ % (24L * 3600L * 1000L);
        Long validity = differ / (24L * 3600L * 1000L);
        if (remaining > 0) {
            validity++;
        }
        return validity.intValue();
    }

    private boolean isWindows() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("windows")) {
            return true;
        }
        return false;
    }

    //证书生成路径
    private String relativePath(LicenseCreatorParamVO paramVO) {

        if (paramVO.getLicenseSavePath() != null) {
            return paramVO.getLicenseSavePath();
        }
        if (isWindows()) {
            return "C:/license/";
        }
        return "/data/license/";
    }

    /**
     * 设置证书生成参数
     */
    private LicenseParam initLicenseParam(LicenseCreatorParam param) {
        Preferences preferences = Preferences.userNodeForPackage(LicenseCreateService.class);
        //设置密钥
        CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());
        KeyStoreParam privateStoreParam = new CustomKeyStoreParam(LicenseCreateService.class, param
            .getPrivateKeysStorePath(), param.getPrivateAlias(), param.getStorePass(), param.getKeyPass());
        LicenseParam licenseParam = new DefaultLicenseParam(param
            .getSubject(), preferences, privateStoreParam, cipherParam);
        return licenseParam;

    }

    /**
     * 设置证书生成内容
     */
    private LicenseContent initLcenseContent(LicenseCreatorParam param) {

        LicenseContent licenseContent = new LicenseContent();
        licenseContent.setHolder(DEFAULT_HOLDER_ISSUER);
        licenseContent.setIssuer(DEFAULT_HOLDER_ISSUER);
        licenseContent.setSubject(param.getSubject());
        licenseContent.setIssued(param.getIssuedTime());
        licenseContent.setNotBefore(param.getIssuedTime());
        licenseContent.setNotAfter(param.getExpiryTime());
        licenseContent.setConsumerType(param.getConsumerType());
        licenseContent.setConsumerAmount(param.getConsumerAmount());
        licenseContent.setInfo(param.getDescription());

        if (param != null && param.getLicenseExtraModel() != null) {
            licenseContent.setExtra(param.getLicenseExtraModel());
        }

        return licenseContent;
    }

}
