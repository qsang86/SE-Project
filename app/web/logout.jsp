<%-- 
    Document   : logout
    Created on : 28-Sep-2017, 18:19:41
    Author     : Lee Kyusang
--%>

<%
    //logout
    session.invalidate();
    response.sendRedirect("index.jsp");
%>
