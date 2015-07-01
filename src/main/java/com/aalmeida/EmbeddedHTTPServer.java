/*
 * Copyright (c) 2015 Alexandre Almeida.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aalmeida;

import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import com.aalmeida.citizencard.CitizenCard;
import com.aalmeida.citizencard.CitizenCardAPI;
import com.aalmeida.citizencard.CitizenCardWebSocket;

/**
 * The Class EmbeddedHTTPServer.
 *
 * @author Alexandre
 */
public class EmbeddedHTTPServer {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Embedded HTTPServer...\n");

        CitizenCard.init();

        Server server = new Server(9095);

//        ResourceHandler resource_handler = new ResourceHandler();
//        resource_handler.setDirectoriesListed(true);
//        resource_handler.setWelcomeFiles(new String[] { "index.html" });
//        resource_handler.setResourceBase(".");
//
//        HandlerList handlers = new HandlerList();
//        handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
//        server.setHandler(handlers);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);
        wscontainer.addEndpoint(CitizenCardWebSocket.class);

        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(1);

        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", CitizenCardAPI.class.getCanonicalName());

        server.start();
        server.join();
    }
}
