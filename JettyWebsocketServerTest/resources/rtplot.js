
//var hostname = "roboRIO-1736-FRC.local:5805" //Robot hostname
var hostname = "localhost:5805" //Local Hostname

var dataSocket = new WebSocket("ws://"+hostname+"/rtplot")

var signal_names = []
var signal_units = []
var signal_display_names = []


dataSocket.onopen = function (event) {
    document.getElementById("id01").innerHTML = "COM Status: Socket Opened.";
};

dataSocket.onerror = function (error) {
    document.getElementById("id01").innerHTML = "COM Status: Error with socket. Reconnect to robot, open driver station, then refresh this page.";
    alert("ERROR from RT Plot: Robot Disconnected!!!\n\nAfter connecting to the robot, open the driver station, then refresh this page.");
};

dataSocket.onclose = function (error) {
    document.getElementById("id01").innerHTML = "COM Status: Error with socket. Reconnect to robot, open driver station, then refresh this page.";
    alert("ERROR from RT Plot: Robot Disconnected!!!\n\nAfter connecting to the robot, open the driver station, then refresh this page.");
};

dataSocket.onmessage = function (event) {
    var data = JSON.parse(event.data);
    if(data.type == "daq_update"){
        addDataToPlot(data.samples);
    } else if(data.type == "signal_list"){
        genSignalListTable(data.signals);
    }

};

function addDataToPlot(data){
    var sig_iter;
    var samp_iter;
    
    console.log("=======================================");
    for(sig_iter = 0; sig_iter < data.length; sig_iter++){
        console.log(data[sig_iter].name);
        for(samp_iter = 0; samp_iter < data[sig_iter].samples.length; samp_iter++){
            console.log("Time: " + data[sig_iter].samples[sig_iter].time + " -- " +data[sig_iter].samples[sig_iter].val)
        }
    }
    
}

function genSignalListTable(arr){
    var i;
    var col_counter = 0;
    var signals_per_row = 2;
    signal_names = [];
    
    var out = "<table><tr>";
    
    for(i = 0; i < arr.length; i++){
        signal_names.push(arr[i].name);
        signal_units.push(arr[i].units);
        signal_display_names.push(arr[i].display_name);
        out += "<td><input type=\"checkbox\" name=\""+arr[i].name+"\" />"+arr[i].display_name+" (" + arr[i].units + ") </td>";
       
        if(col_counter >= (signals_per_row-1)){
            //start a new row
            col_counter = 0;
            out += "</tr><tr>";
        } else {
            col_counter++;
        }
    }
    out +="</tr></table>";
    document.getElementById("id02").innerHTML = out;

}

function handleStartBtnClick(){
    var cmd = "start:";
    
    //Disable signal selection
    document.getElementById("clear_btn").disabled = true;
    for(i = 0; i < signal_names.length; i++){
        checkboxes = document.getElementsByName(signal_names[i]);
        for(var j=0, n=checkboxes.length;j<n;j++) {
            checkboxes[j].disabled = true;
        }
    }
    
    //Select only checked signals
    for(i = 0; i < signal_names.length; i++){
        checkboxes = document.getElementsByName(signal_names[i]);
        for(var j=0, n=checkboxes.length;j<n;j++) {
            
            if(checkboxes[j].checked == true){
                cmd += signal_names[i] + ",";
            }
        }
    }

    //Request data from robot
    dataSocket.send(cmd); 
}

function handleStopBtnClick(){
    //Request stopping data from robot
    dataSocket.send("stop:"); 
    
    //re-enable siagnal selection
    //Disable signal selection
    document.getElementById("clear_btn").disabled = false;
    for(i = 0; i < signal_names.length; i++){
        checkboxes = document.getElementsByName(signal_names[i]);
        for(var j=0, n=checkboxes.length;j<n;j++) {
            checkboxes[j].disabled = false;
        }
    }
}

function handleRefreshSignalsBtnClick(){
    dataSocket.send("get_list:"); 
}

function handleClearBtnClick(){
    var i;
    //Reset all checkboxes to unchecked.
    for(i = 0; i < signal_names.length; i++){
        checkboxes = document.getElementsByName(signal_names[i]);
        for(var j=0, n=checkboxes.length;j<n;j++) {
            checkboxes[j].checked = false;
        }
    }

}

