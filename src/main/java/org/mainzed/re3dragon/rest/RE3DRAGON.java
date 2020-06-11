package org.mainzed.re3dragon.rest;

import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import com.sun.jersey.server.impl.modelapi.annotation.IntrospectionModeller;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mainzed.re3dragon.config.POM;
import org.mainzed.re3dragon.log.Logging;
import org.mainzed.re3dragon.restconfig.ResponseGZIP;
import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Path("/")
public class RE3DRAGON {

    @GET
    @Path("/")
    @Tag(name = "Example")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(
                schema = @Schema(implementation = String.class)
            )
        ),
        description = "List of \"Hello, world!\"-messages."
    )
    public Response getInfo(@HeaderParam("Accept-Encoding") String acceptEncoding, @HeaderParam("Accept") String acceptHeader) throws IOException {
        try {
            return ResponseGZIP.setResponse(acceptEncoding, POM.getInfo().toString());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "org.mainzed.re3dragon.rest.RE3DRAGON"))
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        }
    }

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response getInfo2(@HeaderParam("Accept-Encoding") String acceptEncoding, @HeaderParam("Accept") String acceptHeader) throws IOException {
        try {
            AbstractResource resource = IntrospectionModeller.createResource(RE3DRAGON.class);
            JSONArray arr = new JSONArray();
            for (AbstractResourceMethod srm : resource.getResourceMethods()) {
                JSONObject tmp = new JSONObject();
                tmp.put("path", resource.getPath().getValue());
                tmp.put("method", srm.getHttpMethod());
                arr.add(tmp);
            }
            for (AbstractSubResourceMethod srm : resource.getSubResourceMethods()) {
                JSONObject tmp = new JSONObject();
                tmp.put("path", resource.getPath().getValue());
                tmp.put("method", srm.getHttpMethod());
                tmp.put("path2", srm.getPath().getValue());
                arr.add(tmp);
            }
            return ResponseGZIP.setResponse(acceptEncoding, arr.toString());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "org.mainzed.re3dragon.rest.RE3DRAGON"))
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        }
    }

}
