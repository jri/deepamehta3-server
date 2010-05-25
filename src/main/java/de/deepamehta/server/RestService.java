package de.deepamehta.server;

import de.deepamehta.server.resources.TopicResource;
import de.deepamehta.server.resources.RelationResource;
import de.deepamehta.server.resources.PluginResource;

import javax.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;



public class RestService extends Application {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public Set getClasses() {
        Set s = new HashSet();
        s.add(TopicResource.class);
        s.add(RelationResource.class);
        s.add(PluginResource.class);
        return s;
    }    
}
