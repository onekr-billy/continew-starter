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

import top.continew.starter.messaging.mqtt.enums.MqttQoS;

/**
 * 遗嘱消息属性
 *
 * @author echo
 * @since 2.15.0
 */
public class MqttWillProperties {

    /**
     * 遗嘱消息的目标主题。
     * 当客户端异常断开时，将向该主题发布遗嘱消息。
     */
    private String topic;

    /**
     * 遗嘱消息内容
     */
    private String payload;

    /**
     * 遗嘱消息的 QoS 等级
     * 0：最多一次；1：至少一次；2：只有一次
     * 默认值：0
     */
    private Integer qos = MqttQoS.AT_MOST_ONCE.value();

    /**
     * 是否设置为保留消息
     * true：新订阅者会立即收到该消息
     * 默认值：false
     */
    private Boolean retained = false;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Integer getQos() {
        return qos;
    }

    public void setQos(Integer qos) {
        this.qos = qos;
    }

    public Boolean getRetained() {
        return retained;
    }

    public void setRetained(Boolean retained) {
        this.retained = retained;
    }
}
