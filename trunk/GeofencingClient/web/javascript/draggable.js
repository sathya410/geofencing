$("#divAddHour").draggable({handle: 'div'});

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