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

import net.coobird.thumbnailator.Thumbnails;
import top.continew.starter.storage.exception.StorageException;
import top.continew.starter.storage.model.context.UploadContext;
import top.continew.starter.storage.model.req.ThumbnailInfo;
import top.continew.starter.storage.model.req.ThumbnailSize;
import top.continew.starter.storage.prehandle.ThumbnailProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 默认缩略图处理器
 *
 * @author echo
 * @since 2.14.0
 */
public class DefaultThumbnailProcessor implements ThumbnailProcessor {

    @Override
    public String getName() {
        return "defaultThumbnail";
    }

    @Override
    public boolean support(UploadContext context) {
        String contentType = context.getFile().getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @Override
    public ThumbnailInfo process(UploadContext context, InputStream sourceInputStream) {
        try {
            ThumbnailSize size = context.getThumbnailSize();
            BufferedImage thumbnail = Thumbnails.of(sourceInputStream)
                .size(size.getWidth(), size.getHeight())
                .keepAspectRatio(size.isKeepAspectRatio())
                .asBufferedImage();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "jpg", baos);

            ThumbnailInfo info = new ThumbnailInfo();
            info.setData(baos.toByteArray());
            info.setFormat("jpg");
            info.setWidth(thumbnail.getWidth());
            info.setHeight(thumbnail.getHeight());

            return info;
        } catch (Exception e) {
            throw new StorageException("生成缩略图失败", e);
        }
    }
}