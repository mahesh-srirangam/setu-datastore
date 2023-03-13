/**
 * ****************************************************************************
 * <p>
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 * <p>
 * <p>
 * NOTICE: All information contained herein is, and remains the property of
 * FarEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye.
 */
package com.fareyeconnect.service;

import com.fareyeconnect.config.PageRequest;
import com.fareyeconnect.config.Paged;
import com.fareyeconnect.config.security.GatewayUser;
import com.fareyeconnect.constant.AppConstant;
import com.fareyeconnect.model.Variable;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Baldeep Singh Kwatra
 * @since 24-Dec-2022, 7:03:58 PM
 */
@ApplicationScoped
@Startup
public class VariableService {

    @Inject
    @CacheName("variable")
    Cache cache;

    @PostConstruct
    public void init() {
        cacheVariables();
    }

    public Uni<?> get(String id) {
        return Variable.findById(id).onItem().ifNull().failWith(EntityNotFoundException::new);
    }

    @ReactiveTransactional
    public Uni<Variable> save(Variable variable) throws ExecutionException, InterruptedException {
        syncVariableToCache(variable);
        return variable.persist();
    }

    @ReactiveTransactional
    public Uni<Variable> update(Variable variable) throws ExecutionException, InterruptedException {
        syncVariableToCache(variable);
        return Panache.getSession()
                .chain(session -> session.merge(variable))
                .chain(entity -> entity.persist());
    }

    public Uni<Long> remove(String ids) {
        Uni<Long> deletedCount = Variable.delete("id in (?1)", Arrays.asList(ids.split(AppConstant.COMMA)));
        return deletedCount;

    }

    @ReactiveTransactional
    public Uni<Paged<Variable>> findAll(PageRequest pageRequest) {
        return new Paged<Variable>().toPage(Variable.findAll(pageRequest.toSort()).page(pageRequest.toPage()));
    }

    /**
     * This method runs on startup
     * Fetch all the current variables from database
     * Create a map and cache them
     */
    @ReactiveTransactional
    public void cacheVariables() {
        PanacheQuery<Variable> variables = Variable.findAll();
        variables.list().subscribe().with(variableList -> {
            Map<String, Map<String, String>> variableMap = new HashMap<>();
            for (Variable variable : variableList) {
                Map<String, String> keyValPair = variableMap.getOrDefault(variable.getCreatedByOrg(), new HashMap<>());
                keyValPair.put(variable.getKey(), variable.getValue());
                variableMap.put(variable.getCreatedByOrg(), keyValPair);
            }
            variableMap.forEach((key, value) -> cache.as(CaffeineCache.class).put(key, CompletableFuture.completedFuture(value)));
        });
    }

    /**
     * On creation of new variable or update of variable sync to local cache
     *
     * @param variable
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void syncVariableToCache(Variable variable) throws ExecutionException, InterruptedException {
        String organizationId = GatewayUser.getUser().getOrganizationId();
        CompletableFuture<Map<String, String>> mapCompletableFuture = cache.as(CaffeineCache.class).getIfPresent(organizationId);
        Map<String, String> variableMap;
        if (mapCompletableFuture == null) {
            variableMap = Collections.singletonMap(organizationId, variable.getValue());
        } else {
            variableMap = mapCompletableFuture.get();
            variableMap.put(variable.getKey(), variable.getValue());
        }
        cache.as(CaffeineCache.class).put(organizationId, CompletableFuture.completedFuture(variableMap));
    }


    /**
     * Fetch variables from instance cache
     * 1) First fetch variables from default org cache
     * 2) Secondly, fetch variables from user specific org
     * 3) Merge the variables and return the map to put into context
     *
     * @return
     */
    public Map<String, String> getVariables() {
        Map<String, String> contextVariableMap = new HashMap<>();
        try {
            String organizationId = GatewayUser.getUser().getOrganizationId();
            CompletableFuture<Map<String, String>> defaultOrgVariables = cache.as(CaffeineCache.class).getIfPresent(AppConstant.DEFAULT_ID);
            CompletableFuture<Map<String, String>> orgSpecificVariables = cache.as(CaffeineCache.class).getIfPresent(organizationId);
            contextVariableMap.putAll(defaultOrgVariables.get());
            contextVariableMap.putAll(orgSpecificVariables.get());
        } catch (Exception e) {
            Log.error("Error putting variables in context "+ e);
        }
        return contextVariableMap;
    }

    @Scheduled(every = "5m", delayed = "5m", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void autoSync() {

    }
}