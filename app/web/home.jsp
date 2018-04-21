<%@page import="entity.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>  

<%@include file="protectUser.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="CSS/layout.css">
        <script src="JavaScript/layout.js" type="text/javascript"></script>
        <style>
            .innercontainer{
                top:50px;
                left: 50px;
                position: absolute;
                height: 150px;
                width: 75%;
                background-color: #cccccc;
                border-style: outset;
                text-align: center;
                font-size: 20px;
            }
        </style>
        <title>SLOCA | HOME</title>
    </head>
    <body>
        <%@include file="layout/layout.jsp"%>
        <div class="container">
            <div class="innercontainer">
                <br><br><br>
                Welcome <%= user.getName() %>! <br>
            </div>
            
            
        </div>
    </body>
</html>