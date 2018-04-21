<%-- 
    Document   : index
    Created on : 23-Sep-2017, 16:14:28
    Author     : Lee Kyusang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="CSS/logincss.css">
        <title>SLOCA | LOGIN</title>
    </head>
    <body background="image/smubackgound.jpg">
	<div id="header"></div>
	<div id="home">
	<div id="left">
            <img class="slocalogo" src="image/SLOCA logo.png">
        </div>
        
        <div class ="right">
            <div class="logincontainer">
		<form method="POST" action="LoginServlet">
                    <table class="tablealign">
                        <%
                            if (request.getAttribute("incorrect") != null) {
                                String incorrect = (String)(request.getAttribute("incorrect"));
                                if(incorrect != null){
                                    out.println(incorrect);
                                    out.println("<br>");
                                }
                            }
                        %>
                        <tr>

                        </tr>
                        <tr>
                            <td>
                                <input type="text" name="userid" placeholder="Email ID" required class="inputbox">
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <input type="password" name="pwd" placeholder="Password" required class="inputbox">
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <input type="submit" value="login!" class="loginbutton">
                            </td>
                        </tr>
                    </table>
                </form>
                <img class="smulogo" src="image/smulogo.JPG" height="200" width="200">
            </div>
        </div>
        </div>
    </body>
</html>
<
