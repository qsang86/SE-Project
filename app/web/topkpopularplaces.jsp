<%@page import="java.util.Map"%>
<%@page import="entity.Location"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@include file="protectUser.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="CSS/layout.css">
        <script src="JavaScript/layout.js" type="text/javascript"></script>
        <title>SLOCA | Popular Places</title>
    </head>
    <body>
        <%@include file="layout/layout.jsp"%>
        <div class="container">
            <div class="container2">
                <h2>Top K Popular Places</h2>
                <form action="TopKPopularPlacesServlet">
                    <table>
                        <tr>
                            <td>
                                Date & Time:
                            </td>
                            <td>    
                                <input id="dateTimeId" type=datetime-local name="dateTime" step="1" required>
                            </td>
                            <td>
                                <input type="submit" value="Get Top-K popular">
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Enter K:
                            </td>
                            <td>
                                <select id="kPopularId" name="kPopular">
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3" selected>3</option>
                                    <option value="4">4</option>
                                    <option value="5">5</option>
                                    <option value="6">6</option>
                                    <option value="7">7</option>
                                    <option value="8">8</option>
                                    <option value="9">9</option>
                                    <option value="10">10</option>
                                  </select>
                            </td>
                        </tr>
                    </table>
                    <%
                        if (request.getAttribute("TopKPopularResult") != null) {
                            String dateTime = (String)request.getAttribute("dateTime");
                            int topNum = (int)request.getAttribute("kNum");
                            HashMap<Integer,ArrayList<Location>> locationMap = (HashMap<Integer,ArrayList<Location>>)request.getAttribute("TopKPopularResult");
                            out.println("<br><table border=1><tr><th>Rank</th><th>Semantic Place</th><th>Count</th></tr>");
                            
                            // loop through each entry in to k popular places map to print out 
                            for (Map.Entry<Integer,ArrayList<Location>> entry : locationMap.entrySet()) {
                                int rank = entry.getKey();
                                ArrayList<Location> locationList = entry.getValue();
                                for (Location location : locationList) {
                                    // print number of people in the semantic place for each rank 
                                    out.println("<tr>");
                                    out.println("<td>" + rank + "</td>");
                                    out.println("<td>" + location.getSemanticPlace() + "</td>");
                                    out.println("<td>" + location.getNumberOfPeople() + "</td>");
                                    out.println("</tr>");
                                }
                            }
                            // to retain details in input box after user presses submit
                            out.println("<script>document.getElementById(\"dateTimeId\").value = \""
                                    + dateTime + "\"\n"
                                    + "document.getElementById(\"kPopularId\").value = \""
                                    + topNum + "\"</script>");    
                        }
                    %>
                </form>
            </div>
        </div>
             
    </body>
</html>
