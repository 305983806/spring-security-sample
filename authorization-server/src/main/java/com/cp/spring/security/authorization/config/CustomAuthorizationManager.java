package com.cp.spring.security.authorization.config;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;

import java.util.function.Supplier;

/**
 * Copyright (C) 2022 YUNTU Inc.All Rights Reserved.
 * FileName:<类名>
 * Description: <类说明>
 * History:
 * 版本号  作者      日期              简要操作以及相关介绍
 * 1.0    CP.Chen  2022/5/25 16:30   Create
 */
public class CustomAuthorizationManager implements AuthorizationManager {
    @Override
    public AuthorizationDecision check(Supplier authentication, Object object) {
        return null;
    }

    @Override
    public void verify(Supplier authentication, Object object) {

        AuthorizationManager.super.verify(authentication, object);
    }
}
