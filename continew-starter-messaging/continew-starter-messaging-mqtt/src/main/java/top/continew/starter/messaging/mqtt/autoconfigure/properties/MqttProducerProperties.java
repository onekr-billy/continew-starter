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

package top.continew.starter.messaging.mqtt.autoconfigure.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import top.continew.starter.messaging.mqtt.enums.MqttQoS;

/**
 * 生产者属性
 *
 * @author echo
 * @since 2.15.0
 */
public class MqttProducerProperties {

    /**
     * 默认的消息服务质量等级（QoS, Quality of Service）。
     * 0：最多一次（AT_MOST_ONCE），不保证送达；
     * 1：至少一次（AT_LEAST_ONCE），可能重复；
     * 2：只有一次（EXACTLY_ONCE），确保仅送达一次。
     * 可在发送时指定不同 QoS，此为默认值。
     * 默认值：0（AT_MOST_ONCE）。
     */
    private Integer defaultQos = MqttQoS.AT_MOST_ONCE.value();

    /**
     * MQTT 客户端 ID。
     * 用于标识连接的唯一客户端，在 broker 中必须唯一；
     * 若为空，通常系统会自动生成。
     */
    private String clientId;

    /**
     * 默认发布的 Topic。
     * 当未指定 topic 时使用此 topic 发送消息。
     * 默认值："producer"。
     */
    private String defaultTopic = "producer";

    /**
     * 是否启用异步发送模式。
     * true 表示消息发送不会阻塞当前线程；
     * false 表示同步等待发送完成。
     * 默认值为 false。
     */
    private Boolean async = false;

    /**
     * 是否异步触发发送事件（例如发送回调等）。
     * 仅在 `async = true` 时生效；
     * 设置为 true 可提升事件处理性能。
     * 默认值为 false。
     */
    private Boolean asyncEvents = false;

    /**
     * 是否设置消息为保留（Retained）消息。
     * Retained 消息在发送后 broker 会保留并在新订阅者订阅时立刻推送；
     * 可用于设备初始状态等场景。
     * 默认值为 false（不保留）。
     */
    private Boolean defaultRetained = false;

    /**
     * MQTT 消息处理线程池配置。
     * 包含核心线程数、最大线程数、队列容量等，控制消费者消息处理能力。
     * 默认配置为 new MqttExecutorProperties()。
     */
    @NestedConfigurationProperty
    private MqttExecutorProperties executor = new MqttExecutorProperties();

    public Integer getDefaultQos() {
        return defaultQos;
    }

    public void setDefaultQos(Integer defaultQos) {
        this.defaultQos = defaultQos;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDefaultTopic() {
        return defaultTopic;
    }

    public void setDefaultTopic(String defaultTopic) {
        this.defaultTopic = defaultTopic;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public Boolean getAsyncEvents() {
        return asyncEvents;
    }

    public void setAsyncEvents(Boolean asyncEvents) {
        this.asyncEvents = asyncEvents;
    }

    public Boolean getDefaultRetained() {
        return defaultRetained;
    }

    public void setDefaultRetained(Boolean defaultRetained) {
        this.defaultRetained = defaultRetained;
    }

    public MqttExecutorProperties getExecutor() {
        return executor;
    }

    public void setExecutor(MqttExecutorProperties executor) {
        this.executor = executor;
    }
}
