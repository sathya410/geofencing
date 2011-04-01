/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


            /*
             * To change this template, choose Tools | Templates
             * and open the template in the editor.
             */
            var map = null;
            var listPoints=[];
            var listeLat="";
            var listeLng="";
            var gpsLatLon;
            var indexPoint = -1;
            var action = "cursor";
            var typePoly = "line";
            var scheduleType = 0;

            /**
             * Function to display GoogleMaps
             */
            function load(){
                if (GBrowserIsCompatible()){
                    
                    geocoder = new GClientGeocoder();
                    map = new GMap2(document.getElementById("map"));
                    map.setUIToDefault();
                    map.setCenter(new GLatLng(47.643742,6.8565076), 16);
                    setAction("cursor");
                }else {
                    alert("Sorry, the Google Maps API is not compatible with this browser");
                }
            }


            /**
             *Fonction to set a state
             */
            function setAction(etat){
                if(etat == "cursor"){
                    setCursorMode();
                    indexPoint = -1;
                    GEvent.clearListeners(map);
                    disableForm(true);
                }
                else if(etat == "remove"){
                    setDeleteMode();
                    indexPoint = -1;
                    document.getElementById("ckPolySuperimpose").checked = false;
                }
                else if(etat == "edit"){
                    setEditMode();
                    indexPoint = -1;
                    document.getElementById("ckPolySuperimpose").checked = false;
                    disableForm(false);
                }
                else if(etat == "add"){
                    setAddMode();
                    document.getElementById("ckPolySuperimpose").checked = false;
                    indexPoint = -1;
                    clearLists();
                    GEvent.clearListeners(map);
                    
                    GEvent.addListener(map, "click", getPoint);
                    clearForm();
                    document.getElementById("selectedGeofence").selectedIndex = 0;
                    disableForm(false);
                }

                if(etat != "cursor"){
                    drawPoly(typePoly);
                }
                action = etat;
            }
           

            /**
             *Fonction sets the type of drawning for the geofence on the GMAP
             */
            function setTypePoly(){

                if(document.getElementById("ckPoly").checked == true){
                    typePoly = "full";
                }
                else{
                    typePoly = "line";
                }
                if(action != "validated"){
                    drawPoly(typePoly);
                }
                
            }

            /**
             * Function allows drowing geofences by calling getPoint and showpoint functions in the map
             */
            function drawGeofence() {
                //clearLists();
                indexPoint = -1;
                action = "add";
                $("#pcursor").text(action);

                gpsLatLon = new GLatLng(47.643742,6.8565076);
                map.setCenter(gpsLatLon, 17);
                map.setUIToDefault();
                //map.clearOverlays();
                GEvent.clearListeners(map);
                GEvent.addListener(map, "click", getPoint);

            }

             /**
             *
             */
            function getPoint(overlay, latLon) {
                address = latLon;
                geocoder.getLocations(latLon, showPoint);
            }

            /**
             * Show the selected point as a Marker with latitude and longitude information
             */
            function showPoint(response) {
                map.clearOverlays();
                place = response.Placemark[0];
                point = new GLatLng(place.Point.coordinates[1],place.Point.coordinates[0]);

                if(action == "add" && indexPoint == -1){
                    listPoints.push(point);
                    listeLat += place.Point.coordinates[1]+" : ";
                    listeLng += place.Point.coordinates[0]+" : ";
                }
                else if(action == "addBefore" && indexPoint != -1){
                    listPoints.splice(indexPoint, 0, point)
                    listeLat = spliceList(listeLat, place.Point.coordinates[1], indexPoint, 0)
                    listeLng = spliceList(listeLng, place.Point.coordinates[0], indexPoint, 0)
                }
                else if(action == "addAfter" && indexPoint != -1){
                    indexPoint++;
                    listPoints.splice(indexPoint, 0, point)
                    listeLat = spliceList(listeLat, place.Point.coordinates[1], indexPoint, 0)
                    listeLng = spliceList(listeLng, place.Point.coordinates[0], indexPoint, 0)
                    
                }

                drawPoly(typePoly);
                
            }


            /*
             *Fonction draws the geofence following a type of drawing
             */
            function drawPoly(type){
                
               var poly;
               if(document.getElementById("ckPolySuperimpose").checked != true){
                   map.clearOverlays();
               }
                var color = document.getElementById ("selectedColor").value;
                if(listPoints.length > 1){
                    if(type == "full"){
                        poly = new GPolygon(listPoints,'#00CCFF',3,3,'#'+color,0.5,{clickable:true});
                    }
                    else{
                         poly = new GPolygon(listPoints,'#000CFF',3,3,'#000CFF',0,{clickable:true});
                    }
                    
                    map.addOverlay(poly);
                }

                if(document.getElementById("ckPolySuperimpose").checked != true){
                    listPointsToMarkers(listPoints);
                }
            }



            /**
             *Function shows the state of the lists, used the test only
             */
            function showList(){
                $("#listeLat").text(listeLat);
                $("#listeLng").text(listeLng);
                $("#listePoints").text(listPoints.toString());
            }

            /**
             *Function returns an geofences thank a selectbox
             */
            function testShowGeofence() {
                clearForm();
                action = "cursor";
                setCursorMode();
                var id = document.getElementById("selectedGeofence").value
                $.post("ajaxCheckpoints.jsp", {selectedGeofence: id, action: "getGeoInfo"},
                    function(data){
                        eval(trim(data));
                        $.post("ajaxCheckpoints.jsp", {selectedGeofence: id,action: "getGeoPoints"},
                            function(data){
                                parsePoints(trim(data));
                                $.post("ajaxSchedule.jsp", {selectedGeofence: id,action: "getGeoSchedule"},
                                    function(data){
                                        
                                        eval(trim(data));
                                        }, "html");
                                }, "html");
                        }, "html");

                    
            }

            /**
             *Function clears of the lists used
             */
            function clearLists()
            {
                indexPoint = -1;
                listPoints = [];
                listeLat = "";
                listeLng = "";
            }


            /**
             *Basic TRIM function
             */
            function trim (myString)
            {
                return myString.replace(/^\s+/g,'').replace(/\s+$/g,'')
            }

            /**
             *Function parses the points
             */
            function parsePoints(points){
                clearLists();
                map.setCenter(gpsLatLon, 16);

                if(document.getElementById("ckPolySuperimpose").checked != true){
                   map.clearOverlays();
                }

                var tabPoits = points.split("#");
                var tabPoint;
                var color = document.getElementById ("selectedColor").value;
                
                for(var i = 0; i < tabPoits.length; i++){
                    tabPoint = tabPoits[i].split("|");
                    point = new GLatLng(tabPoint[0],tabPoint[1]);
                    listPoints.push(point);
                    listeLat += tabPoint[0] +" : ";
                    listeLng += tabPoint[1] +" : ";
                }
                drawPoly(typePoly);

                
            }

            /**
             *Funtions creates markers on the map with a list of points
             */
            function listPointsToMarkers(list){
                
                for(var i = 0; i < list.length; i++){
                    marker = new GMarker(list[i], {draggable:true} );
                    marker.value = i;
                    addDragEvent(marker);
                    map.addOverlay(marker);

                }
            }



            /**
             *Function removes item from a list
             */
            function spliceList(list, lat, i, nbRm){
                
                var temp = list.split(" : ");
                if(lat != -1){
                    temp.splice(i, nbRm, lat);
                }
                else{
                    temp.splice(i, nbRm);
                }
                
                var res = "";
                for(var j = 0; j < temp.length; j++){
                    if(temp.length == j + 1){
                        res += temp[j]
                    }
                    else
                    {
                         res += temp[j] + " : ";
                    }

                }
                return res;

            }

            /**
             *Function adds drag and drop event on the marker
             */
            function addDragEvent(marker){
                GEvent.addListener(marker, "dragstart", function() {
                      map.closeInfoWindow();
                      });

                GEvent.addListener(marker, "dragend", function() {
                  //listPoints.push(this.getLatLng());
                  
                  listPoints.splice(this.value,1,this.getLatLng());
                  listeLat = spliceList(listeLat,this.getLatLng().lat(), this.value, 1);
                  listeLng = spliceList(listeLng,this.getLatLng().lng(), this.value, 1);
                  drawPoly(typePoly);
                  
                  
                  });

                  GEvent.addListener(marker, "click", function(){
                      indexPoint = marker.value;
                      if(action == "remove"){
                          removePoint(indexPoint);
                      }
                      else{
                        action = "addBefore";
                        //$("#pcursor").text(action);
                        GEvent.clearListeners(map);
                        GEvent.addListener(map, "click", getPoint);
                      }
                      
                  });

                  GEvent.addListener(marker, "dblclick", function(){
                      if(action == "remove"){
                        removePoint(indexPoint);
                      }
                      else{
                        indexPoint = marker.value;
                        action = "addAfter";
                        //$("#pcursor").text(action);
                        GEvent.clearListeners(map);
                        GEvent.addListener(map, "click", getPoint);
                      }
                      
                  });

            }

            /**
             *Function removes point from a list
             */
            function removePoint(i){
                if(i != -1){
                    listPoints.splice(i,1);
                    listeLat = spliceList(listeLat,-1, i, 1);
                    listeLng = spliceList(listeLng,-1, i, 1);
                    drawPoly(typePoly);
                    
                }                
            }

            

            /**
             *Function sets informations into the form
             */
            function setGeoInfo(name, speed, height, weight, color, vehicles){
                document.getElementById ("txtGeofenceTitle").value = name;
                document.getElementById ("txtSpeedLimit").value = speed;
                document.getElementById ("txtMaxWeight").value = weight;
                document.getElementById ("txtMaxHeight").value = height;
                document.getElementById("selectedColor").selectedIndex = getIndexColor(trim(color));
                for(i=0; i< vehicles.length; i++){
                    if(vehicles[i] == 1){
                        document.getElementById("selectedVehiclesProhibited").options[0].selected = true;
                    }
                    if(vehicles[i] == 2){
                        document.getElementById("selectedVehiclesProhibited").options[1].selected = true;
                    }
                    if(vehicles[i] == 3){
                        document.getElementById("selectedVehiclesProhibited").options[2].selected = true;
                    }
                }
            }


            /**
             *
             */
            function getIndexColor(color){
                var selectColor = document.getElementById("selectedColor");
                for(var i = 0; i < selectColor.length; i++){
                    if(selectColor.options[i].value == color){
                        return i;
                    }
                }
                return 0;
            }
            /**
             * This function allow to validate the geofence drawn on the map
             */
            function validate(){
                disableForm(true);
                action = "validated";
                map.setCenter(gpsLatLon, 16);
                map.clearOverlays();
                var color = document.getElementById ("selectedColor").value;
                if(listPoints.length > 1){
                    var poly = new GPolygon(listPoints,'#00CCFF',3,3,'#'+color,0.5,{clickable:true});
                    map.addOverlay(poly);
                }
            }

            /**
             * This function recall the addGeofence servelet to save data into MySQL database
             */
            function saveGeofence() {
                var titre = document.getElementById ("txtGeofenceTitle").value;
                var color = document.getElementById ("selectedColor").value;
                var VMax=document.getElementById ("txtSpeedLimit").value;
                var PMax=document.getElementById ("txtMaxWeight").value;
                var HMax=document.getElementById ("txtMaxHeight").value;
                var TypeV = $("#selectedVehiclesProhibited").val() || [];
                var isTDG=false;
                if (document.getElementById ("chkIsTDG").checked){
                    isTDG=true;
                }
                var geo=document.getElementById ("selectedGeofence").value;
                var schedule = "&scheduleType=" + scheduleType;

                if(scheduleType == 1){
                    schedule += "&day=" + $("#datepickeroneDay").val();
                }
                else if(scheduleType == 2){
                    schedule += "&dayBegin=" + $("#datepickerDTDBeginDay").val();
                    schedule += "&dayEnd=" + $("#datepickerDTDEndDay").val();
                    schedule += "&hourBegin=" + $("#txtDTDBeginHour").val();
                    schedule += "&hourEnd=" + $("#txtDTDEndHour").val();
                }
                else if(scheduleType == 3){
                    schedule += "&day=" + $("#datepickerDTDay").val();
                    schedule += "&hourBegin=" + $("#txtDTDBeginHour").val();
                    schedule += "&hourEnd=" + $("#txtDTDEndHour").val();
                }
                else if(scheduleType == 4){

                    schedule += "&days=";

                    if (document.getElementById ("chkMon").checked){
                        schedule += "1,"
                    }
                    else{
                        schedule += "0,"
                    }
                    if (document.getElementById ("chkTue").checked){
                        schedule += "1,"
                    }
                    else{
                        schedule += "0,"
                    }
                    if (document.getElementById ("chkWed").checked){
                        schedule += "1,"
                    }
                    else{
                        schedule += "0,"
                    }
                    if (document.getElementById ("chkThu").checked){
                        schedule += "1,"
                    }
                    else{
                        schedule += "0,"
                    }
                    if (document.getElementById ("chkFri").checked){
                        schedule += "1,"
                    }
                    else{
                        schedule += "0,"
                    }
                    if (document.getElementById ("chkSat").checked){
                        schedule += "1,"
                    }
                    else{
                        schedule += "0,"
                    }
                    if (document.getElementById ("chkSun").checked){
                        schedule += "1"
                    }
                    else{
                        schedule += "0"
                    }

                    schedule += "&hourBegin=" + $("#txtBeginning").val();
                    schedule += "&hourEnd=" + $("#txtEnd").val();
                }


                if(listeLat==""){
                    alert("The forbidden area is not shown in the map");
                }
                else if(TypeV == ""){
                    alert("Select at least one Vehicle");
                    //
                }
                else{
                    window.location = "/GeofencingClient/addGeofence?titre="+titre+"&couleur="+color+"&VMax="+VMax+"&PMax="+PMax+"&HMax="+HMax+"&TypeV="+TypeV.join(",")+"&lat="+listeLat+"&lng="+listeLng+"&isTDG="+isTDG+"&geo="+geo+schedule;
                }
            }


            /**
             * Function sets the schedule form with data from the database
             */function setSchedule(schedule){
                clearSchedule();
                scheduleType = schedule[0];
                 $("#pHours").html("");

                if(schedule[0] == "1"){
                    $("#datepickeroneDay").val(schedule[1]);
                    $("#pHours").html("Geofence available on " + schedule[1]);
                }else if(schedule[0] == "2"){
                    $("#datepickerDTDBeginDay").val(schedule[1]);
                    $("#txtDTDBeginHour").val(schedule[2]);
                    $("#datepickerDTDEndDay").val(schedule[3]);
                    $("#txtDTDEndHour").val(schedule[4]);
                    $("#pHours").html("Geofence available from " + schedule[1] + " to " + schedule[3] + "</br> everyday from " + schedule[2] + " to " + schedule[4]);
                }else if(schedule[0] == "3"){
                    $("#datepickerDTDay").val(schedule[1]);
                    $("#txtDTBeginHour").val(schedule[2]);
                    $("#txtDTEndHour").val(schedule[3]);
                    $("#pHours").html("Geofence available on " + schedule[1] + " from " + schedule[2] + " to " + schedule[3]);

                }else if(schedule[0] == "4"){
                    var days = "";
                    if (schedule[1] == "1"){
                        document.getElementById ("chkMon").checked = true;
                        days = days + " mondays ";
                    }
                    else{
                        document.getElementById ("chkMon").checked = false;
                    }
                    if (schedule[2] == "1"){
                        document.getElementById ("chkTue").checked = true;
                        days = days + " tuesdays ";
                    }
                    else{
                        document.getElementById ("chkTue").checked = false;
                    }
                    if (schedule[3] == "1"){
                        document.getElementById ("chkWed").checked = true;
                        days = days + " wednesdays ";
                    }
                    else{
                        document.getElementById ("chkWed").checked = false;
                    }
                    if (schedule[4] == "1"){
                        document.getElementById ("chkThu").checked = true;
                        days = days + " thursdays ";
                    }
                    else{
                        document.getElementById ("chkThu").checked = false;
                    }
                    if (schedule[5] == "1"){
                        document.getElementById ("chkFri").checked = true;
                        days = days + " fridays ";
                    }
                    else{
                        document.getElementById ("chkFri").checked = false;
                    }
                    if (schedule[6] == "1"){
                        document.getElementById ("chkSat").checked = true;
                        days = days + " saturdays ";
                    }
                    else{
                        document.getElementById ("chkSat").checked = false;
                    }
                    if (schedule[7] == "1"){
                        document.getElementById ("chkSun").checked = true;
                        days = days + " sundays ";
                    }
                    else{
                        document.getElementById ("chkSun").checked = false;
                    }

                    $("#txtBeginning").val(schedule[8]);
                    $("#txtEnd").val(schedule[9]);
                    $("#pHours").html("Geofence available on:</br> " + days + "</br>from " + schedule[8] + " to " + schedule[9] );
                }
                else
                    $("#pHours").html("Geofence always available");
            }

            function updateTextSchedule(){
                 $("#pHours").html("");

                if(scheduleType == "1"){

                    $("#pHours").html("Geofence available on " + $("#datepickeroneDay").val());
                }else if(scheduleType == "2"){
                    $("#pHours").html("Geofence available from " + $("#datepickerDTDBeginDay").val() + " to " + $("#datepickerDTDEndDay").val() + "</br> everyday from " + $("#txtDTDBeginHour").val() + " to " + $("#txtDTDEndHour").val());
                }else if(scheduleType == "3"){
                    ;
                    ;
                    ;
                    $("#pHours").html("Geofence available on " + $("#datepickerDTDay").val() + " from " + $("#txtDTBeginHour").val() + " to " + $("#txtDTEndHour").val());

                }else if(scheduleType == "4"){
                    var days = "";
                    if (document.getElementById ("chkMon").checked == true){
                        days = days + " mondays ";
                    }
                    if (document.getElementById ("chkTue").checked == true){
                        days = days + " tuesdays ";
                    }
                    if (document.getElementById ("chkWed").checked == true){
                        days = days + " wednesdays ";
                    }
                    if (document.getElementById ("chkThu").checked == true){
                        days = days + " thursdays ";
                    }
                    if (document.getElementById ("chkFri").checked == true){
                        days = days + " fridays ";
                    }
                    if (document.getElementById ("chkSat").checked == true){
                        days = days + " saturdays ";
                    }
                    if (document.getElementById ("chkSun").checked == true){
                        days = days + " sundays ";
                    }

                    $("#pHours").html("Geofence available on:</br> " + days + "</br>from " + $("#txtBeginning").val() + " to " + $("#txtEnd").val() );
                }
                else
                    $("#pHours").html("Geofence always available");
            }


            /**
             * Function clears the schedule form
             */
            function clearSchedule(){
                $("#datepickeroneDay").val("");

                $("#datepickerDTDBeginDay").val("");
                $("#txtDTDBeginHour").val("");
                $("#datepickerDTDEndDay").val("");
                $("#txtDTDEndHour").val("");

                $("#datepickerDTDay").val("");
                $("#txtDTDBeginHour").val("");
                $("#txtDTDEndHour").val("");

                document.getElementById ("chkMon").checked = false;
                document.getElementById ("chkTue").checked = false;
                document.getElementById ("chkWed").checked = false;
                document.getElementById ("chkThu").checked = false;
                document.getElementById ("chkFri").checked = false;
                document.getElementById ("chkSat").checked = false;
                document.getElementById ("chkSun").checked = false;
                $("#txtBeginning").val("");
                $("#txtEnd").val("");
            }

            /**
             * Undo the changes
             */
            function cancelGeofence(){
                var geo=document.getElementById ("selectedGeofence").value;
                if(geo!= -1){
                    clearForm();
                    testShowGeofence();
                }
                else{
                    clearForm();
                    clearSchedule();
                }
            }

            /**
             * Function clears the geofence form
             */
            function clearForm(){
                $("#txtGeofenceTitle").val("");
                $("#selectedColor").attr("selectedIndex", "0");
                $("#txtSpeedLimit").val("");
                $("#txtMaxWeight").val("");
                $("#txtMaxHeight").val("");
                $("#chkIsTDG").attr("checked", false);
                for(var i = 0; i < 3; i++){
                    document.getElementById("selectedVehiclesProhibited").options[i].selected = false;
                }
            }

            /**
             * Function deletes geofence from a Database
             */
            function deleteGeofence(){
                var select = document.getElementById("selectedGeofence");
                var id = select.value;
                var text = select.options[select.selectedIndex].text;
                if(id != "-1"){
                        if (confirm("Do you want to delete geofence : "  + id + " - " + text +  " ?")) { // Clic sur OK
                           $.post("ajaxDelete.jsp", {selectedGeofence: id,action: "deleteGeofence"},
                                                function(data){
                                                    $("#selectedGeofence").html(data);
                                                    }, "html");

                            clearForm();
                            clearLists();
                            clearSchedule();
                            document.getElementById("selectedGeofence").selectedIndex = 0;
                            map.clearOverlays();
                       }
                }else{
                    alert("Select a Geofence");
                }
                
            }

            /**
             * Function allows user to see severals geofence in the same time
             */
            function setSuperimpose(){
                if(document.getElementById("ckPolySuperimpose").checked == true){
                    document.getElementById("ckPoly").checked = true;
                    setAction("cursor");
                    setTypePoly();
                    validate();
                    disableForm(true);
                }else{
                    setAction("cursor");
                    setTypePoly();

                }              
                
            }

            /**
             * Functions clears all form
             */
            function clearAll(){
                clearForm();
                clearLists();
                clearSchedule();
                document.getElementById("selectedGeofence").selectedIndex = 0;
            }

            /**
             * Function disables the forms
             */
            function disableForm(bool){
                document.getElementById("txtGeofenceTitle").disabled = bool;
                document.getElementById("selectedColor").disabled = bool;
                document.getElementById("txtSpeedLimit").disabled = bool;
                document.getElementById("txtMaxWeight").disabled = bool;
                document.getElementById("txtMaxHeight").disabled = bool;
                document.getElementById("chkIsTDG").disabled = bool;
                document.getElementById("selectedVehiclesProhibited").disabled = bool;
                document.getElementById("btnSave").disabled = bool;

            }


