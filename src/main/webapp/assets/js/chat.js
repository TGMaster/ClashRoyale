/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global WebSocket, webSocket, socketUrl, userId, userName, receiverId, screenHeight, userListHeight, allowPublicChat, _status, by, actions */

var receiverId = -1;
var actions = {
	_JOIN: "join",
	_LEAVE: "leave",
	_COMMAND: "cmd",
	_MANA: "mana",
	_TROOPS: "troops",
	_TEAM: "team"
};
var by = {
	id: "Id",
	tag: "TagName",
	class: "ClassName"
};

function onOpen(event) {/*Session created.*/
}

function onClose(event) {/*Session closed - e.g Server down/unavailable*/
}

function onError(event) {
	/*Error occured while communicating server...*/
	console.log(event);
}

function onMessage(event) {
	var liClasses = "";
	var spanClasses = "";
	var extraLiAttributes = "";
	var extraSpanAttributes = "";
	var template = "";
	var display = "show";
	var response = JSON.parse(event.data);
	//If new user entered chat room, notify online friends and update friends list
	if (response.action === actions._JOIN) {
		updateUserList(response);
	}
	//If new user left chat room, notify others and update users list
	if (response.action === actions._LEAVE) {
		if (response.id === userId) {
			return;
		}

		if (receiverId === parseInt(response.id) || receiverId === -1) {
			display = "show";
		} else {
			display = "hide";
		}

		liClasses = "list-item text-wrap " + display;
		spanClasses = "label label-danger";
		extraLiAttributes = "";
		extraSpanAttributes = "";
		template = prepareMessageTemplate(
			liClasses,
			spanClasses,
			extraLiAttributes,
			extraSpanAttributes,
			response.name,
			response.message);
		updateChatBox(template);

		//remove offline user from list
		//var users_list = getElement("users_list" , by.id);
		//users_list.removeChild(getElement(response.id , by.id));
		// getElement(response.id, by.id).setAttribute("status", _status[0]);
		// getElement(response.id, by.id).className = "list-group-item d-none";

		// if (parseInt(response.id) === receiverId || countOnlineUsers() === 0) {
		// 	receiverId = -1;
		// 	loadPublicChat();
		// }
	}
	if (response.action === actions._COMMAND) {
		display = "show";

		var senderName;
		if (response.id === userId || response.name === "") {
			display += " text-right";
			senderName = "Me";
			spanClasses = "label label-default";
		} else {
			senderName = response.name;
			spanClasses = "label label-primary";
		}
		liClasses = "list-item text-wrap " + display;
		extraLiAttributes = "";
		extraSpanAttributes = "";

		template = prepareMessageTemplate(
			liClasses,
			spanClasses,
			extraLiAttributes,
			extraSpanAttributes,
			senderName,
			response.message);

		updateChatBox(template);
	}
	if (response.action === actions._MANA) {
		// Mana
		getElement("mana", by.id).style.width = parseInt(response.message)*10 + '%';
	}
	if (response.action === actions._TROOPS) {
		getElement("troops", by.id).innerHTML = response.message;
	}
	if (response.action === actions._TEAM) {
		if (response.team) {
			userTeam = "team1";
		} else {
			userTeam = "team2";
		}
		console.log(userId + " " + userTeam);
	}

	setChatScrollPos();
}

function updateChatBox(template) {
	getElement("chat_body", by.id).innerHTML += template;
}

function setChatScrollPos() {
	var panel_body = getElement("panel_body", by.id);
	panel_body.scrollTop = panel_body.scrollHeight;
}

function joinChat() {
	var request = {
		action: actions._JOIN,
		id: userId
	};
	sendRequest(request);
}

function leaveChat() {
	var request = {
		action: actions._LEAVE,
		id: userId
	};
	sendRequest(request);
}

function sendRequest(request) {
	if (webSocket === undefined || webSocket.readyState === WebSocket.CLOSED) {
		updateChatBox("Connection lost to Server. Please try again later.");
		return;
	}
	webSocket.send(JSON.stringify(request));
}

function updateUserList(user) {
	if (user.id !== userId) {
		var display = "show";
		if (receiverId > 0) {
			display = "hide";
		}
		var liClasses = "list-item text-wrap " + display;
		var spanClasses = "label label-success";
		var extraLiAttributes = "";
		var extraSpanAttributes = "";
		var template = prepareMessageTemplate(
			liClasses,
			spanClasses,
			extraLiAttributes,
			extraSpanAttributes,
			user.name,
			user.message);
		updateChatBox(template);

		// var disabled = "";
		// if (user.status === _status[0]) {
		// 	disabled = "d-none";
		// }

		// var users_list = getElement("users_list", by.id);
		// var user_a = document.createElement("a");
		// user_a.setAttribute("id", user.id);
		// user_a.setAttribute("href", "#");
		// user_a.setAttribute("status", user.status);
		// user_a.className = "list-group-item " + disabled;
		// user_a.onclick = function () {
		// 	setReceiverId(user.id, this);
		// };
		// var createAText = document.createTextNode(user.name);
		// user_a.appendChild(createAText);
		// users_list.appendChild(user_a);
	}
}

