<%@page import="java.util.TreeMap"%>
<%@page import="entity.Companion"%>
<%@page import="entity.Group"%>
<%@page import="java.util.Map"%>
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
        <title>SLOCA | Automatic Group Detection</title>
    </head>
    <body>
        <%@include file="layout/layout.jsp"%>
        <div class="container">
            <div class="container2">
                <h2>Automatic Group Detection</h2>
                <form action="AutomaticGroupDetectionServlet">
                    <table>
                        <tr>
                            <td>
                                Date & Time:
                            </td>
                            <td>
                                <input type=datetime-local name="dateTime" step="1">
                            </td>
                            <td>
                                <input type="submit" value="Detect Group">
                            </td>
                        </tr>
                    </table>
                </form>
                <br>
                <br>
                <% 
                    if (request.getAttribute("error") != null) {
                        String error = (String)request.getAttribute("error");
                        out.println(error);
                    }
                    
                    
                    
                if (request.getAttribute("groupdetect") != null) {
                        ArrayList<Group> groups = (ArrayList<Group>)request.getAttribute("groupdetect");
                        int numUsers = (int)request.getAttribute("numUsers");
                        
                        out.println("<b>Total Number of users:</b>");
                        out.println("<b>" + numUsers + "</b>");
                        out.println("<br>");
                        out.println("<b>Total number of groups:</b>");
                        out.println("<b>" + groups.size() + "</b>");
                        out.println("<br>");
                        out.println("<br>");
                        out.println("<br>");
                        
                        for (Group group : groups) {
                            out.println("<table border = '1'>");
                            out.println("<tr><td colspan='2' scope='colgroup' align='center'><b>GROUP DETAILS</b></td></tr>");
                            ArrayList<Companion> personList = group.getGroup();
                            
                            //print out details of each person in the group 
                            
                            out.println("<tr>");
                            out.println("<td><b>Email</b></td>");
                            out.println("<td><b>MAC Address</b></td>");
                            out.println("</tr>");
                            
                            for (Companion person : personList) {
                                
                                out.println("<tr>");
                                if (person.getEmail() == null) {
                                    out.println("<td></td>");
                                } else {
                                    out.println("<td>" + person.getEmail() + "</td>");
                                }
                                
                                out.println("<td>" + person.getMacAddress() + "</td>");
                                out.println("</tr>");
                            }
                            
                            //print out details about each location and total time spent as a group
                            
                            
                            out.println("<tr><td colspan='2' scope='colgroup' align='center'><b>LOCATIONS</b></td></tr>");
                            
                            out.println("<tr>");
                            out.println("<td><b>Location ID</b></td>");
                            out.println("<td><b>Time Spent</b></td>");
                            out.println("</tr>");
                            
                            HashMap<String,Integer> locationAndTime = group.getLocationAndTime();
                            TreeMap<String,Integer> orderedLocationAndTime = new TreeMap<>(locationAndTime);
                            for (Map.Entry<String,Integer> entry : orderedLocationAndTime.entrySet()) {
                                out.println("<tr>");
                                out.println("<td>"+ entry.getKey() + "</td>");
                                out.println("<td>" + entry.getValue() + "</td>");
                                out.println("</tr>");
                            }
                            
                            out.println("</table>");
                            out.println("<br>");
                            out.println("<br>");
                            out.println("<br>");
                        }
                    }
                %>
            </div>
        </div>
             
    </body>
</html>
