/* 
 * Author : Kenan Cedric
 * This file contains all the scripts necessary for the .jsp
 * page to track positions and create geofences
 */

//variables used to display and manage the map
//also to display client's position
var map = null;
var track = true;
var currentPosition = null;
var lastPosition = null;
var myMarker = null;
var markerManager = null;
var timerID;
var line;
var markers;
var index = 0;
//the positions of the clients
var tabPositions = new Array();
//the previous position of the clients
//data used to create google lines
var oldPositions = new Array();
//booleans to see if we can delete the
//displayed informations client and geofence
var canDelClient = true; var canDelGeofence = true;

//erases the error message
var deleteUpdateMsg = function(){
    document.getElementById("satelliteError").innerHTML = "";
}
//erases the message containing client's info like id
var deleteMsgClientInfo =  function(){
    document.getElementById("clientInfo").innerHTML = "";
    canDelClient = true;
}

//delete message for geofence info
var deleteMsgGeofenceInfo = function(){
    document.getElementById("geoFenceInfo").innerHTML = "";
}

//requesting the positions of the clients using AJAX
var getPosition = function(){

    var myHTMLRequest = new Request({
        url:'map_jspAjax.jsp',
        noCache: true,
        method:'get',
        onSuccess: function(html){
            //if we receive all the positions we get
            //each position for each client and display them on the map
            var pos_str = html.trim();
            var positions = pos_str.split("/");
            var sucess = true;
            clearTab();
            for (var i=0; i < positions.length; i++){
                var latlon = positions[i].split(";");
                //if there is an error
                if ( latlon[0].toString() == "Error" ){

                    if ( latlon[1].toString() == "Error : IOException ! ")
                        document.getElementById("satelliteError").innerHTML = "Error : Cannot access Server !";
                    else
                       document.getElementById("satelliteError").innerHTML = latlon[1].toString();
                    sucess = false;
                    break;
                }
                else //if no error
                    document.getElementById("satelliteError").innerHTML = "";
                    
                if(latlon.length != 4)
                    continue;
                var clientId = latlon[0].toFloat();
                var lat = latlon[1].toFloat();
                var lon = latlon[2].toFloat();
                var status = latlon[3];
                document.getElementById("clientInfo").innerHTML = "<br/>Client's Id : <b>" + clientId + "</b> Status : <b>" + status +"</b><br/>";

               showCurrentPosition(clientId, lat, lon);
            }
            if ( sucess == true ){
                document.getElementById("satelliteError").innerHTML = " .";
                deleteUpdateMsg.delay(1000);
            }
            getPosition.delay(5000);
        },
        onFailure: function(xhr){
            getPosition.delay(5000);
        }
    });
    myHTMLRequest.send();
}

//creating the map and loading it
function load(){
    if(GBrowserIsCompatible()){
        map = new GMap2(document.getElementById("map"));
        map.addControl(new GLargeMapControl());
        map.addControl(new GMapTypeControl());
        LatLng=new GLatLng(47.643742,6.8565076);
        map.setCenter(LatLng, 13);
        //map.setCenter(new GLatLng(48.125768, 7.481689), 8);
    }else {
        alert("Sorry, the Google Maps API is not compatible with this browser");
    }
    getPosition.delay(2000);
    //getGeofences.delay(4000);
}

//add a marker on the map (represents a client's position)
function showCurrentPosition(clientId, lat, lon){
    if (lat != 0.0 && lon != 0.0){
        currentPosition = new GLatLng(lat,lon);
        var icon = new GIcon();
        icon.image = 'http://www.google.com/mapfiles/marker.png';
        icon.iconSize = new GSize(20, 30);
        icon.iconAnchor = new GPoint(16, 16);
        opts = {
                "icon": icon,
                "clickable": true,
                "labelText": clientId,
                "labelOffset": new GSize(-3, -33)
        };
        //the class LabeledMarker allows to put a custome String as name for a marker
        //tabPositions.push(new LabeledMarker(currentPosition, opts));
        /*********IF YOU DON'T WANNA USE LABELED MAKER JUST PUT THE LINE BELLOW INSTEAD OF THE OTHER**********/
        tabPositions.push(new GMarker(currentPosition));
        //tabPositions[tabPositions.length-1].bindInfoWindow("sdfsdf");
        
        map.addOverlay(tabPositions[tabPositions.length-1]);
        GEvent.addListener(tabPositions[tabPositions.length-1], "click", function() {
        document.getElementById("clientInfo").innerHTML = "<br/>Client's Id : <b>" + clientId + "</b>";
            //if ( canDelClient == true )
            //    deleteMsgClientInfo.delay(5000);
        });
//        var polyline = new GPolyline([
//          oldPositions[clientId],
//          currentPosition
//        ], "#ff0000", 7, 0.7);
//        map.addOverlay(polyline);
        oldPositions[clientId]  = currentPosition;
        $('satelliteError').set("html", "");
    }
    else {
       $('satelliteError').set("html", "No satellite !");
    }
}

