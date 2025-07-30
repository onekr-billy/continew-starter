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

import org.springframework.web.multipart.MultipartFile;
import top.continew.starter.storage.model.context.UploadContext;
import top.continew.starter.storage.model.req.MockMultipartFile;
import top.continew.starter.storage.model.req.ThumbnailInfo;
import top.continew.starter.storage.model.req.ThumbnailSize;
import top.continew.starter.storage.model.resp.FileInfo;
import top.continew.starter.storage.prehandle.*;
import top.continew.starter.storage.util.StorageUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传预处理器，支持链式调用
 *
 * @author echo
 * @since 2.14.0
 */
public class UploadPretreatment {

    private final FileStorageService storageService;
    private final UploadContext context;
    private final List<FileValidator> validators = new ArrayList<>();
    private FileNameGenerator nameGenerator;
    private FilePathGenerator pathGenerator;
    private ThumbnailProcessor thumbnailProcessor;
    private final List<UploadCompleteProcessor> completeProcessors = new ArrayList<>();

    public UploadPretreatment(FileStorageService storageService, MultipartFile file) {
        this.storageService = storageService;
        this.context = new UploadContext();
        this.context.setFile(file);
        // 设置默认平台
        this.context.setPlatform(storageService.getDefaultPlatform());
    }

    /**
     * 设置存储平台
     *
     * @param platform 站台
     * @return {@link UploadPretreatment }
     */
    public UploadPretreatment setPlatform(String platform) {
        context.setPlatform(platform);
        return this;
    }

    /**
     * 设置桶
     *
     * @param bucket 桶
     * @return {@link UploadPretreatment }
     */
    public UploadPretreatment setBucket(String bucket) {
        context.setBucket(bucket);
        return this;
    }

    /**
     * 设置路径
     */
    public UploadPretreatment setPath(String path) {
        context.setPath(path);
        return this;
    }

    /**
     * 设置文件名
     */
    public UploadPretreatment setFileName(String fileName) {
        context.setFileName(fileName);
        return this;
    }

    /**
     * 添加元数据
     */
    public UploadPretreatment addMetadata(String key, String value) {
        context.getMetadata().put(key, value);
        return this;
    }

    /**
     * 添加扩展属性
     */
    public UploadPretreatment addAttribute(String key, Object value) {
        context.getAttributes().put(key, value);
        return this;
    }

    /**
     * 启用缩略图
     */
    public UploadPretreatment enableThumbnail(int width, int height) {
        context.setGenerateThumbnail(true);
        context.setThumbnailSize(new ThumbnailSize(width, height));
        return this;
    }

    /**
     * 添加验证器
     */
    public UploadPretreatment addValidator(FileValidator validator) {
        validators.add(validator);
        return this;
    }

    /**
     * 设置文件名生成器
     */
    public UploadPretreatment setNameGenerator(FileNameGenerator generator) {
        this.nameGenerator = generator;
        return this;
    }

    public UploadPretreatment setPathGenerator(FilePathGenerator generator) {
        this.pathGenerator = generator;
        return this;
    }

    /**
     * 设置缩略图处理器
     */
    public UploadPretreatment setThumbnailProcessor(ThumbnailProcessor processor) {
        this.thumbnailProcessor = processor;
        return this;
    }

    /**
     * 添加上传完成处理器
     */
    public UploadPretreatment addCompleteProcessor(UploadCompleteProcessor processor) {
        completeProcessors.add(processor);
        return this;
    }

    /**
     * 执行上传
     */
    public FileInfo upload() {
        // 应用处理器
        applyProcessors();

        // 执行验证
        validate();

        // 生成默认存储桶（如果未设置）
        if (context.getBucket() == null || context.getBucket().trim().isEmpty()) {
            context.setBucket(generateDefaultBucket());
        }

        // 生成文件名
        if (context.getFileName() == null) {
            context.setFileName(generateFileName());
        }

        // 生成文件路径
        if (context.getPath() == null) {
            context.setPath(generateFilePath());
        }

        // 执行上传
        FileInfo fileInfo = storageService.doUpload(context);

        // 处理缩略图
        if (context.isGenerateThumbnail()) {
            processThumbnail(fileInfo);
        }

        // 触发完成事件
        triggerCompleteEvent(fileInfo);

        return fileInfo;
    }

    /**
     * 应用处理器
     */
    private void applyProcessors() {
        // 从存储服务获取全局处理器
        ProcessorRegistry registry = storageService.getProcessorRegistry();

        // 合并处理器：自定义 > 平台 > 全局
        if (nameGenerator == null) {
            nameGenerator = registry.getNameGenerator(context.getPlatform());
        }

        if (pathGenerator == null) {
            pathGenerator = registry.getPathGenerator(context.getPlatform());
        }

        if (thumbnailProcessor == null && context.isGenerateThumbnail()) {
            thumbnailProcessor = registry.getThumbnailProcessor(context.getPlatform());
        }

        // 合并验证器
        validators.addAll(0, registry.getValidators(context.getPlatform()));

        // 合并完成处理器
        completeProcessors.addAll(0, registry.getCompleteProcessors(context.getPlatform()));
    }

    /**
     * 执行验证
     */
    private void validate() {
        for (FileValidator validator : validators) {
            if (validator.support(context)) {
                validator.validate(context);
            }
        }
    }

    /**
     * 生成文件名
     */
    private String generateFileName() {
        if (nameGenerator != null && nameGenerator.support(context)) {
            return nameGenerator.generate(context);
        }
        return StorageUtils.generateFileName(context.getFile().getOriginalFilename());
    }

    private String generateFilePath() {
        if (pathGenerator != null && pathGenerator.support(context)) {
            return pathGenerator.path(context);
        }
        // 默认使用时间戳
        return StorageUtils.generatePath();
    }

    /**
     * 生成默认存储桶
     */
    private String generateDefaultBucket() {
        return storageService.getDefaultBucket(context.getPlatform());
    }

    /**
     * 处理缩略图
     *
     * @param fileInfo 文件信息
     */
    private void processThumbnail(FileInfo fileInfo) {
        if (thumbnailProcessor != null && thumbnailProcessor.support(context)) {
            try (InputStream is = storageService.download(context.getPlatform(), fileInfo.getPath())) {
                ThumbnailInfo thumbnailInfo = thumbnailProcessor.process(context, is);
                // 上传缩略图
                String thumbnailPath = fileInfo.getPath() + "_thumb." + thumbnailInfo.getFormat();
                // 创建模拟的文件信息
                MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnail", "thumbnail." + thumbnailInfo
                    .getFormat(), "image/" + thumbnailInfo.getFormat(), thumbnailInfo.getData());

                storageService.upload(context.getPlatform(), context.getBucket(), thumbnailPath, thumbnailFile);
                fileInfo.setThumbnailPath(thumbnailPath);
            } catch (Exception e) {

            }
        }
    }

    /**
     * 触发完成事件
     */
    private void triggerCompleteEvent(FileInfo fileInfo) {
        for (UploadCompleteProcessor processor : completeProcessors) {
            if (processor.support(context)) {
                try {
                    processor.onComplete(fileInfo);
                } catch (Exception e) {
                }
            }
        }
    }

}