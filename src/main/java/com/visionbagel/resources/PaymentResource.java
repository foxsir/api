package com.visionbagel.resources;

import com.alipay.api.AlipayApiException;
import com.visionbagel.config.AlipayProperties;
import com.visionbagel.entitys.Trade;
import com.visionbagel.entitys.Wallet;
import com.visionbagel.payload.ResultOfData;
import com.visionbagel.payload.TopUpBody;
import com.visionbagel.payload.TradePagePayBody;
import com.visionbagel.repositorys.UserRepository;
import com.visionbagel.utils.alipay.AlipayTradePay;
import io.quarkus.arc.Lock;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Path("/payment")
@RolesAllowed({"User"})
public class PaymentResource {

    @Inject
    public UserRepository userRepository;

    @Inject
    public AlipayTradePay alipayTradePay;

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response pay(@Valid TopUpBody body) throws AlipayApiException {
        String check = TopUpBody.check(String.valueOf(body.money));

        if(check.isEmpty()) {
            return Response
                .status(Response.Status.OK.getStatusCode())
                .entity(new ResultOfData<>(alipayTradePay.generateOrder( String.valueOf(body.money) )))
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
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response callback(@Valid TradePagePayBody data) throws AlipayApiException {
        Trade t = null;
        if(AlipayTradePay.queryTrade(data).isSuccess()) {
            if(AlipayTradePay.queryTrade(data).getTradeStatus().equals("TRADE_SUCCESS")) {
                Optional<Trade> trade = Trade.find("tradeNo", data.out_trade_no).firstResultOptional();
                if(trade.isPresent() && !trade.get().payStatus) {
                    t = trade.get();
                    if(String.valueOf(t.money).equals(data.total_amount)) {
                        t.payStatus = true;
                        t.persistAndFlush();

                        Wallet w = Wallet.find("user", userRepository.authUser()).firstResult();
                        if(w != null) {
                            w.balance = w.balance.add(t.money);
                            w.persistAndFlush();
                        }
                    }
                }
            }
        }

        return Response
            .status(Response.Status.OK.getStatusCode())
            .entity(new ResultOfData<>(t))
            .build();
    }
}
