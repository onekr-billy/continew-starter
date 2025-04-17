## continew-starter-license-generate 食用方法

1. 引入依赖

```java
        <dependency>
            <groupId>top.continew</groupId>
            <artifactId>continew-starter-license-generate</artifactId>
            <version>2.11.0-SNAPSHOT</version>
        </dependency>
```

2. 开发对外接口

```
/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.controller.license;

import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.continew.license.dto.LicenseCreatorParamVO;
import top.continew.license.dto.LicenseExtraModel;
import top.continew.license.service.LicenseCreateService;

import java.util.Calendar;
import java.util.Date;

/**
 * @Desc:
 * @Author loach
 * @ClassName top.continew.admin.controller.license.LicenseGenerateController
 * @Date 2025-04-15 10:52
 */
@RestController
@RequestMapping("/license")
public class LicenseGenerateController {

    @Autowired
    private LicenseCreateService licenseCreateService;

    @GetMapping("/generate")
    public void listDict() throws Exception {
        //设置证书校验参数
        LicenseCreatorParamVO paramVO = new LicenseCreatorParamVO();
        paramVO.setCustomerName("continew");
        paramVO.setDescription("continew");
        paramVO.setKeyPass("123456a");
        paramVO.setStorePass("123456a");
        //设置过期时间
        Calendar calendar = Calendar.getInstance();
        long expire = new Date().getTime() + (24L * 3600L * 1000L);
        calendar.setTimeInMillis(expire);
        paramVO.setExpireTime(calendar.getTime());
        //设置额外校验参数(服务器信息)
        LicenseExtraModel extraModel = licenseCreateService.getServerInfo();
        paramVO.setLicenseExtraModel(extraModel);
        licenseCreateService.generateLicense(paramVO);
    }

}

```



注：默认生成 license 为C:/license下。将压缩包发送给客户端使用。