package com.cp.spring.security.resource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Copyright (C) 2022 YUNTU Inc.All Rights Reserved.
 * FileName:<类名>
 * Description: <类说明>
 * History:
 * 版本号  作者      日期              简要操作以及相关介绍
 * 1.0    CP.Chen  2022/5/25 16:30   Create
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2ResourceServerConfiguration {
    /**
     * Full(proxyBeanMethods = true) :proxyBeanMethods参数设置为true时即为：Full 全模式。 该模式下注入容器中的同一个组件无论被取出多少次都是同一个bean实例，即单实例对象，在该模式下SpringBoot每次启动都会判断检查容器中是否存在该组件。
     * Lite(proxyBeanMethods = false) :proxyBeanMethods参数设置为false时即为：Lite 轻量级模式。该模式下注入容器中的同一个组件无论被取出多少次都是不同的bean实例，即多实例对象，在该模式下SpringBoot每次启动会跳过检查容器中是否存在该组件。
     */

    @Bean
    public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
        SimpleAccessDeniedHandler accessDeniedHandler = new SimpleAccessDeniedHandler();
        SimpleAuthenticationEntryPoint authenticationEntryPoint = new SimpleAuthenticationEntryPoint();

        SecurityFilterChain filterChain = http.authorizeRequests()
                .mvcMatchers("/**").hasAnyAuthority("SCOPE_message.read")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling(exceptionConfigurer -> exceptionConfigurer
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                // 配置 oauth2 资源服务器
                .oauth2ResourceServer(resourceServer -> resourceServer
                        // 处理 403 问题
                        .accessDeniedHandler(accessDeniedHandler)
                        // 处理 401 问题
                        .authenticationEntryPoint(authenticationEntryPoint)
                        // 使用透明令牌 JWT
                        .jwt()
                )
                .build();
        return filterChain;
    }

    /**
     * 由于Spring Security 的角色关键字默认是以 ROLE_开头，我们在传入权限时使用 SCOPE_开头，会导致访问时无权限问题。
     * 因此需要自定义转换器
     *
     * @return
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        // 用户名 可以放 sub
        jwtAuthenticationConverter.setPrincipalClaimName(JwtClaimNames.SUB);
        return jwtAuthenticationConverter;
    }
}
