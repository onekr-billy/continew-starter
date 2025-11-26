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

package top.continew.starter.messaging.mqtt.strategy;

import java.util.Collection;
import java.util.List;

/**
 * MQTT 客户端操作接口
 * <p>
 * 提供 topic 管理与消息发布能力。
 * 实现类可基于不同 MQTT 客户端或通信方式进行扩展。
 * </p>
 *
 * @author echo
 * @since 2.15.0
 */
public interface MqttOptions {

    /**
     * 添加topic
     *
     * @param topic topic
     */
    void addTopic(String topic);

    /**
     * 添加topic
     *
     * @param topic topic
     * @param qos   qos
     */
    void addTopic(String topic, int qos);

    /**
     * 删除topic
     *
     * @param topic topic
     */
    void removeTopic(String topic);

    /**
     * 查询订阅的所有topic
     *
     * @return topic list
     */
    List<String> listTopics();

    /**
     * 批量添加订阅主题（默认 QoS）
     *
     * @param topics 订阅的主题列表
     */
    void addTopics(String... topics);

    /**
     * 批量添加订阅主题，指定统一 QoS 等级
     *
     * @param qos    消息服务质量等级（0/1/2）
     * @param topics 订阅的主题列表
     */
    void addTopics(int qos, String... topics);

    /**
     * 批量添加订阅主题（默认 QoS）
     *
     * @param topics 订阅的主题集合（List 形式）
     */
    void addTopics(List<String> topics);

    /**
     * 批量添加订阅主题，指定统一 QoS 等级
     *
     * @param topics 订阅的主题集合（List 形式）
     * @param qos    消息服务质量等级（0/1/2）
     */
    void addTopics(List<String> topics, int qos);

    /**
     * 批量取消订阅主题
     *
     * @param topics 要取消订阅的主题列表
     */
    void removeTopics(String... topics);

    /**
     * 批量取消订阅主题
     *
     * @param topics 要取消订阅的主题集合（List 形式）
     */
    void removeTopics(List<String> topics);

    /**
     * 批量取消订阅主题
     *
     * @param topics 要取消订阅的主题集合（Collection 形式）
     */
    void removeTopics(Collection<String> topics);

    /**
     * 判断是否已订阅指定主题
     *
     * @param topic 主题名称
     * @return 如果已订阅该主题，返回 true；否则返回 false
     */
    boolean containsTopic(String topic);

    /**
     * 清空所有已订阅的主题
     */
    void clearAllTopics();

    /**
     * 获取当前已订阅的主题数量
     *
     * @return 已订阅的主题数量
     */
    int getTopicCount();
}
