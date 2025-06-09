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

package top.continew.starter.cache.redisson.util;

import cn.hutool.extra.spring.SpringUtil;
import org.redisson.api.*;

import java.util.concurrent.TimeUnit;

/**
 * Redis 队列工具类
 *
 * @author Charles7c
 * @since 2.12.1
 */
public class RedisQueueUtils {

    private static final RedissonClient CLIENT = SpringUtil.getBean(RedissonClient.class);

    private RedisQueueUtils() {
    }

    /**
     * 获取 Redisson 客户端实例
     *
     * @return Redisson 客户端实例
     */
    public static RedissonClient getClient() {
        return CLIENT;
    }

    /**
     * 获取阻塞队列实例
     *
     * @param key 键
     * @return 阻塞队列实例
     */
    public static <T> RBlockingQueue<T> getBlockingQueue(String key) {
        return CLIENT.getBlockingQueue(key);
    }

    /**
     * 添加元素到阻塞队列尾部
     * <p>
     * 如果队列已满，则返回 false
     * </p>
     *
     * @param key   键
     * @param value 值
     * @return true：添加成功；false：添加失败
     */
    public static <T> boolean addBlockingQueueData(String key, T value) {
        RBlockingQueue<T> queue = getBlockingQueue(key);
        return queue.offer(value);
    }

    /**
     * 获取并移除阻塞队列头部元素
     * <p>
     * 如果队列为空，则返回 null
     * </p>
     *
     * @param key 键
     * @return 队列头部元素，如果队列为空，则返回 null
     */
    public static <T> T getBlockingQueueData(String key) {
        RBlockingQueue<T> queue = getBlockingQueue(key);
        return queue.poll();
    }

    /**
     * 删除阻塞队列中的指定元素
     *
     * @param key   键
     * @param value 值
     * @return true：删除成功；false：删除失败
     */
    public static <T> boolean removeBlockingQueueData(String key, T value) {
        RBlockingQueue<T> queue = getBlockingQueue(key);
        return queue.remove(value);
    }

    /**
     * 获取延迟队列实例
     *
     * @param key 键
     * @return 延迟队列实例
     */
    public static <T> RDelayedQueue<T> getDelayedQueue(String key) {
        RBlockingQueue<T> blockingQueue = getBlockingQueue(key);
        return CLIENT.getDelayedQueue(blockingQueue);
    }

    /**
     * 添加元素到延迟队列
     *
     * @param key   键
     * @param value 值
     * @param delay 延迟时间
     * @param unit  时间单位
     */
    public static <T> void addDelayedQueueData(String key, T value, long delay, TimeUnit unit) {
        RDelayedQueue<T> delayedQueue = getDelayedQueue(key);
        delayedQueue.offer(value, delay, unit);
    }

    /**
     * 获取并移除延迟队列头部元素
     * <p>
     * 如果队列为空，则返回 null
     * </p>
     *
     * @param key 键
     * @return 队列头部元素，如果队列为空，则返回 null
     */
    public static <T> T getDelayedQueueData(String key) {
        RDelayedQueue<T> delayedQueue = getDelayedQueue(key);
        return delayedQueue.poll();
    }

    /**
     * 移除延迟队列中的指定元素
     *
     * @param key   键
     * @param value 值
     * @return true：移除成功；false：移除失败
     */
    public static <T> boolean removeDelayedQueueData(String key, T value) {
        RDelayedQueue<T> delayedQueue = getDelayedQueue(key);
        return delayedQueue.remove(value);
    }
}
