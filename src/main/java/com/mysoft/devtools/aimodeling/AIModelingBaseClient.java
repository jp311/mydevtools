package com.mysoft.devtools.aimodeling;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.services.AppSettingsStateService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author hezd   2023/7/6
 */
public abstract class AIModelingBaseClient<TRequest, TResponse> {
    protected static final String HOST = AppSettingsStateService.getInstance().getState().aiConfigurable.getHost();
    private static final Gson GSON = new Gson();

    private static final Integer CONNECTION_TIMEOUT = 3000;

    private static final Integer TIMEOUT = 300000;

    protected abstract String getUrl();

    public abstract Boolean isAuthorization();

    protected TResponse postJson(TRequest request) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        // 创建HttpClient对象，并使用SSL连接套接字工厂
        HttpClient httpClient = getHttpClient();

        // 创建HttpGet请求对象
        HttpPost httpPost = getHttpPost();
        httpPost.setHeader("Content-Type", "application/json");

        StringEntity requestEntity = new StringEntity(GSON.toJson(request), ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);

        // 发送请求并获取响应
        HttpResponse response = httpClient.execute(httpPost);

        // 获取响应状态码
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 200) {
            throw new HttpResponseException(statusCode, LocalBundle.message("ai.server.http.fail"));
        }
        // 获取响应实体
        HttpEntity entity = response.getEntity();

        // 将实体转换为字符串
        String responseString = EntityUtils.toString(entity);

        Type responseType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Class<TResponse> responseClass = (Class<TResponse>) responseType;

        if (responseClass == String.class) {
            return (TResponse) responseString;
        }
        return GSON.fromJson(responseString, responseClass);
    }

    protected TResponse post(List<? extends NameValuePair> parameters) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        // 创建HttpClient对象，并使用SSL连接套接字工厂
        HttpClient httpClient = getHttpClient();

        // 创建HttpGet请求对象
        HttpPost httpPost = getHttpPost();
        //httpPost.setHeader("Content-Type", "x-www-form-urlencoded");

        UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8);
        httpPost.setEntity(requestEntity);

        // 发送请求并获取响应
        HttpResponse response = httpClient.execute(httpPost);

        // 获取响应状态码
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 200) {
            throw new HttpResponseException(statusCode, LocalBundle.message("ai.server.http.fail"));
        }
        // 获取响应实体
        HttpEntity entity = response.getEntity();

        // 将实体转换为字符串
        String responseString = EntityUtils.toString(entity);

        Type responseType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Class<TResponse> responseClass = (Class<TResponse>) responseType;

        if (responseClass == String.class) {
            return (TResponse) responseString;
        }
        return GSON.fromJson(responseString, responseClass);
    }

    protected HttpPost getHttpPost() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HttpPost httpPost = new HttpPost(getUrl());
        //认证token
        if (isAuthorization()) {
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + AIAccessTokenClientAIModeling.getInstance().getToken().getAccessToken());
        }

        //设置超时
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT)
                .build();
        httpPost.setConfig(requestConfig);
        return httpPost;
    }

    protected HttpClient getHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        if (getUrl().toLowerCase().startsWith("https://")) {
            // 创建SSL上下文
            SSLContext sslContext = SSLContextBuilder.create().build();

            // 创建SSL连接套接字工厂
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);

            // 创建HttpClient对象，并使用SSL连接套接字工厂
            return HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
        } else {
            return HttpClients.createDefault();
        }
    }

    @Data
    @EqualsAndHashCode
    public static class BaseResponse {
        @SerializedName("error")
        private String error;
        @SerializedName("error_description")
        private String errorDescription;
        @SerializedName("error_uri")
        private String errorUri;
    }
}
