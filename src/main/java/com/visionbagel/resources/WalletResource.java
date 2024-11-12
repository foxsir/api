package com.visionbagel.resources;

import com.visionbagel.entitys.User;
import com.visionbagel.entitys.Wallet;
import com.visionbagel.payload.ResultOfData;
import com.visionbagel.repositorys.UserRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.Optional;

@Path("/wallet")
@RolesAllowed({ "User" })
public class WalletResource {
    @Inject
    public UserRepository userRepository;

    @GET()
    @APIResponse(
            responseCode = "200",
            description = "get wallet info",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ResultOfData.class,
                            properties = {
                                    @SchemaProperty(name = "data", type = SchemaType.OBJECT, implementation = Wallet.class),
                            }
                    )
            )
    )
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        User user = userRepository.authUser();
        Optional<Wallet> wallet = Wallet.find("user", user).firstResultOptional();

        if(wallet.isPresent()) {
            return Response.status(Response.Status.OK.getStatusCode()).entity(
                    new ResultOfData<>(wallet.get())
            ).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity(
                    new ResultOfData<>(new Wallet())
            ).build();
        }
    }
}
