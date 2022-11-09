package com.cp.spring.security.resource.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * Copyright (C) 2022 YUNTU Inc.All Rights Reserved.
 * FileName:<类名>
 * Description: 动态权限控制
 * History:
 * 版本号  作者      日期              简要操作以及相关介绍
 * 1.0    CP.Chen  2022/5/25 16:30   Create
 */
public class JdbcRoleChecker implements RoleChecker{
    private static final Logger log = LoggerFactory.getLogger(JdbcRoleChecker.class);

    private Predicate<HttpServletRequest> whitePredicate = request -> false;

    @Override
    public boolean check(Authentication authentication, HttpServletRequest request) {
        boolean test = whitePredicate.test(request);
        if (test) {
            return true;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (log.isInfoEnabled()) {
            log.info("authorities = " + authorities);
        }

        String requestURI = request.getRequestURI();
        if (log.isInfoEnabled()) {
            log.info("requestURI = " + requestURI);
        }
        return authorities.contains(new SimpleGrantedAuthority("ROLE_USER")) && requestURI.equals("/foo/bar");
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    /**
     * 用于实现白名单策略
     * @param whitePredicate
     */
    public void setWhitePredicate(Predicate<HttpServletRequest> whitePredicate) {
        this.whitePredicate = whitePredicate;
    }
}
