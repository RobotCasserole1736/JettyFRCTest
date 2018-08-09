
//Note - this PORT string must be aligned with the port the webserver is served on.
var port = "5805";
var hostname = window.location.hostname+":"+port;

//Render Constants
var PX_PER_FOOT = 13;
var FIELD_COLOR = '#fdd';
var BOT_COLOR = '#d22';
var CANVAS_MARGIN_PX = 20;

var ROBOT_W_PX = 0;
var ROBOT_H_PX = 0;

//Websocket variables
var dataSocket = new WebSocket("ws://"+hostname+"/poseview")
var numTransmissions = 0;

dataSocket.onopen = function (event) {
  document.getElementById("id01").innerHTML = "Socket Open";
};

dataSocket.onmessage = function (event) {
  procData(event.data);
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

function procData(json_data) {
    
    //Parse incoming websocket packet as JSON
    var data = JSON.parse(json_data);
    
    //Grab a reference to the canvases
    this.canvas = document.getElementById("field_bg_canvas");
    this.ctx = this.canvas.getContext("2d");
    
    this.canvas_robot = document.getElementById("robot_canvas");
    this.ctx_robot = this.canvas_robot.getContext("2d");

    if(data.step == "init"){
        //Handle view init information

        //Get extrema of the described shape and set canvas size
        max_x_px = 0;
        min_x_px = 0;
        max_y_px = 0;
        min_y_px = 0;
        for(i = 0; i < data.field_polygon.length; i++){
            x_px = data.field_polygon[i].x*PX_PER_FOOT;
            y_px = data.field_polygon[i].y*PX_PER_FOOT;
            
            max_x_px = Math.max(x_px,max_x_px);
            min_x_px = Math.min(x_px,min_x_px);
            max_y_px = Math.max(y_px,max_y_px);
            min_y_px = Math.min(y_px,min_y_px);
        }
        this.ctx.canvas.height  = max_y_px - min_y_px;
        this.ctx.canvas.width = max_x_px - min_x_px;
        this.ctx_robot.canvas.height  = max_y_px - min_y_px;
        this.ctx_robot.canvas.width = max_x_px - min_x_px;
        
        this.bot_origin_offset_x = -1 * min_x_px;
        this.bot_origin_offset_y = -1 * min_y_px;
        
        //Configure the appearance 
        this.ctx.fillStyle = FIELD_COLOR;
        
        
        //Draw polygon based on recieved points 
        this.ctx.beginPath();
        for(i = 0; i < data.field_polygon.length; i++){
            x_px = data.field_polygon[i].x*PX_PER_FOOT + this.bot_origin_offset_x;
            y_px = this.ctx.canvas.height - (data.field_polygon[i].y*PX_PER_FOOT) + this.bot_origin_offset_y; //transform from software refrence frame to html/js canvas reference frame.
            
            if(i == 0){
                this.ctx.moveTo(x_px,y_px);
            } else {
                this.ctx.lineTo(x_px,y_px);
            }
            
        }
        this.ctx.closePath();
        this.ctx.fill();
        
        //Save robot dimensions
        ROBOT_W_PX = data.bot_dims.w*PX_PER_FOOT;
        ROBOT_H_PX = data.bot_dims.h*PX_PER_FOOT;

        
    } else if (data.step == "updt") {
        //Handle robot pose update
        drawRobot(this.canvas_robot,
                 this.ctx_robot,
                 data.robot_pose.x*PX_PER_FOOT + this.bot_origin_offset_x, 
                 (ctx.canvas.height - data.robot_pose.y*PX_PER_FOOT) + this.bot_origin_offset_y, 
                 -1 * data.robot_pose.theta );

    }
}

drawRobot = function(canvas, ctx, x_pos_px, y_pos_px, rotation_deg){
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    //Configure the appearance 
    
    
    ctx.translate(x_pos_px,y_pos_px);
    ctx.rotate(rotation_deg*Math.PI/180);
    ctx.fillStyle = BOT_COLOR;
    ctx.fillRect(-ROBOT_W_PX/2,-ROBOT_H_PX/2,ROBOT_W_PX,ROBOT_H_PX);
    ctx.fillStyle = '#000';
    drawArrowhead(ctx, 0, ROBOT_H_PX/2, 0, -ROBOT_H_PX/3, 8);
    ctx.rotate(-1*rotation_deg*Math.PI/180);
    ctx.translate(-x_pos_px,-y_pos_px);
}


function drawArrowhead(context, from_x, from_y, to_x, to_y, radius) {
	var x_center = to_x;
	var y_center = to_y;

	var angle;
	var x;
	var y;

	context.beginPath();

	angle = Math.atan2(to_y - from_y, to_x - from_x)
	x = radius * Math.cos(angle) + x_center;
	y = radius * Math.sin(angle) + y_center;

	context.moveTo(x, y);

	angle += (1.0/3.0) * (2 * Math.PI)
	x = radius * Math.cos(angle) + x_center;
	y = radius * Math.sin(angle) + y_center;

	context.lineTo(x, y);

	angle += (1.0/3.0) * (2 * Math.PI)
	x = radius *Math.cos(angle) + x_center;
	y = radius *Math.sin(angle) + y_center;

	context.lineTo(x, y);

	context.closePath();

	context.fill();
}