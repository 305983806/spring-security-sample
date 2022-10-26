package com.cp.spring.security.resource.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2022 YUNTU Inc.All Rights Reserved.
 * FileName:<类名>
 * Description: 携带了 token 而且 token 合法，但是权限不足以访问资源 403
 * History:
 * 版本号  作者      日期              简要操作以及相关介绍
 * 1.0    CP.Chen  2022/5/25 16:30   Create
 */
public class SimpleAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Map<String, String> map = new HashMap<>();
        map.put("uri", request.getRequestURI());
        map.put("msg", "您没有权限访问该资源");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        String resBody = objectMapper.writeValueAsString(map);
        PrintWriter printWriter = response.getWriter();
        printWriter.print(resBody);
        printWriter.flush();
        printWriter.close();
    }
}
