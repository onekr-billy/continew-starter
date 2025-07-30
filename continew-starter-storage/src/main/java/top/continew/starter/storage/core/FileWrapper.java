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
import top.continew.starter.json.jackson.util.JSONUtils;
import top.continew.starter.storage.exception.StorageException;
import top.continew.starter.storage.model.req.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 文件包装器，用于统一处理不同类型的输入
 *
 * @author echo
 * @since 2.14.0
 */
public class FileWrapper {

    private MultipartFile multipartFile;
    private byte[] bytes;
    private InputStream inputStream;
    private String originalFilename;
    private String contentType;
    private long size;

    private FileWrapper() {
    }

    /**
     * 从 MultipartFile 创建
     */
    public static FileWrapper of(MultipartFile file) {
        FileWrapper wrapper = new FileWrapper();
        wrapper.multipartFile = file;
        wrapper.originalFilename = file.getOriginalFilename();
        wrapper.contentType = file.getContentType();
        wrapper.size = file.getSize();
        return wrapper;
    }

    /**
     * 从 byte[] 创建
     */
    public static FileWrapper of(byte[] bytes, String filename, String contentType) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new StorageException("文件名不能为空");
        }
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new StorageException("文件类型不能为空");
        }

        FileWrapper wrapper = new FileWrapper();
        wrapper.bytes = bytes;
        wrapper.originalFilename = filename;
        wrapper.contentType = contentType;
        wrapper.size = bytes.length;
        return wrapper;
    }

    /**
     * 从 InputStream 创建
     */
    public static FileWrapper of(InputStream inputStream, String filename, String contentType) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new StorageException("文件名不能为空");
        }
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new StorageException("文件类型不能为空");
        }

        FileWrapper wrapper = new FileWrapper();
        wrapper.inputStream = inputStream;
        wrapper.originalFilename = filename;
        wrapper.contentType = contentType;
        wrapper.size = -1;
        return wrapper;
    }

    /**
     * 从 Object 创建（智能识别）
     */
    public static FileWrapper of(Object obj) {
        return of(obj, null, null);
    }

    /**
     * 从 Object 创建，可指定文件名和类型
     */
    public static FileWrapper of(Object obj, String filename, String contentType) {
        if (obj == null) {
            throw new StorageException("对象不能为空");
        }

        // 如果是 MultipartFile，直接处理
        if (obj instanceof MultipartFile) {
            return of((MultipartFile)obj);
        }

        // 如果是 byte[]
        if (obj instanceof byte[]) {
            if (filename == null || contentType == null) {
                throw new StorageException("byte[] 类型必须指定文件名和文件类型");
            }
            return of((byte[])obj, filename, contentType);
        }

        // 如果是 InputStream
        if (obj instanceof InputStream) {
            if (filename == null || contentType == null) {
                throw new StorageException("InputStream 类型必须指定文件名和文件类型");
            }
            return of((InputStream)obj, filename, contentType);
        }

        // 其他对象，转换为 JSON
        String json = convertToJson(obj);
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        String finalFilename = filename != null ? filename : "data.json";
        String finalContentType = contentType != null ? contentType : "application/json";

        return of(jsonBytes, finalFilename, finalContentType);
    }

    /**
     * 转换为 MultipartFile
     */
    public MultipartFile toMultipartFile() {
        if (multipartFile != null) {
            return multipartFile;
        }

        if (bytes != null) {
            return new MockMultipartFile(getFilenameWithoutExtension(originalFilename), originalFilename, contentType, bytes);
        }

        if (inputStream != null) {
            try {
                byte[] data = inputStream.readAllBytes();
                return new MockMultipartFile(getFilenameWithoutExtension(originalFilename), originalFilename, contentType, data);
            } catch (IOException e) {
                throw new StorageException("读取输入流失败", e);
            }
        }

        throw new IllegalStateException("无法转换为 MultipartFile");
    }

    private static String getFilenameWithoutExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(0, lastDotIndex) : filename;
    }

    private static String convertToJson(Object obj) {
        try {
            return JSONUtils.toJsonStr(obj);
        } catch (Exception e) {
            throw new StorageException("对象转换为 JSON 失败", e);
        }
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }
}