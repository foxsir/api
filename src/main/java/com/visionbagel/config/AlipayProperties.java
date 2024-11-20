package com.visionbagel.config;


import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "alipay")
public interface AlipayProperties {
    String notifyUrl();
    String returnUrl();
}