function displayDiv(div) {
    var divToDisplay = document.getElementById(div);
    if(divToDisplay != null){
        divToDisplay.style.visibility = "visible";
    }
}

function stopDisplayDiv(div){
    var divToDisplay = document.getElementById(div);
    if(divToDisplay != null){
        divToDisplay.style.visibility = "hidden";
    }
}

function saveSchedule(type){
    scheduleType = type;
    var id=document.getElementById ("selectedGeofence").value;
    if(id != "-1"){
        var schedule = "&scheduleType=" + scheduleType;

        if(scheduleType == 1){
            $.post("ajaxSchedule.jsp", {type : "1", selectedGeofence: id,action: "setSchedule", day : $("#datepickeroneDay").val()},
                    function(data){

                        }, "html");
        }
        else if(scheduleType == 2){
            $.post("ajaxSchedule.jsp", {type : "2", selectedGeofence: id,action: "setSchedule", dayBegin : $("#datepickerDTDBeginDay").val(), dayEnd : $("#datepickerDTDEndDay").val(), hourBegin : $("#txtDTDBeginHour").val(), hourEnd : $("#txtDTDEndHour").val()},
                    function(data){

                        }, "html");
        }
        else if(scheduleType == 3){
            $.post("ajaxSchedule.jsp", {type : "3", selectedGeofence: id,action: "setSchedule", dayBegin : $("#datepickerDTDBeginDay").val(), hourBegin : $("#txtDTDBeginHour").val(), hourEnd : $("#txtDTDEndHour").val()},
                    function(data){

                        }, "html");
        }
        else if(scheduleType == 4){

            var monday = 0;
            var tuesday = 0;
            var wednesday = 0;
            var thursday = 0;
            var friday = 0;
            var saturday = 0;
            var sunday = 0;

            if (document.getElementById ("chkMon").checked){
                monday = "1"
            }

            if (document.getElementById ("chkTue").checked){
                tuesday = "1"
            }

            if (document.getElementById ("chkWed").checked){
                wednesday = "1"
            }

            if (document.getElementById ("chkThu").checked){
                thursday = "1"
            }

            if (document.getElementById ("chkFri").checked){
                friday = "1"
            }

            if (document.getElementById ("chkSat").checked){
                saturday = "1"
            }

            if (document.getElementById ("chkSun").checked){
                sunday = "1"
            }

            he = $("#txtEnd").val();
            hb = $("#txtBeginning").val();

            $.post("ajaxSchedule.jsp", {type : "4", selectedGeofence: id,action: "setSchedule",mon : monday, tue : tuesday, wed : wednesday, thu : thursday, fri : friday, sat : saturday, sun : sunday, hourBegin : hb, hourEnd : he },
                    function(data){

                        }, "html");

        }

    }
    updateTextSchedule()
    pushClose()
}

