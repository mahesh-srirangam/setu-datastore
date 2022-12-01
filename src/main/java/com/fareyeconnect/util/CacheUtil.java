/**
 * ****************************************************************************
 *
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 *
 *
 * NOTICE: All information contained herein is, and remains the property of
 * FaEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye
 */

package com.fareyeconnect.util;

// import org.springframework.context.annotation.Scope;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.enterprise.context.Dependent;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.Scheduled.ConcurrentExecution;

/**
 *
 * @author Baldeep Singh Kwatra
 * @since 13-Apr-2022, 10:55:03 AM
 */
@Dependent
public class CacheUtil<K, V> {

    ConcurrentMap<K, V> cacheMap = new ConcurrentHashMap<>();
    CacheValue<K, V> value;

    public void setValue(CacheValue<K, V> value) {
        this.value = value;
    }

    /**
     * Get Value of a Key from Cache
     * 
     * @param key
     * @return
     */
    public V get(K key) {
        return cacheMap.get(key);
    }

    /**
     * Get the cache map
     * 
     * @return
     */
    public ConcurrentMap<K, V> get() {
        return cacheMap;
    }

    /**
     * Blocking sync operation to be used at startup only.
     */
    public void sync() {
        if (value != null)
            cacheMap = value.get();
    }

    /**
     * Async Operation for syncing cache when any record is modified
     */
    // @Async
    // public void async() {
    //     sync();
    // }

    /**
     * Executed automatically to perform sync behind the scenes
     */
    @Scheduled(every = "5m", delayed = "5m", concurrentExecution = ConcurrentExecution.SKIP)
    public void autoSync() {
        sync();
    }

    public interface CacheValue<K, V> {
        ConcurrentMap<K, V> get();
    }
}