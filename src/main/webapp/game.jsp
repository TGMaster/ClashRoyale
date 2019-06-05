<%@page import="Entity.Player" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <title>Clash Royale</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="assets/css/font-awesome.min.css">
    <link rel="stylesheet" href="assets/css/game.css">
</head>

<body onbeforeunload="return closeSocket()">
    <%
        Player player = (Player) session.getAttribute("player");
    %>
    <div class="container">
        <div class="row">
            <h4>Welcome! <span id="user"></span></h4>
            <a href="index.jsp" id="logout">Go back</a>
        </div>
        <div class="row">
            <!-- Chat Area Div-->
            <div class="col-sm-6">
                <div id="panel_body">
                    <ul class="list-group" id="chat_body"></ul>
                </div>
                <div class="row">
                    <div class="col-sm-10">
                        <input type="text" class="form-control" rows="1" id="comment"
                            placeholder="Type your message..." />
                    </div>
                    <div class="col-sm-2">
                        <button type="button" class="btn btn-primary btn-send" onclick="sendMessage()" id="sendBtn"><i
                                class="fa fa-paper-plane"></i></button>
                    </div>
                </div>

            </div>
        </div>
        <div class="row">
            <div class="col-sm-12">Time left: <span id="timeleft"></span></div>
            <div class="col-sm-1">Mana</div>
            <div class="col-sm-11">
                <div class="progress">
                    <div id="mana" class="progress-bar progress-bar-danger progress-bar-striped active"
                        style="width:0%;"></div>
                </div>
            </div>
            <div class="col-sm-12">Troops: <p><span id="troops"></span></p>
            </div>

            <!-- Battle Button -->
            <div class="couche1" id="red1">

                <div class="couche2" id="red2">

                    <div class="couche23" id="red23">

                        <div class="couche3" id="red3">

                            <div class="couche4" id="red4">

                                <span class="battle" id="battle_red">Battle</span>

                                <div class="couche5" id="red5">

                                </div>

                            </div>

                        </div>

                    </div>

                </div>

            </div>
        </div>
    </div>
    <script src="assets/js/jquery-3.2.1.js"></script>
    <script type="text/javascript">

        // WebSocket Config

        //var socketUrl = "ws://171.244.50.210:8080/chat-server/chat_room";
        var socketUrl = "ws://localhost:8080/room";
        var userId = "<%=player.getId()%>";
        var userName = "<%=player.getUsername()%>";
        var userTeam;
        var userListHeight = screen.height;
        var allowPublicChat = true;

    </script>
    <script src="assets/js/chat.js"></script>
    <script type="text/javascript">
        start();
    </script>
</body>

</html>