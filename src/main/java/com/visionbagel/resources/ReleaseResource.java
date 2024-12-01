package com.visionbagel.resources;

import com.alipay.api.AlipayApiException;
import com.visionbagel.entitys.Example;
import com.visionbagel.payload.PageParams;
import com.visionbagel.payload.ResultOfPaging;
import com.visionbagel.repositorys.ExampleRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.api.validation.ViolationReport;

import java.util.HashMap;
import java.util.Map;

@Path("/release")
public class ReleaseResource {

    @Inject
    public ExampleRepository exampleRepository;

    @Tag(name = "Hello", description = "Operations related to gaskets")
    @Operation(summary = "Update an existing pet")
    @APIResponse(
        responseCode = "200",
        description = "Example List",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ResultOfPaging.class,
                properties = {
                    @SchemaProperty(name = "records", type = SchemaType.ARRAY, implementation = Example.class),
                }
            )
        )
    )
    @APIResponse(
        responseCode = "400",
        description = "User not found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ViolationReport.class
            )
        )
    )
    @GET()
    @Path("{target}/{arch}/{current_version}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> hello(
        @PathParam("target") String target,
        @PathParam("arch") String arch,
        @PathParam("current_version") String current_version
    ) {
        System.out.println(target);
        System.out.println(arch);
        System.out.println(current_version);

        return Map.of(
            "version", "x",
            "pub_date", "x",
            "url", "x",
            "signature", "x",
            "notes", "x"
        );
    }
}
