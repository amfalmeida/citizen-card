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
package com.aalmeida.citizencard.controller.model;

/**
 * @author Alexandre
 *
 */
public class WebSocketMessage {

    private String op;
    private String token;

    /**
     * Gets the op.
     *
     * @return the op
     */
    public String getOp() {
        return op;
    }

    /**
     * Sets the op.
     *
     * @param pOp
     *            the op to set
     */
    public void setOp(String pOp) {
        op = pOp;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param pToken the token to set
     */
    public void setToken(String pToken) {
        token = pToken;
    }





}
