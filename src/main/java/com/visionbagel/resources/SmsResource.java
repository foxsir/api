package com.visionbagel.resources;

import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.visionbagel.payload.ResultOfData;
import com.visionbagel.payload.SendCodeBody;
import com.visionbagel.utils.SmsTools;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.Instant;

@Path("/sms")
public class SmsResource {
    @Inject
    SmsTools smsTools;

    @Tag(name = "sms", description = "send sms")
    @Operation(summary = "send verification code")
    @APIResponse(
        responseCode = "200",
        description = "send successful",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ResultOfData.class,
                properties = {
                    @SchemaProperty(name = "data", type = SchemaType.STRING, implementation = String.class),
                }
            )
        )
    )
    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("verifyCode")
    public Response verifyCode(@Valid SendCodeBody body) throws Exception {
        SendSmsResponse sendSmsResponse = smsTools.sendVerifyCode(body.mobile);

        int statusCode = Response.Status.OK.getStatusCode();
        String message = "发送成功, 请查收";
        if(sendSmsResponse.getBody().getCode().equals("isv.BUSINESS_LIMIT_CONTROL")) {
            message = "发送频繁, 请稍后发送";
            statusCode = Response.Status.TOO_MANY_REQUESTS.getStatusCode();
        }

        return Response
                .status(sendSmsResponse.getStatusCode())
                .entity(new ResultOfData<>(sendSmsResponse).code(statusCode).message(message))
                .build();
    }
}
