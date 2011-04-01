<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Help</title>
        <link rel="stylesheet" type="text/css" href="css/design.css" media="screen" />
        <script src="javascript/jquery-1.4.4.min.js" type="text/javascript"></script>
        <script type="text/javascript" src="javascript/jquery.ui.all.js"></script>
        <script type="text/javascript" src="http://jqueryui.com/ui/jquery.ui.core.js"></script>
        <script type="text/javascript" src="http://jqueryui.com/ui/jquery.ui.widget.js"></script>
        <script type="text/javascript" src="javascript/jquery.ui.accordion.js"></script>

        <link rel="Stylesheet" media="screen" href="css/ui.core.css" />
        <link rel="stylesheet" type="text/css" href="css/jquery-ui-1.8.6.custom.css" />

    </head>
    <body>
        <div id="global">
            <div id="header">
                <img alt="Logo" src="Images/logoGeofences.jpg" />
            </div><!-- header -->
            <div id="navigation">
                <ul>
                    <li class="gauche"><a href="home.jsp">Home</a></li>
                    <li class="gauche">|</li>
                    <li class="gauche"><a href="index.jsp">Geofences</a></li>
                    <li class="gauche">|</li>
                    <li class="gauche"><a href="account.jsp">Account</a></li>
                    <li class="gauche">|</li>
                    <li class="gauche"><a href="help.jsp">Help</a></li>
                </ul>
            </div><!-- navigation -->
            <div id="centre">
                <div id="accordion">
                    <h3><a href="#">Create a new geofence  </a></h3>
                    <div>
                            <p>
                                Click on the icon : <img  src="Images/add.png">, you can now fill the form in.
                            </p>
                            <p>
                                Click on the map to add points in order to draw the geofence. You can also drag and drop a point, if you clicked on the wrong location.
                            </p>
                            <p>
                                You can add a specific type of schedule for the geofence by clicking on this icon : <img id="imgAddSchedule" alt="Add Schedule" src="Images/clock_add.png">
                            </p>
                            <p>
                                Click on button "save" to insert the geofence in the databases.
                            </p>
                    </div>
                    <h3><a href="#">Add a schedule</a></h3>
                    <div>
                            <p>
                                There are 5 kind of schedules
                            </p>
                            <p>
                                <ul>
                                    <li>
                                        A whole day
                                    </li>
                                    <li>
                                        From a day and time to another day and time
                                    </li>
                                    <li>
                                        For a day, from Hour to another one
                                    </li>
                                    <li>
                                        Periodic, from Hour to another one, for exemple every friday from 9am to 11pm
                                    </li>
                                    <li>
                                        Always
                                    </li>
                                </ul>
                            </p>
                    </div>
                    <h3><a href="#">Edit a geofence</a></h3>
                    <div>
                            <p>
                                First select the geofence you want to edit.
                            </p>
                            <p>
                                Click on this icon : <img id="imgEditMode" alt="Edit Mode" src="Images/edit.png"> and edit the forms or relocate the points by drag and drop.
                            </p>
                            <p>
                                You can add a point
                            <ul>
                                <li>
                                    After a point by clicking on a point
                                </li>
                                <li>
                                    After a point by double clicking on a point
                                </li>
                            </ul>

                            </p>
                            <p>
                                You can remove a point by clicking on this icon : <img id="imgDeleteMode" alt="Delete Mode" src="Images/delete.png" > then choose the point you want to delete.
                            </p>
                    </div>
                    <h3><a href="#">View several geofence simultaneously</a></h3>
                    <div>
                            <p>
                                Check the option : Superimpose, then select the geofence you want to see.
                            </p>

                    </div>
            </div>
            </div>
            <div id="copyright">
                <span>  Copyright &copy; GSEM 2010. Tous droits résérvés.</span>
            </div>
        </div>
        <script>
	$(function() {
		$( "#accordion" ).accordion();
	});
	</script>
    </body>
</html>

