<%@page import="java.util.Arrays"%>
<%@page import="java.util.Map"%>
<%@page import="entity.BreakdownOrder"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.ArrayList"%>
<%@include file="protectUser.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="CSS/layout.css">
        <script src="JavaScript/layout.js" type="text/javascript"></script>
        <script type="text/javascript" src="JavaScript/jquery-1.9.1.js"></script>
        <style>
             table, th, td{
                 padding:2px;
                 
             }
             td{
                 border: 1px solid black;
             }
             
            select, input[type="text"] {
                width: 120px;
                box-sizing: border-box;
            }
            section {
                padding: 8px;
                background-color: #f0f0f0;
                overflow: auto;
            }
            section > div {
                float: left;
                padding: 4px;
            }
            section > div + div {
                width: 40px;
                text-align: center;
            }
        </style>
        
        <title>SLOCA | Student Breakdown</title>
    </head>
    <body>
        <%@include file="layout/layout.jsp"%>
        <div class="container">
            <div class="container2">
                <h2>School, Year, Gender Breakdown</h2>
                <form action="StudentBreakdownServlet">
                    Date & Time: <input id="dateTimeId" type=datetime-local name="dateTime" step="1" required> 
                    <br><br>
                    Sort by:
                    
                    <section>
                        <div>
                            <select id="leftBoxId" size="3" multiple>
                                <option value="year">year</option>
                                <option value="gender">gender</option>
                                <option value="school">school</option>
                            </select>
                        </div>
                  
                        
                        <div id="number">
                        <b id="num1"style="display: none;">1</b>
                        <br/>
                        <b id="num2"style="display: none;">2</b>
                        <br/>
                        <b id="num3"style="display: none;">3</b>
                        <br/>
                        </div>
                        <div>
                            <select id="rightBoxId" size="3" multiple required></select>
                        </div>

                    </section>
                    <div>
                        <input type="hidden" id="orderId" name="order"/>
                    </div>
                    
                    <input id="submitBtnId" type="submit" value="Get Student Breakdown">
                </form>
                
                <br>
                
                <script>

                    $("#rightBoxId").click(function () {
                        var selectedItem = $("#rightBoxId option:selected");
                        $("#leftBoxId").append(selectedItem);
                        
                        var allItems = $("#rightBoxId option");
                        $("#num" + 1).hide(1);
                        $("#num" + 2).hide(1);
                        $("#num" + 3).hide(1);
                        var i = 0;
                        var values = $.map(allItems,function(option){
                            $("#num" + ++i).show(1);
                            $("#rightBoxId option[value='" + option.value + "']").prop("selected", true);
                           return option.value; 
                        });

                        $("#orderId").val(values); 
                    });

 
                    $("#leftBoxId").click(function () {
                        var selectedItem = $("#leftBoxId option:selected");
                        $("#rightBoxId").append(selectedItem);          
                        
                        var allItems = $("#rightBoxId option");
                        var i = 0;
                        $("#num" + 1).hide(1);
                        $("#num" + 2).hide(1);
                        $("#num" + 3).hide(1);
                        var values = $.map(allItems,function(option){
                            $("#num" + ++i).show(1);
                           return option.value; 
                        });
                        $("#orderId").val(values);
                    });

                </script>

                
                <%
                    if (request.getAttribute("ErrMsg") != null) {
                        String message = (String)request.getAttribute("ErrMsg");
                        out.println(message);
                    }
                    
                    
                    
                if (request.getAttribute("breakdown") != null) {
                    BreakdownOrder breakdownOrder = (BreakdownOrder)request.getAttribute("breakdown");
                    int userSize = (int)request.getAttribute("userSize");
                    String orderStr = (String)request.getAttribute("orderStr");
                    ArrayList<String> sorting = (ArrayList<String>)request.getAttribute("sorting");
                    String dateTime = (String)request.getAttribute("dateTime");
          
                    out.print("<br>");
                    TreeMap<String, ArrayList<User>> firstBreakdown = breakdownOrder.getFirst();
                    TreeMap<String, TreeMap<String, ArrayList<User>>> secondBreakdown = breakdownOrder.getSecond();
                    TreeMap<String, TreeMap<String, ArrayList<User>>> thirdBreakdown = breakdownOrder.getThird();

                    out.print("<table>");

                    for (Map.Entry<String, ArrayList<User>> entry : firstBreakdown.entrySet()) {
                        out.print("<tr><td>");
                        String firstOptionName = entry.getKey();

                        out.print("<b>" + firstOptionName + "</b><br>");
                        int firstBreakdownSize = entry.getValue().size();
                        out.print("Number of People: " + firstBreakdownSize + "<br>");
                        double percentage = (double) firstBreakdownSize / userSize * 100;
                        long roundedPercent = Math.round(percentage);

                        out.print(roundedPercent + "% <br></td>");

                        TreeMap<String, ArrayList<User>> secondOption = secondBreakdown.get(firstOptionName);
                        if (secondOption != null) {
                            out.print("<td><table>");

                            for (Map.Entry<String, ArrayList<User>> secondEntry : secondOption.entrySet()) {
                                out.print("<tr><td>");
                                String secondOptionName = secondEntry.getKey();
                                out.print("<b>" + secondOptionName + "</b><br>");

                                int secondBreakdownSize = secondEntry.getValue().size();

                                out.print("Number of people: " + secondBreakdownSize + "<br>");

                                double secondPercentage = (double) secondBreakdownSize / userSize * 100;
                                long secondRoundedPercent = Math.round(secondPercentage);

                                out.print(secondRoundedPercent + "% <br></td>");

                                TreeMap<String, ArrayList<User>> thirdOption = thirdBreakdown.get(firstOptionName + secondOptionName);
                                if (thirdOption != null) {
                                    out.print("<td><table>");

                                    for (Map.Entry<String, ArrayList<User>> thirdEntry : thirdOption.entrySet()) {
                                        int thirdBreakdownSize = thirdEntry.getValue().size();
                                        out.print("<tr><td><b>" + thirdEntry.getKey() + "</b><br>"
                                                + "Number of people: " + thirdBreakdownSize + "<br>");

                                        double thirdPercentage = (double) thirdBreakdownSize / userSize * 100;
                                        long thirdRoundedPercent = Math.round(thirdPercentage);

                                        out.print(thirdRoundedPercent + "% <br></td></tr>");
                                    }
                                    out.print("</table>");
                                }
                                out.print("</td></tr>");
                            }
                            out.print("</table></td>");
                        }
                        out.print("</tr>");
                    }
                    out.print("</table>");
                    out.print("<script>document.getElementById(\"dateTimeId\").value = \""
                            + dateTime + "\"\n");
//                 
                    ArrayList<String> tempList = new ArrayList<>(Arrays.asList("year", "gender", "school"));
                    out.print("document.getElementById(\"rightBoxId\").options.length = 0;");
                    int sortNo = 0;
                    for (String str : sorting) {
                        if (tempList.contains(str)) {
                            tempList.remove(str);
                        }
                        out.print("var opt = document.createElement(\"option\");"
                                + "opt.value = \"" + str + "\";"
                                + "opt.innerHTML = \""+ str + "\";"
                                + "opt.selected = true;"
                                + "document.getElementById(\"num" + ++sortNo + "\").style.display = \"inline-block\";"
                                + "document.getElementById(\"rightBoxId\").appendChild(opt);");
                    
                    }
                    out.print("document.getElementById(\"leftBoxId\").options.length = 0;");
                    for (String str : tempList) { //remaining items
                        out.print("var opt = document.createElement(\"option\");"
                                + "opt.value = \"" + str + "\";"
                                + "opt.innerHTML = \"" + str + "\";"
                                + "opt.selected = true;"
                                + "document.getElementById(\"leftBoxId\").appendChild(opt);");
                    }
                    out.print("document.getElementById(\"orderId\").value=\"" + orderStr + "\"\n");
                    out.print("</script>");

                }
                    
                %>
                
            </div>
        </div>
             
    </body>
</html>
