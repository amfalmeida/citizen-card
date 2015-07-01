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

import java.util.HashMap;
import java.util.Map;

import com.aalmeida.citizencard.entities.CitizenCardData;

import pteidlib.PTEID_ID;
import pteidlib.PTEID_TokenInfo;
import pteidlib.PteidException;
import pteidlib.pteid;

/**
 * The Class CitizenCard.
 *
 * @author Alexandre
 */
public class CitizenCard {
    static {
        try {
            System.loadLibrary("pteidlibj");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Não foi possivel carregar a biblioteca.\n" + e);
        }
    }

    private static CitizenCard instance;
    private static Map<String, CitizenCardData> dataCache;

    private CitizenCard() {
        dataCache = new HashMap<String, CitizenCardData>();
    }

    /**
     * Inits the.
     */
    public static void init() {
        instance = new CitizenCard();
    }
    
    /**
     * Exit.
     */
    private void exit() {
        try {
            pteid.Exit(pteid.PTEID_EXIT_LEAVE_CARD);
        } catch (PteidException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the single instance of CitizenCard.
     *
     * @return single instance of CitizenCard
     */
    public static CitizenCard getInstance() {
        return instance;
    }

    /**
     * Check card.
     *
     * @return true, if successful
     */
    public String checkCard() {
        try {
            pteid.Init("");
            pteid.SetSODChecking(false);

            return getToken();
        } catch (PteidException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            exit();
        }
    }

    /**
     * Gets the data.
     *
     * @return the data
     * @throws PteidException
     *             the pteid exception
     */
    public CitizenCardData getData() throws PteidException {
        try {
            pteid.Init("");
            pteid.SetSODChecking(false);

            String token = getToken();
            if (dataCache.containsKey(token)) {
                return dataCache.get(token);
            }
            
            CitizenCardData ccData = new CitizenCardData();
            ccData.setToken(token);
            while (ccData.getFirstName() == null) {
                PTEID_ID idData = pteid.GetID();
                ccData.setFirstName(idData.firstname);
                ccData.setSurname(idData.name);
                dataCache.put(token, ccData);
            }
            return ccData;
        } finally {
            exit();
        }
    }
    
    public CitizenCardData getData(final String token) throws PteidException {
        if (dataCache.containsKey(token)) {
            return dataCache.get(token);
        }
        return getData();
    }

    /**
     * Inits the and retrive token.
     *
     * @return the string
     * @throws PteidException
     *             the pteid exception
     */
    private String getToken() throws PteidException {
        PTEID_TokenInfo token = pteid.GetTokenInfo();
        return token.serial;
    }
}
