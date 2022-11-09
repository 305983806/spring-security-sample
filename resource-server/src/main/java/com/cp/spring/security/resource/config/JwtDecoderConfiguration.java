package com.cp.spring.security.resource.config;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Collection;

/**
 * Copyright (C) 2022 YUNTU Inc.All Rights Reserved.
 * FileName:<类名>
 * Description: <类说明>
 * History:
 * 版本号  作者      日期              简要操作以及相关介绍
 * 1.0    CP.Chen  2022/5/25 16:30   Create
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtProperties.class)
public class JwtDecoderConfiguration {

    private final JwtProperties jwtProperties;

    public JwtDecoderConfiguration(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

    }

    /**
     * 校验jwt发行者 issuer 是否合法
     *
     * @return
     */
    @Bean
    public JwtIssuerValidator jwtIssuerValidator() {
        return new JwtIssuerValidator(this.jwtProperties.getClaims().getIssuer());
    }

    /**
     * 校验jwt是否过期
     * @return
     */
    @Bean
    public JwtTimestampValidator jwtTimestampValidator() {
        return new JwtTimestampValidator(Duration.ofSeconds((long)this.jwtProperties.getClaims().getExpiresAt()));
    }

    /**
     * jwt token 委托校验器，集中校验的策略{@link OAuth2TokenValidator}
     *
     * @param tokenValidators
     * @return
     */
    @Bean
    public DelegatingOAuth2TokenValidator<Jwt> delegatingTokenValidator(Collection<OAuth2TokenValidator<Jwt>> tokenValidators) {
        return new DelegatingOAuth2TokenValidator<Jwt>(tokenValidators);
    }

    /**
     * 基于Nimbus的jwt解码器，并增加了一些自定义校验策略
     * @param validator
     * @return
     * @throws IOException
     * @throws CertificateException
     * @throws JOSEException
     */
    @Bean
    public JwtDecoder jwtDecoder(@Qualifier("delegatingTokenValidator") DelegatingOAuth2TokenValidator<Jwt> validator) throws IOException, CertificateException, JOSEException {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri("http://localhost:9000/oauth2/jwks").build();
        return jwtDecoder;
    }
}
