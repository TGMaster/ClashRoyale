/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global socketUrl, userId, WebSocket, userName, userUrl, userImg, websocket */
var actions = {
    JOIN_CHAT: "join",
    LEAVE_CHAT: "leave",
    CHAT_MSG: "chat",
    UPDATE_LIST: "update",
    JOIN_TEAM: "team",
    SWAP_TEAM: "swap",
    START_GAME: "start",
    REMOVE_MATCH: "remove"
};
var by = {
    id: "Id",
    tag: "TagName",
    class: "ClassName"
};

function start() {
    websocket = new WebSocket(socketUrl);
    websocket.onmessage = onMessage;
    websocket.onopen = onOpen;
    websocket.onclose = onClose;
    websocket.onerror = onError;
    setTimeout(function () {
        joinChat();
    }, 100);
}

function onOpen(event) {/*Session created.*/
    updateChatBox("Server connected...");
}

function onClose(event) {/*Session closed - e.g Server down/unavailable*/
    if (event.code === 1008) {
        alert(event.reason);
        window.location.href = 'users';
    }
    updateChatBox("Server disconnected...");
}

function onError(event) {/*Error occured while communicating server...*/
}

function onMessage(event) {
    var response = JSON.parse(event.data);
    if (response.action === actions.JOIN_CHAT) {
        if (response.message !== undefined)
            updateChatBox(response.name + " " + response.message);
        var owner = getElement("onlyOwner", by.id);
        if (response.owner === true) {
            var select = document.createElement('select');
            var Bo = {
                1: "1 map",
                2: "2 maps",
                3: "3 maps",
                5: "5 maps"
            };
            for (var index in Bo) {
                select.options[select.options.length] = new Option(Bo[index], index);
            }
            select.setAttribute("class", "form-control");
            select.id = "BO-chooser";

            var i = document.createElement('i');
            i.setAttribute("class", "fa fa-play");
            i.setAttribute("style", "font-size:17px");

            var button = document.createElement("button");
            button.setAttribute("type", "submit");
            button.setAttribute("class", "btn btn-default");
            button.id = "startBtn";
            var t = document.createElement('strong');
            t.innerHTML = " START";
            button.appendChild(i);
            button.appendChild(t);

            var div = document.createElement("div");
            div.id = "BO";
            div.appendChild(select);
            owner.appendChild(div);

            document.getElementById('btnGroup').appendChild(button);
        }
    }
    //If new user left chat room, notify others and update users list
    if (response.action === actions.LEAVE_CHAT) {
        if (response.id === userId)
            return;

        var team1 = getElement("TeamCT", by.id);
        var team2 = getElement("TeamT", by.id);
        var u = getElement(response.id, by.id);

        if (team1.contains(u)) {
            team1.removeChild(u);
        } else if (team2.contains(u)) {
            team2.removeChild(u);
        }

        updateChatBox(response.name + " " + response.message);
    }

    if (response.action === actions.UPDATE_LIST) {
        if (response.id === userId)
            return;
        updateChatBox(response.name + " " + response.message);
    }

    // Send message
    if (response.action === actions.CHAT_MSG) {
        updateChatBox(response.name + ": " + response.message);
    }

    if (response.action === actions.JOIN_TEAM) {
        updateTeamList(response);
    }

    if (response.action === actions.SWAP_TEAM) {
        changeTeam(response);
    }

    if (response.action === actions.START_GAME) {
        getServer(response);
    }

    if (response.action === actions.REMOVE_MATCH) {
        if (response.id !== userId) {
            alert(response.message);
            closeSocket();
            window.location.href = 'users';
        }
    }
}

function updateChatBox(message) {
    var chatBox = getElement("textAreaMessage", by.id);
    chatBox.innerHTML += message + " \n";
    chatBox.scrollTop = chatBox.scrollHeight;
}

// Main Functions
function sendMessage() {
    var message = getElementText("textMessage", by.id);
    message = message.trim();
    if (message === null || message === "") {
        return;
    }
    var request = {
        action: actions.CHAT_MSG,
        id: userId,
        message: message
    };
    setElementText("textMessage", by.id, "");
    getElement("textMessage", by.id).focus();
    sendRequest(request);
}

function joinChat() {
    var request = {
        action: actions.JOIN_CHAT,
        id: userId,
        name: userName,
        url: userUrl,
        img: userImg
    };
    sendRequest(request);
}

function leaveChat() {
    var request = {
        action: actions.LEAVE_CHAT,
        id: userId
    };
    sendRequest(request);
}

function swapTeam() {
    var request = {
        action: actions.SWAP_TEAM,
        id: userId
    };
    sendRequest(request);
}

function startGame() {
    var request = {
        action: actions.START_GAME,
        id: userId
    };
    sendRequest(request);
}

function sendRequest(request) {
    if (websocket === undefined || websocket.readyState === WebSocket.CLOSED) {
        alert("Connection lost to Server. Please try again later.");
        return;
    }
    websocket.send(JSON.stringify(request));
}

function closeSocket() {
    leaveChat();
    setTimeout(function () {
        if (websocket !== undefined || websocket.readyState !== WebSocket.CLOSED) {
            websocket.close();
        }
        return;
    }, 1000);

}

// Stuffs
function getElement(id, type) {
    var element;
    switch (type) {
        case by.id:
            element = document.getElementById(id);
            break;
        case by.tag:
            element = document.getElementsByTagName(id);
            break;
        case by.class:
            element = document.getElementsByClassName(id);
            break;
        default:
            element = document.getElementById(id);
            break;
    }
    return element;
}

