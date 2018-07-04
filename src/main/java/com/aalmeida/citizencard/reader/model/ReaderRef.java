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

package com.aalmeida.citizencard.reader.model;

public class ReaderRef {

    private final String name;
    private final long handle;
    private long cardID;

    public ReaderRef(String name, long handle) {
        this.name = name;
        this.handle = handle;
    }

    public String getName() {
        return name;
    }

    public long getHandle() {
        return handle;
    }

    public long getCardID() {
        return cardID;
    }

    public void setCardID(long cardID) {
        this.cardID = cardID;
    }

    @Override
    public String toString() {
        return String.format("ReaderRef{name='%s', handle=%d, cardID=%d}", name, handle, cardID);
    }
}
