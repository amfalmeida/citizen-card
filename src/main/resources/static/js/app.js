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

var Client = (function() {

    var stompClient = null;

    function setConnected(connected) {
        console.log('Connected: ' + connected);
    }

    function connect() {
        var socket = new SockJS('/citizencard-websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log(frame);
            setConnected(true);
            stompClient.subscribe('/topic/status', function (data) {
                var status = $.parseJSON(data.body);
                if (status === "READING") {
                    CitizenCard.cardReading();
                } else if (status === "CHECK_IF_CARD_CORRECT_INSERTED" || status === "NO_CARD" ) {
                    CitizenCard.cardNotPresent();
                } else if (status === "NOT_CC_CARD") {
                    CitizenCard.cardNotPresent("The card inserted is not a Citizen Card. Please insert a Citizen Card.");
                } else if (status === "ERROR") {
                    CitizenCard.cardNotPresent("Error reading the card, please try to remove and insert the card again.");
                } else {
                    CitizenCard.cardNotPresent();
                }
            });
            stompClient.subscribe('/topic/data', function (data) {
                var json = $.parseJSON(data.body);
                CitizenCard.cardDataFetched(json);
            });

            checkStatus();
        });
    }

    function disconnect() {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
    }

    function checkStatus() {
        stompClient.send("/app/status", {}, {});
    }

    return {
        connect: connect,
        disconnect: disconnect,
        checkStatus: checkStatus
    }

})();

var CitizenCard = (function() {
    var $messages = $("#messages"),
        $data = $("#data"),
        $dataPhoto = $("#data-photo");

    function cardReading() {
        $data.hide();

        $messages.removeClass("alert-danger").addClass("alert-primary");
        $messages.text("Card inserted. Please wait!!");
        $messages.show();
    }

    function cardNotPresent(message) {
        message = message || "Insert your card on the card reader.";
        $data.hide();
        $dataPhoto.hide();

        $messages.addClass("alert-danger").removeClass("alert-primary");
        $messages.text(message);
        $messages.show();
    }

    function cardDataFetched(json) {
        for(var item in json) {
            console.log(item + " -- " + json[item]);
            $("#data-" + item).html(json[item]);
        }

        //$("#data-photo").attr("src", "http://" + CitizenCard.url + "/photo?nif=" + data.nif);
        //$("#data-photo").show();
        $data.show();
        $messages.hide();
    }

    return {
        cardReading: cardReading,
        cardNotPresent: cardNotPresent,
        cardDataFetched: cardDataFetched
    };
})();

window.onbeforeunload = function() {
    Client.disconnect();
};

$(function () {
    Client.connect();
});

