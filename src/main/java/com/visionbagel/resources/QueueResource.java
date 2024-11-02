package com.visionbagel.resources;

import ai.fal.client.exception.FalException;
import com.google.gson.Gson;
import com.visionbagel.utils.Translate;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
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

    @Inject
    public Translate translate;

    @Path("result")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response result(@QueryParam("path") String path, @QueryParam("requestId") String requestId) {
        var fal = FalClient.withEnvCredentials();

        try {
            Gson gson = new Gson();
            var result = fal.queue().result(path, QueueResultOptions.withRequestId(requestId));

            List<Map<String, String>> images = gson.fromJson(result.getData().get("images").toString(), List.class);

            images.forEach(image -> {
                System.out.println(image.get("url").length()/1024/1024*2/10*7);
            });

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
            var result = fal.queue().submit(
                    path,
                    QueueSubmitOptions.builder()
                            .input(input)
                            .build()
            );

            return Response
                    .status(HttpResponseStatus.OK.code())
                    .entity(result)
                    .build();
        } catch (FalException e) {
            // 余额不足
            return Response
                    .status(HttpResponseStatus.BAD_REQUEST.code())
                    .build();
        }
    }
}
