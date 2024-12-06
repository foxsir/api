package com.visionbagel.payload;

import org.jboss.resteasy.annotations.jaxrs.FormParam;

public class TradePagePayBody {
    @FormParam("out_trade_no")
    public String out_trade_no;

    @FormParam("method")
    public String method;

    @FormParam("total_amount")
    public String total_amount;

    @FormParam("sign")
    public String sign;

    @FormParam("trade_no")
    public String trade_no;

    @FormParam("auth_app_id")
    public String auth_app_id;

    @FormParam("version")
    public String version;

    @FormParam("app_id")
    public String app_id;

    @FormParam("sign_type")
    public String sign_type;

    @FormParam("seller_id")
    public String seller_id;

    @FormParam("timestamp")
    public String timestamp;
}
