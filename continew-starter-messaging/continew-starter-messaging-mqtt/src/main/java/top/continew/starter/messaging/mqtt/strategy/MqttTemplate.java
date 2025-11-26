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

import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import top.continew.starter.messaging.mqtt.enums.MqttQoS;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * MQTT 客户端发布+订阅封装器
 *
 * @author echo
 * @since 2.15.0
 **/
@SuppressWarnings("ClassCanBeRecord")
public class MqttTemplate implements MqttOptions {

    private final MqttPahoMessageDrivenChannelAdapter adapter;

    public MqttTemplate(MqttPahoMessageDrivenChannelAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void addTopic(String topic) {
        this.addTopic(topic, MqttQoS.AT_MOST_ONCE.value());
    }

    @Override
    public void addTopic(String topic, int qos) {
        Set<String> topics = Set.of(adapter.getTopic());
        if (topics.contains(topic)) {
            return;
        }
        adapter.addTopic(topic, qos);
    }

    @Override
    public void removeTopic(String topic) {
        Set<String> topics = Set.of(adapter.getTopic());
        if (!topics.contains(topic)) {
            return;
        }
        adapter.removeTopic(topic);
    }

    @Override
    public List<String> listTopics() {
        return List.of(adapter.getTopic());
    }

    @Override
    public void addTopics(String... topics) {
        addTopics(MqttQoS.AT_MOST_ONCE.value(), topics);
    }

    @Override
    public void addTopics(int qos, String... topics) {
        if (topics == null) {
            return;
        }
        for (String topic : topics) {
            addTopic(topic, qos);
        }
    }

    @Override
    public void addTopics(List<String> topics) {
        addTopics(topics, MqttQoS.AT_MOST_ONCE.value());
    }

    @Override
    public void addTopics(List<String> topics, int qos) {
        if (topics == null || topics.isEmpty()) {
            return;
        }
        topics.forEach(topic -> addTopic(topic, qos));
    }

    @Override
    public void removeTopics(String... topics) {
        if (topics == null) {
            return;
        }
        for (String topic : topics) {
            removeTopic(topic);
        }
    }

    @Override
    public void removeTopics(List<String> topics) {
        if (topics == null || topics.isEmpty()) {
            return;
        }
        topics.forEach(this::removeTopic);
    }

    @Override
    public void removeTopics(Collection<String> topics) {
        if (topics == null || topics.isEmpty()) {
            return;
        }
        topics.forEach(this::removeTopic);
    }

    @Override
    public boolean containsTopic(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            return false;
        }
        Set<String> topics = Set.of(adapter.getTopic());
        return topics.contains(topic);
    }

    @Override
    public void clearAllTopics() {
        List<String> currentTopics = listTopics();
        currentTopics.forEach(this::removeTopic);
    }

    @Override
    public int getTopicCount() {
        return listTopics().size();
    }
}
