package com.visionbagel.resources;

import ai.fal.client.exception.FalException;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.visionbagel.entitys.FalImage;
import com.visionbagel.entitys.User;
import com.visionbagel.entitys.Wallet;
import com.visionbagel.entitys.WalletRecord;
import com.visionbagel.enums.E_COST_TYPE;
import com.visionbagel.payload.*;
import com.visionbagel.repositorys.UserRepository;
import com.visionbagel.utils.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.narayana.jta.runtime.TransactionConfiguration;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import ai.fal.client.*;
import ai.fal.client.queue.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@OpenAPIDefinition(
    tags = {
        @Tag(name="History", description="Operations related to History")
    },
    info = @Info(
        title="History API",
        version = "1.0.1",
        contact = @Contact(
            name = "History API Support",
            url = "http://exampleurl.com/contact",
            email = "techsupport@example.com"),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0.html"))
)
@Path("/queue")
@RolesAllowed({ "User" })
public class QueueResource {

    private static final Logger log = LoggerFactory.getLogger(QueueResource.class);

    @ConfigProperty(name = "alioss.domain")
    public String ossDomain;

    @Inject
    public Translate translate;

    @Inject
    public ContentCensor contentCensor;

    @Inject
    public UserRepository userRepository;

    @Path("status")
    @GET
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response status(@QueryParam("path") String path, @QueryParam("requestId") String requestId) {
        var fal = FalClient.withEnvCredentials();
        var result = fal.queue().status(path, QueueStatusOptions.withRequestId(requestId));

        return Response
            .status(HttpResponseStatus.OK.code())
            .entity(new ResultOfData<>(result))
            .build();
    }

    @Path("result")
    @GET
    @Transactional
    @TransactionConfiguration(timeout = 10000)
    @Produces(MediaType.APPLICATION_JSON)
    public Response result(@QueryParam("path") String path, @QueryParam("requestId") String requestId) {
        var fal = FalClient.withEnvCredentials();

        try {
            long queueStart = new Date().getTime();
            Gson gson = new Gson();
            var result = fal.queue().result(path, QueueResultOptions.withRequestId(requestId));
            System.out.println("========queue().result=========");
            System.out.println("========queue().result=========");
            System.out.println((new Date().getTime() - queueStart) / 1000);

            List<LinkedTreeMap<String, Object>> images = gson.fromJson(result.getData().get("images").toString(), List.class);

            User user = userRepository.authUser();
            Optional<Wallet> wallet = Wallet.find("user", user).firstResultOptional();

            List<FalImage> imageList = new ArrayList<>();

            if(wallet.isPresent()) {
                Wallet w = wallet.get();
                WalletRecord record = new WalletRecord();
                record.cost = BigDecimal.ZERO;
                StorageForOSS oss = new StorageForOSS();

                images.forEach(image -> {
                    long width = Math.round(
                        Double.parseDouble(image.get("width").toString())
                    );
                    long height = Math.round(
                        Double.parseDouble(image.get("height").toString())
                    );
                    BigDecimal cost = Biller.calc(
                        width,
                        height,
                        path
                    ).setScale(2, RoundingMode.HALF_UP);
                    w.balance = w.balance.subtract(cost);
                    record.cost = record.cost.add(cost);
                    record.dataSize += width * height;

                    try {
                        long updateStart = new Date().getTime();
                        String objectName = oss.updateBase64(image.get("url").toString());
                        System.out.println("========updateStart=========");
                        System.out.println("========updateStart=========");
                        System.out.println((new Date().getTime() - updateStart) / 1000);

                        String url = String.join("/", ossDomain, objectName);
                        FalImage fi = new FalImage();
                        fi.content_type = image.get("content_type").toString();
                        fi.width = width;
                        fi.height = height;
                        fi.url = url;
                        imageList.add(fi);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                record.user = user;
                record.requestId = requestId;
                record.costType = E_COST_TYPE.DECREMENT;
                record.persistAndFlush();

                w.persistAndFlush();
            }

            return Response
                    .status(HttpResponseStatus.OK.code())
                    .entity(new ResultOfData<>(new FalResult(result, imageList)))
                    .build();
        } catch (FalException e) {
            return Response
                    .status(HttpResponseStatus.BAD_REQUEST.code())
                    .header("message", e.getMessage())
                    .build();
        }
    }

    @Path("submit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response submit(@QueryParam("path") String path, SchemaInput input) throws NoSuchAlgorithmException {
        var fal = FalClient.withEnvCredentials();
        input.output_format = "png";
        try {
            Gson gson = new Gson();
            ArrayList prompt = (ArrayList) gson.fromJson(translate.run(input.prompt, "auto", "en"), Map.class).get("translation");
            input.prompt = prompt.getFirst().toString()
                .replaceAll("< ", "<")
                .replaceAll(" >", ">");

            // 检查敏感词
            if(!contentCensor.censor(input.prompt)) {
                return Response
                        .status(HttpResponseStatus.BAD_REQUEST.code())
                        .entity(new ResultOfData<>().message("存在敏感词").code(HttpResponseStatus.BAD_REQUEST.code()))
                        .build();
            }

            // 检查余额
            Optional<Wallet> wallet = Wallet.find("user", userRepository.authUser()).firstResultOptional();
            if(wallet.isPresent()) {
                if(wallet.get().balance.compareTo(BigDecimal.valueOf(0)) <= 0) {
                    return Response.
                            status(HttpResponseStatus.BAD_REQUEST.code())
                            .entity(new ResultOfData<>().message("余额不足, 请充值").code(HttpResponseStatus.BAD_REQUEST.code()))
                            .build();
                }
            } else {
                return Response.
                        status(HttpResponseStatus.BAD_REQUEST.code())
                        .entity(new ResultOfData<>().message("余额不足, 请充值").code(HttpResponseStatus.BAD_REQUEST.code()))
                        .build();
            }

            var result = fal.queue().submit(
                    path,
                    QueueSubmitOptions.builder()
                            .input(gson.fromJson(gson.toJson(input), Map.class))
                            .build()
            );

            return Response
                    .status(HttpResponseStatus.OK.code())
                    .entity(new ResultOfData<>(result))
                    .build();
        } catch (FalException e) {
            // fal 余额不足, 短信报警
            return Response
                    .status(HttpResponseStatus.BAD_REQUEST.code())
                    .entity(new ResultOfData<>().message("系统暂时不可用, 请稍后再试").code(500))
                    .build();
        }
    }
}
