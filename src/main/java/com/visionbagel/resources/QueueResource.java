package com.visionbagel.resources;

import ai.fal.client.exception.FalException;
import com.google.gson.Gson;
import com.visionbagel.entitys.User;
import com.visionbagel.entitys.Wallet;
import com.visionbagel.payload.ResultOfData;
import com.visionbagel.repositorys.UserRepository;
import com.visionbagel.utils.ContentCensor;
import com.visionbagel.utils.Translate;
import io.netty.handler.codec.http.HttpResponseStatus;
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

import java.math.BigDecimal;
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

    @Inject
    public Translate translate;

    @Inject
    public ContentCensor contentCensor;

    @Inject
    public UserRepository userRepository;

    @Path("result")
    @GET
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response result(@QueryParam("path") String path, @QueryParam("requestId") String requestId) {
        var fal = FalClient.withEnvCredentials();

        try {
            Gson gson = new Gson();
            var result = fal.queue().result(path, QueueResultOptions.withRequestId(requestId));

            List<Map<String, String>> images = gson.fromJson(result.getData().get("images").toString(), List.class);

            User user = userRepository.authUser();
            Optional<Wallet> wallet = Wallet.find("user", user).firstResultOptional();


            if(wallet.isPresent()) {
                Wallet w = wallet.get();
                System.out.println("cost");
                images.forEach(image -> {
                    double cost = (double) image.get("url").length() /1024/1024/10*7*3;
                    System.out.println(cost);
                    w.balance = w.balance.subtract(BigDecimal.valueOf(cost));
                });
                w.persistAndFlush();
            }

            return Response
                    .status(HttpResponseStatus.OK.code())
                    .entity(gson.toJson(result)).build();

        } catch (FalException e) {
            return Response
                    .status(HttpResponseStatus.BAD_REQUEST.code())
                    .header("message", e.getMessage())
                    .build();
        }
    }

    @ConfigProperty(name = "youdao.key")
    String key;

    @ConfigProperty(name = "youdao.secret")
    String secret;

    @Path("submit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response submit(@QueryParam("path") String path, Map<String, String> input) throws NoSuchAlgorithmException {
        var fal = FalClient.withEnvCredentials();

        try {
            Gson gson = new Gson();
            ArrayList prompt = (ArrayList) gson.fromJson(translate.run(input.get("prompt"), "auto", "en"), Map.class).get("translation");
            input.replace("prompt", prompt.getFirst().toString());

            // 检查敏感词
            if(!contentCensor.censor(input.get("prompt"))) {
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
                            .input(input)
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
