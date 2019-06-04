<%-- 
    Document   : info
    Created on : Jun 24, 2018, 1:19:19 AM
    Author     : TGMaster
--%>

<%@page import="POJO.Player"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" href="css/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" href="css/fontawesome/font-awesome.min.css">
    </head>
    <body>
        <%
            boolean isNull = false;
            Player player = (Player) session.getAttribute("player");
            if (player == null) {
                isNull = true;
            }
            if (!isNull) {
        %>
        <div class="navbar navbar-default navbar-fixed-top" role="navigation">
            <div class="container-fluid">

            </div>
        </div>


        <p>ID: <%=player.getId()%></p>
        <p>Name: <%=player.getName()%></p>
        <p>URL: <a href="<%=player.getUrl()%>"><%=player.getUrl()%></a></p>
        <p><img src="<%=player.getAvatar()%>"></p>
        <p><a href="scrim" class="button">Chat Room</a></p>
        <p><a href="match" class="button">Match Room</a></p>
        <p><a href="users?action=logout" class="button">Logout</a></p>
        <% } else {%>
        <a href="users?action=login"><img src="images/steam/sits_01.png"></a>
            <% }%>

        <script src="css/bootstrap/js/bootstrap.min.js"></script>
    </body>
</html>
