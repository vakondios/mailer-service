package com.avakio.mailer.caches;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;

/**
 * Abstract Class for the internal caches
 *
 * @author avacondios-xps
 * @since v.0.0.0
 */
@Slf4j
public abstract class Cache {

    public org.springframework.cache.Cache internal = null;

    public Cache(String cacheName, CacheManager cacheManager) {
        try {
            this.internal =  cacheManager.getCache(cacheName);
            if (log.isInfoEnabled()) log.info("System initialized the cache '{}'", cacheName);
        } catch (Exception err) {
            if (log.isInfoEnabled()) log.info("System cannot initialize the cache '{}'", cacheName);
            log.error(err.getMessage(), err);
        }
    }

     void updateCache(Object key, Object value) {
        if (internal.get(key) != null) {
            internal.put(key, value);
        }
    }

     void newCache(Object key, Object value) {
        Object obj = internal.get(key);
        if (obj == null) {
            internal.putIfAbsent(key, value);
        }
    }

     void deleteCache(Object key) {
        if (internal.get(key) != null) {
            internal.evictIfPresent(key);
        }
    }

     org.springframework.cache.Cache.ValueWrapper readCache(Object key) {
        return internal.get(key);
    }

}
