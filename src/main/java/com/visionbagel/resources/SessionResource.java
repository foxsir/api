package com.visionbagel.resources;

import com.visionbagel.entitys.User;
import com.visionbagel.payload.LoginForSMSBody;
import com.visionbagel.payload.RegisterBody;
import com.visionbagel.payload.ResultOfData;
import com.visionbagel.repositorys.SessionRepository;
import com.visionbagel.repositorys.UserRepository;
import com.visionbagel.utils.GenerateViolationReport;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.api.validation.ViolationReport;

@OpenAPIDefinition(
    info = @Info(title="Session API", version = "1.0.1")
)
@Tag(name="Session", description="Register and login operations")
@Path("/session")
@Produces(MediaType.APPLICATION_JSON)
@Consumes({MediaType.APPLICATION_JSON})
public class SessionResource {
    @Inject
    protected UserRepository userRepository;

    @Inject
    protected GenerateViolationReport generateViolationReport;

    @Inject
    protected SessionRepository sessionRepository;

    @POST
    @Path("login")
    @Operation(summary = "User login")
    @APIResponse(
        responseCode = "200",
        description = "successful",
        content = @Content(
            schema = @Schema(
                implementation = LoginForSMSBody.class,
                properties = {
                    @SchemaProperty(name = "data", type = SchemaType.OBJECT, implementation = User.class),
                }
            )
        )
    )
    @APIResponse(
        responseCode = "400",
        description = "sms code is not valid",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ViolationReport.class
            )
        )
    )
    public Response login(@Valid LoginForSMSBody loginBody) {
        User user = sessionRepository.login(loginBody);
        return Response
                .status(HttpResponseStatus.OK.code())
                .entity(new ResultOfData<>(user))
                .build();
    }

    @POST
    @Path("register")
    @Operation(summary = "User register")
    public Response register(@Valid RegisterBody registerBody) {
        if(sessionRepository.validOfRegister(registerBody)) {
            User user = sessionRepository.create(registerBody);
            return Response
                .status(HttpResponseStatus.OK.code())
                .entity(user)
                .build();
        } else {
            return Response
                .status(HttpResponseStatus.BAD_REQUEST.code())
                .entity(sessionRepository.getViolationReport())
                .build();
        }
    }

    @GET
    @Path("user")
    @Authenticated
    @Transactional
    public Response user() {
        User user = userRepository.authUser();
        return Response.status(200).entity(user).build();
    }
}
