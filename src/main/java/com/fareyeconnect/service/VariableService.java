/**
 * ****************************************************************************
 *
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 *
 *
 * NOTICE: All information contained herein is, and remains the property of
 * FarEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye.
 */
package com.fareyeconnect.service;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import com.fareyeconnect.config.PageRequest;
import com.fareyeconnect.config.Paged;
import com.fareyeconnect.config.security.GatewayUser;
import com.fareyeconnect.constant.AppConstant;
import com.fareyeconnect.model.Variable;
import com.fareyeconnect.util.CacheUtil;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;

// import co.fareye.config.security.GatewayUser;
// import co.fareye.constant.AppConstant;
// import co.fareye.dao.VariableDao;
// import co.fareye.model.Variable;
// import co.fareye.util.CacheUtil;

/**
 *
 * @author Baldeep Singh Kwatra
 * @since 24-Dec-2022, 7:03:58 PM
 */
@ApplicationScoped
// @DependsOn(value = "beanUtil")
public class VariableService {

    @Inject
    private CacheUtil<String, String> variableCache;

    @PostConstruct
    public void init() {
        // variableCache.setValue(this::map);
        variableCache.sync();
    }

    public Uni<?> get(String id) {
        return Variable.findById(id).onItem().ifNull().failWith(EntityNotFoundException::new);
    }

    @ReactiveTransactional
    public Uni<Variable> save(Variable variable) {
        variableCache.sync();
        return variable.persist();
    }

    @ReactiveTransactional
    public Uni<Variable> update(Variable variable) {
        variableCache.sync();
        return Panache.getSession()
                .chain(session -> session.merge(variable))
                .chain(entity -> entity.persist());
    }

    public Uni<Long> remove(String ids) {
        Uni<Long> deletedCount = Variable.delete("id in (?1)", Arrays.asList(ids.split(AppConstant.COMMA)));
        variableCache.sync();
        return deletedCount;

    }

    @ReactiveTransactional
    public Uni<Paged<Variable>> findAll(PageRequest pageRequest)  {
        return new Paged<Variable>().toPage(Variable.findAll(pageRequest.toSort()).page(pageRequest.toPage()));
    }

    // private ConcurrentMap<String, String> map() {
    // return variableDao.findAll().stream()
    // .collect(Collectors.toConcurrentMap(v -> v.getKey() + v.getCreatedByOrg(),
    // Variable::getValue));
    // }

    /**
     * Provide the variable key to fetch the corresponding value from the relevant
     * org. In case the key does not exists it will lookup in default org
     * 
     * @param key
     * @return
     */
    public String getValue(String key) {
        Map<String, String> variableMap = variableCache.get();
        String keyWithCurrentOrg = key + GatewayUser.getUser().getOrganizationId();
        return variableMap.containsKey(keyWithCurrentOrg) ? variableMap.get(keyWithCurrentOrg)
                : variableMap.get(key + AppConstant.DEFAULT_ID);
    }

    /**
     * Fetch the key from a specific org.
     * 
     * @param key
     * @param organizationId
     * @return
     */
    public String getValue(String key, String organizationId) {
        return variableCache.get(key + organizationId);
    }
}