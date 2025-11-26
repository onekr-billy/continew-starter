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

package top.continew.starter.messaging.mqtt.exception;

import top.continew.starter.core.exception.BaseException;

import java.io.Serial;

/**
 * mqtt异常
 *
 * @author echo
 * @since 2.15.0
 */
public class MqttException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MqttException() {
    }

    public MqttException(String message) {
        super(message);
    }

    public MqttException(Throwable cause) {
        super(cause);
    }

    public MqttException(String message, Throwable cause) {
        super(message, cause);
    }
}
