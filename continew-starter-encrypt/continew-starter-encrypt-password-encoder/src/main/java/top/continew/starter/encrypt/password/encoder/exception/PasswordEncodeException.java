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

package top.continew.starter.encrypt.password.encoder.exception;

import top.continew.starter.core.exception.BaseException;

import java.io.Serial;

/**
 * 密码编码异常
 *
 * @author Charles7c
 * @since 2.13.3
 */
public class PasswordEncodeException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public PasswordEncodeException() {
    }

    public PasswordEncodeException(String message) {
        super(message);
    }

    public PasswordEncodeException(Throwable cause) {
        super(cause);
    }

    public PasswordEncodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
