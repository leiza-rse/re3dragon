package org.mainzed.re3dragon.rest;

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

@Path("/")
public class RE3DRAGON {

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response getInfo(@HeaderParam("Accept-Encoding") String acceptEncoding, @HeaderParam("Accept") String acceptHeader) throws IOException {
        try {
            return ResponseGZIP.setResponse(acceptEncoding, POM.getInfo().toString());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "org.mainzed.re3dragon.rest.AlligatorAPI"))
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        }
    }

}
