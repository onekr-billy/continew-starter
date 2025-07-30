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
import top.continew.starter.storage.autoconfigure.properties.StorageProperties;
import top.continew.starter.storage.exception.StorageException;
import top.continew.starter.storage.model.context.UploadContext;
import top.continew.starter.storage.model.resp.*;
import top.continew.starter.storage.router.StorageStrategyRouter;
import top.continew.starter.storage.service.FileRecorder;
import top.continew.starter.storage.strategy.StorageStrategy;
import top.continew.starter.storage.strategy.impl.LocalStorageStrategy;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 增强版文件存储服务
 * 支持链式调用和更多功能
 *
 * @author echo
 * @since 2.14.0
 */
public class FileStorageService {

    private final StorageStrategyRouter router;
    private final StorageProperties storageProperties;
    private final ProcessorRegistry processorRegistry;
    private final StrategyProxyFactory proxyFactory;
    private final FileRecorder fileRecorder;

    public FileStorageService(StorageStrategyRouter router,
                              StorageProperties storageProperties,
                              ProcessorRegistry processorRegistry,
                              StrategyProxyFactory proxyFactory,
                              FileRecorder fileRecorder) {
        this.router = router;
        this.storageProperties = storageProperties;
        this.processorRegistry = processorRegistry;
        this.proxyFactory = proxyFactory;
        this.fileRecorder = fileRecorder;
    }

    /**
     * 获取默认存储平台
     */
    public String getDefaultPlatform() {
        return storageProperties.getDefaultPlatform();
    }

    /**
     * 获取处理器注册表
     */
    public ProcessorRegistry getProcessorRegistry() {
        return processorRegistry;
    }

    /**
     * 创建上传预处理器（链式调用入口）
     */
    public UploadPretreatment of(MultipartFile file) {
        return new UploadPretreatment(this, file);
    }

    /**
     * 创建上传预处理器，指定平台
     */
    public UploadPretreatment of(MultipartFile file, String platform) {
        return new UploadPretreatment(this, file).setPlatform(platform);
    }

    /**
     * 创建上传预处理器（支持 byte[]）
     */
    public UploadPretreatment of(byte[] bytes, String filename, String contentType) {
        FileWrapper wrapper = FileWrapper.of(bytes, filename, contentType);
        return new UploadPretreatment(this, wrapper.toMultipartFile());
    }

    /**
     * 创建上传预处理器（支持 InputStream）
     */
    public UploadPretreatment of(InputStream inputStream, String filename, String contentType) {
        FileWrapper wrapper = FileWrapper.of(inputStream, filename, contentType);
        return new UploadPretreatment(this, wrapper.toMultipartFile());
    }

    /**
     * 创建上传预处理器（支持任意对象）
     */
    public UploadPretreatment of(Object obj) {
        FileWrapper wrapper = FileWrapper.of(obj);
        return new UploadPretreatment(this, wrapper.toMultipartFile());
    }

    /**
     * 创建上传预处理器（支持任意对象，指定文件名和类型）
     */
    public UploadPretreatment of(Object obj, String filename, String contentType) {
        FileWrapper wrapper = FileWrapper.of(obj, filename, contentType);
        return new UploadPretreatment(this, wrapper.toMultipartFile());
    }

    /**
     * 执行上传（内部方法）
     */
    public FileInfo doUpload(UploadContext context) {
        StorageStrategy strategy = getStrategy(context.getPlatform());

        // 执行上传
        strategy.upload(context.getBucket(), context.getFullPath(), context.getFile());

        // 构建文件信息
        FileInfo fileInfo = strategy.getFileInfo(context.getBucket(), context.getFullPath());
        fileInfo.setOriginalFileName(context.getFile().getOriginalFilename());
        fileInfo.getMetadata().putAll(context.getMetadata());

        // 保存文件记录
        if (fileRecorder != null) {
            fileRecorder.save(fileInfo);
        }

        return fileInfo;
    }

