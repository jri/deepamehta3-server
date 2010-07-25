package de.deepamehta.server.resources;

import de.deepamehta.core.model.RelatedTopic;
import de.deepamehta.core.model.Topic;
import de.deepamehta.core.util.JSONHelper;
import de.deepamehta.core.util.UploadedFile;
import de.deepamehta.server.osgi.Activator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.io.File;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;



@Path("/command")
public class CommandResource {

    private Logger logger = Logger.getLogger(getClass().getName());

    @POST
    @Consumes("multipart/form-data")
    @Produces("text/plain")
    // Note: Although this request returns a JSON response we use text/plain as media type because (in contrast
    // to Safari) Firefox can't "display" an application/json response (e.g. in a hidden iframe) but always want
    // to save it to disc, even if a "Content-Disposition: inline" response header is set.
    public String executeCommand(FormDataMultiPart multiPart, @HeaderParam("Cookie") String cookie) throws Exception {
        String command = multiPart.getField("command").getValue();
        Map params = multiPartToMap(multiPart);
        //
        Map clientContext = JSONHelper.cookieToMap(cookie);
        logger.info("### cookie: " + clientContext);
        //
        return Activator.getService().executeCommand(command, params, clientContext).toString();
    }



    // ***********************
    // *** Private Helpers ***
    // ***********************



    private Map multiPartToMap(FormDataMultiPart multiPart) {
        Map params = new HashMap();
        Map<String, List<FormDataBodyPart>> fields = multiPart.getFields();
        logger.info("### multiPart: " + fields.size() + " parts");
        for (String name : fields.keySet()) {
            FormDataBodyPart part = fields.get(name).get(0);
            //
            if (part.isSimple()) {
                String value = part.getValue();
                logger.info("### \"" + name + "\" => \"" + value + "\"");
                if (!name.equals("command")) {
                    params.put(name, value);
                }
            } else {
                InputStream in = part.getValueAs(InputStream.class);
                String fileName = part.getContentDisposition().getFileName();
                String mimeType = part.getMediaType().toString();
                UploadedFile file = new UploadedFile(in, fileName, mimeType);
                logger.info("### \"" + name + "\" => " + file);
                params.put(name, file);
            }
        }
        return params;
    }
}
