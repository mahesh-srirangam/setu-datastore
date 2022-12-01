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
package com.fareyeconnect.config.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.util.CollectionUtils;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
import com.fareyeconnect.constant.AppConstant;

/**
 *
 * @author Baldeep Singh Kwatra
 * @since 21-May-2022, 8:54:42 PM
 */
@Data
public class GatewayUser {//implements UserDetails {

    private String firstName;

    private String lastName;

    private String id = AppConstant.DEFAULT_ID;

    private String email;

    private String path = "template";

    private String organizationId = AppConstant.DEFAULT_ID;

    private List<String> permission;

    private String timezone;

    // @Override
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    //     Set<GrantedAuthority> authorities = new HashSet<>();
    //     if (permission != null)
    //         permission.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority)));
    //     return authorities;
    // }

    // @Override
    // public String getPassword() {
    //     throw new UnsupportedOperationException(AppConstant.NOT_SUPPORTED);
    // }

    // @Override
    // public boolean isAccountNonExpired() {
    //     throw new UnsupportedOperationException(AppConstant.NOT_SUPPORTED);
    // }

    // @Override
    // public boolean isAccountNonLocked() {
    //     throw new UnsupportedOperationException(AppConstant.NOT_SUPPORTED);
    // }

    // @Override
    // public boolean isCredentialsNonExpired() {
    //     throw new UnsupportedOperationException(AppConstant.NOT_SUPPORTED);
    // }

    // @Override
    // public boolean isEnabled() {
    //     throw new UnsupportedOperationException(AppConstant.NOT_SUPPORTED);
    // }

    // @Override
    // public String getUsername() {
    //     return this.email;
    // }

    public static GatewayUser getUser() {
        try {
            return new GatewayUser();//(GatewayUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return new GatewayUser();
        }
    }

    // public static MultiValueMap<String, String> getHeaders() {
    //     GatewayUser gatewayUser = getUser();
    //     MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    //     headers.add("gateway-id", gatewayUser.getId());
    //     headers.add("gateway-lastName", gatewayUser.getLastName());
    //     headers.add("gateway-firstName", gatewayUser.getFirstName());
    //     headers.add("gateway-email", gatewayUser.getEmail());
    //     headers.add("gateway-path", gatewayUser.getPath());
    //     if (!CollectionUtils.isEmpty(gatewayUser.getPermission()))
    //         headers.add("gateway-permission", String.join(",", gatewayUser.getPermission()));
    //     headers.add("gateway-organizationId", gatewayUser.getOrganizationId());
    //     return headers;
    // }
}