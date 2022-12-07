package org.mainzed.re3dragon.rest;

import io.swagger.v3.oas.annotations.Hidden;
import link.archaeology.re3dragon.action.IconClass;
import link.archaeology.re3dragon.action.Wikidata;
import link.archaeology.re3dragon.action.GettyAAT;
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
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import link.archaeology.re3dragon.action.GND;
import link.archaeology.re3dragon.action.Lair;
import link.archaeology.re3dragon.action.LinkedSamianWare;
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
    @Hidden
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
    @Hidden
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
                            @QueryParam("uri") String uri, @QueryParam("format") String format) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            if (format == null) {
                format = "json";
            }
            if (format.contains("html")) {
                URI targetURIForRedirection = new URI("../dragonitem.html" + "?uri=" + uri.replace("(", "%28").replace(")", "%29"));
                return Response.seeOther(targetURIForRedirection).build();
            } else if (format.contains("geojson")) {
                return null; // https://github.com/linkedgeodesy/geojson-plus/blob/master/datamodel.md - http://geojsonplus.linkedgeodesy.org/
                // beispiel https://chronontology.dainst.org/spi/place/getty/7004449 - https://chronontology.dainst.org/spi/place?q=Mainz&type=getty
            } else {
                JSONObject jsonOut = new JSONObject();
                if (uri.contains("iconclass.org")) {
                    jsonOut = IconClass.item(uri);
                } else if (uri.contains("wikidata.org")) {
                    jsonOut = Wikidata.info(uri);
                } else if (uri.contains("/aat/")) {
                    jsonOut = GettyAAT.info(uri);
                } else if (uri.contains("lod.archaeology.link/data/samian/")) {
                    jsonOut = LinkedSamianWare.item(uri);
                } else if (uri.contains("/gnd/")) {
                    jsonOut = GND.info(uri);
                }
                return ResponseGZIP.setResponse(acceptEncoding, jsonOut.toJSONString());
            }
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
                                @QueryParam("q") String q, @QueryParam("type") String type, @QueryParam("format") String format, @QueryParam("repo") String repo) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            if (format == null) {
                format = "json";
            }
            if (format.contains("html")) {
                URI targetURIForRedirection = new URI("../dragonitems.html" + "?q=" + q + "&repo=" + repo);
                return Response.seeOther(targetURIForRedirection).build();
            } else if (format.contains("geojson")) {
                return null; // https://github.com/linkedgeodesy/geojson-plus/blob/master/datamodel.md - http://geojsonplus.linkedgeodesy.org/
                // beispiel https://chronontology.dainst.org/spi/place/getty/7004449 - https://chronontology.dainst.org/spi/place?q=Mainz&type=getty
            } else {
                JSONArray jsonOut = new JSONArray();
                if (repo.contains("iconclass")) {
                    jsonOut = IconClass.search(q);
                } else if (repo.contains("wikidata")) {
                    jsonOut = Wikidata.search(q);
                } else if (repo.contains("gettyaat")) {
                    jsonOut = GettyAAT.search(q);
                } else if (repo.contains("linkedsamianware")) {
                    ///jsonOut = LinkedSamianWare.item(uri);
                }
                return ResponseGZIP.setResponse(acceptEncoding, jsonOut.toJSONString());
            }
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
                                   @QueryParam("ids") String ids, @QueryParam("type") String type, @QueryParam("format") String format) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            if (format == null) {
                format = "json";
            }
            if (format.contains("html")) {
                URI targetURIForRedirection = new URI("../dragonitems.html" + "?ids=" + ids);
                return Response.seeOther(targetURIForRedirection).build();
            } else if (format.contains("geojson")) {
                return null; // https://github.com/linkedgeodesy/geojson-plus/blob/master/datamodel.md - http://geojsonplus.linkedgeodesy.org/
                // beispiel https://chronontology.dainst.org/spi/place/getty/7004449 - https://chronontology.dainst.org/spi/place?q=Mainz&type=getty
            } else {
                JSONArray jsonOut = new JSONArray();
                if (ids.contains("iconclass.org")) {
                    jsonOut = IconClass.items(ids);
                } else if (ids.contains("wikidata.org")) {
                    jsonOut = Wikidata.items(ids);
                } else if (ids.contains("/aat/")) {
                    jsonOut = GettyAAT.items(ids);
                } else if (ids.contains("lod.archaeology.link/data/samian/")) {
                    //jsonOut = LinkedSamianWare.item(ids);
                }
                return ResponseGZIP.setResponse(acceptEncoding, jsonOut.toJSONString());
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "org.mainzed.re3dragon.rest.RE3DRAGON"))
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        }
    }
    
    @POST
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
            description = "search item list by POST"
    )
    public Response searchItemListPOST(@HeaderParam("Accept-Encoding") String acceptEncoding, @HeaderParam("Accept") String acceptHeader,
                                   @FormParam("ids") String ids, @FormParam("type") String type, @FormParam("format") String format) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        int z = 0;
        z = 0;
        try {
            if (format == null) {
                format = "json";
            }
            if (format.contains("html")) {
                URI targetURIForRedirection = new URI("../dragonitems.html" + "?ids=" + ids);
                return Response.seeOther(targetURIForRedirection).build();
            } else if (format.contains("geojson")) {
                return null; // https://github.com/linkedgeodesy/geojson-plus/blob/master/datamodel.md - http://geojsonplus.linkedgeodesy.org/
                // beispiel https://chronontology.dainst.org/spi/place/getty/7004449 - https://chronontology.dainst.org/spi/place?q=Mainz&type=getty
            } else {
                JSONArray jsonOut = new JSONArray();
                if (ids.contains("iconclass.org")) {
                    jsonOut = IconClass.items(ids);
                } else if (ids.contains("wikidata.org")) {
                    jsonOut = Wikidata.items(ids);
                } else if (ids.contains("/aat/")) {
                    jsonOut = GettyAAT.items(ids);
                } else if (ids.contains("lod.archaeology.link/data/samian/")) {
                    //jsonOut = LinkedSamianWare.item(ids);
                }
                return ResponseGZIP.setResponse(acceptEncoding, jsonOut.toJSONString());
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "org.mainzed.re3dragon.rest.RE3DRAGON"))
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        }
    }

    @GET
    @Path("/lairs")
    @Tag(name = "Lair Overview")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = String.class)
                    )
            ),
            description = "lair overview"
    )
    public Response getLairs(@HeaderParam("Accept-Encoding") String acceptEncoding, @HeaderParam("Accept") String acceptHeader,
                             @QueryParam("ids") String ids, @QueryParam("type") String type) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            return ResponseGZIP.setResponse(acceptEncoding, Lair.lairs().toJSONString());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "org.mainzed.re3dragon.rest.RE3DRAGON"))
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        }
    }

}
