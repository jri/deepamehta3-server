package de.deepamehta.server.resources;

import de.deepamehta.core.model.DataField;
import de.deepamehta.core.model.Topic;
import de.deepamehta.core.model.TopicType;
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
@Consumes("application/json")
@Produces("application/json")
public class TopicTypeResource {

    private Logger logger = Logger.getLogger(getClass().getName());

    @GET
    public JSONArray getTopicTypeUris() throws JSONException {
        JSONArray typeUris = new JSONArray();
        for (String typeUri : Activator.getService().getTopicTypeUris()) {
            typeUris.put(typeUri);
        }
        return typeUris;
    }

    @GET
    @Path("/{id}")
    public JSONObject getTopicType(@PathParam("id") String id) throws Exception {
        return Activator.getService().getTopicType(id).toJSON();
    }

    @POST
    public JSONObject createTopicType(JSONObject topicType) throws Exception {
        TopicType tt = new TopicType(topicType);
        return Activator.getService().createTopicType(tt.getProperties(), tt.getDataFields()).toJSON();
    }

    @POST
    @Path("/{id}")
    public void addDataField(@PathParam("id") String id, JSONObject dataField) throws Exception {
        Activator.getService().addDataField(id, new DataField(dataField));
    }

    @PUT
    @Path("/{id}")
    public void updateDataField(@PathParam("id") String id, JSONObject dataField) throws Exception {
        Activator.getService().updateDataField(id, new DataField(dataField));
    }

    @DELETE
    @Path("/{id}/field/{fieldUri}")
    public void removeDataField(@PathParam("id") String id, @PathParam("fieldUri") String fieldUri) throws Exception {
        Activator.getService().removeDataField(id, fieldUri);
    }
}
