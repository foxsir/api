package com.visionbagel.resources;

import com.alipay.api.AlipayApiException;
import com.visionbagel.entitys.Trade;
import com.visionbagel.entitys.Wallet;
import com.visionbagel.payload.ResultOfData;
import com.visionbagel.payload.TopUpBody;
import com.visionbagel.payload.TradePagePayBody;
import com.visionbagel.repositorys.UserRepository;
import com.visionbagel.utils.alipay.AlipayTradePay;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.annotations.Form;
import java.util.Optional;

@Path("/payment")
public class PaymentResource {

    @Inject
    public UserRepository userRepository;

    @Inject
    public AlipayTradePay alipayTradePay;

    @Transactional
    protected Trade checkTrade(TradePagePayBody data)  throws AlipayApiException {
        Trade t = null;
        if(AlipayTradePay.queryTrade(data).isSuccess()) {
            if(AlipayTradePay.queryTrade(data).getTradeStatus().equals("TRADE_SUCCESS")) {
                Optional<Trade> trade = Trade.find("tradeNo", data.out_trade_no).firstResultOptional();
                if(trade.isPresent() && !trade.get().payStatus) {
                    t = trade.get();

                    if(String.valueOf(t.money).equals(data.total_amount)) {
                        t.payStatus = true;
                        t.persistAndFlush();

                        Wallet w = Wallet.find("user", t.user).firstResult();
                        if(w != null) {
                            w.balance = w.balance.add(t.money);
                            w.persistAndFlush();
                        } else {
                            w = new Wallet();
                            w.balance = t.money;
                            w.user = t.user;
                            w.persistAndFlush();
                        }
                    }
                } else if(trade.isPresent()) {
                    t = trade.get();
                }
            }
        }

        return t;
    }

    @APIResponse(
        responseCode = "200",
        description = "Example List",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ResultOfData.class,
                properties = {
                    @SchemaProperty(name = "data", type = SchemaType.OBJECT, implementation = String.class),
                }
            )
        )
    )
    @POST()
    @Transactional
    @RolesAllowed({"User"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response pay(@Valid TopUpBody body) throws AlipayApiException {
        String check = TopUpBody.check(String.valueOf(body.money));

        if(check.isEmpty()) {
            return Response
                .status(Response.Status.OK.getStatusCode())
                .entity(new ResultOfData<>(alipayTradePay.generateOrder( String.valueOf(body.money), userRepository.authUser() )))
                .build();
        } else {
            return Response
                .status(Response.Status.OK.getStatusCode())
                .entity(new ResultOfData<>().code(Response.Status.BAD_REQUEST.getStatusCode()).message(check))
                .build();
        }
    }

    @APIResponse(
        responseCode = "200",
        description = "Example List",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ResultOfData.class,
                properties = {
                    @SchemaProperty(name = "data", type = SchemaType.OBJECT, implementation = Trade.class),
                }
            )
        )
    )
    @POST()
    @Path("callback")
    @Produces(MediaType.APPLICATION_JSON)
    public Response callback(@Valid TradePagePayBody data) throws AlipayApiException {
        Trade t = checkTrade(data);

        return Response
            .status(Response.Status.OK.getStatusCode())
            .entity(new ResultOfData<>(t))
            .build();
    }

    @POST()
    @Path("notify")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.TEXT_PLAIN)
    public String notify(@Form TradePagePayBody data) throws AlipayApiException {
        Trade t = checkTrade(data);

        if(t != null && t.payStatus) {
            return "success";
        } else {
            return "fail";
        }
    }
}
