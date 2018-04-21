<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="entity.Companion"%>
<%@include file="protectUser.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="CSS/layout.css">
        <script src="JavaScript/layout.js" type="text/javascript"></script>
        <title>SLOCA | Location</title>
    </head>
    <body>
        <%@include file="layout/layout.jsp"%>
        <div class="container">
            <div class="container2">
                <h2>Top K Companions</h2>
                <br>
                <br>
                <form action="TopKCompanionServlet">
                    <table>
                        <tr>
                            <td>
                                Date & Time:
                            </td>
                            <td>
                                <input type=datetime-local name="dateTime" step="1">
                            </td>
                            <td>
                                Enter k: <input type="number" name="kCompanions" max="10" min="1" value="3"> 
                            </td>
                            <td>
                                Enter macaddress: <input type="text" name="macaddress" value="2bb139986fcb3714f18fe0ab9d91c4df222db836"> 
                            </td>
                            <td>
                                <input type="submit" value="Get Top K Companion">
                            </td>
                        </tr>
                    </table>
                </form>
                
                <br>
                <br>
                <% 
                    // print any error messages
                    if (request.getAttribute("error") != null) {
                        String error = (String)request.getAttribute("error");
                        out.println(error);
                    }
                    
                    
                    //print results
                    if (request.getAttribute("topkcompanions") != null) {
                        HashMap<Integer,ArrayList<Companion>> companionMap = (HashMap<Integer,ArrayList<Companion>>)request.getAttribute("topkcompanions");
                        
                        out.println("<table border = '1'>");
                        out.println("<tr><td colspan='4' scope='colgroup' align='center'><b>COMPANION DETAILS</b></td></tr>");
                        
                        out.println("<tr>");
                        out.println("<td><b>Rank</b></td>");
                        out.println("<td><b>Email</b></td>");
                        out.println("<td><b>MAC Address</b></td>");
                        out.println("<td><b>Total Time</b></td>");
                        out.println("</tr>");
                        
                        for (Map.Entry<Integer,ArrayList<Companion>> rank : companionMap.entrySet()) {
                            
                            
                            
                            ArrayList<Companion> companionList = rank.getValue();
                            
                            for (Companion companion : companionList) {
                                out.println("<tr>");
                                out.println("<td>" + rank.getKey() + "</td>");
                                if (companion.getEmail() != null) {
                                    out.println("<td>" + companion.getEmail() + "</td>");
                                } else {
                                    out.println("<td></td>");
                                }
                                out.println("<td>" + companion.getMacAddress() + "</td>");
                                out.println("<td>" + companion.getTotalDuration() + "</td>");
                            }
                            out.println("</tr>");
                        }
                        
                        out.println("</table>");
                        out.println("<br>");
                        out.println("<br>");
                        out.println("<br>");
                    }
                %>
            </div>
        </div>
             
    </body>
</html>
