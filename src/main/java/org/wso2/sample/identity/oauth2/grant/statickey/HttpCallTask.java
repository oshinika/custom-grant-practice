package org.wso2.sample.identity.oauth2.grant.statickey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

public class HttpCallTask implements Runnable {
    private static final Log log = LogFactory.getLog(HttpCallTask.class);

    private final String requestJson;
    private final String serviceUrl;
    private JSONObject response;
    private Exception exception;

    public HttpCallTask(String requestJson, String serviceUrl) {
        this.requestJson = requestJson;
        this.serviceUrl = serviceUrl;
    }

    @Override
    public void run() {
        try {
            log.info("Making HTTP call in background thread");
            String responseStr = HttpClientUtil.callExternalService(
                    serviceUrl,
                    requestJson,
                    "application/json"
            );
            this.response = new JSONObject(responseStr);
        } catch (Exception e) {
            this.exception = e;
            log.error("HTTP call failed", e);
        }
    }

    public JSONObject getResponse() throws Exception {
        if (exception != null) {
            throw exception;
        }
        return response;
    }
}