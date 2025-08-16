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

package top.continew.starter.storage.service;

import top.continew.starter.storage.domain.model.resp.FileInfo;
import top.continew.starter.storage.domain.model.resp.FilePartInfo;

import java.util.List;

/**
 * 文件记录器接口，用于保存文件上传记录
 *
 * @author echo
 * @since 2.14.0
 */
public interface FileRecorder {

    /**
     * 保存文件记录
     * 
     * @param fileInfo 文件信息
     * @return 是否保存成功
     */
    boolean save(FileInfo fileInfo);

    /**
     * 更新文件记录
     * 
     * @param fileInfo 文件信息
     * @return 是否更新成功
     */
    boolean update(FileInfo fileInfo);

    /**
     * 删除文件记录
     * 
     * @param platform 存储平台
     * @param path     文件路径
     * @return 是否删除成功
     */
    boolean delete(String platform, String path);

    /**
     * 保存文件分片信息
     * 
     * @param filePartInfo 文件分片信息
     */
    void saveFilePart(FilePartInfo filePartInfo);

    /**
     * 获取文件所有分片信息
     * 
     * @param fileId 文件ID
     * @return 分片信息列表
     */
    List<FilePartInfo> getFileParts(String fileId);

    /**
     * 删除文件分片信息
     * 
     * @param fileId 文件ID
     */
    void deleteFileParts(String fileId);
}