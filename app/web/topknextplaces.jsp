<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.sql.Array"%>
<%@page import="java.util.Collections"%>
<%@page import="entity.Location"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="DAO.LocationDAO"%>
<%@page import="java.util.ArrayList"%>
<%@include file="protectUser.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="CSS/layout.css">
        <script src="JavaScript/layout.js" type="text/javascript"></script>
        <title>SLOCA | Next Place</title>
    </head>
    <body>
        <%@include file="layout/layout.jsp"%>
        <div class="container">
            <div class="container2">
                <h2>Top K Next Places</h2>

                <br>
                <br>
                <form action="TopKNextPlacesServlet">
                    <table>
                        <tr>
                            <td>
                                Date & Time:
                            </td>
                            <td>
                                <input id="dateTimeId" type=datetime-local name="date" step="1" required>
                            </td>
                            <td>
                                Enter k:
                                <select id="kId" name="k">
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
                            <td>
                                Semantic Place: <select id="semanticId" class="form-control custom-dropdown" name="origin">
                                    <option value ="no_value">Select Semantic Place</option>
                                    <%                                        
                                        ArrayList<String> semanticPlace = LocationDAO.getAllSemanticPlace();
                                        for (int i = 0; i < semanticPlace.size(); i++) {
                                            String spName = semanticPlace.get(i);

                                    %>
                                    <option class="level-0" value="<%=spName%>"><%=spName%></option>
                                    <%
                                        }
                                    %>

                                </select>
                            </td>

                            <td>
                                <input type="submit" value="Get Top K Next Places">
                            </td>
                        </tr>
                    </table>
                </form>

                <br>
                <br>
                <!-- jquery to print errormsgs or no result found -->
                ${errorMsg}
                ${noresult}

                <%
                    int preUsers = 0;
                    if (request.getAttribute("previousUsers") != null) {
                        preUsers = (Integer) request.getAttribute("previousUsers");
                        out.println("total-users:" + preUsers + "<br/>");
                    }
                    if (request.getAttribute("topknextplaceresult") != null) {
                        Integer nextUsers = (Integer) request.getAttribute("nextUsers");
                        out.println("total-next-place-users:" + nextUsers + "<br/>");
                        
                        TreeMap<Integer, ArrayList<String>> topkMap = new TreeMap(Collections.reverseOrder());
                        topkMap = (TreeMap<Integer, ArrayList<String>>) request.getAttribute("topknextplaceresult");
                        int k = (Integer) request.getAttribute("k");//get the number to display
                        

                        if (topkMap.size() > 0) { //if there is result
                            out.println("<table border=1><tr><th>Rank</th><th>Semantic Place</th><th>Count</th><th>Percentage</th></tr>"); //create table with header
                            if (topkMap.size() < k) { //if the number of result smaller than requested
                                k = topkMap.size();//requested reduce to the result size
                                out.println("Maximum number of rows is " + k + "<br/><br/>");//prompt for the user to know
                            }
                            Integer[] mapKeys = topkMap.keySet().toArray(new Integer[topkMap.size()]);//put all the keyset into array
                            for (int i = 0; i < k; i++) {
                                out.println("<tr>"
                                        + "<td>" + (i+1) + "</td>");
                                ArrayList<String> rowList = topkMap.get(mapKeys[i]);//get the value from the key
                                out.println("<td>");
                                out.println(rowList.get(0));//get the first semantic place
                                for (int row = 1; row < rowList.size(); row++) { //check if there is more than one semantic place
                                    out.println("," + rowList.get(row));//print out the subsequent ones
                                }
                                out.println("</td>");
                                out.println("<td>" + mapKeys[i] + "</td>");//print the count number
                                out.println("<td>");
                                double percent = (double) mapKeys[i] / preUsers * 100;
                                DecimalFormat df = new DecimalFormat("0.00");
                                out.println(df.format(percent) + "%");//print the percentage
                                out.println("</td></tr>");
                            }
                            out.println("</table><br/>");
                        }
                    }

                %>
                                
            </div>
        </div>
        ${returnSelection}
    </body>

</html>
