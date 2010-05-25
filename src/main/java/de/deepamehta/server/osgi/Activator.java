package de.deepamehta.server.osgi;

import de.deepamehta.core.service.DeepaMehtaService;

import com.sun.jersey.spi.container.servlet.ServletContainer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;



public class Activator implements BundleActivator {

    private static ServiceTracker deepamehtaServiceTracker;
    private ServiceTracker httpServiceTracker;

    private HttpService httpService = null;

    private Logger logger = Logger.getLogger(getClass().getName());



    // **************************************
    // *** BundleActivator Implementation ***
    // **************************************



    public void start(BundleContext context) {
        logger.info("Starting DeepaMehta Server bundle");
        //
        httpServiceTracker = createHttpServiceTracker(context);
        httpServiceTracker.open();
        //
        deepamehtaServiceTracker = createDeepamehtaServiceTracker(context);
        deepamehtaServiceTracker.open();
    }

    public void stop(BundleContext context) {
        logger.info("Stopping DeepaMehta Server bundle");
        //
        httpServiceTracker.close();
        deepamehtaServiceTracker.close();
    }



    // **************
    // *** Public ***
    // **************



    public static DeepaMehtaService getService() {
        DeepaMehtaService service = (DeepaMehtaService) deepamehtaServiceTracker.getService();
        if (service == null) {
            throw new RuntimeException("DeepaMehta service is currently not available");
        }
        return service;
    }



    // ***********************
    // *** Private Helpers ***
    // ***********************



    private ServiceTracker createHttpServiceTracker(BundleContext context) {
        return new ServiceTracker(context, HttpService.class.getName(), null) {

            @Override
            public Object addingService(ServiceReference serviceRef) {
                logger.info("Adding HTTP service");
                httpService = (HttpService) super.addingService(serviceRef);
                registerServlet();
                return httpService;
            }

            @Override
            public void removedService(ServiceReference ref, Object service) {
                if (service == httpService) {
                    logger.info("Removing HTTP service");
                    unregisterServlet();
                    httpService = null;
                }
                super.removedService(ref, service);
            }
        };
    }

    private ServiceTracker createDeepamehtaServiceTracker(BundleContext context) {
        return new ServiceTracker(context, DeepaMehtaService.class.getName(), null);
    }

    // ---

    private void registerServlet() {
        try {
            Dictionary initParams = new Hashtable();
            // initParams.put("com.sun.jersey.config.property.packages", "de.deepamehta.service.rest.resources");
            initParams.put("javax.ws.rs.Application", "de.deepamehta.server.RestService");
        	//
            logger.info("Registering REST resources");
            httpService.registerServlet("/rest", new ServletContainer(), initParams, null);
            logger.info("REST recources registered!");
        } catch (Throwable ie) {
            throw new RuntimeException(ie);
        }
    }

    private void unregisterServlet() {
        if (httpService != null) {
            logger.info("Unregistering REST resources");
            httpService.unregister("/rest");
        }
    }
}
