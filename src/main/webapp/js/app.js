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
var CitizenCard = CitizenCard || {};

CitizenCard.url = "127.0.0.1:9095";

CitizenCard.cardInserted = false;
CitizenCard.isChecking = false;
CitizenCard.isRetrivingData = false;
CitizenCard.$messages = $("#messages");
CitizenCard.$data = $("#data");
CitizenCard.$data.firstname = $("#data-firstname");
CitizenCard.$data.surname = $("#data-surname");

CitizenCard.CardReading = function() {
	CitizenCard.cardInserted = true;
	CitizenCard.$data.hide();
	CitizenCard.$messages.text("Card inserted. Please wait!!");
	CitizenCard.$messages.show();
}

CitizenCard.cardNotPresent = function(message) {	
	message = message || "Insert your card on the card reader.";
	CitizenCard.cardInserted = false;
	CitizenCard.$data.hide();
	CitizenCard.$messages.text(message);
	CitizenCard.$messages.show();
}

CitizenCard.cardDataFetched = function( data ) {
	CitizenCard.$data.firstname.val(data.firstName);
	CitizenCard.$data.surname.val(data.surname);
	CitizenCard.$data.show();
	CitizenCard.$messages.hide();
}

CitizenCard.socketError = function() {
	CitizenCard.$messages.html("An error as occurred. Please go to <a href=\"https://github.com/amfalmeida/citizen-card\" target=\"_blank\">https://github.com/amfalmeida/citizen-card</a> and follow the instructions.");
}

var CitizenCardSocket = CitizenCardSocket || {};

CitizenCardSocket.open = function() {
	var websocket;
	if(websocket !== undefined && websocket.readyState !== websocket.CLOSED){
		CitizenCardSocket.writeResponse("WebSocket is already opened.");
        return;
	}
    websocket = new WebSocket("ws://" + CitizenCard.url + "/websocket/citizensocket");
	websocket.onopen = function( event ) {
	     if(event.data === undefined) {
	         return;
	     }
	     CitizenCardSocket.writeResponse( event.data );
	};
	
	websocket.onmessage = function(event) {
		 CitizenCardSocket.writeResponse(event.data);
		 var json = $.parseJSON( event.data );
		 if (json.status) {
			 if (json.status === "READING") {
		    	CitizenCard.CardReading();
			 } else if (json.status === "READ" && json.data) {
	    		CitizenCard.cardDataFetched(json.data);
			 } else if (json.status === "CHECK_IF_CARD_CORRECT_INSERTED" 
				 || json.status === "UNKNOW_ERROR" ) {
		    	CitizenCard.cardNotPresent();
			 } else if (json.status === "NOT_CC_CARD") {
				 CitizenCard.cardNotPresent("The card inserted is not a Citizen Card. Please insert a Citizen Card.");
			 } else if (json.status === "ERROR") {
				 CitizenCard.cardNotPresent("Please try to remove and insert the card again.");
			 } else {
				 CitizenCard.cardNotPresent();
			 }
		}
	
	};
	
    websocket.onclose = function( event ) {
    	if (event.code != 1000) {
    		CitizenCard.socketError();
    		CitizenCardSocket.open();
    	} else {
	    	CitizenCardSocket.writeResponse("Connection closed");
			CitizenCard.cardNotPresent();
    	}
    };
}

CitizenCardSocket.writeResponse = function( message ) {
	console.log(message);
}

$(function() {
	CitizenCardSocket.open();
});
