package com.mysoft.devtools.aimodeling;

import com.google.gson.annotations.SerializedName;
import com.mysoft.devtools.bundles.LocalBundle;
import lombok.Data;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hezd   2023/7/6
 */
public class AIAccessTokenClientAIModeling extends AIModelingBaseClient<List<? extends NameValuePair>, AIAccessTokenClientAIModeling.AccessTokenResponse> {

    private static AIAccessTokenClientAIModeling cache;

    private AIAccessTokenClientAIModeling() {

    }

    public static AIAccessTokenClientAIModeling getInstance() {
        if (cache == null) {
            cache = new AIAccessTokenClientAIModeling();
        }
        return cache;
    }

    private AccessTokenResponse tokenCache;

    @Override
    protected String getUrl() {
        return HOST + "/connect/token";
    }

    @Override
    public Boolean isAuthorization() {
        return false;
    }

    public AccessTokenResponse getToken() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        if (tokenCache != null) {
            //防止时间差提前5分钟重新获取
            if (System.currentTimeMillis() < tokenCache.getNow() + 3600000 - 30000) {
                return tokenCache;
            }
        }
        List<BasicHeader> params = new ArrayList<>();
        params.add(new BasicHeader("client_id", "AIModeling_App"));
        params.add(new BasicHeader("grant_type", "password"));
        params.add(new BasicHeader("scope", "AIModeling"));
        params.add(new BasicHeader("username", LocalBundle.message("token.username")));
        params.add(new BasicHeader("password", LocalBundle.message("token.password")));
        AccessTokenResponse accessTokenResponse = super.post(params);

        if (accessTokenResponse.getErrorDescription() != null && !accessTokenResponse.getErrorDescription().isEmpty()) {
            throw new HttpResponseException(500, accessTokenResponse.getErrorDescription());
        }

        tokenCache = accessTokenResponse;
        tokenCache.setNow(System.currentTimeMillis());

        return tokenCache;
    }

    @Data
    public static final class AccessTokenResponse extends BaseResponse implements Serializable {
        @SerializedName("access_token")
        private String accessToken;

        @SerializedName("token_type")
        private String tokenType;

        @SerializedName("expires_in")
        private long expiresIn;

        private long now;
    }
}
