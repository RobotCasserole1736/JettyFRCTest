

//var hostname = "roboRIO-1736-FRC.local:5805" //Robot hostname
var hostname = "localhost:5805" //Local Hostname

var dataSocket = new WebSocket("ws://"+hostname+"/driverviewstream")
var numTransmissions = 0;
var display_objs = {};

//Class for a dial

var casseroleDial = function(elementID_in, min_in, max_in, min_acceptable_in, max_acceptable_in, step_in, name_in) {
    this.min = min_in;
    this.max = max_in;
    this.min_acceptable = min_acceptable_in;
    this.max_acceptable = max_acceptable_in;
    this.step = step_in;
    this.name = name_in;
    this.value = min_in;
    this.elementID = elementID_in;
    
   var chart = {      
      type: 'solidgauge'
   };
   var title = this.name;

   var pane = {
      center: ['50%', '85%'],
      size: '140%',
      startAngle: -150,
      endAngle: 150,
      background: {
         backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || '#EEE',
         innerRadius: '60%',
         outerRadius: '100%',
         shape: 'arc'
      }
   };

   var tooltip = {
      enabled: false
   };
      
   // the value axis
   var yAxis = {
      stops: [
         [this.min_acceptable, '#FF0000'], // red
         [this.max_acceptable, '#00FF00'], // green
         [this.max, '#FF0000'] // red
      ],
      lineWidth: 0,
      minorTickInterval: null,
      tickPixelInterval: 400,
      tickWidth: 0,
      title: {
         y: -70
      },
      labels: {
         y: 16
      },
	  min: this.min,
      max: this.max,
      title: {
         text: this.name
      }
   };	  
   
   var plotOptions = {
      solidgauge: {
         dataLabels: {
            y: 5,
            borderWidth: 0,
            useHTML: true
         }
      }
   };
   
   var credits = {
      enabled: false
   };

   var series = [{
      name: this.name,
      data: [80],
      dataLabels: {
         format: '<div style="text-align:center"><span style="font-size:25px;color:' +
         ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y}</span><br/>' +
         '<span style="font-size:12px;color:silver">km/h</span></div>'
      },
      tooltip: {
         valueSuffix: ' units'
      }
   }];
	  
   var json = {};   
   json.chart = chart; 
   json.title = title;       
   json.pane = pane; 
   json.tooltip = tooltip; 
   json.yAxis = yAxis; 
   json.credits = credits; 
   json.series = series;     
   //$("\""+this.elementID+"\"").highcharts(json);  

    this.settings = json;

    this.init();
}

casseroleDial.prototype.init = function(){
    //this.canvas = document.getElementById(this.elementID);
    this.chart = Highcharts.chart(this.elementID, this.settings);
    
    
    this.settings;

}


casseroleDial.prototype.setValue = function(new_value){
    
    this.chart.series[0].points[0].update(new_value);

}


//END Dial Class


//START String Box Display class
var casseroleStringBox = function(elementID_in, name_in) {

    //initial input values
    this.name = name_in;
    this.value = "N/A"; //init to something meaningful
    this.elementID = elementID_in;
    
    //Initialize data
    this.init();
    
    //Do initial draw
    this.drawName();
    this.drawValBox();
    this.drawVal();

}

casseroleStringBox.prototype.init = function(){
    this.canvas = document.getElementById(this.elementID);
    this.ctx = this.canvas.getContext("2d");
    
    //Appearance tune params
    this.valueBoxX = this.canvas.width*0.025;  
    this.valueBoxY = this.canvas.height*0.1;  
    this.valueBoxWidth  = this.canvas.width  * 0.95;  
    this.valueBoxHeight = this.canvas.height * 0.4;  
    this.valueTextY = this.valueBoxY + (this.valueBoxHeight*0.85);
	this.valueTextX = this.valueBoxX*2;
    
    this.nameBoxX = this.canvas.width*0.025;  
    this.nameBoxY = this.canvas.height* 0.550;  
    this.nameBoxWidth  = this.canvas.width  * 0.95;  
    this.nameBoxHeight = this.canvas.height * 0.4;  
    this.nameTextY = this.nameBoxY + (this.nameBoxHeight*0.85);
	this.nameTextX = this.nameBoxX*2;
        
    this.textcolor = "#333";
    this.bgcolor = "white";
    
}

casseroleStringBox.prototype.drawName = function(){
    this.ctx.fillStyle=this.bgcolor;
    this.ctx.fillRect(this.nameBoxX, this.nameBoxY, this.nameBoxWidth, this.nameBoxHeight );
    this.ctx.font="bold " + this.nameBoxHeight*0.50 + "px arial";
    this.ctx.fillStyle=this.textcolor;
    this.ctx.fillText(this.name.toString(), this.nameTextX, this.nameTextY);

}

casseroleStringBox.prototype.drawValBox = function(){
    this.ctx.fillStyle=this.bgcolor;
    this.ctx.fillRect(this.valueBoxX, this.valueBoxY, this.valueBoxWidth, this.valueBoxHeight );

}

