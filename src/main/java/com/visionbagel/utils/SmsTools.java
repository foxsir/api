package com.visionbagel.utils;

import com.visionbagel.entitys.SmsCode;
import jakarta.enterprise.context.ApplicationScoped;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import jakarta.transaction.Transactional;

import java.util.Random;


@ApplicationScoped
public class SmsTools {
    private Client getClient() throws Exception {
        String accessKeyId = FileTools.getResource("alioss/accessKeyId.txt");
        String accessKeySecret = FileTools.getResource("alioss/accessKeySecret.txt");
        Config config = new Config()
                // 配置 AccessKey ID，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID。
                .setAccessKeyId(accessKeyId)
                // 配置 AccessKey Secret，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
                .setAccessKeySecret(accessKeySecret);

        // 配置 Endpoint
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
    }

    @Transactional
    public SendSmsResponse sendVerifyCode(String mobile) throws Exception {
        Random random = new Random();
        int r = random.nextInt(10000);
        String code = String.format("%14d", r).trim();

        Client client = this.getClient();
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(mobile)
                .setSignName("微氏科技")
                .setTemplateCode("SMS_306950179")
                .setTemplateParam("{\"code\":\"" + code + "\"}");
        // 获取响应对象
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
        if(sendSmsResponse.getBody().getCode().equalsIgnoreCase("ok")) {
            SmsCode smsCode = new SmsCode();
            smsCode.mobile = mobile;
            smsCode.code = code;
            smsCode.persistAndFlush();
        }

        return sendSmsResponse;
    }
}
