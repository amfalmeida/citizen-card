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
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.util.JSONPObject;

import pteidlib.PteidException;

/**
 * The Class CitizenCardAPI.
 *
 * @author Alexandre
 */
@Path("rest")
public class CitizenCardAPI {

    /**
     * Check card.
     *
     * @param callback
     *            the callback
     * @return the string
     */
    @GET
    @Path("checkCard")
    @Produces(MediaType.APPLICATION_JSON)
    public String checkCard(@QueryParam("callback") String callback) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cardInserted", CitizenCard.getInstance().checkCard());
            return objectMapper.writeValueAsString(new JSONPObject(callback, map));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets the data.
     *
     * @param callback
     *            the callback
     * @return the data
     */
    @GET
    @Path("getData")
    @Produces(MediaType.APPLICATION_JSON)
    public String getData(@QueryParam("callback") String callback) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("data", CitizenCard.getInstance().getData());
            return objectMapper.writeValueAsString(new JSONPObject(callback, map));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (PteidException e) {
            e.printStackTrace();
            return null;
        }
    }
}