casseroleStringBox.prototype.drawVal = function(){
    this.ctx.font="bold " + this.valueBoxHeight*0.50 + "px arial";
    this.ctx.fillStyle=this.textcolor;
    this.ctx.fillText(this.value.toString(), this.valueTextX, this.valueTextY);
}

casseroleStringBox.prototype.setValue = function(new_value){
    this.value = new_value;
    this.drawValBox(); //draw over the existing value
    this.drawVal(); //draw new value

}
//END String Display class

//START Boolean Display class
var casseroleBooleanDisplay= function(elementID_in, name_in, color_in) {

    //initial input values
    this.name = name_in;
	this.color = color_in;
    this.value = false;
    this.elementID = elementID_in;
    
    //Initialize data
    this.init();
    
    //Do initial draw
    this.drawBgBox();
    this.drawInd();

}

casseroleBooleanDisplay.prototype.init = function(){
    this.canvas = document.getElementById(this.elementID);
    this.ctx = this.canvas.getContext("2d");
    
    //Appearance tune params
	this.borderSize = 0.05;
	
	this.bgBoxX = this.canvas.width*this.borderSize; 
	this.bgBoxY = this.canvas.height*this.borderSize;  
	this.bgBoxWidth = this.canvas.width*(1-2*this.borderSize); 
	this.bgBoxHeight = this.canvas.height*(1-2*this.borderSize);  
	
	this.indBoxX = this.canvas.width*this.borderSize*2; 
	this.indBoxY = this.canvas.height*this.borderSize*2;  
	this.indBoxWidth = this.canvas.width*(1-4*this.borderSize); 
	this.indBoxHeight = this.canvas.height*(1-4*this.borderSize);  
	
	this.textBoxX = this.canvas.width*this.borderSize*3; 
	this.textBoxY = this.canvas.height*this.borderSize*3;  
	this.textBoxWidth = this.canvas.width*(1-6*this.borderSize); 
	this.textBoxHeight = this.canvas.height*(1-6*this.borderSize);  
        
		
	if(this.color == "red"){
		this.indBoxOffColor = "#440000";
		this.indBoxOnColor = "#CC0000";
		this.textOffColor = "#774444";
		this.textOnColor = "#FFAAAA";
	} else if(this.color == "yellow"){
		this.indBoxOffColor = "#603300";
		this.indBoxOnColor = "#DDAA00";
		this.textOffColor = "#885500";
		this.textOnColor = "#FFEE99";
	}	else { //presume green
		this.indBoxOffColor = "#003300";
		this.indBoxOnColor = "#30CC30";
		this.textOffColor = "#2A662A";
		this.textOnColor = "#A9FFA9";
	}

    this.bgcolor = "#BBBBBB";
    
}

casseroleBooleanDisplay.prototype.drawBgBox = function(){
    this.ctx.fillStyle=this.bgcolor;
    this.ctx.fillRect(this.bgBoxX, this.bgBoxY, this.bgBoxWidth, this.bgBoxHeight );
}

casseroleBooleanDisplay.prototype.drawInd= function(){
	if(this.value == true){
		this.ctx.fillStyle=this.indBoxOnColor;
	} else {
		this.ctx.fillStyle=this.indBoxOffColor;
	}
	this.ctx.fillRect(this.indBoxX, this.indBoxY, this.indBoxWidth, this.indBoxHeight );
	
	if(this.value == true){
		this.ctx.fillStyle=this.textOnColor;
	} else {
		this.ctx.fillStyle=this.textOffColor;
	}
    this.ctx.font="bold " + this.textBoxHeight*0.20 + "px arial";
	this.drawWrapText(this.name, this.textBoxX, this.textBoxY + this.textBoxHeight/4, this.textBoxWidth, this.textBoxHeight/4);
}


casseroleBooleanDisplay.prototype.drawWrapText = function(text, x, y, maxWidth, lineHeight) {
	var words = text.split(' ');
	var line = '';

	for(var n = 0; n < words.length; n++) {
	  var testLine = line + words[n] + ' ';
	  var metrics = this.ctx.measureText(testLine);
	  var testWidth = metrics.width;
	  if (testWidth > maxWidth && n > 0) {
		this.ctx.fillText(line, x, y);
		line = words[n] + ' ';
		y += lineHeight;
	  }
	  else {
		line = testLine;
	  }
	}
	this.ctx.fillText(line, x, y);
}

casseroleBooleanDisplay.prototype.setValue = function(new_value){
	if(new_value == "False"){
		this.value = false;
	} else if (new_value == "True"){
		this.value = true;
	}
	//else, do nothing

    this.drawInd(); //draw over the existing box
}

//END Boolean Display class



//Data socket handlers
dataSocket.onopen = function (event) {
  document.getElementById("id01").innerHTML = "COM Status: Socket Open";
};

dataSocket.onerror = function (error) {
  document.getElementById("id01").innerHTML = "COM Status: Error with socket. Reconnect to robot, open driver station, then refresh this page.";
  alert("ERROR from Driver View: Robot Disconnected!!!\n\nAfter connecting to the robot, open the driver station, then refresh this page.");
};

