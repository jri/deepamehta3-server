package de.deepamehta.server.resources;

import de.deepamehta.core.model.Topic;
import de.deepamehta.core.model.Relation;
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



@Path("/relation")
// @Consumes("application/json")
// @Produces("application/json")
public class RelationResource {

    private Logger logger = Logger.getLogger(getClass().getName());

    @GET
    public JSONObject getRelation(@QueryParam("src") long srcTopicId, @QueryParam("dst") long dstTopicId)
                                                                                        throws JSONException {
        logger.info("srcTopicId=" + srcTopicId + " dstTopicId=" + dstTopicId);
        //
        Relation rel = Activator.getService().getRelation(srcTopicId, dstTopicId);
        //
        if (rel != null) {
            return rel.toJSON();
        }
        return null;
    }

    @POST
    public JSONObject createRelation(JSONObject relation) throws JSONException{
        //
        String typeId = relation.getString("type_id");
        long srcTopicId = relation.getLong("src_topic_id");
        long dstTopicId = relation.getLong("dst_topic_id");
        Map properties = JSONHelper.toMap(relation.getJSONObject("properties"));
        //
        Relation rel = Activator.getService().createRelation(typeId, srcTopicId, dstTopicId, properties);
        //
        JSONObject response = new JSONObject();
        response.put("relation_id", rel.id);
        return response;
    }

    @DELETE
    @Path("/{id}")
    public void deleteRelation(@PathParam("id") long id) {
        Activator.getService().deleteRelation(id);
    }
}
