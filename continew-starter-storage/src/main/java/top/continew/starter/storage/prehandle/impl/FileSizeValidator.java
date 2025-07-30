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

package top.continew.starter.storage.prehandle.impl;

import cn.hutool.core.io.FileUtil;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import top.continew.starter.storage.exception.StorageException;
import top.continew.starter.storage.model.context.UploadContext;
import top.continew.starter.storage.prehandle.FileValidator;

/**
 * 文件大小验证器
 *
 * @author echo
 * @since 2.14.0
 */
public class FileSizeValidator implements FileValidator {

    private final long maxSize;

    public FileSizeValidator(MultipartProperties multipartProperties) {
        this.maxSize = multipartProperties.getMaxFileSize().toBytes();
    }

    public FileSizeValidator() {
        // 提供默认大小 10MB
        this(10 * 1024 * 1024L);
    }

    public FileSizeValidator(long maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public String getName() {
        return "fileSize";
    }

    @Override
    public boolean support(UploadContext context) {
        return true;
    }

    @Override
    public void validate(UploadContext context) throws StorageException {
        long fileSize = context.getFile().getSize();
        if (fileSize > maxSize) {
            throw new StorageException(String.format("文件大小超过限制: %s (当前: %s)", FileUtil
                .readableFileSize(maxSize), FileUtil.readableFileSize(fileSize)));
        }
    }

    /**
     * 创建默认的文件大小验证器（10MB）
     */
    public FileSizeValidator create() {
        return new FileSizeValidator(maxSize);
    }

    /**
     * 创建指定大小的验证器
     */
    public static FileSizeValidator maxSize(long bytes) {
        return new FileSizeValidator(bytes);
    }

    /**
     * 创建指定KB大小的验证器
     */
    public static FileSizeValidator maxKB(long kb) {
        return new FileSizeValidator(kb * 1024);
    }

    /**
     * 创建指定MB大小的验证器
     */
    public static FileSizeValidator maxMB(long mb) {
        return new FileSizeValidator(mb * 1024 * 1024);
    }

    /**
     * 创建指定GB大小的验证器
     */
    public static FileSizeValidator maxGB(long gb) {
        return new FileSizeValidator(gb * 1024L * 1024 * 1024);
    }
}
