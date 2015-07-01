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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
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

    private static void openBrowser() {
        String url = "http://localhost:9095/index.html";

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Embedded HTTPServer...\n");

        String currDir = System.getProperty("user.dir");
        String webdir = currDir + "\\src\\main\\webapp\\";

        CitizenCard.init();

        File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        File scdir = new File(tmpdir.toString(), "embedded-jetty-jsp");

        if (!scdir.exists()) {
            if (!scdir.mkdirs()) {
                throw new IOException("Unable to create scratch directory: " + scdir);
            }
        }

        Server server = new Server(9095);

        ResourceHandler htmlHandler = new ResourceHandler();
        htmlHandler.setResourceBase(webdir);

        ServletContextHandler apiHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        apiHandler.setContextPath("/websocket/*");
        
        HandlerList hl = new HandlerList();
        hl.setHandlers(new Handler[]{htmlHandler, apiHandler});
        server.setHandler(hl);

        ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(apiHandler);
        wscontainer.addEndpoint(CitizenCardWebSocket.class);

        ServletHolder jerseyServlet = apiHandler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/api/*");
        jerseyServlet.setInitOrder(1);
        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", CitizenCardAPI.class.getCanonicalName());

        openBrowser();
        
        server.start();
        server.join();
    }
}
