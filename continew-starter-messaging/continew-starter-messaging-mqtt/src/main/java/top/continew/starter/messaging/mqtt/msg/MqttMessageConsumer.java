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

package top.continew.starter.messaging.mqtt.msg;

import top.continew.starter.messaging.mqtt.handler.MqttMessageInboundHandler;
import top.continew.starter.messaging.mqtt.model.MqttMessage;

/**
 * 消息监听 - 消费者
 *
 * @author echo
 * @since 2.15.0
 */
public interface MqttMessageConsumer {

    /**
     * 消息订阅
     *
     * @param message {@link MqttMessage}
     * @see MqttMessageInboundHandler
     */
    void onMessage(MqttMessage message);
}