    /**
     * 初始化分片上传
     *
     * @param bucket      存储桶
     * @param platform    平台
     * @param path        路径
     * @param contentType 内容类型
     * @param metadata    元数据
     * @return {@link MultipartInitResp }
     */
    public MultipartInitResp initMultipartUpload(String bucket,
                                                 String platform,
                                                 String path,
                                                 String contentType,
                                                 Map<String, String> metadata) {
        bucket = bucket == null ? getDefaultBucket(platform) : bucket;
        StorageStrategy strategy = getStrategy(platform);
        MultipartInitResp result = strategy.initMultipartUpload(bucket, path, contentType, metadata);

        // 记录文件信息
        if (fileRecorder != null) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileId(result.getFileId());
            fileInfo.setPlatform(platform);
            fileInfo.setBucket(bucket);
            fileInfo.setPath(path);
            fileInfo.setContentType(contentType);
            fileInfo.setMetadata(metadata != null ? new HashMap<>(metadata) : new HashMap<>());
            fileInfo.getMetadata().put("uploadId", result.getUploadId());
            fileInfo.getMetadata().put("status", "UPLOADING");
            fileRecorder.save(fileInfo);
        }
        return result;
    }

    /**
     * 上传分片
     *
     * @param platform   平台
     * @param bucket     存储桶
     * @param path       路径
     * @param uploadId   上传id
     * @param partNumber 分片编号
     * @param data       数据
     * @return {@link MultipartUploadResp }
     */
    public MultipartUploadResp uploadPart(String platform,
                                          String bucket,
                                          String path,
                                          String uploadId,
                                          int partNumber,
                                          InputStream data) {
        StorageStrategy strategy = getStrategy(platform);
        MultipartUploadResp result = strategy.uploadPart(bucket, path, uploadId, partNumber, data);

        // 记录分片信息
        if (fileRecorder != null && result.isSuccess()) {
            FilePartInfo partInfo = new FilePartInfo();
            partInfo.setUploadId(uploadId);
            partInfo.setBucket(bucket);
            partInfo.setPath(path);
            partInfo.setPartNumber(partNumber);
            partInfo.setPartETag(result.getPartETag());
            partInfo.setPartSize(result.getPartSize());
            partInfo.setStatus("SUCCESS");
            partInfo.setUploadTime(LocalDateTime.now());
            fileRecorder.saveFilePart(partInfo);
        }

        return result;
    }

    /**
     * 完成分片上传
     *
     * @param platform    平台
     * @param bucket      存储桶
     * @param path        路径
     * @param uploadId    上传id
     * @param clientParts 分片信息
     * @return {@link FileInfo }
     */
    public FileInfo completeMultipartUpload(String platform,
                                            String bucket,
                                            String path,
                                            String uploadId,
                                            List<MultipartUploadResp> clientParts) {
        // 从 FileRecorder 获取所有分片信息
        List<FilePartInfo> recordedParts = fileRecorder != null
            ? fileRecorder.getFileParts(uploadId)
            : new ArrayList<>();

        // 转换为 MultipartUploadResp
        List<MultipartUploadResp> parts = recordedParts.stream().map(partInfo -> {
            MultipartUploadResp resp = new MultipartUploadResp();
            resp.setPartNumber(partInfo.getPartNumber());
            resp.setPartETag(partInfo.getPartETag());
            resp.setPartSize(partInfo.getPartSize());
            resp.setSuccess("SUCCESS".equals(partInfo.getStatus()));
            return resp;
        }).collect(Collectors.toList());

        // 如果没有记录，使用客户端传入的分片信息
        if (parts.isEmpty() && clientParts != null) {
            parts = clientParts;
        }

        // 验证分片完整性
        validatePartsCompleteness(parts);

        // 获取策略，判断是否需要验证
        boolean needVerify = true;
        StorageStrategy strategy = getStrategy(platform);
        if (strategy instanceof LocalStorageStrategy) {
            needVerify = false;
        }

        // 完成上传
        FileInfo fileInfo = strategy.completeMultipartUpload(bucket, path, uploadId, parts, needVerify);

        // 更新文件记录
        if (fileRecorder != null) {
            fileInfo.getMetadata().put("uploadId", uploadId);
            fileInfo.getMetadata().put("status", "COMPLETED");
            fileRecorder.update(fileInfo);

            // 删除分片记录
            fileRecorder.deleteFileParts(uploadId);
        }

        return fileInfo;
    }

    /**
     * 取消分片上传
     *
     * @param platform 平台
     * @param bucket   存储桶
     * @param path     路径
     * @param uploadId 上传id
     */
    public void abortMultipartUpload(String platform, String bucket, String path, String uploadId) {
        StorageStrategy strategy = getStrategy(platform);
        strategy.abortMultipartUpload(bucket, path, uploadId);

        // 删除相关记录
        if (fileRecorder != null) {
            fileRecorder.deleteFileParts(uploadId);
        }
    }

    /**
     * 验证分片完整性
     *
     * @param parts 分片信息
     */
    private void validatePartsCompleteness(List<MultipartUploadResp> parts) {
        if (parts.isEmpty()) {
            throw new StorageException("没有找到任何分片信息");
        }

        // 检查分片编号连续性
        List<Integer> partNumbers = parts.stream().map(MultipartUploadResp::getPartNumber).sorted().toList();

        for (int i = 0; i < partNumbers.size(); i++) {
            if (partNumbers.get(i) != i + 1) {
                throw new StorageException("分片编号不连续，缺失分片: " + (i + 1));
            }
        }

        // 检查是否所有分片都成功
        List<Integer> failedParts = parts.stream()
            .filter(part -> !part.isSuccess())
            .map(MultipartUploadResp::getPartNumber)
            .collect(Collectors.toList());

        if (!failedParts.isEmpty()) {
            throw new StorageException("存在失败的分片: " + failedParts);
        }
    }

    /**
     * 列出已上传的分片
     *
     * @param platform 平台
     * @param bucket   存储桶
     * @param path     路径
     * @param uploadId 上传id
     * @return {@link List }<{@link MultipartUploadResp }>
     */
    public List<MultipartUploadResp> listParts(String platform, String bucket, String path, String uploadId) {
        StorageStrategy strategy = router.route(platform);
        return strategy.listParts(bucket, path, uploadId);
    }

    /**
     * 获取存储策略（应用代理）
     */
    private StorageStrategy getStrategy(String platform) {
        StorageStrategy strategy = router.route(platform);
        return proxyFactory.createProxy(strategy);
    }

    /**
     * 获取默认存储桶
     *
     * @param platform 站台
     * @return {@link String }
     */
    public String getDefaultBucket(String platform) {
        return router.route(platform).defaultBucket();
    }

    /**
     * 上传
     *
     * @param platform 平台
     * @param bucket   铲斗
     * @param path     路径
     * @param file     文件
     */
    public void upload(String platform, String bucket, String path, MultipartFile file) {
        router.route(platform).upload(path, bucket, file);
    }

    /**
     * 下载文件
     */
    public InputStream download(String platform, String bucket, String path) {
        return router.route(platform).download(bucket, path);
    }

    /**
     * 使用默认存储下载
     */
    public InputStream download(String bucket, String path) {
        return download(storageProperties.getDefaultPlatform(), bucket, path);
    }

    /**
     * 批量下载
     */
    public InputStream batchDownload(String platform, String bucket, List<String> paths) {
        return router.route(platform).batchDownload(bucket, paths);
    }

    /**
     * 删除文件
     */
    public void delete(String platform, String bucket, String path) {
        router.route(platform).delete(bucket, path);
    }

    /**
     * 删除文件
     *
     * @param info 信息
     */
    public void delete(FileInfo info) {
        router.route(info.getPlatform()).delete(info.getBucket(), info.getFullPath());
    }

    /**
     * 批量删除
     */
    public void batchDelete(String platform, String bucket, List<String> paths) {
        router.route(platform).batchDelete(bucket, paths);
    }

    /**
     * 检查文件是否存在
     */
    public boolean exists(String platform, String bucket, String path) {
        return router.route(platform).exists(bucket, path);
    }

    /**
     * 获取文件信息
     */
    public FileInfo getFileInfo(String platform, String bucket, String path) {
        return router.route(platform).getFileInfo(bucket, path);
    }

    /**
     * 列出文件
     */
    public List<FileInfo> list(String platform, String bucket, String prefix, int maxKeys) {
        return router.route(platform).list(bucket, prefix, maxKeys);
    }

    /**
     * 复制文件
     */
    public void copy(String platform, String sourceBucket, String targetBucket, String sourcePath, String targetPath) {
        router.route(platform).copy(sourceBucket, targetBucket, sourcePath, targetPath);
    }

    /**
     * 移动文件
     */
    public void move(String platform, String sourceBucket, String targetBucket, String sourcePath, String targetPath) {
        router.route(platform).move(sourceBucket, targetBucket, sourcePath, targetPath);
    }

    /**
     * 生成预签名URL
     */
    public String generatePresignedUrl(String platform, String bucket, String path, long expireSeconds) {
        return router.route(platform).generatePresignedUrl(bucket, path, expireSeconds);
    }

    /**
     * 动态注册存储策略
     */
    public <T extends StorageStrategy> void register(T strategy) {
        router.registerDynamic(strategy);
    }

    /**
     * 卸载动态注册的策略
     */
    public boolean unload(String platform) {
        if (!router.isDynamic(platform)) {
            throw new StorageException("只能卸载动态注册的策略: " + platform);
        }
        return router.unloadDynamic(platform);
    }

    /**
     * 获取所有可用策略代码
     */
    public Set<String> getAvailablePlatform() {
        return router.getAllPlatform();
    }

    /**
     * 检查策略是否存在
     */
    public boolean exists(String platform) {
        return router.getAllPlatform().contains(platform);
    }

    /**
     * 检查是否为动态注册的策略
     */
    public boolean isDynamic(String platform) {
        return router.isDynamic(platform);
    }

    /**
     * 检查是否为配置文件策略
     */
    public boolean isFromConfig(String platform) {
        return router.isFromConfig(platform);
    }

    /**
     * 获取策略详细信息
     */
    public Map<String, StrategyStatus> getStrategyStatus() {
        return router.getFullStrategyStatus();
    }

    /**
     * 获取当前生效的策略信息
     */
    public Map<String, String> getActiveStrategyInfo() {
        return router.getActiveStrategyInfo();
    }
}