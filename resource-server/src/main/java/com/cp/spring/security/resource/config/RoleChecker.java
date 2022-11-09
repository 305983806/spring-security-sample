package com.cp.spring.security.resource.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * Copyright (C) 2022 YUNTU Inc.All Rights Reserved.
 * FileName:<类名>
 * Description: <类说明>
 * History:
 * 版本号  作者      日期              简要操作以及相关介绍
 * 1.0    CP.Chen  2022/5/25 16:30   Create
 */
public interface RoleChecker extends InitializingBean {

    boolean check(Authentication authentication, HttpServletRequest request);
}
