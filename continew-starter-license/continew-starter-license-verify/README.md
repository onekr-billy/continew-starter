## continew-starter-license-verify 食用方法

1. 引入依赖

```java
        <dependency>
            <groupId>top.continew</groupId>
            <artifactId>continew-starter-license-verify</artifactId>
            <version>2.11.0-SNAPSHOT</version>
        </dependency>
```

2. 配置YML（license 压缩包存放位置）

```yaml
continew-starter:
  license:
    verify:
      storePath: D:/license
```

注：默认加载 `FileUtil.getTmpDirPath()` 位置。