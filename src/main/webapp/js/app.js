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

CitizenCard.mode = 2; // 1 - ajax; 2 - socket

CitizenCard.cardInserted = false;
CitizenCard.isChecking = false;
CitizenCard.isRetrivingData = false;
CitizenCard.$messages = $("#messages");
CitizenCard.$data = $("#data");
CitizenCard.$data.firstname = $("#data-firstname");
CitizenCard.$data.surname = $("#data-surname");

CitizenCard.CardPresent = function() {
	CitizenCard.cardInserted = true;
	CitizenCard.$data.hide();
	CitizenCard.$messages.text("Card inserted. Please wait!!");
	CitizenCard.$messages.show();
}

CitizenCard.cardNotPresent = function() {
	CitizenCard.cardInserted = false;
	CitizenCard.$data.hide();
	CitizenCard.$messages.text("Insert your card on the card reader.");
	CitizenCard.$messages.show();
}

CitizenCard.cardDataFetched = function( data ) {
	CitizenCard.$data.firstname.val(data.firstName);
	CitizenCard.$data.surname.val(data.surname);
	CitizenCard.$data.show();
	CitizenCard.$messages.hide();
}

CitizenCard.check = function() {
	if (CitizenCard.isChecking || CitizenCard.isRetrivingData) {
		return;
	}
	CitizenCard.isChecking = true;
	$.ajax({
	    url: "http://10.0.1.104:8085/api/checkCard",
	    jsonp: "callback",
	    dataType: "jsonp",
	    success: function( response ) {
	    	if (CitizenCard.cardInserted && response.cardInserted) {
	    		return;
	    	}
	        if (response.cardInserted) {
	        	CitizenCard.CardPresent();
	        	CitizenCard.getData();
	        } else {
	        	CitizenCard.cardNotPresent();
	        }
	    },
	    error: function( error ) {
	    	CitizenCard.$messages.text("An error occurred when try to check if a card is present.");
	    },
	    complete: function() {
	    	CitizenCard.isChecking = false;
	    }
	    
	});
};

CitizenCard.getData = function() {
	if (CitizenCard.isRetrivingData) {
		return;
	}
	CitizenCard.$data.firstname.val("");
	CitizenCard.$data.surname.val("");
	
	CitizenCard.isRetrivingData = true;
	$.ajax({
	    url: "http://127.0.0.1:8085/api/getData",
	    jsonp: "callback",
	    dataType: "jsonp",
	    success: function( response ) {
	    	if (response.data) {
	    		CitizenCard.cardDataFetched(response.data);
	    	}
	    },
	    error: function( error ) {
	    	CitizenCard.$messages.text("An error occurred when try to retrieve card data.");
	    },
	    complete: function() {
	    	CitizenCard.isRetrivingData = false;
	    }
	});
}

var CitizenCardSocket = CitizenCardSocket || {};

CitizenCardSocket.open = function() {
	var websocket;
	if(websocket !== undefined && websocket.readyState !== websocket.CLOSED){
		CitizenCardSocket.writeResponse("WebSocket is already opened.");
        return;
     }
     // Create a new instance of the websocket
	websocket = new WebSocket("ws://127.0.0.1:8085/websocket");
	websocket.onopen = function( event ) {
         // For reasons I can't determine, onopen gets called twice
         // and the first time event.data is undefined.
         // Leave a comment if you know the answer.
         if(event.data === undefined)
             return;

         CitizenCardSocket.writeResponse( event.data );
     };
     
     websocket.onmessage = function(event) {
    	 CitizenCardSocket.writeResponse(event.data);
    	 var json = $.parseJSON( event.data );
    	 if (json.cardInserted) {
        	CitizenCard.CardPresent();
        	CitizenCardSocket.getData(websocket, json.token);
    	 } else if (json.data) {
    		 CitizenCard.cardDataFetched(json.data);
    	 } else {
        	CitizenCard.cardNotPresent();
    	 }
    	 
     };

     websocket.onclose = function( event ) {
    	 CitizenCardSocket.writeResponse("Connection closed");
     };
}

CitizenCardSocket.getData = function( websocket, token ) {
	websocket.send(JSON.stringify({
		  op: "getData",
		  token: token
	}));
}

CitizenCardSocket.writeResponse = function( message ) {
	console.log(message);
}

$(function() {
	if (CitizenCard.mode == 1) {
		setInterval(function() {
			CitizenCard.check(); 
		}, 1000);
	} else if (CitizenCard.mode == 2) {
		CitizenCardSocket.open();
	}
});