function clearScheduleForm(){
    //to do when quitting add hour
}

function highlightButton(btn){
    var btnToHighlight = document.getElementById(btn);
    if(btnToHighlight != null){
        btnToHighlight.style.background = "grey";
    }
}

function stopHighlightButton(btn){
    var btnToHighlight = document.getElementById(btn);
    if(btnToHighlight != null){
        btnToHighlight.style.background = "";
        btnToHighlight.style.background.hover = "darkgrey";
    }
}

$(function(){
    $('#txtBeginning').timepickr({updateLive: false});
    $('#txtEnd').timepickr({updateLive: false});
    $('#txtDTDBeginHour').timepickr({updateLive: false});
    $('#txtDTDEndHour').timepickr({updateLive: false});
    $('#txtDTBeginHour').timepickr({updateLive: false});
    $('#txtDTEndHour').timepickr({updateLive: false});
});

$(function(){
    $.datepicker.setDefaults();
    $("#datepickeroneDay").datepicker({dateFormat : "yy-mm-dd"});
    $("#datepickerDTDBeginDay").datepicker({dateFormat : "yy-mm-dd"});
    $("#datepickerDTDEndDay").datepicker({dateFormat : "yy-mm-dd"});
    $("#datepickerDTDay").datepicker({dateFormat : "yy-mm-dd"});
});

