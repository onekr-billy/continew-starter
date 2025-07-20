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

package top.continew.starter.extension.datapermission.model;

import java.util.Collections;
import java.util.Set;

/**
 * 用户数据
 *
 * @author Charles7c
 * @since 1.1.0
 */
public class UserData {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 角色列表
     */
    private Set<RoleData> roles = Collections.emptySet();

    /**
     * 部门 ID
     */
    private Long deptId;

    public UserData() {
    }

    public UserData(Long userId, Long deptId, Set<RoleData> roles) {
        this.userId = userId;
        this.deptId = deptId;
        this.roles = roles != null ? roles : Collections.emptySet();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<RoleData> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleData> roles) {
        this.roles = roles != null ? roles : Collections.emptySet();
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    /**
     * 检查用户数据是否有效
     *
     * @return 是否有效
     */
    public boolean isValid() {
        return userId != null && deptId != null && !roles.isEmpty();
    }
}
