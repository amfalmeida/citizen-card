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

package com.aalmeida.citizencard.controller;

import com.aalmeida.citizencard.reader.model.CardData;
import com.aalmeida.citizencard.reader.model.ReadingStatus;
import com.aalmeida.citizencard.service.CitizenCardService;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
public class HomeController {

    private final CitizenCardService citizenCardService;
    private final SimpMessagingTemplate template;

    public HomeController(CitizenCardService citizenCardService, SimpMessagingTemplate template) {
        this.citizenCardService = citizenCardService;
        this.template = template;
    }

    @MessageMapping("/status")
    public void checkStatus() {
        ReadingStatus readingStatus = citizenCardService.getStatus();
        if (ReadingStatus.READ.equals(readingStatus)) {
            template.convertAndSend("/topic/data", citizenCardService.getData());
        } else {
            template.convertAndSend("/topic/status", readingStatus);
        }
    }

    @GetMapping(value = "/picture/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") long id) {
        byte[] image = citizenCardService.getPicture(id);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
    }

    @EventListener()
    public void handleReaderStatusEvent(ReadingStatus status) {
        template.convertAndSend("/topic/status", status);
    }

    @EventListener()
    public void handleCardReadEvent(CardData cardData) {
        template.convertAndSend("/topic/data", cardData);
    }
}
