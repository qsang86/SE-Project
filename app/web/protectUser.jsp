<%@page import="entity.User"%>

<% 
User user = (User)session.getAttribute("user");

if (user == null) {
    request.setAttribute("incorrect", "you have not logged in");
    RequestDispatcher view = request.getRequestDispatcher("index.jsp");
    view.forward(request, response);
}
%>