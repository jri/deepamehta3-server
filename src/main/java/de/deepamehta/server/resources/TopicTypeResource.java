package de.deepamehta.server.resources;

import de.deepamehta.core.model.Topic;
import de.deepamehta.core.util.JSONHelper;
import de.deepamehta.server.osgi.Activator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;



@Path("/topictype")
public class TopicTypeResource {

    private Logger logger = Logger.getLogger(getClass().getName());

    @GET
    public JSONArray getTopicTypeIds() throws JSONException {
        JSONArray typeIds = new JSONArray();
        for (String tpyeId : Activator.getService().getTopicTypeIds()) {
            typeIds.put(tpyeId);
        }
        return typeIds;
    }

    @GET
    @Path("/{id}")
    public JSONObject getTopic(@PathParam("id") String id) throws JSONException {
        return Activator.getService().getTopicType(id).toJSON();
    }
}
