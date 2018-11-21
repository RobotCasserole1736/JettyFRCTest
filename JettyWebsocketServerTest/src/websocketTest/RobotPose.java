package websocketTest;

import org.usfirst.frc.team1736.lib.DataServer.Signal;

public class RobotPose {

	//Robot Physical Constants
	public final double wheelRadius_In = 3;
	public final double WHEEL_BASE_WIDTH_FT = 2.0;
	public final double SIDE_LINEAR_DISTANCE_PER_ROBOT_ROTATION_FT = WHEEL_BASE_WIDTH_FT*Math.PI;

	public double leftVelosity_RPM;
	public double rightVelosity_RPM;
	public double poseX;
	public double poseY;
	public double poseT;

	double prevLoopTime = 0;

	Signal poseXSig;
	Signal poseYSig;
	Signal poseTSig;

	public RobotPose(){
		poseXSig = new Signal("botposex", "ft");
		poseYSig = new Signal("botposey", "ft");
		poseTSig = new Signal("botposet", "deg");
		prevLoopTime = System.currentTimeMillis();

		//Field start position
		poseX = 0;
		poseY = 10;
		poseT = 0;
	}
	
	public void setLeftMotorSpeed(double speed) {
		leftVelosity_RPM = speed;
	}

	public void setRightMotorSpeed(double speed){
		rightVelosity_RPM = speed;
    }
	
	public void update() {
		double sampleTime = System.currentTimeMillis();
		double delta_t_sec = (sampleTime - prevLoopTime)*0.001;
		
		//Robot frome velocity
		double leftVelosity_FPS = leftVelosity_RPM * (2*3.14*wheelRadius_In / 60);
		double rightVelosity_FPS = rightVelosity_RPM * (2*3.14*wheelRadius_In / 60);
		
		//Tank-drive robot frame displacement
		double delta_y_robot_ft = (leftVelosity_FPS + rightVelosity_FPS)/2 *delta_t_sec;
		double delta_x_robot_ft = 0;
		double delta_t_robot_deg= ((-1.0 * leftVelosity_FPS) + rightVelosity_FPS) * delta_t_sec * (1/SIDE_LINEAR_DISTANCE_PER_ROBOT_ROTATION_FT) * 360.0;
		
		//Transform to field coordinates
		poseX += -1.0*Math.sin(poseT*Math.PI/180.0)*delta_y_robot_ft + Math.cos(poseT*Math.PI/180.0)*delta_x_robot_ft;
		poseY += Math.cos(poseT*Math.PI/180.0)*delta_y_robot_ft + Math.sin(poseT*Math.PI/180.0)*delta_x_robot_ft;
		poseT += delta_t_robot_deg;

		//Output to webpage
		poseXSig.addSample(sampleTime, poseX);
		poseYSig.addSample(sampleTime, poseY);
		poseTSig.addSample(sampleTime, poseT);
		prevLoopTime = sampleTime;
	}
	
}
