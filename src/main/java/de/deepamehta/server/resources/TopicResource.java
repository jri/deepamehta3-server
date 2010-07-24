package de.deepamehta.server.resources;

import de.deepamehta.core.model.RelatedTopic;
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
                                      @QueryParam("include_rel_types")   List includeRelTypes,
                                      @QueryParam("exclude_rel_types")   List excludeRelTypes) throws JSONException {
        logger.info("id=" + id +
            ", includeTopicTypes=" + includeTopicTypes + " (" + includeTopicTypes.size() + " include items)" +
            ", includeRelTypes="   + includeRelTypes   + " (" + includeRelTypes.size()   + " include items)" +
            ", excludeRelTypes="   + excludeRelTypes   + " (" + excludeRelTypes.size()   + " exclude items)");
        return JSONHelper.relatedTopicsToJson(Activator.getService().getRelatedTopics(id, includeTopicTypes,
                                                                                          includeRelTypes,
                                                                                          excludeRelTypes));
    }

    @GET
    @Path("/by_type/{typeUri}")
    public JSONArray getTopics(@PathParam("typeUri") String typeUri) throws JSONException {
        logger.info("typeUri=" + typeUri);
        return JSONHelper.topicsToJson(Activator.getService().getTopics(typeUri));
    }

    @GET
    public JSONObject searchTopics(@QueryParam("search") String searchTerm,
                                   @QueryParam("field")  String fieldName,
                                   @QueryParam("wholeword") boolean wholeWord,
                                   @HeaderParam("Cookie") String cookie) throws JSONException {
        Map clientContext = JSONHelper.cookieToMap(cookie);
        logger.info("searchTerm=" + searchTerm + ", fieldName=" + fieldName + ", wholeWord=" + wholeWord +
            ", cookie=" + clientContext);
        return Activator.getService().searchTopics(searchTerm, fieldName, wholeWord, clientContext).toJSON();
    }

    @POST
    public JSONObject createTopic(JSONObject topic, @HeaderParam("Cookie") String cookie) throws JSONException {
        String typeUri = topic.getString("type_uri");
        Map properties = JSONHelper.toMap(topic.getJSONObject("properties"));
        Map clientContext = JSONHelper.cookieToMap(cookie);
        logger.info("### cookie: " + clientContext);
        //
        return Activator.getService().createTopic(typeUri, properties, clientContext).toJSON();
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
}
