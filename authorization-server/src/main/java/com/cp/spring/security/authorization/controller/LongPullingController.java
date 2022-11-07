package com.cp.spring.security.authorization.controller;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * Copyright (C) 2022 YUNTU Inc.All Rights Reserved.
 * FileName:<类名>
 * Description: <类说明>
 * History:
 * 版本号  作者      日期              简要操作以及相关介绍
 * 1.0    CP.Chen  2022/5/25 16:30   Create
 */
@RestController
public class LongPullingController {
    private static final Logger log = LoggerFactory.getLogger(LongPullingController.class);

    // 创建一个Multimap 类型的map，guava 提供的多值 Map，一个 key 可以对应多个 value
    private Multimap<String, AsyncTask> dataIdContext = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    private ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("longPulling-timeout-checker-%d")
            .build();
    // 创建线程池
    private ScheduledExecutorService timeoutChecker =  new ScheduledThreadPoolExecutor(1, threadFactory);

    @GetMapping("/publish")
    public String publishConfig(@RequestParam String dataId, String info) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("publish configInfo dataId: [{}], configInfo: {}", dataId, info);
        }

        Collection<AsyncTask> asyncTasks = dataIdContext.removeAll(dataId);
        for (AsyncTask asyncTask: asyncTasks) {
            asyncTask.setTimeout(false);
            HttpServletResponse response = (HttpServletResponse) asyncTask.getAsyncContext().getResponse();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(info);
            asyncTask.getAsyncContext().complete();
        }
        return "success";
    }

    private static class AsyncTask {
        // 长轮询请求上下文，包括请求和响应体
        private AsyncContext asyncContext;
        // 超时标记
        private boolean timeout;

        public AsyncTask(AsyncContext asyncContext, boolean timeout) {
            this.asyncContext = asyncContext;
            this.timeout = timeout;
        }

        public AsyncContext getAsyncContext() {
            return asyncContext;
        }

        public void setAsyncContext(AsyncContext asyncContext) {
            this.asyncContext = asyncContext;
        }

        public boolean isTimeout() {
            return timeout;
        }

        public void setTimeout(boolean timeout) {
            this.timeout = timeout;
        }
    }

}
