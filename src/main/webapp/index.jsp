<%-- 
    Document   : index
    Created on : Jul 8, 2018, 6:42:16 PM
    Author     : TGMaster
--%>

<%@page import="Entity.Player" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>IU Gaming Tour</title>
        <link rel="icon" href="assets/images/iugt-black.png">
        <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" href="assets/css/font-awesome.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Aldrich">
        <link rel="stylesheet" href="assets/css/styles.css">
    </head>

    <body>
        <%
            boolean isLogin = false;
            Player player = (Player) session.getAttribute("player");
            if (player != null) {
                isLogin = true;
            }
        %>
        <section>
            <nav class="navbar navbar-default navbar-fixed-top navigation-clean-button"
                 style="background-color:rgba(0, 0, 0, 0.5);color:rgb(255,255,255);">
                <div class="container" style="width: 100% !important;">
                    <div class="navbar-header">
                        <a class="navbar-brand" href="index.jsp" style="font-family:Aldrich, sans-serif;font-size:18px;">
                            <img class="img-responsive" src="assets/images/iugt-white.png"
                                 style="width:50px;height:50px;margin-top:-15px;">IU GAMING TOUR
                        </a>
                        <button class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navcol-1">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>
                    </div>
                    <div class="collapse navbar-collapse" id="navcol-1">
                        <% if (isLogin) {%>
                        <ul class="nav navbar-nav navbar-right">
                            <li><a class="btn btn-default action-button" role="button" href="game.jsp" id="play"><i class="fa fa-play" style="font-size:15px;"></i> <strong>PLAY</strong></a></li>
                            <li class="dropdown">
                                <a class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false" href="#"
                                   style="color:rgb(255,255,255);font-size:14px;">
                                    <img src="assets/images/default-avatar.png" class="dropdown-image"
                                         style="width:50px;height:50px;margin-top:-15px;">
                                    <strong><%=player.getUsername()%>
                                    </strong>
                                    <span class="caret" style="color:white;"></span>
                                </a>
                                <ul class="dropdown-menu dropdown-menu-right" role="menu">
                                    <li role="presentation"><a class="text-uppercase" href="#"><i class="fa fa-user-circle-o" style="font-size:15px;"></i> <strong>PROFILE</strong></a></li>
                                    <li role="presentation"><a class="text-uppercase" href="user?action=logout"><i class="fa fa-sign-out" style="font-size:15px;"></i> <strong>LOG OUT</strong></a></li>
                                </ul>
                            </li>
                        </ul>
                        <%} else {%>
                        <ul class="nav navbar-nav navbar-right" style="margin-right: 50px">
                            <li class="dropdown">
                                <a class="btn btn-default action-button dropdown-toggle" href="#" data-toggle="dropdown" id="login"><i class="fa fa-sign-in" style="font-size:15px;"></i> <strong>LOG IN</strong></a>
                                <div class="dropdown-menu" style="padding: 15px; padding-bottom: 10px;">
                                    <form action="user" class="form-horizontal"  method="post" accept-charset="UTF-8">
                                        <input id="username" class="form-control login" type="text" name="username" placeholder="Username..." />
                                        <input id="password" class="form-control login" type="password" name="password" placeholder="Password..."/>
                                        <button class="btn btn-primary" type="submit" name="action" value="login">LOGIN</button>
                                    </form>
                                </div>
                            </li>
                        </ul>
                        <%}%>
                    </div>
                </div>
            </nav>
        </section>

        <div class="carousel slide" data-ride="carousel" id="carousel-1">
            <div class="carousel-inner" role="listbox">
                <div class="item"><img class="img-responsive" src="assets/images/slide/1161401.jpg" alt="Slide Image"></div>
                <div class="item"><img class="img-responsive" src="assets/images/slide/42d70adab7cd290131c582b1b775404b.jpg"
                                       alt="Slide Image"></div>
                <div class="item"><img class="img-responsive" src="assets/images/slide/Counter-Strike-games-selfie-1885251.jpeg"
                                       alt="Slide Image"></div>
                <div class="item"><img class="img-responsive" src="assets/images/slide/daniel-johnson-clutch.jpg"
                                       alt="Slide Image"></div>
                <div class="item"><img class="img-responsive" src="assets/images/slide/maxresdefault (4).jpg" alt="Slide Image">
                </div>
                <div class="item active"><img class="img-responsive" src="assets/images/slide/v065cssvidh01.jpg"
                                              alt="Slide Image"></div>
                <div class="item"><img class="img-responsive" src="assets/images/slide/xamhiy16swyy.jpg" alt="Slide Image">
                </div>
            </div>
        </div>

        <script src="assets/js/jquery-3.2.1.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
    </body>

</html>