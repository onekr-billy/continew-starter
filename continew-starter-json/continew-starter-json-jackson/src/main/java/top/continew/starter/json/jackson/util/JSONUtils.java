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

package top.continew.starter.json.jackson.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * json 工具
 *
 * @author echo
 * @since 2.11.0
 */
public class JSONUtils {

    private JSONUtils() {
    }

    /**
     * Jackson 对象映射器，用于 JSON 解析与序列化。
     */
    private static final ObjectMapper OBJECT_MAPPER = SpringUtil.getBean(ObjectMapper.class);

    /**
     * 获取 Jackson 对象映射器。
     *
     * @return {@link ObjectMapper} Jackson 对象映射器
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * 对象转为 json 字符串
     *
     * @param object 对象
     * @return {@link String }
     */
    public static String toJsonStr(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将对象转换为 JsonNode。
     *
     * @param obj 需要转换的对象
     * @return 转换后的 {@link JsonNode}，如果 obj 为空，则返回 null
     */
    public static JsonNode toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.valueToTree(obj);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将 List 转换为 JsonNode。
     *
     * @param list 输入的 List
     * @return 转换后的 {@link JsonNode}
     */
    public static JsonNode listToJson(List<?> list) {
        return toJson(list);
    }

    /**
     * 将 Map 转换为 JsonNode。
     *
     * @param map 输入的 Map
     * @return 转换后的 {@link JsonNode}
     */
    public static JsonNode mapToJson(Map<?, ?> map) {
        return toJson(map);
    }

    /**
     * 将 JsonNode 转换为 List<String>，用于环境变量格式解析。
     *
     * @param jsonNode 需要转换的 JsonNode
     * @return 转换后的 List<String>
     */
    public static List<String> jsonToEnvList(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull()) {
            return new ArrayList<>();
        }
        List<String> envList = new ArrayList<>();
        jsonNode.fields().forEachRemaining(field -> {
            String key = field.getKey();
            JsonNode valueNode = field.getValue();
            String value = valueNode.isValueNode() ? valueNode.asText() : valueNode.toString();
            envList.add(key + "=" + value);
        });
        return envList;
    }

    /**
     * 将 JsonNode 转换为 List<String>。
     *
     * @param jsonNode 需要转换的 JsonNode
     * @return 转换后的 List<String>
     */
    public static List<String> jsonToStringList(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull()) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.convertValue(jsonNode, new TypeReference<>() {
            });
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将 JsonNode 转换为指定类型的 Java 对象。
     *
     * @param jsonNode JSON 数据
     * @param clazz    目标 Java 类
     * @return 解析后的 Java 对象
     */
    public static <T> T fromJson(JsonNode jsonNode, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.treeToValue(jsonNode, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析 JSON 字符串为 Java 对象。
     *
     * @param str   JSON 字符串
     * @param clazz 目标 Java 类
     * @return 解析后的 Java 对象
     */
    public static <T> T parseObject(String str, Class<T> clazz) {
        if (CharSequenceUtil.isEmpty(str)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(str, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串 解析为 list<T>
     *
     * @param str   字符串
     * @param clazz 目标 Java 类
     * @return 解析后的 List<T>
     */
    public static <T> List<T> parseArray(String str, Class<T> clazz) {
        if (CharSequenceUtil.isEmpty(str)) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(str, OBJECT_MAPPER.getTypeFactory()
                .constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断字符串是否为 JSON 格式。
     *
     * @param str 字符串
     * @return 是否为 JSON 格式
     */
    public static boolean isTypeJSON(String str) {
        if (CharSequenceUtil.isEmpty(str)) {
            return false;
        }
        try {
            OBJECT_MAPPER.readTree(str);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 将 JSON 字符串转换为指定类型的 Java 对象。
     *
     * @param str   字符串
     * @param clazz 目标对象的 Class 类型
     * @return 解析后的 Java 对象
     */
    public static <T> T toBean(String str, Class<T> clazz) {
        if (CharSequenceUtil.isEmpty(str)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(str, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
