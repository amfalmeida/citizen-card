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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@CacheConfig(cacheNames = "citizenCard")
public class CitizenCardService implements Loggable, CitizenCardEventListener {

    private final ApplicationEventPublisher publisher;
    private final CitizenCardReader citizenCard;

    public CitizenCardService(ApplicationEventPublisher publisher, CitizenCardReader citizenCard) {
        this.publisher = publisher;
        this.citizenCard = citizenCard;

        citizenCard.addListener(this);
    }

    public ReadingStatus getStatus() {
        return citizenCard.getStatus();
    }

    //@Cacheable(key = "#citizenCardData.getCardNumber()")
    public CardData getData() {
        return citizenCard.getCardData();
    }

    public byte[] getPicture(long id) {
        return citizenCard.getPicture(id);
    }

    @Override
    public void cardChangedEvent(ReadingStatus status) {
        this.publisher.publishEvent(status);
    }

    @Override
    public void cardReadEvent(CardData cardData) {
        this.publisher.publishEvent(cardData);
    }
}
