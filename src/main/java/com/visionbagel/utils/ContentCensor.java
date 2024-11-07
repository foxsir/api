package com.visionbagel.utils;

import com.baidu.aip.contentcensor.AipContentCensor;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONObject;

@ApplicationScoped
public class ContentCensor {
    @ConfigProperty(name = "baidu.aip.appId")
    String appId;

    @ConfigProperty(name = "baidu.aip.appKey")
    String appKey;

    @ConfigProperty(name = "baidu.aip.appToken")
    String appToken;


    public Boolean censor(String content) {
        AipContentCensor client = new AipContentCensor(appId, appKey, appToken);
        JSONObject response = client.textCensorUserDefined(content);

        // 1.合规，2.不合规，3.疑似，4.审核失败
        return response.get("conclusionType").equals(1) || response.get("conclusionType").equals(3);
    }
}
