package org.wso2.sample.identity.oauth2.grant.statickey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
//import org.wso2.sample.identity.oauth2.grant.statickey.model.ExternalServiceResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for sending HTTP POST requests with JSON payloads.
 * Uses Apache HttpClient with connection pooling for better performance.
 */
public class HttpClientUtil {

    private static final Log log = LogFactory.getLog(HttpClientUtil.class);


    private static final PoolingHttpClientConnectionManager connectionManager;
    private static final CloseableHttpClient httpClient;

    static {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(50);


        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000) // 5 seconds
                .setConnectionRequestTimeout(5000) // 5 seconds
                .setSocketTimeout(15000) // 15 seconds
                .build();


        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictIdleConnections(30, TimeUnit.SECONDS)
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                connectionManager.close();
                httpClient.close();
            } catch (IOException e) {
                log.error("Error closing HTTP client resources", e);
            }
        }));
    }

    public static String callExternalService(String url, String jsonInput, String contentType) throws IOException {
        HttpPost httpPost = new HttpPost(url);

        try {

            httpPost.setHeader("Content-Type", contentType);
            httpPost.setHeader("Accept", "application/json");

            StringEntity entity = new StringEntity(jsonInput, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            String responseString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
            int statusCode = response.getStatusLine().getStatusCode();

            log.debug("HTTP " + statusCode + " - Response: " + responseString);

            EntityUtils.consume(responseEntity);

            return responseString;

        } finally {
            httpPost.releaseConnection();
        }
    }



}



