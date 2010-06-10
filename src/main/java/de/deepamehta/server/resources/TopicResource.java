package de.deepamehta.server.resources;

import de.deepamehta.core.model.Topic;
import de.deepamehta.core.util.JSONHelper;
import de.deepamehta.server.osgi.Activator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;



@Path("/topic")
@Consumes("application/json")
@Produces("application/json")
public class TopicResource {

    private Logger logger = Logger.getLogger(getClass().getName());

    @GET
    @Path("/{id}")
    public JSONObject getTopic(@PathParam("id") long id) throws JSONException {
        return Activator.getService().getTopic(id).toJSON();
    }

    @GET
    @Path("/{id}/related_topics")
    public JSONArray getRelatedTopics(@PathParam("id") long id,
                                      @QueryParam("include_topic_types") List includeTopicTypes,
                                      @QueryParam("exclude_rel_types") List excludeRelTypes) throws JSONException {
        logger.info("id=" + id +
            ", includeTopicTypes=" + includeTopicTypes + " (" + includeTopicTypes.size() + " include items)" +
            ", excludeRelTypes=" + excludeRelTypes + " (" + excludeRelTypes.size() + " exclude items)");
        return listToJson(Activator.getService().getRelatedTopics(id, includeTopicTypes, excludeRelTypes));
    }

    @GET
    @Path("/by_type/{typeId}")
    public JSONArray getTopics(@PathParam("typeId") String typeId) throws JSONException {
        logger.info("typeId=" + typeId);
        return listToJson(Activator.getService().getTopics(typeId));
    }

    @GET
    public JSONObject searchTopics(@QueryParam("search") String searchTerm,
                                   @QueryParam("field")  String fieldName,
                                   @QueryParam("wholeword") boolean wholeWord) throws JSONException {
        logger.info("searchTerm=" + searchTerm + ", fieldName=" + fieldName + ", wholeWord=" + wholeWord);
        return Activator.getService().searchTopics(searchTerm, fieldName, wholeWord).toJSON();
    }

    @POST
    public JSONObject createTopic(JSONObject topic, @HeaderParam("Cookie") String cookie) throws JSONException {
        String typeId = topic.getString("type_id");
        Map properties = JSONHelper.toMap(topic.getJSONObject("properties"));
        Map clientContext = cookieToMap(cookie);
        logger.info("### cookie: " + clientContext);
        //
        return Activator.getService().createTopic(typeId, properties, clientContext).toJSON();
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



    // ***********************
    // *** Private Helpers ***
    // ***********************



    private JSONArray listToJson(List<Topic> topics) throws JSONException {
        JSONArray array = new JSONArray();
        for (Topic topic : topics) {
            array.put(topic.toJSON());
        }
        return array;
    }

    /**
      * Converts a "Cookie" header value (String) to a map (key=String, value=String).
      * E.g. "user=jri; workspace_id=123" => {"user"="jri", "workspace_id"="123"}
      */
    private Map<String, String> cookieToMap(String cookie) {
        Map cookieValues = new HashMap();
        if (cookie != null) {
            for (String value : cookie.split("; ")) {
                String[] val = value.split("=");
                cookieValues.put(val[0], val[1]);
            }
        }
        return cookieValues;
    }
}
