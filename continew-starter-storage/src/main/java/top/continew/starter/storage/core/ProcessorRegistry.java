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

package top.continew.starter.storage.core;

import top.continew.starter.storage.prehandle.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 全局处理器注册表
 *
 * @author echo
 * @since 2.14.0
 */
public class ProcessorRegistry {

    // 全局处理器
    private final Map<String, FileNameGenerator> globalNameGenerators = new ConcurrentHashMap<>();
    private final Map<String, FilePathGenerator> globalPathGenerators = new ConcurrentHashMap<>();
    private final Map<String, ThumbnailProcessor> globalThumbnailProcessors = new ConcurrentHashMap<>();
    private final List<FileValidator> globalValidators = new CopyOnWriteArrayList<>();
    private final List<UploadCompleteProcessor> globalCompleteProcessors = new CopyOnWriteArrayList<>();

    // 平台特定处理器
    private final Map<String, Map<String, FileNameGenerator>> platformNameGenerators = new ConcurrentHashMap<>();
    private final Map<String, Map<String, FilePathGenerator>> platformPathGenerators = new ConcurrentHashMap<>();
    private final Map<String, Map<String, ThumbnailProcessor>> platformThumbnailProcessors = new ConcurrentHashMap<>();
    private final Map<String, List<FileValidator>> platformValidators = new ConcurrentHashMap<>();
    private final Map<String, List<UploadCompleteProcessor>> platformCompleteProcessors = new ConcurrentHashMap<>();

    /**
     * 注册全局文件名生成器
     */
    public void registerGlobalNameGenerator(FileNameGenerator generator) {
        globalNameGenerators.put(generator.getName(), generator);
    }

    public void registerGlobalPathGenerator(FilePathGenerator generator) {
        globalPathGenerators.put(generator.getName(), generator);
    }

    /**
     * 注册平台特定的文件名生成器
     */
    public void registerPlatformNameGenerator(String platform, FileNameGenerator generator) {
        platformNameGenerators.computeIfAbsent(platform, k -> new ConcurrentHashMap<>())
            .put(generator.getName(), generator);
    }

    public void registerPlatformPathGenerator(String platform, FilePathGenerator generator) {
        platformPathGenerators.computeIfAbsent(platform, k -> new ConcurrentHashMap<>())
            .put(generator.getName(), generator);
    }

    /**
     * 注册全局缩略图处理器
     */
    public void registerGlobalThumbnailProcessor(ThumbnailProcessor processor) {
        globalThumbnailProcessors.put(processor.getName(), processor);
    }

    /**
     * 注册平台特定的缩略图处理器
     */
    public void registerPlatformThumbnailProcessor(String platform, ThumbnailProcessor processor) {
        platformThumbnailProcessors.computeIfAbsent(platform, k -> new ConcurrentHashMap<>())
            .put(processor.getName(), processor);
    }

    /**
     * 注册全局验证器
     */
    public void registerGlobalValidator(FileValidator validator) {
        globalValidators.add(validator);
    }

    /**
     * 注册平台特定的验证器
     */
    public void registerPlatformValidator(String platform, FileValidator validator) {
        platformValidators.computeIfAbsent(platform, k -> new CopyOnWriteArrayList<>()).add(validator);
    }

    /**
     * 注册全局完成处理器
     */
    public void registerGlobalCompleteProcessor(UploadCompleteProcessor processor) {
        globalCompleteProcessors.add(processor);
    }

    /**
     * 注册平台特定的完成处理器
     */
    public void registerPlatformCompleteProcessor(String platform, UploadCompleteProcessor processor) {
        platformCompleteProcessors.computeIfAbsent(platform, k -> new CopyOnWriteArrayList<>()).add(processor);
    }

    /**
     * 获取文件名生成器（平台 > 全局）
     */
    public FileNameGenerator getNameGenerator(String platform) {
        // 先查找平台特定的
        Map<String, FileNameGenerator> platformGenerators = platformNameGenerators.get(platform);
        if (platformGenerators != null && !platformGenerators.isEmpty()) {
            return platformGenerators.values()
                .stream()
                .max(Comparator.comparingInt(FileNameGenerator::getOrder))
                .orElse(null);
        }

        // 再查找全局的
        return globalNameGenerators.values()
            .stream()
            .max(Comparator.comparingInt(FileNameGenerator::getOrder))
            .orElse(null);
    }

    public FilePathGenerator getPathGenerator(String platform) {
        // 先查找平台特定的
        Map<String, FilePathGenerator> platformGenerators = platformPathGenerators.get(platform);
        if (platformGenerators != null && !platformGenerators.isEmpty()) {
            return platformGenerators.values()
                .stream()
                .max(Comparator.comparingInt(FilePathGenerator::getOrder))
                .orElse(null);
        }

        // 再查找全局的
        return globalPathGenerators.values()
            .stream()
            .max(Comparator.comparingInt(FilePathGenerator::getOrder))
            .orElse(null);
    }

    /**
     * 获取缩略图处理器（平台 > 全局）
     */
    public ThumbnailProcessor getThumbnailProcessor(String platform) {
        // 先查找平台特定的
        Map<String, ThumbnailProcessor> platformProcessors = platformThumbnailProcessors.get(platform);
        if (platformProcessors != null && !platformProcessors.isEmpty()) {
            return platformProcessors.values()
                .stream()
                .max(Comparator.comparingInt(ThumbnailProcessor::getOrder))
                .orElse(null);
        }

        // 再查找全局的
        return globalThumbnailProcessors.values()
            .stream()
            .max(Comparator.comparingInt(ThumbnailProcessor::getOrder))
            .orElse(null);
    }

    /**
     * 获取验证器列表（合并全局和平台）
     */
    public List<FileValidator> getValidators(String platform) {
        List<FileValidator> validators = new ArrayList<>();

        // 先添加全局验证器
        validators.addAll(globalValidators);

        // 再添加平台特定验证器
        List<FileValidator> platformSpecific = platformValidators.get(platform);
        if (platformSpecific != null) {
            validators.addAll(platformSpecific);
        }

        // 按优先级排序（优先级高的在前）
        validators.sort(Comparator.comparingInt(FileValidator::getOrder).reversed());

        return validators;
    }

    /**
     * 获取完成处理器列表（合并全局和平台）
     */
    public List<UploadCompleteProcessor> getCompleteProcessors(String platform) {
        List<UploadCompleteProcessor> processors = new ArrayList<>();

        // 先添加全局处理器
        processors.addAll(globalCompleteProcessors);

        // 再添加平台特定处理器
        List<UploadCompleteProcessor> platformSpecific = platformCompleteProcessors.get(platform);
        if (platformSpecific != null) {
            processors.addAll(platformSpecific);
        }

        // 按优先级排序
        processors.sort(Comparator.comparingInt(UploadCompleteProcessor::getOrder).reversed());

        return processors;
    }
}