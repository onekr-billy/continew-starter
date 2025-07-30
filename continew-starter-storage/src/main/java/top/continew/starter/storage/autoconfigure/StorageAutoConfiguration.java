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

package top.continew.starter.storage.autoconfigure;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.continew.starter.storage.autoconfigure.properties.StorageProperties;
import top.continew.starter.storage.core.FileStorageService;
import top.continew.starter.storage.core.ProcessorRegistry;
import top.continew.starter.storage.core.StrategyProxyFactory;
import top.continew.starter.storage.prehandle.*;
import top.continew.starter.storage.prehandle.impl.*;
import top.continew.starter.storage.router.StorageStrategyRegistrar;
import top.continew.starter.storage.router.StorageStrategyRouter;
import top.continew.starter.storage.service.FileRecorder;
import top.continew.starter.storage.service.impl.DefaultFileRecorder;
import top.continew.starter.storage.strategy.StorageStrategyOverride;

import java.util.List;

/**
 * 存储自动配置
 *
 * @author echo
 * @since 2.14.0
 */
@AutoConfiguration
@EnableConfigurationProperties(StorageProperties.class)
@Import({ProcessorRegistry.class, StrategyProxyFactory.class})
public class StorageAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(StorageAutoConfiguration.class);

    private final StorageProperties properties;

    public StorageAutoConfiguration(StorageProperties properties) {
        this.properties = properties;
    }

    /**
     * 策略路由器
     *
     * @param registrars 注册
     * @return {@link StorageStrategyRouter }
     */
    @Bean
    public StorageStrategyRouter strategyRouter(List<StorageStrategyRegistrar> registrars) {
        return new StorageStrategyRouter(registrars);
    }

    /**
     * S3存储自动配置
     *
     * @return {@link S3StorageAutoConfiguration }
     */
    @Bean
    public S3StorageAutoConfiguration s3StorageAutoConfiguration() {
        return new S3StorageAutoConfiguration(properties);
    }

    /**
     * 本地存储自动配置
     *
     * @return {@link LocalStorageAutoConfiguration }
     */
    @Bean
    public LocalStorageAutoConfiguration localStorageAutoConfiguration() {
        return new LocalStorageAutoConfiguration(properties);
    }

    /**
     * 文件存储服务
     *
     * @param router            路由
     * @param storageProperties 存储属性
     * @param processorRegistry 处理器注册表
     * @param proxyFactory      代理工厂
     * @return {@link FileStorageService }
     */
    @Bean
    public FileStorageService fileStorageService(StorageStrategyRouter router,
                                                 StorageProperties storageProperties,
                                                 ProcessorRegistry processorRegistry,
                                                 StrategyProxyFactory proxyFactory,
                                                 FileRecorder fileRecorder) {
        return new FileStorageService(router, storageProperties, processorRegistry, proxyFactory, fileRecorder);
    }

    /**
     * 文件记录器
     *
     * @return {@link FileRecorder }
     */
    @Bean
    @ConditionalOnMissingBean
    public FileRecorder fileRecorder() {
        return new DefaultFileRecorder();
    }

    /**
     * 默认文件名生成器
     *
     * @param registry 登记处
     * @return {@link FileNameGenerator }
     */
    @Bean
    @ConditionalOnMissingBean(name = "defaultFileNameGenerator")
    public FileNameGenerator defaultFileNameGenerator(ProcessorRegistry registry) {
        DefaultFileNameGenerator generator = new DefaultFileNameGenerator();
        registry.registerGlobalNameGenerator(generator);
        return generator;
    }

    /**
     * 默认文件路径生成器
     *
     * @param registry 注册
     * @return {@link FilePathGenerator }
     */
    @Bean
    @ConditionalOnMissingBean(name = "defaultFilePathGenerator")
    public FilePathGenerator defaultFilePathGenerator(ProcessorRegistry registry) {
        DefaultFilePathGenerator generator = new DefaultFilePathGenerator();
        registry.registerGlobalPathGenerator(generator);
        return generator;
    }

    /**
     * 默认缩略图处理器
     *
     * @param registry 注册
     * @return {@link ThumbnailProcessor }
     */
    @Bean
    @ConditionalOnMissingBean(name = "defaultThumbnailProcessor")
    @ConditionalOnClass(name = "net.coobird.thumbnailator.Thumbnails")
    public ThumbnailProcessor defaultThumbnailProcessor(ProcessorRegistry registry) {
        DefaultThumbnailProcessor processor = new DefaultThumbnailProcessor();
        registry.registerGlobalThumbnailProcessor(processor);
        return processor;
    }

    /**
     * 文件大小验证器
     *
     * @param registry 注册
     * @return {@link FileValidator }
     */
    @Bean
    @ConditionalOnMissingBean(name = "fileSizeValidator")
    public FileValidator fileSizeValidator(ProcessorRegistry registry, MultipartProperties multipartProperties) {
        FileSizeValidator validator = new FileSizeValidator(multipartProperties);
        registry.registerGlobalValidator(validator);
        return validator;
    }

    /**
     * 文件类型验证器
     *
     * @param registry 注册
     * @return {@link FileValidator }
     */
    @Bean
    @ConditionalOnMissingBean(name = "fileTypeValidator")
    public FileValidator fileTypeValidator(ProcessorRegistry registry) {
        FileTypeValidator validator = new FileTypeValidator();
        registry.registerGlobalValidator(validator);
        return validator;
    }

    /**
     * 策略重写自动注册
     */
    @Configuration
    @ConditionalOnBean(StorageStrategyOverride.class)
    public static class StrategyOverrideConfiguration {

        /**
         * 注册覆盖
         */
        @Autowired
        public void registerOverrides(List<StorageStrategyOverride<?>> overrides, StrategyProxyFactory proxyFactory) {
            for (StorageStrategyOverride<?> override : overrides) {
                proxyFactory.registerOverride(override);
            }
        }
    }

    /**
     * 处理器自动注册
     */
    @Configuration
    public static class ProcessorAutoConfiguration {
        @Autowired(required = false)
        public void registerGlobalProcessors(List<FileNameGenerator> nameGenerators,
                                             List<FilePathGenerator> pathGenerators,
                                             List<ThumbnailProcessor> thumbnailProcessors,
                                             List<FileValidator> validators,
                                             List<UploadCompleteProcessor> completeProcessors,
                                             ProcessorRegistry registry) {

            // 注册全局处理器
            if (nameGenerators != null) {
                nameGenerators.forEach(registry::registerGlobalNameGenerator);
            }
            if (pathGenerators != null) {
                pathGenerators.forEach(registry::registerGlobalPathGenerator);
            }
            if (thumbnailProcessors != null) {
                thumbnailProcessors.forEach(registry::registerGlobalThumbnailProcessor);
            }
            if (validators != null) {
                validators.forEach(registry::registerGlobalValidator);
            }
            if (completeProcessors != null) {
                completeProcessors.forEach(registry::registerGlobalCompleteProcessor);
            }
        }
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[ContiNew Starter] - Auto Configuration 'Storage' completed initialization.");
    }
}
