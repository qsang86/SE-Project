<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <div id="header">
            <div class="dropdown">
                <button onclick="myFunction()" class="dropbtn"><img src="image/userprofile.png" height="25" width="25">&nbsp <%= user.getName()%>&nbsp&nbsp<i class="downArrow"></i></button>
                <div id="myDropdown" class="dropdown-content">
                    <a href="#home">Home</a>
                    <a href="#about">About</a>
                    <a href="#contact">Contact</a>
                    <a href="logout.jsp">Logout</a> 
                </div>
            </div>
        </div>
        <div class="tab">
            <img class="smulogo" src="image/smulogo.JPG" height="105" width="217">
            <ul>
                <li><a href="home.jsp"><img src="image/home.png" height="20" width="20">&nbsp Home</a></li>
                <li><a href="heatmap.jsp"><img src="image/heatmap.png" height="20" width="20">&nbsp Heatmap</a></li>
                <div class="dropdown2">
                    <li><a href="#blr" onclick="myFunction2()" class="droptab"><img src="image/blr.png" height="20" width="20">&nbsp Basic Location Report&nbsp&nbsp<i class="downArrow"></i></a></li>
                    <div id="myDropdown2" class="dropdown-content2">
                        <a href="studentbreakdown.jsp"><img src="image/student.png" height="20" width="20">&nbsp Student Breakdown</a>
                        <a href="topkpopularplaces.jsp"><img src="image/popular.png" height="20" width="20">&nbsp Popular Places</a>
                        <a href="topkcompanions.jsp"><img src="image/location.png" height="20" width="20">&nbsp Same Locations</a>
                        <a href="topknextplaces.jsp"><img src="image/nextplace.png" height="20" width="20">&nbsp Top Next Places</a>
                      </div>
                </div>
                <li><a href="groupdetection.jsp"><img src="image/ari.png" height="20" width="20">&nbsp Automatic Group Detection</a></li>
            </ul>
        </div>
    </body>
</html>
