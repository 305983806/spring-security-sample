package com.cp.spring.security.resource.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Copyright (C) 2022 YUNTU Inc.All Rights Reserved.
 * FileName:<类名>
 * Description: <类说明>
 * History:
 * 版本号  作者      日期              简要操作以及相关介绍
 * 1.0    CP.Chen  2022/5/25 16:30   Create
 */
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private CertInfo certInfo;
    private Claims claims;

    public CertInfo getCertInfo() {
        return certInfo;
    }

    public void setCertInfo(CertInfo certInfo) {
        this.certInfo = certInfo;
    }

    public Claims getClaims() {
        return claims;
    }

    public void setClaims(Claims claims) {
        this.claims = claims;
    }

    public static class Claims {
        private String issuer;
        private Integer expiresAt;

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public Integer getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(Integer expiresAt) {
            this.expiresAt = expiresAt;
        }
    }

    public static class CertInfo {
        private String publicKeyLocation;

        public String getPublicKeyLocation() {
            return publicKeyLocation;
        }

        public void setPublicKeyLocation(String publicKeyLocation) {
            this.publicKeyLocation = publicKeyLocation;
        }
    }

}
