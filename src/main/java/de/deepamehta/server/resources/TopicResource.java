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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;



@Path("/topic")
// @Consumes("application/json")
// @Produces("application/json")
public class TopicResource {

    private Logger logger = Logger.getLogger(getClass().getName());

    @GET
    @Path("/{id}")
    public JSONObject getTopic(@PathParam("id") long id) throws JSONException {
        return Activator.getService().getTopic(id).toJSON();
    }

    @GET
    @Path("/{id}/related_topics")
    public JSONArray getRelatedTopics(@PathParam("id") long id, @QueryParam("exclude") List excludeRelTypes)
                                                                                        throws JSONException {
        logger.info("id=" + id + " exclude=" + excludeRelTypes.toString() +
            " (" + excludeRelTypes.size() + " items)");
        return listToJson(Activator.getService().getRelatedTopics(id, excludeRelTypes));
    }

    @GET
    public JSONObject searchTopics(@QueryParam("search") String searchTerm) throws JSONException {
        logger.info("searchTerm=" + searchTerm);
        return Activator.getService().searchTopics(searchTerm).toJSON();
    }

    @POST
    public JSONObject createTopic(JSONObject topic) throws JSONException {
        String type = topic.getString("type_id");
        Map properties = JSONHelper.toMap(topic.getJSONObject("properties"));
        return Activator.getService().createTopic(type, properties).toJSON();
    }

    @PUT
    @Path("/{id}")
    public void setTopicProperties(@PathParam("id") long id, JSONObject properties) throws JSONException {
        Activator.getService().setTopicProperties(id, JSONHelper.toMap(properties));
    }

    @DELETE
    @Path("/{id}")
    public void deleteTopic(@PathParam("id") long id) throws JSONException {
        Activator.getService().deleteTopic(id);
    }

    // *** Private Helpers ***

    private JSONArray listToJson(List<Topic> topics) throws JSONException {
        JSONArray array = new JSONArray();
        for (Topic topic : topics) {
            array.put(topic.toJSON());
        }
        return array;
    }
}
