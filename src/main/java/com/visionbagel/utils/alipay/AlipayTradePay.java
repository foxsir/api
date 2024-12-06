package com.visionbagel.utils.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.diagnosis.DiagnosisUtils;
import com.alipay.api.domain.*;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.visionbagel.config.AlipayProperties;
import com.visionbagel.entitys.Trade;
import com.visionbagel.entitys.User;
import com.visionbagel.payload.TradePagePayBody;
import com.visionbagel.utils.FileTools;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class AlipayTradePay {
    @Inject
    public AlipayProperties alipayProperties;

    private static final Logger log = LoggerFactory.getLogger(AlipayTradePay.class);

    public String generateOrder(String num, User user) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayTradePay.getAlipayConfig());
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        String tradeNo = OrderTools.generateOrderNo();
        model.setOutTradeNo(tradeNo);
        model.setTotalAmount(num);
        model.setSubject(String.join("", "充值:", num, "元"));
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        ExtUserInfo extUserInfo = new ExtUserInfo();
        extUserInfo.setMobile(user.phoneNumber);
        model.setExtUserInfo(extUserInfo);
        request.setBizModel(model);

        if(!alipayProperties.notifyUrl().isEmpty()) {
            request.setNotifyUrl(alipayProperties.notifyUrl());
        }
        request.setReturnUrl(alipayProperties.returnUrl());

        AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "POST");
        // 如果需要返回GET请求，请使用
        // AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "GET");
        String pageRedirectionData = response.getBody();
        if (response.isSuccess()) {
            Trade trade = new Trade();
            trade.tradeNo = tradeNo;
            trade.payStatus = false;
            trade.user = user;
            trade.money = BigDecimal.valueOf(Long.parseLong(num));
            trade.persistAndFlush();

            return pageRedirectionData;
        } else {
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
            String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            log.error(diagnosisUrl);
            return null;
        }
    }

     public static AlipayTradeQueryResponse queryTrade(TradePagePayBody data) throws AlipayApiException {
         // 初始化SDK
         AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

         // 构造请求参数以调用接口
         AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
         AlipayTradeQueryModel model = new AlipayTradeQueryModel();

         // 设置订单支付时传入的商户订单号
         model.setOutTradeNo(data.out_trade_no);

         // 设置支付宝交易号
         model.setTradeNo(data.trade_no);

         // 设置查询选项
         model.setQueryOptions(List.of("trade_settle_info"));

         request.setBizModel(model);
         // 第三方代调用模式下请设置app_auth_token
         // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");

         AlipayTradeQueryResponse response = alipayClient.execute(request);
         return response;
     }

    private static AlipayConfig getAlipayConfig() {
        String privateKey  = FileTools.getResource("alipay/applicationPrivateKey.txt");
        String alipayPublicKey = FileTools.getResource("alipay/alipayPublicKey_RSA2.txt");
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setAppId(FileTools.getResource("alipay/appId.txt"));
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }
}
