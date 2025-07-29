package org.wso2.sample.identity.oauth2.grant.statickey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClientUtil {
    private static final Log log = LogFactory.getLog(HttpClientUtil.class);

    public static String callExternalService(String url, String jsonInput, String contentType) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL externalUrl = new URL(url);
            conn = (HttpURLConnection) externalUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                log.debug("External service response: " + response);
                return response.toString();
            }
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}



