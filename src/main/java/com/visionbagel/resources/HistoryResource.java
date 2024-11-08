package com.visionbagel.resources;

import com.visionbagel.entitys.History;
import com.visionbagel.payload.PageParams;
import com.visionbagel.payload.ResultOfPaging;
import com.visionbagel.repositorys.HistoryRepository;
import com.visionbagel.repositorys.UserRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

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
@Path("/history")
@RolesAllowed({ "User" })
public class HistoryResource {

    @Inject
    public UserRepository userRepository;

    @Inject
    public HistoryRepository historyRepository;

    @Tag(name = "History", description = "Operations related to History")
    @Operation(summary = "Create History")
    @APIResponse(
        responseCode = "200",
        description = "Create History",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = History.class
            )
        )
    )
    @POST()
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(History history) {
        return Response
            .status(200)
            .entity(this.historyRepository.create(history))
            .build();
    }

    @Tag(name = "History", description = "Operations related to History")
    @Operation(summary = "Create History")
    @APIResponse(
            responseCode = "200",
            description = "Create History",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = History.class
                    )
            )
    )
    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@BeanParam PageParams pageParams) {
        return Response
                .status(200)
                .entity(
                        new ResultOfPaging<>(
                                History.find("user", Sort.by("whenCreated"), userRepository.authUser()),
                                PageParams.of(pageParams)
                        )
                )
                .build();
    }
}
