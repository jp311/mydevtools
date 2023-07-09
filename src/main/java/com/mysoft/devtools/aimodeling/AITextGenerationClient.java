package com.mysoft.devtools.aimodeling;

import com.google.gson.annotations.SerializedName;
import com.mysoft.devtools.utils.Base64Util;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hezd   2023/7/6
 */
public class AITextGenerationClient extends AIModelingBaseClient<Map<String, Object>, String> {
    private static AITextGenerationClient cache;

    private AITextGenerationClient() {

    }

    public static AITextGenerationClient getInstance() {
        if (cache == null) {
            cache = new AITextGenerationClient();
        }
        return cache;
    }


    public String invoke(String code) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        Map<String, Object> data = new HashMap<>();
        List<KeyValuePair> variables = new ArrayList<>();
        variables.add(KeyValuePair.builder().key("collectionName").value("").build());
        variables.add(KeyValuePair.builder().key("promptTemplate").value("JavaTest").build());
        variables.add(KeyValuePair.builder().key("input").value(Base64Util.encode(code)).build());
        variables.add(KeyValuePair.builder().key("messageId").value("").build());
        data.put("variables", variables);
        return Base64Util.decode(postJson(data));
    }

    @Override
    protected String getUrl() {
        return HOST + "/api/appStore/skills/TextGenerationSkill/functions/TextGeneration/invoke";
    }

    @Override
    public Boolean isAuthorization() {
        return true;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private final static class KeyValuePair implements Serializable {
        @SerializedName("key")
        private String key;

        @SerializedName("value")
        private String value;
    }

}