function prepareMessageTemplate(
	liClasses,
	spanClasses,
	extraLiAttributes,
	extraSpanAttributes,
	sender,
	message) {
	var template =
		"<li class='" + liClasses + "' " + extraLiAttributes + ">"
		+ "<span class='" + spanClasses + "' " + extraSpanAttributes + ">"
		+ sender
		+ "</span>  "
		+ message
		+ "</li>";
	return template;
}

// function countOnlineUsers() {
// 	var online = 0;
// 	var friends = getElement("users_list", by.id).getElementsByTagName("a");
// 	for (i = 0; i < friends.length; i++) {
// 		if (friends[i].getAttribute("status") === _status[1]) {
// 			online++;
// 		}
// 	}
// 	return online;
// }

function sendMessage() {
	var message = getElementText("comment", by.id);
	message = message.trim();
	if (message === null || message === "") {
		return;
	}

	// if (countOnlineUsers() === 0) {
	//     return;//don't send message if no single recipient available
	// }

	if (!allowPublicChat && receiverId === -1) {
		return;//if public chat is false and no receiver is selected, don't send message
	}
	var request;
	console.log(userTeam);
	request = {
		action: actions._COMMAND,
		id: userId,
		team: userTeam,
		message: message
	};
	setElementText("comment", by.id, "");
	getElement("comment", by.id).focus();
	sendRequest(request);
}

function closeSocket() {
	//Todo-task:
	//update user status to offline before closing socket
	leaveChat();
	setTimeout(function () {
		if (webSocket !== undefined || webSocket.readyState !== WebSocket.CLOSED) {
			webSocket.close();
		}
		return;
	}, 1000);

}

function start() {
	webSocket = new WebSocket(socketUrl);
	webSocket.onmessage = onMessage;
	webSocket.onopen = onOpen;
	webSocket.onclose = onClose;
	webSocket.onerror = onError;
	setTimeout(function () {
		joinChat();
	}, 1000);
}

function setUserName() {
	setElementInnerText("user", by.id, "<i>" + userName + "</i>");
}

// function setReceiverId(id, object) {
// 	if (getElement(id, by.id).getAttribute("status") === _status[0]) {
// 		return;
// 	} else {
// 		if (receiverId > 0) {
// 			getElement(receiverId, by.id).className = "list-group-item";
// 			if (receiverId === id) {
// 				receiverId = -1;//reset receiverId
// 				loadPublicChat();
// 			} else {
// 				getElement(id, by.id).className = "list-group-item active";
// 				receiverId = id;
// 				loadPrivateChat();
// 			}

// 		} else {
// 			receiverId = id;
// 			getElement(id, by.id).className = "list-group-item active";
// 			loadPrivateChat();
// 		}
// 	}
// }

// function loadPrivateChat() {
// 	var chat_body = getElement("chat_body", by.id);
// 	var list = chat_body.getElementsByTagName("li");
// 	if (list.length > 0) {
// 		for (i = 0; i < list.length; i++) {
// 			if (list[i].hasAttribute("sender") && list[i].hasAttribute("receiver")) {
// 				var sender = parseInt(list[i].getAttribute("sender"));
// 				var receiver = parseInt(list[i].getAttribute("receiver"));
// 				if ((userId === receiver && receiverId === sender) || (userId === sender && receiver === receiverId)) {
// 					list[i].className = list[i].className.replace("hide", "show");
// 				} else {
// 					list[i].className = list[i].className.replace("show", "hide");
// 				}
// 			} else {
// 				list[i].className = list[i].className.replace("show", "hide");
// 			}
// 		}
// 	}
// }

// function loadPublicChat() {
// 	var chat_body = getElement("chat_body", by.id);
// 	var list = chat_body.getElementsByTagName("li");
// 	if (list.length > 0) {
// 		for (i = 0; i < list.length; i++) {
// 			if (list[i].hasAttribute("sender") && list[i].hasAttribute("receiver")) {
// 				list[i].className = list[i].className.replace("show", "hide");
// 			} else {
// 				list[i].className = list[i].className.replace("hide", "show");
// 			}
// 		}
// 	}
// }

function getSenderName(id) {
	return getElementInnerText(id, by.id);
}

function getElement(id, type) {
	var element;
	switch (type) {
		case by.id:
			element = document.getElementById(id);
			break;
		case by.tag:
			element = document.getElementsByTagName(id);
			break;
		default:
			element = document.getElementById(id);
			break;
	}
	return element;
}

function getElementText(id, by) {
	return getElement(id, by).value;
}

function getElementInnerText(id, by) {
	return getElement(id, by).innerHTML;
}

function setElementInnerText(id, by, text) {
	getElement(id, by).innerHTML = text;
}

function setElementText(id, by, text) {
	getElement(id, by).value = text;
}

function setUserListHeight() {
	getElement("users_list").style.height = userListHeight;
	getElement("users_list").setAttribute("style", "height:" + userListHeight + "px;");
}

setUserName();
// setUserListHeight();

// Enter key
var input = getElement("comment", by.id);
input.addEventListener("keyup", function (event) {
	// Number 13 is the "Enter" key on the keyboard
	if (event.keyCode === 13) {
		// Cancel the default action, if needed
		event.preventDefault();
		// Trigger the button element with a click
		getElement("sendBtn", by.id).click();
	}
});
