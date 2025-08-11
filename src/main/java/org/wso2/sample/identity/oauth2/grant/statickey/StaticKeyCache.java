package org.wso2.sample.identity.oauth2.grant.statickey;

import org.wso2.carbon.identity.core.cache.BaseCache;

public class StaticKeyCache extends BaseCache<StaticKeyCacheKey, StaticKeyCacheEntry> {
    private static final String CACHE_NAME = "StaticKeyGrantCache";
    private static final int TENANT_ID = -1234;
    private static volatile StaticKeyCache instance;

    private StaticKeyCache() {
        super(CACHE_NAME);
    }

    public static StaticKeyCache getInstance() {
        if (instance == null) {
            synchronized (StaticKeyCache.class) {
                if (instance == null) {
                    instance = new StaticKeyCache();
                }
            }
        }
        return instance;
    }

    public void addToCache(StaticKeyCacheKey key, StaticKeyCacheEntry entry) {
        if (entry != null && !entry.isExpired()) {
            super.addToCache(key, entry, TENANT_ID);
        }
    }

    public StaticKeyCacheEntry getFromCache(StaticKeyCacheKey key) {
        StaticKeyCacheEntry entry = super.getValueFromCache(key, TENANT_ID);
        if (entry != null && entry.isExpired()) {
            super.clearCacheEntry(key, TENANT_ID);
            return null;
        }
        return entry;
    }
}