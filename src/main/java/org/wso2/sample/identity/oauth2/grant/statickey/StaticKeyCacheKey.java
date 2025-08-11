package org.wso2.sample.identity.oauth2.grant.statickey;

import org.wso2.carbon.identity.core.cache.CacheKey;

public class StaticKeyCacheKey extends CacheKey {
    private final String authCode;
    private final String username;

    public StaticKeyCacheKey(String authCode, String username) {
        this.authCode = authCode;
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticKeyCacheKey that = (StaticKeyCacheKey) o;
        return authCode.equals(that.authCode) && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return authCode.hashCode() + 31 * username.hashCode();
    }
}