function clearTab(){
 for (var i=0; i < tabPositions.length; i++){
     map.removeOverlay(tabPositions[i]);
     //tabPositions[i] = null;
 }
 tabPositions = new Array();
}

/**This part concerns the geofences **/
var tabZone = [];
//string of forbidden vehicles for a geofence
var forbiddensS = [];
//colors for geofences depending on the vehicle which is forbidden
var cTruck = "#0033FF"; var cTruckCar = "#6633FF"; var cTruckTw = "#9933CC";
var cCar = "#CC3300"; var cCarTw = "#993300"; var cTw = "#660066"; var cAll = "#FF0000";
//the color for the current geofence
var currentColor = cTruck;
//types of possible vehicles
var tCar = "CAR"; var tTruck = "TRUCK"; var tTw = "TWOWHEEL";
//booleans to know which types of vehicles is prohibited in the zone
var isCar = false; var isTruck = false; var isTw = false;
//tab containing all the geofences
var tabGeo = [];
//getting the geofences string contained in html

   //gets the geofences and display them on the map using google polygons
   var getGeofences = function (){
      var  geofencesHtml = document.getElementById("geofences");
      var geofencesString = geofencesHtml.innerHTML;

      //Split and get each geofence caracteristics
       var geofenceLine = geofencesString.split("#");
       if ( geofenceLine[0] == "Error")
            document.getElementById("satelliteError").innerHTML = geofenceLine[1].toString();
       else{
           //for each geofence we get the point table
            for (var i=0; i < geofenceLine.length-1; i++){
               isCar = false; isTruck = false; isTw = false;
               var aGeofence = geofenceLine[i].split("%%");

                var vehiculeTypes = aGeofence[1].split(",");
                vehiculeTypes[0] =  vehiculeTypes[0].replace("[", "");
                vehiculeTypes[vehiculeTypes.length-1] = vehiculeTypes[vehiculeTypes.length-1].replace("]", "");

                forbiddensS[forbiddensS.length-1] = "";
                //gets the type of forbidden vehicle for this geofence
                if (vehiculeTypes.length == 1){
                    updateTypeOfVehicle(vehiculeTypes[0].trim().replace(" ", ""));
                    forbiddensS[forbiddensS.length-1] = vehiculeTypes[0].trim().replace(" ", "");
                }
                else{
                    for (var z=0; z < vehiculeTypes.length; z++){
                        updateTypeOfVehicle(vehiculeTypes[z].trim().replace(" ", ""));
                        forbiddensS[forbiddensS.length-1] += vehiculeTypes[z].trim().replace(" ", "") + ", ";
                    }
                }
               
                //choose the right color according to forbiddens
                updateChoseOfColor();

                //create and display a geofence
                var allCoords  = aGeofence[5].split(",");
                if ( allCoords.length > 1 ){
                    allCoords[0] =  allCoords[0].replace("[", "");
                    allCoords[allCoords.length-1] = allCoords[allCoords.length-1].replace("]", "");
                    tabZone=[];
                    //for each coordinates we put them in a table
                    for (var j=0; j < allCoords.length-1; j++){
                        var aCoord = allCoords[j].split(";");
                        point = new GLatLng(aCoord[0],aCoord[1]);
                        tabZone.push(point);
                    }
                    //and we create the polygon to display the geofence
                    if(tabZone.length>1){

                        var optsP = {
                                "clickable": true
                        };
                        tabGeo[tabGeo.length-1] = new GPolygon(tabZone, currentColor,3,3,currentColor,0.5,optsP);
                        addListenerPolygon( tabGeo[tabGeo.length-1], forbiddensS[forbiddensS.length-1]);
                        map.addOverlay(tabGeo[tabGeo.length-1]);
                        
                    }
                }
            }
       }
   }
   getGeofences.delay(1000);

   //gets the right type of vehicle
   function updateTypeOfVehicle(type){
        if ( type == tCar)
            isCar = true;
        else if ( type == tTruck )
            isTruck = true;
        else if ( type == tTw )
            isTw = true;
   }

   //chosing the right color according to forbiddens
   function updateChoseOfColor(){
        if ( isCar && !isTruck && !isTw )
            currentColor = cCar;
        else if ( !isCar && isTruck && !isTw  )
            currentColor = cTruck;
        else if ( !isCar && !isTruck && isTw )
            currentColor = cTw;
        else if ( isCar && isTruck && !isTw )
            currentColor = cTruckCar;
        else if ( !isCar && isTruck && isTw )
            currentColor = cTruckTw;
        else if ( isCar && !isTruck && isTw  )
            currentColor = cCarTw;
        else if ( isCar && isTruck && isTw )
            currentColor = cAll;
        else
            currentColor = cTruck;

   }

   function addListenerPolygon(polyg, desc){
       GEvent.addListener(polyg, "click", function() {
            document.getElementById("geoFenceInfo").innerHTML = "Frobidden for : <b>" + desc  + "</b>";
            //deleteMsgGeofenceInfo.delay(30000);
       });

   }

