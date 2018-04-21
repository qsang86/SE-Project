<%@page import="entity.BootstrapError"%>
<%@page import="entity.BootstrapStat"%>
<%@include file="protectAdminPage.jsp" %>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" href="CSS/adminlayout.css">
        <title>Administrator</title>
        <script>
            function myFunction() {
                document.getElementById("myDropdown").classList.toggle("show");
            }

// Close the dropdown if the user clicks outside of it
            window.onclick = function (event) {
                if (!event.target.matches('.dropbtn')) {

                    var dropdowns = document.getElementsByClassName("dropdown-content");
                    var i;
                    for (i = 0; i < dropdowns.length; i++) {
                        var openDropdown = dropdowns[i];
                        if (openDropdown.classList.contains('show')) {
                            openDropdown.classList.remove('show');
                        }
                    }
                }
            }
            
        </script>
    </head>
    <body>
        <div id="header">
            <div id="header">
                <div class="dropdown">
                    <button onclick="myFunction()" class="dropbtn"><img src="image/userprofile.png" height="25" width="25">&nbsp <%= user.getName()%></button>
                    <div id="myDropdown" class="dropdown-content">
                        <a href="logout.jsp">Logout</a>
                    </div>
                </div>
            </div>
            
        </div>
        <div class="tab"><img class="smulogo" src="image/smulogo.JPG" height="105" width="217"></div>     
        <div class="container">
            <div class="innercontainer">
                <h1>Administration</h1>
                <br>
                <h2>Bootstrap</h2>
                <form action="UploadServlet" method="post" enctype="multipart/form-data">  
                    Select File:<input type="file" name="fname"/>
                    <input type="submit" value="Bootstrap">
                </form>  
                <br>
                <br>
                <h2>Upload Additional Files</h2>
                <form action="UploadAdditionFileServlet" method="post" enctype="multipart/form-data">  
                    Select File:<input type="file" name="fileName"/>
                    <input type="submit" value="Upload Additional files">
                </form> 




            <%

                // print out exception error message
                if (request.getAttribute("exception") != null) {
                    Exception error = (Exception)request.getAttribute("exception");
                    out.println("<br>");
                    out.println(error.getMessage());
                }


                // print "something wrong with uploadctrl" for any errors in loading file
                if (request.getAttribute("error") != null) {
                    String error = (String)request.getAttribute("error");
                    out.println("<br>");
                    out.println(error);
                }


                // print out file name and num of records for bootstrap
                if (request.getAttribute("result") != null) {
                    ArrayList<BootstrapStat> results = (ArrayList<BootstrapStat>)request.getAttribute("result");
                    for (BootstrapStat stat : results) {
                        out.println("<br>");
                        out.println(stat.getFileName());
                        out.println("<br>");
                        out.println(stat.getNumRecords());
                        out.println("<br>");
                    }

                    // print out list of errors
                    for (BootstrapStat line : results) {
                        ArrayList<BootstrapError> errors = line.getErrors();
                        for (BootstrapError bootstraperror : errors) {
                            out.println("<br>");
                            out.println(bootstraperror);
                        }
                    }
                } 
                
                // print out file name and num of records for upload additional
                if (request.getAttribute("addResult") != null) {
                    ArrayList<BootstrapStat> addResult = (ArrayList<BootstrapStat>)request.getAttribute("addResult");
                    for (BootstrapStat stat : addResult) {
                        out.println("<br>");
                        out.println(stat.getFileName());
                        out.println("<br>");
                        out.println(stat.getNumRecords());
                        out.println("<br>");
                    }

                    // print out list of errors
                    for (BootstrapStat line : addResult) {
                        ArrayList<BootstrapError> errors = line.getErrors();
                        for (BootstrapError bootstraperror : errors) {
                            out.println("<br>");
                            out.println(bootstraperror);
                        }
                    }
                }

              %>





            <br>
            </div>
        </div>
    </body>
</html>