function setCursorMode(){
    highlightButton('imgCursorMode');
    stopHighlightButton('imgAddMode');
    stopHighlightButton('imgEditMode');
    stopHighlightButton('imgDeleteMode');
    stopHighlightButton('imgValidate');
}

function setAddMode(){
    highlightButton('imgAddMode');
    stopHighlightButton('imgEditMode');
    stopHighlightButton('imgDeleteMode');
    stopHighlightButton('imgValidate');
    stopHighlightButton('imgCursorMode');
}

function setEditMode(){
    highlightButton('imgEditMode');
    stopHighlightButton('imgAddMode');
    stopHighlightButton('imgDeleteMode');
    stopHighlightButton('imgValidate');
    stopHighlightButton('imgCursorMode');
}

function setDeleteMode(){
    highlightButton('imgDeleteMode');
    stopHighlightButton('imgAddMode');
    stopHighlightButton('imgEditMode');
    stopHighlightButton('imgValidate');
    stopHighlightButton('imgCursorMode');
}

function setValidateMode(){
    highlightButton('imgValidate');
    stopHighlightButton('imgAddMode');
    stopHighlightButton('imgEditMode');
    stopHighlightButton('imgDeleteMode');
    stopHighlightButton('imgCursorMode');
}

function pushClose(){
    stopDisplayDiv('divDate');
    stopDisplayDiv('divAddHour');
    stopDisplayDiv('divDateToDate');
    stopDisplayDiv('divDatetime');
    stopDisplayDiv('divDaysAndHoursInWeek');
    stopHighlightButton('imgWeek');
    stopHighlightButton('imgDate');
    stopHighlightButton('imgDatetime');
    stopHighlightButton('imgDaysAndHoursInWeek');
}

