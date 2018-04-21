function myFunction() {
                document.getElementById("myDropdown").classList.toggle("show");
            }
            
function myFunction2() {
            document.getElementById("myDropdown2").classList.toggle("show");
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
                
                 if (!event.target.matches('.droptab')) {

                    var dropdowns = document.getElementsByClassName("dropdown-content2");
                    var i;
                    for (i = 0; i < dropdowns.length; i++) {
                        var openDropdown = dropdowns[i];
                        if (openDropdown.classList.contains('show')) {
                            openDropdown.classList.remove('show');
                        }
                    }
                }
            }
            
            
            



            
            
            