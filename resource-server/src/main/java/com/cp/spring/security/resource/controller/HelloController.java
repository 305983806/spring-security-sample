package com.cp.spring.security.resource.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright (C) 2022 YUNTU Inc.All Rights Reserved.
 * FileName:<类名>
 * Description: <类说明>
 * History:
 * 版本号  作者      日期              简要操作以及相关介绍
 * 1.0    CP.Chen  2022/5/25 16:30   Create
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "无名") String name) {
        String str = String.format("Hello %s!", name);
        return str;
    }

}
