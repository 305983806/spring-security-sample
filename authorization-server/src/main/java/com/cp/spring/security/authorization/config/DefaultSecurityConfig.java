package com.cp.spring.security.authorization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;

/**
 * Copyright (C) 2022 YUNTU Inc.All Rights Reserved.
 * FileName:<类名>
 * Description: <类说明>
 * History:
 * 版本号  作者      日期              简要操作以及相关介绍
 * 1.0    CP.Chen  2022/5/25 16:30   Create
 */
@EnableWebSecurity(debug=true)
public class DefaultSecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder() throws CertificateException, IOException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        // 读取cer公钥证书来配置解码器
        ClassPathResource resource = new ClassPathResource("pub.cer");
        Certificate certificate = certificateFactory.generateCertificate(resource.getInputStream());
        RSAPublicKey publicKey = (RSAPublicKey) certificate.getPublicKey();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

}
