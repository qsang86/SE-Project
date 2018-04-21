
<%@include file="protectUser.jsp" %>
<%@page import="java.util.ArrayList" %>
<%@page import="entity.Location" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="CSS/layout.css">
        <script src="JavaScript/layout.js" type="text/javascript"></script>
        <title>SLOCA | Heatmap</title>
        <script>
            function imageFunc(imageid){
	
            var Imageplace=document.getElementById("myImage");
             Imageplace.src="heatmap/"+imageid+".png";
             }
        </script>
        
    </head>
    <body>
        <%@include file="layout/layout.jsp"%>
        <div class="container">
            <div class="container2">
                <h2>Heatmap</h2>
                <form action="HeatmapServlet">
                    <table>
                        <tr>
                            <td>
                                Date & Time:
                            </td>
                            <td>
                                <input id="dateTimeId" type=datetime-local name="dateTime" step="1" required>
                            </td>
                            <td>
                                <input type="submit" value="Check Heatmap">
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Floor Level: 
                            </td>
                            <td>
                                <select id="floorId" name="floor" class="details"  id="mySelect" onchange="imageFunc(this.value)">
                                    <option value="0">0</option>
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                    <option value="4">4</option>
                                    <option value="5">5</option>
                                </select>
                                F
                            </td>
                        </tr>
                        
                    </table>
                          <img id="myImage" name="mapImage" src="heatmap/0.png" width="450" height="450"/>
                </form>
                <%
                if(request.getAttribute("specificHeatmap") != null){
                    ArrayList<Location> specificHeatmap = (ArrayList<Location>)request.getAttribute("specificHeatmap");
                    String dateTime = (String)request.getAttribute("dateTime");
                    int floorLevel = (int)request.getAttribute("floorLevel");
                    out.println("<table border=1><tr><th>Location Name</th><th>Number of People</th><th>Density</th></tr>");
                    for(Location location : specificHeatmap){
                        //print out the density and the number of people for that semantic place
                        out.println("<tr>");
                        out.println("<td>" + location.getSemanticPlace() + "</td>"); 
                        out.println("<td>" + location.getNumberOfPeople() + "</td>");
                        out.println("<td>" + location.getDensity(location.getNumberOfPeople()) + "</td>");
                        out.println("</tr>");
                    }
                    // to retain details in input box after user clicks enter
                    out.println("</table><script>(\"dateTimeId\").value = \""
                    + dateTime + "\"\n"
                    + "document.getElementById(\"floorId\").value = \""
                    + floorLevel + "\"\n"
                    + "document.getElementById(\"myImage\").src = \"" 
                    + "heatmap/"+floorLevel+".png" + "\"</script>");
                }
                %>
            </div>
            
        </div>
    </body>
</html>
