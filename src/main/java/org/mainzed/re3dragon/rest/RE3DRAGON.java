package org.mainzed.re3dragon.rest;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mainzed.re3dragon.config.POM;
import org.mainzed.re3dragon.log.Logging;
import org.mainzed.re3dragon.restconfig.ResponseGZIP;
import java.io.IOException;
import java.net.URI;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.mainzed.re3dragon.exceptions.ResourceNotAvailableException;
import org.mainzed.re3dragon.exceptions.RetcatException;

@Path("/")
public class RE3DRAGON {

    @GET
    @Path("/")
    @Tag(name = "Info")
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
    @Tag(name = "Info")
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
    public Response getInfo2(@HeaderParam("Accept-Encoding") String acceptEncoding, @HeaderParam("Accept") String acceptHeader) throws IOException {
        try {
            URI targetURIForRedirection = new URI("../swagger-ui/index.html");
            return Response.seeOther(targetURIForRedirection).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "org.mainzed.re3dragon.rest.RE3DRAGON"))
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        }
    }

    @GET
    @Path("/item")
    @Tag(name = "Item Info")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = String.class)
                    )
            ),
            description = "get item"
    )
    public Response getItem(@HeaderParam("Accept-Encoding") String acceptEncoding, @HeaderParam("Accept") String acceptHeader,
                            @QueryParam("uri") String uri) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            JSONObject jsonOut = new JSONObject();
            if (uri.contains("iconclass.org")) {
                jsonOut = IconClass.info(uri);
            }
            return ResponseGZIP.setResponse(acceptEncoding, jsonOut.toJSONString());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "org.mainzed.re3dragon.rest.RE3DRAGON"))
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        }
    }

    @GET
    @Path("/search")
    @Tag(name = "Item Search")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = String.class)
                    )
            ),
            description = "search items"
    )
    public Response searchItems(@HeaderParam("Accept-Encoding") String acceptEncoding, @HeaderParam("Accept") String acceptHeader,
                                @QueryParam("q") String q, @QueryParam("type") String type) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            JSONArray jsonOut = new JSONArray();
            if (type.equals("gettyaat")) {
                jsonOut = GettyAAT.query(q);
            }
            return ResponseGZIP.setResponse(acceptEncoding, jsonOut.toJSONString());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "org.mainzed.re3dragon.rest.RE3DRAGON"))
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        }
    }

    @GET
    @Path("/items")
    @Tag(name = "Item Search")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = String.class)
                    )
            ),
            description = "search item list"
    )
    public Response searchItemList(@HeaderParam("Accept-Encoding") String acceptEncoding, @HeaderParam("Accept") String acceptHeader,
                                   @QueryParam("ids") String ids, @QueryParam("type") String type) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            JSONArray jsonOut = new JSONArray();
            if (type.equals("gettyaat")) {
                jsonOut = GettyAAT.queryList(ids);
            }
            return ResponseGZIP.setResponse(acceptEncoding, jsonOut.toJSONString());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "org.mainzed.re3dragon.rest.RE3DRAGON"))
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        }
    }

}
