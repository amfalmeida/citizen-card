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
package com.aalmeida.citizencard;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jackson.map.ObjectMapper;

import pteidlib.PteidException;

import com.aalmeida.citizencard.entities.WebSocketMessage;

/**
 * The Class CitizenCardWebSocket.
 *
 * @author Alexandre
 */
@ServerEndpoint("/citizensocket")
public class CitizenCardWebSocket {

    private static final Set<Session> SESSIONS = Collections.synchronizedSet(new HashSet<Session>());
    private static boolean isCardInserted = false;

    private final Timer cardCheckTimer = new Timer(true);
    
    public CitizenCardWebSocket() {
        cardCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String token = CitizenCard.getInstance().checkCard();
                    boolean isCardPresent = token != null && !token.trim().isEmpty();
                    if (isCardPresent != isCardInserted) {
                        isCardInserted = isCardPresent;
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("cardInserted", token != null && !token.trim().isEmpty());
                        map.put("token", token);              
                        sendMessageToAll(objectMapper.writeValueAsString(map));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5000);
    }

    /**
     * On open.
     *
     * @param session
     *            the session
     */
    @OnOpen
    public void onOpen(final Session session) {
        System.out.println(session.getId() + " has opened a connection");
        SESSIONS.add(session);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = new HashMap<String, Object>();
            String token = CitizenCard.getInstance().checkCard();
            map.put("cardInserted", token != null && !token.trim().isEmpty());
            map.put("token", token);
            session.getBasicRemote().sendText(objectMapper.writeValueAsString(map));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * On message.
     *
     * @param message
     *            the message
     * @param session
     *            the session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message from " + session.getId() + ": " + message);
        ObjectMapper mapper = new ObjectMapper();
        try {
            WebSocketMessage msg = mapper.readValue(message, WebSocketMessage.class);
            if (msg == null) {
                return;
            }
            if (msg.getOp() != null && msg.getOp().equals("getData")) {
                Map<String, Object> map = new HashMap<String, Object>();
                if (msg.getToken() != null && !msg.getToken().trim().isEmpty()) {
                    map.put("data", CitizenCard.getInstance().getData(msg.getToken()));
                } else {
                    map.put("data", CitizenCard.getInstance().getData());    
                }
                session.getBasicRemote().sendText(mapper.writeValueAsString(map));
                return;
            }
            session.getBasicRemote().sendText(message);
        } catch (IOException | PteidException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * On close.
     *
     * @param session
     *            the session
     */
    @OnClose
    public void onClose(Session session) {
        System.out.println("Session " + session.getId() + " has ended");
    }

    /**
     * Send message to all.
     *
     * @param message
     *            the message
     */
    private void sendMessageToAll(final String message) {
        if (SESSIONS == null || SESSIONS.isEmpty()) {
            return;
        }
        for (Session s : SESSIONS) {
            try {
                s.getBasicRemote().sendObject(message);
            } catch (IOException | EncodeException ex) {
                ex.printStackTrace();
            }
        }
    }

}
