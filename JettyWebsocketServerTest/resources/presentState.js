
//Note - this PORT string must be aligned with the port the Data Server is served on.
var port = "5806";
var hostname = window.location.hostname+":"+port;

var dataSocket = new WebSocket("ws://"+hostname+"/ds")
var numTransmissions = 0;

dataSocket.onopen = function (event) {
  document.getElementById("id01").innerHTML = "Socket Open";

  // Send the command to get the list of all signals
  dataSocket.send(JSON.stringify({cmd: "getSig"}));

  //Nothing else to do on init, at least till the server responds with the signal list
  
};

dataSocket.onmessage = function (event) {
  serverMsg = JSON.parse(event.data);

  var daq_request_cmd = {};

  if(serverMsg.type == "sig_list") {
    // When the server sends us a signal list, we respond by requesting a single DAQ List with every signal
    daq_request_cmd.cmd = "addDaq";
    daq_request_cmd.id = "main";
    daq_request_cmd.tx_period_ms = "100";
    daq_request_cmd.samp_period_ms = "100";

    var i = 0;
    for(signal in serverMsg.signals){
      
      daq_request_cmd.id[i++] = signal.id[i++]
    }

    websocket.send(JSON.stringify(daq_request_cmd));

  } else if(serverMsg.type == "daq_update") {
    if(serverMsg.daq_id == "main"){

    }
  }

  genTable(serverMsg.signals);
  numTransmissions = numTransmissions + 1;
  document.getElementById("id01").innerHTML = "COM Status: Socket Open. RX Count:" + numTransmissions; 
};

dataSocket.onerror = function (error) {
  document.getElementById("id01").innerHTML = "COM Status: Error with socket. Reconnect to robot, open driver station, then refresh this page.";
  alert("ERROR from Present State: Robot Disconnected!!!\n\nAfter connecting to the robot, open the driver station, then refresh this page.");
};

dataSocket.onclose = function (error) {
  document.getElementById("id01").innerHTML = "COM Status: Error with socket. Reconnect to robot, open driver station, then refresh this page.";
  alert("ERROR from Present State: Robot Disconnected!!!\n\nAfter connecting to the robot, open the driver station, then refresh this page.");
};

function genTable(arr) {
    var i;
    var out = "<table border=\"1\">";

    for(i = 0; i < arr.state_array.length; i++) {
        out += "<tr><td>" +
        arr.state_array[i].name +
        "</td><td style=\"width: 200px;\">" +
        arr.state_array[i].value +
        "</td></tr>";
    }
    out += "</table>";
    document.getElementById("id02").innerHTML = out;
}