dataSocket.onclose = function (error) {
  document.getElementById("id01").innerHTML = "COM Status: Error with socket. Reconnect to robot, open driver station, then refresh this page.";
  alert("ERROR from Driver View: Robot Disconnected!!!\n\nAfter connecting to the robot, open the driver station, then refresh this page.");
};

dataSocket.onmessage = function (event) {
  var arr = JSON.parse(event.data);
  
  if(arr.step == "init"){
    //initial setup of the things on the page
    dialCanvasTexts = "";
	stringboxCanvasTexts = "";
	booleansCanvasTexts = "";
    webcamTexts = "";
    
    //Part 1 - HTML Setup
    for(i = 0; i < arr.obj_array.length; i++){
        if(arr.obj_array[i].type == "dial"){
            dialCanvasTexts += "<canvas id=\"obj"+ (arr.obj_array[i].name) +"\" width=\"175\" height=\"175\" style=\"background-color:#333\"></canvas>"
        } else if(arr.obj_array[i].type == "stringbox"){
            stringboxCanvasTexts += "<canvas id=\"obj"+ (arr.obj_array[i].name) +"\" width=\"150\" height=\"75\" style=\"background-color:#333\"></canvas>"
        } else if(arr.obj_array[i].type == "boolean"){
            booleansCanvasTexts += "<canvas id=\"obj"+ (arr.obj_array[i].name) +"\" width=\"75\" height=\"75\" style=\"background-color:#333\"></canvas>"
        } else if(arr.obj_array[i].type == "webcam"){
			var tgt_x_pct = arr.obj_array[i].marker_x;
			var tgt_y_pct = arr.obj_array[i].marker_y;
			var rotation = arr.obj_array[i].rotation_deg;
			//Draw webcam plus crosshairs overlaid
			webcamTexts += "<td><div id=\"outter\" style=\"position:relative;width:300px;height:auto;\"><img src=\""+arr.obj_array[i].url+"\" style=\"width:300px;height:auto;transform:rotate("+rotation.toString()+"deg)\"/><div id=\"crosshair_vert"+ (arr.obj_array[i].name) +"\" style=\"background:yellow;position:absolute;top:"+tgt_y_pct.toString()+"%;left:"+tgt_x_pct.toString()+"%;width:2px;height:30px;transform:translate(-50%, -50%)\"/><div id=\"crosshair_horiz"+ (arr.obj_array[i].name) +"\" style=\"background:yellow;position:absolute;top:"+tgt_y_pct.toString()+"%;left:"+tgt_x_pct.toString()+"%;width:30px;height:2px;transform:translate(-50%, -50%)\"/></div></td>";    
		 }
    }
	
	//Part 2 - update the HTML on the page
    document.getElementById("webcams").innerHTML = webcamTexts;
	document.getElementById("booleans").innerHTML = booleansCanvasTexts;
	document.getElementById("stringboxes").innerHTML = stringboxCanvasTexts;
	document.getElementById("dials").innerHTML = dialCanvasTexts;
    
    //Part 3 - init the data elements
    for(i = 0; i < arr.obj_array.length; i++){
        if(arr.obj_array[i].type == "dial"){
            display_objs[arr.obj_array[i].name] = (new casseroleDial("obj"+(arr.obj_array[i].name), arr.obj_array[i].min, arr.obj_array[i].max, arr.obj_array[i].min_acceptable, arr.obj_array[i].max_acceptable, arr.obj_array[i].step, arr.obj_array[i].displayName));
        } else if(arr.obj_array[i].type == "stringbox") {
			display_objs[arr.obj_array[i].name] = (new casseroleStringBox("obj"+(arr.obj_array[i].name), arr.obj_array[i].displayName));
        } else if(arr.obj_array[i].type == "boolean") {
			display_objs[arr.obj_array[i].name] = (new casseroleBooleanDisplay("obj"+(arr.obj_array[i].name), arr.obj_array[i].displayName, arr.obj_array[i].color));
		} 
		//ignore other types
    }
	

  } else if(arr.step == "valUpdate"){
    for(i = 0; i < arr.obj_array.length; i++){
        if(arr.obj_array[i].type == "webcam"){
            document.getElementById("crosshair_vert"+arr.obj_array[i].name).setAttribute("style",  "background:red;position:absolute;top:"+arr.obj_array[i].marker_y+"%;left:"+arr.obj_array[i].marker_x+"%;width:2px;height:30px;transform:translate(-50%, -50%)");
            document.getElementById("crosshair_horiz"+arr.obj_array[i].name).setAttribute("style", "background:white;position:absolute;top:"+arr.obj_array[i].marker_y+"%;left:"+arr.obj_array[i].marker_x+"%;width:30px;height:2px;transform:translate(-50%, -50%)");
        } else {
            display_objs[arr.obj_array[i].name].setValue(arr.obj_array[i].value);
        }

    }
  }
  //ignore other messages
  
  
};




//Main Execution