function setDate(){
    displayDiv('divDate');
    stopDisplayDiv('divDateToDate');
    stopDisplayDiv('divDatetime');
    stopDisplayDiv('divDaysAndHoursInWeek');
    highlightButton('imgDate');
    stopHighlightButton('imgWeek');
    stopHighlightButton('imgDatetime');
    stopHighlightButton('imgDaysAndHoursInWeek');
}

function setDateToDate(){
    displayDiv('divDateToDate');
    stopDisplayDiv('divDate');
    stopDisplayDiv('divDatetime');
    stopDisplayDiv('divDaysAndHoursInWeek');
    highlightButton('imgWeek');
    stopHighlightButton('imgDate');
    stopHighlightButton('imgDatetime');
    stopHighlightButton('imgDaysAndHoursInWeek');
}

function setDateTime(){
    displayDiv('divDatetime');
    stopDisplayDiv('divDate');
    stopDisplayDiv('divDateToDate');
    stopDisplayDiv('divDaysAndHoursInWeek');
    highlightButton('imgDatetime');
    stopHighlightButton('imgDate');
    stopHighlightButton('imgWeek');
    stopHighlightButton('imgDaysAndHoursInWeek');
}

function setDaysAndHoursInWeek(){
    displayDiv('divDaysAndHoursInWeek')
    stopDisplayDiv('divDate');
    stopDisplayDiv('divDateToDate');
    stopDisplayDiv('divDatetime');
    highlightButton('imgDaysAndHoursInWeek');
    stopHighlightButton('imgDate');
    stopHighlightButton('imgWeek');
    stopHighlightButton('imgDatetime');
}