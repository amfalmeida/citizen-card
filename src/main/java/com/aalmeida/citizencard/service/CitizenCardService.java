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

import com.aalmeida.citizencard.logging.Loggable;
import com.aalmeida.citizencard.reader.CitizenCard;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CitizenCardService implements Loggable {

    private final SimpMessagingTemplate template;
    private final CitizenCard citizenCard;

    public CitizenCardService(SimpMessagingTemplate template, CitizenCard citizenCard) {
        this.template = template;
        this.citizenCard = citizenCard;
    }

    @Scheduled(fixedRate = 1000)
    public void publishStatus() {
        //citizenCard.read();
        //template.convertAndSend("/topic/status", citizenCard.getStatus());
    }

}
