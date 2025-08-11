package org.wso2.sample.identity.oauth2.grant.statickey;

import org.json.JSONObject;
import org.wso2.carbon.identity.core.cache.CacheEntry;

public class StaticKeyCacheEntry extends CacheEntry {
    private final JSONObject response;
    private final long expiryTime;

    public StaticKeyCacheEntry(JSONObject response, long validityPeriodMs) {
        this.response = response;
        this.expiryTime = System.currentTimeMillis() + validityPeriodMs;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }

    public JSONObject getResponse() {
        return response;
    }
}

