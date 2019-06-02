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
    <style type="text/css">
        .show {
            display: inline-block;
        }

        .hide {
            display: none;
        }

        ul {
            list-style-type: none;
            padding: 0px;
            margin: 0px;
        }

        ul li {
            padding: 5px 10px;
            display: inline-block;
            word-wrap: break-word;
        }

        #users_list {
            /*height: 300px;*/
            overflow: auto;
        }

        #users_list a {
            padding: 5px;
        }

        #panel_body {
            margin-bottom: 5px;
            height: 300px;
            overflow-y: auto;
        }

        .btn-send {
            padding: 6px 31px !important;
        }

        input {
            border-width: 0 0 1px 0 !important;
        }
    </style>
</head>

<body onbeforeunload="return closeSocket()">
    <%
        boolean isLogin = false;
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
                    <div class="col-s">
                        <button type="button" class="btn btn-primary btn-send" onclick="sendMessage()" id="sendBtn"><i
                                class="fa fa-paper-plane"></i></button>
                    </div>
                </div>

            </div>
        </div>
        <div class="row">
            <div class="col-sm-1">Mana: <span id="mana"></span></div>
            <div class="col-sm-1"></div>
            <div class="col-sm-10">Troops: <p><span id="troops"></span></p>
            </div>
        </div>
    </div>
    <script src="assets/js/jquery-3.2.1.js"></script>
    <script type="text/javascript">
        function getRandomInt(min, max) {
            min = Math.ceil(min);
            max = Math.floor(max);
            return Math.floor(Math.random() * (max - min)) + min; //The maximum is exclusive and the minimum is inclusive
        }
    </script>
    <script type="text/javascript">

        // WebSocket Config

        //var socketUrl = "ws://171.244.50.210:8080/chat-server/chat_room";
        var socketUrl = "ws://localhost:8080/room";
        var userId = "<%=player.getId()%>";
        var userName = "<%=player.getUsername()%>";
        var userListHeight = screen.height;
        var allowPublicChat = true;

    </script>
    <script src="chat.js"></script>
    <script type="text/javascript">
        start();
    </script>
</body>

</html>