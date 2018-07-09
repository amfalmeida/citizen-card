/*
 * Copyright (c) 2018 Alexandre Almeida.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.aalmeida.citizencard;

import com.aalmeida.citizencard.logging.Loggable;
import com.aalmeida.citizencard.reader.CitizenCardReader;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class ApplicationListener implements Loggable {

    @EventListener(ContextRefreshedEvent.class)
    public void onStart() {

    }

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        if(!Desktop.isDesktopSupported()){
            logger().warn("Desktop is not supported.");
            return;
        }
        try {
            URI homepage = new URI("http://localhost:8080/");
            Desktop.getDesktop().browse(homepage);
        } catch (URISyntaxException | IOException e) {
            logger().error("Failed to open browser.", e);
        }
    }

    @EventListener(ContextClosedEvent.class)
    public void contextClosedEvent() {

    }


}
