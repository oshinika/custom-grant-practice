package org.wso2.sample.identity.oauth2.grant.statickey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
            log.info("Making HTTP call to {} in background thread with data: {}");
            String responseStr = HttpClientUtil.callExternalService(
                    serviceUrl,
                    requestJson,
                    "application/json"
            );

            if (responseStr == null || responseStr.trim().isEmpty()) {
                throw new IOException("External service returned an empty or null response.");
            }

            this.response = new JSONObject(responseStr);
            log.info("Received response from {}: {}");
        } catch (IOException e) {
            //TODO: check what is exception changing is and what are the beneficent of it
            this.exception = e;
            log.error("HTTP call to {} failed due to I/O error: {}");
        } catch (JSONException e) {
            this.exception = e;
            log.error("HTTP call to {} succeeded but failed to parse JSON response: {}");
        } catch (Exception e) {
            this.exception = e;
            log.error("HTTP call to {} failed due to an unexpected error: {}");
        }
    }

    public JSONObject getResponse() throws Exception {
        if (exception != null) {
            throw exception;
        }
        if (response == null) {
            throw new IllegalStateException(
                    "HTTP call completed without exception, but response is null. " +
                            "This indicates an internal logic error.");
        }
        return response;
    }
}



