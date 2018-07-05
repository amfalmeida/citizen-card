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

package com.aalmeida.citizencard.service;

import com.aalmeida.citizencard.reader.model.CardData;
import com.aalmeida.citizencard.reader.model.ReadingStatus;
import com.aalmeida.citizencard.logging.Loggable;
import com.aalmeida.citizencard.reader.CitizenCardReader;
import com.aalmeida.citizencard.reader.CitizenCardEventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class CitizenCardService implements Loggable, CitizenCardEventListener {

    private final SimpMessagingTemplate template;
    private final CitizenCardReader citizenCard;

    public CitizenCardService(SimpMessagingTemplate template, CitizenCardReader citizenCard) {
        this.template = template;
        this.citizenCard = citizenCard;

        citizenCard.addListener(this);
    }

    public ReadingStatus getStatus() {
        return citizenCard.getStatus();
    }

    public CardData getData() {
        return citizenCard.getCardData();
    }

    public byte[] getPicture(long id) {
        return citizenCard.getPicture(id);
    }

    @Override
    public void cardChangedEvent(ReadingStatus status) {
        template.convertAndSend("/topic/status", status);
    }

    @Override
    public void cardReadEvent(CardData citizenCardData) {
        template.convertAndSend("/topic/data", citizenCardData);
    }
}
