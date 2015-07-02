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

import java.util.EventListener;

import com.aalmeida.citizencard.entities.CitizenCardData;
import com.aalmeida.citizencard.entities.ReadingStatus;

/**
 * The listener interface for receiving ICitizenCardEvent events. The class that
 * is interested in processing a ICitizenCardEvent event implements this
 * interface, and the object created with that class is registered with a
 * component using the component's
 * <code>addICitizenCardEventListener<code> method. When
 * the ICitizenCardEvent event occurs, that object's appropriate
 * method is invoked.
 *
 * @author Alexandre
 */
public interface ICitizenCardEventListener extends EventListener {
    
    /**
     * Card state changed event.
     *
     * @param ccData
     *            the cc data
     * @param status
     *            the status
     */
    void cardChangedEvent(final CitizenCardData ccData, final ReadingStatus.Status status);

}
