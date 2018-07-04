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

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class HomeController {

    @MessageMapping("/hello")
    public String hello(String greeting) {
        return String.format("[%s: %s", Instant.now().toString(), greeting);
    }

    @MessageMapping("/checkCard")
    @SendTo("/topic/status")
    public void checkCard() {

    }

    @MessageMapping("/getData")
    @SendTo("/topic/data")
    public void getData() {

    }

}