function setElementText(id, by, text) {
    getElement(id, by).value = text;
}

function getElementText(id, by) {
    return getElement(id, by).value;
}

// Execute a function when the user releases a key on the keyboard
getElement("textMessage", by.id).addEventListener("keyup", function (event) {
    // Cancel the default action, if needed
    event.preventDefault();
    // Number 13 is the "Enter" key on the keyboard
    if (event.keyCode === 13) {
        // Send message
        sendMessage();
    }
});

function infoUser(user) {

    // Info
    var h3 = document.createElement("h3");
    h3.innerHTML = user.name;
    h3.id = "name";

    var a = document.createElement("a");
    a.setAttribute("href", user.url);
    a.appendChild(h3);
    a.target = "_blank";

    var img = document.createElement("img");
    img.src = user.img;

    var span = document.createElement("span");
    if (user.team === "team1")
        span.setAttribute("class", "player-avatar pull-left");

    if (user.team === "team2")
        span.setAttribute("class", "player-avatar pull-right");
    span.appendChild(img);

    var div = document.createElement("div");
    div.setAttribute("class", "player-name clearfix");
    div.appendChild(a);

    var u = document.createElement("div");
    if (user.team === "team1")
        u.setAttribute("class", "player-slot TeamCT");
    if (user.team === "team2")
        u.setAttribute("class", "player-slot TeamT");
    u.setAttribute("id", user.id);

    u.appendChild(span);
    u.appendChild(div);
    return u;
}

function updateTeamList(user) {
    var team1 = getElement("TeamCT", by.id);
    var team2 = getElement("TeamT", by.id);
    var u = infoUser(user);
    if (user.team === "team1") {
        team1.appendChild(u);
    } else if (user.team === "team2") {
        team2.appendChild(u);
    }
}

function changeTeam(user) {
    var team1 = getElement("TeamCT", by.id);
    var team2 = getElement("TeamT", by.id);
    var u = infoUser(user);
    if (user.team === "team1") {
        team2.removeChild(getElement(user.id, by.id));
        team1.appendChild(u);
    } else if (user.team === "team2") {
        team1.removeChild(getElement(user.id, by.id));
        team2.appendChild(u);
    }
}

function getServer(data) {
    //Hide some buttons
    if (getElement("BO", by.id) !== null)
        remove("BO");
    remove("switchBtn");
    if (getElement("startBtn", by.id) !== null)
        remove("startBtn");

    //Button
    var button = document.createElement('button');
    button.setAttribute('class', 'btn-danger btn-lg');
    button.setAttribute('onclick', "window.open('" + data.url + "','_blank')");
    button.setAttribute('type', 'button');
    var text = document.createElement('strong');
    text.innerHTML = "CONNECT TO SERVER";
    button.appendChild(text);

    var btn = document.createElement('div');
    btn.id = 'connect-btn';
    btn.appendChild(button);
    
    //Label
    var label = document.createElement('label');
    label.innerHTML = "Or copy this command and paste in your console";

    //Input
    var input = document.createElement('input');
    input.setAttribute('type', "text");
    input.setAttribute("class", "form-control");
    input.value = data.ip;
    input.id = "copyTarget";
    input.setAttribute("readonly", "");

    //Icon
    var i = document.createElement("i");
    i.setAttribute("class", "fa fa-copy");
    i.setAttribute("style", "font-size:20px; padding: 7px; color: black");
    var a = document.createElement("a");
    a.id = "copyButton";
    a.setAttribute("onclick", "copy('copyTarget')");
    a.setAttribute("data-toggle", "tooltip");
    a.setAttribute("title", "Copy to clipboard");
    a.href = "#";
    a.appendChild(i);

    var icon = document.createElement("div");
    icon.setAttribute("class", "icon-wrapper");
    icon.appendChild(a);

    //Input Final
    var div = document.createElement('div');
    div.setAttribute("class", "server-ip-wrapper");
    div.appendChild(label);
    div.appendChild(input);
    div.appendChild(icon);

    //Display
    var server = getElement("server", by.id);
    server.appendChild(btn);
    server.appendChild(div);

    //Play sound
    var audio = new Audio('assets/sound/startgame.mp3');
    audio.play();

}

$(document).ready(function () {
    $('.btn-leave').click(function () {
        closeSocket();
        window.location.href = 'users';
    });
    var x_timer;
    $('#teamList').submit(function (e) {
        e.preventDefault();

        // Get number of maps
        var num = $('#BO-chooser').val();

        clearTimeout(x_timer);
        x_timer = setTimeout(function () {
            $.ajax({
                url: 'match',
                data: {
                    action: 'start',
                    numMaps: num
                },
                success: function (response) {
                    if (response.status === 200) {
                        startGame();
                        console.log(response.msg);
                    } else
                        alert(response.msg);
                }
            });
        }, 1000);

    });
});

function copy(id) {
    var input = getElement(id, by.id);
    input.select();
    document.execCommand("copy");
}

function remove(id) {
    var ele = getElement(id, by.id);
    ele.parentNode.removeChild(ele);
}

function imgSwapCT(userid) {
    var team1 = getElement("TeamCT", by.id);
    var user = getElement(userid, by.id);
    !team1.contains(user) && swapTeam();
}

function imgSwapT(userid) {
    var team2 = getElement("TeamT", by.id);
    var user = getElement(userid, by.id);
    !team2.contains(user) && swapTeam();
}