package com.visionbagel.utils;

import com.visionbagel.utils.youdao.AuthV3Util;
import com.visionbagel.utils.youdao.HttpUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 网易有道智云翻译服务api调用demo
 * api接口: https://openapi.youdao.com/api
 */
@ApplicationScoped
public class Translate {

    @ConfigProperty(name = "youdao.key")
    String key;

    @ConfigProperty(name = "youdao.secret")
    String secret;

    public String run(String q, String from, String to) throws NoSuchAlgorithmException {
        // 添加请求参数
        Map<String, String[]> params = new HashMap<String, String[]>() {{
            put("q", new String[]{q});
            put("from", new String[]{from});
            put("to", new String[]{to});
        }};

        // 添加鉴权相关参数
        AuthV3Util.addAuthParams(key, secret, params);
        // 请求api服务
        byte[] result = HttpUtil.doPost("https://openapi.youdao.com/api", null, params, "application/json");
        // 打印返回结果

        return new String(result, StandardCharsets.UTF_8);
    }
}
